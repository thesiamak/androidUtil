package ir.drax.processor;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import ir.drax.annotations.Keep;
import ir.drax.annotations.WithPermission;

public class Processor extends AbstractProcessor {

    private Filer filer;
    private Messager messager;
    private Elements elementUtils;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
        elementUtils = processingEnv.getElementUtils();
    }

    /*
     * annotations: list of unique annotations that are getting processed
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {

            // find all the classes that uses the supported annotations
            Set<TypeElement> typeElements = ProcessingUtils.getTypeElementsToProcess(roundEnv.getRootElements(), annotations);

            // for each such class create a wrapper class for binding
            for (TypeElement typeElement : typeElements) {
                messager.printMessage(Diagnostic.Kind.NOTE, typeElement.getQualifiedName());

                String packageName = elementUtils.getPackageOf(typeElement).getQualifiedName().toString();
                String typeName = typeElement.getSimpleName().toString();
                ClassName className = ClassName.get(packageName, typeName);

                ClassName generatedClassName = ClassName
                        .get(packageName, NameStore.getGeneratedClassName(typeName));

                // define the wrapper class
                TypeSpec.Builder classBuilder = TypeSpec.classBuilder(generatedClassName)
                        .addField(className, NameStore.Variable.ANDROID_ACTIVITY,Modifier.PRIVATE,Modifier.STATIC)
                        .addModifiers(Modifier.PUBLIC);

                // add constructor
                classBuilder.addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(className, NameStore.Variable.ANDROID_ACTIVITY)
                        .addStatement("this.$N = $N", NameStore.Variable.ANDROID_ACTIVITY,NameStore.Variable.ANDROID_ACTIVITY)
                        .build());



                for (ExecutableElement executableElement : ElementFilter.methodsIn(typeElement.getEnclosedElements())) {
                    WithPermission withPermission = executableElement.getAnnotation(WithPermission.class);

                    if (withPermission != null) {
                        ClassName contextCompat = ClassName.get(NameStore.Package.CONTENT, "ContextCompat");
                        ClassName packageManager = ClassName.get(NameStore.Package.PACKAGE_MANAGER, "PackageManager");

                        // Bind permission method constructor
                        MethodSpec.Builder bindPermissions = MethodSpec.methodBuilder(executableElement.getSimpleName().toString())
                                .addModifiers(executableElement.getModifiers())
                                .addModifiers(Modifier.STATIC)
                                .returns(TypeName.get(executableElement.getReturnType()));

                        executableElement.getParameters().forEach(param->
                                bindPermissions.addParameter(ParameterSpec.get(param)));

                        executableElement.getAnnotationMirrors().forEach(annotationMirror->{
                                if(!annotationMirror.getAnnotationType().toString().equals(WithPermission.class.getName()))
                                bindPermissions.addAnnotation(AnnotationSpec.get(annotationMirror));
                        });


                        //*** START: Null activity check started
                        bindPermissions
                                .beginControlFlow("if($N == null)", NameStore.Variable.ANDROID_ACTIVITY)
                                .addStatement("new NullPointerException(\"Permissioner not initialized properly.\").printStackTrace()")
                                .nextControlFlow("else");

                        //*** START: Permissions status checks generated
                        for (String parameter : withPermission.value()) {
                            bindPermissions
                                    .beginControlFlow("if($T.checkSelfPermission($N.getContext(),$S) != $T.PERMISSION_GRANTED)",
                                            contextCompat,
                                            NameStore.Variable.ANDROID_ACTIVITY,
                                            parameter,
                                            packageManager)

                                    .addStatement("new SecurityException(\"Required Permission $N  not granted.\\n\\n" +
                                            "Annotated method($N) will NOT be fired as a consequent.\").printStackTrace()", parameter,executableElement.getSimpleName())
                            .nextControlFlow("else");

                        }
                        //*** END: Permissions status checks generated

                        AtomicReference<String> params = new AtomicReference<>("");
                        executableElement.getParameters().forEach(param-> {
                            params.set(param.getSimpleName() + ",");
                        });
                        params.set(params.get().substring(0,params.get().length()-1));


                        if(!TypeName.get(executableElement.getReturnType()).equals(TypeName.VOID))
                            bindPermissions.addStatement("return $N.$N($N)",
                                NameStore.Variable.ANDROID_ACTIVITY,
                                executableElement.getSimpleName(),
                                    params.get());
                        else
                            bindPermissions.addStatement("$N.$N($N)",
                                NameStore.Variable.ANDROID_ACTIVITY,
                                executableElement.getSimpleName(),
                                    params.get());


                        for (String parameter : withPermission.value())
                            bindPermissions.endControlFlow();

                        //*** END: Null activity closing
                        bindPermissions.endControlFlow();

                        if(!TypeName.get(executableElement.getReturnType()).equals(TypeName.VOID))
                            bindPermissions.addStatement("return ($T)null",TypeName.get(executableElement.getReturnType()).box());



                        classBuilder.addMethod(bindPermissions.build());
                    }

                }



                // write the defines class to a java file
                try {
                    JavaFile.builder(packageName,
                            classBuilder.build())
                            .build()
                            .writeTo(filer);
                } catch (IOException e) {
                    messager.printMessage(Diagnostic.Kind.ERROR, e.toString(), typeElement);
                }
            }
            messager.printMessage(Diagnostic.Kind.NOTE, "*****End class");
        }
        return true;
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return new TreeSet<>(Arrays.asList(
                WithPermission.class.getCanonicalName(),

                Keep.class.getCanonicalName()));
    }
}
