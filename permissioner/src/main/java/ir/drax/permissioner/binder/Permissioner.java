package ir.drax.permissioner.binder;

import android.app.Activity;
import android.content.Context;


import androidx.fragment.app.Fragment;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import ir.drax.annotations.internal.BindingSuffix;

public class Permissioner {

    private Permissioner() {
        // not to be instantiated in public
    }

    private static void instantiateBinder(Object target) {
        Class<?> targetClass = target.getClass();
        String className = targetClass.getName();
        try {
            Class<?> bindingClass = targetClass
                    .getClassLoader()
                    .loadClass(className + BindingSuffix.GENERATED_CLASS_SUFFIX);
            Constructor<?> classConstructor = bindingClass.getConstructor(targetClass);
            try {
                classConstructor.newInstance(target);
            } catch (IllegalAccessException e) {
                new RuntimeException("Unable to invoke " + classConstructor, e).printStackTrace();
            } catch (InstantiationException e) {
                new RuntimeException("Unable to invoke " + classConstructor, e).printStackTrace();
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof RuntimeException) {
                    cause.printStackTrace();
                }
                if (cause instanceof Error) {
                    cause.printStackTrace();
                }
                new RuntimeException("Unable to create instance.", cause).printStackTrace();
            }
        } catch (ClassNotFoundException e) {
            new RuntimeException("Unable to find Class for " + className + BindingSuffix.GENERATED_CLASS_SUFFIX, e).printStackTrace();
        } catch (NoSuchMethodException e) {
            new RuntimeException("Unable to find constructor for " + className + BindingSuffix.GENERATED_CLASS_SUFFIX, e).printStackTrace();
        }
    }

    public static <T extends Activity> void bind(T activity) {
        instantiateBinder(activity);
    }

    public static <T extends Fragment> void bind(T fragment) {
        instantiateBinder(fragment);
    }
}
