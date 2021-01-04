package ir.drax.annotations


@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION)
annotation class WithPermission(vararg val permission: String,
                                val grantPermission:Boolean = false,
                                val successMessageText:Int = 0,
                                val failureMessageText:Int = 0,
                                val messageText:Int = 0,
                                val icon:Int = 0)