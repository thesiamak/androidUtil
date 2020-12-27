package ir.drax.annotations

import com.sun.xml.internal.fastinfoset.util.StringArray
import kotlin.reflect.KClass

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class WithPermission(vararg val value: String)