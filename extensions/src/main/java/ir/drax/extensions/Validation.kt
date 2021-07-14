package ir.drax.extensions

import com.google.gson.Gson


fun Any.toJson():String{
    return Gson().toJson(this)
}
fun String.toValidDate(): String? {
    return when{
        split("/").size!=3 ->null
        else -> {
            split("/").joinToString("/") {
                if (it.length == 1)
                    "0$it"
                else it
            }
        }
    }
}

fun String.toPersianNumbers(): String {
    var a = this
    val pNum = arrayOf("۰", "۱", "۲", "۳", "۴", "۵", "۶", "۷", "۸", "۹")
    a = a.replace("0", pNum[0])
    a = a.replace("1", pNum[1])
    a = a.replace("2", pNum[2])
    a = a.replace("3", pNum[3])
    a = a.replace("4", pNum[4])
    a = a.replace("5", pNum[5])
    a = a.replace("6", pNum[6])
    a = a.replace("7", pNum[7])
    a = a.replace("8", pNum[8])
    a = a.replace("9", pNum[9])
    return a
}

fun String.toEnglishNumbers(): String? {
    var a = this
    val pNum = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
    a = a.replace("۰", pNum[0])
    a = a.replace("۱", pNum[1])
    a = a.replace("۲", pNum[2])
    a = a.replace("۳", pNum[3])
    a = a.replace("۴", pNum[4])
    a = a.replace("۵", pNum[5])
    a = a.replace("۶", pNum[6])
    a = a.replace("۷", pNum[7])
    a = a.replace("۸", pNum[8])
    a = a.replace("۹", pNum[9])
    return a
}


fun String.floatToInt(): Int {
    return this.toFloat().toInt()
}

fun formatTimeString(hour: Int, minute: Int): String {
    if (minute < 10)
        return "$hour:0$minute"
    return "$hour:$minute"
}

/**
 * Standard password validation
 * At least 1 uppercase+ 1 lowercase+ numbers+ symbols+ min 4 chars
 *
 * @return
 */
fun String.isValidPassword(): Boolean {
    return when {
        length < 8 -> false
        !Regex("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{4,}$")
            .containsMatchIn(this) -> false

        else -> true
    }
}



/**
 * Number validation system
 * First latin and then persian numbers validation.
 */

private fun String.isLatinNumber(minLength:Int=Int.MIN_VALUE ,maxLength:Int= Int.MAX_VALUE) =
        Regex("^[0-9]{$minLength,$maxLength}$").containsMatchIn(this)

private fun String.isPersianNumber(minLength:Int ,maxLength:Int) =
        Regex("^[۰-۹]{$minLength,$maxLength}$").containsMatchIn(this)


/**
 * isValidNumber
 * Main number validation method.
 */
fun String.isValidNumber(minLength:Int=Int.MIN_VALUE ,maxLength:Int= Int.MAX_VALUE): Boolean {
    return isLatinNumber(minLength,maxLength) || isPersianNumber(minLength,maxLength)
}

fun Int.isValidNumber(min:Int= Int.MIN_VALUE, max:Long= Long.MAX_VALUE) = this in min..max



fun String.isValidNumber(maxLength: Int? = null): Boolean {
    return !(maxLength != null && this.length > maxLength)
}


/**
 * Phone number validation
 *
 * @return
 */
fun String.isValidMobileNumber(): Boolean {
    if(this.length != 11)
        return false

    val englishPattern = Regex("^0?9[0|1|2|3|4|9][0-9]{8}$")
    val farsiPattern = Regex("^۰?۹[۰|۱|۲|۳|۴|۹][۰-۹]{8}$")
    return englishPattern.containsMatchIn(this) ||
            farsiPattern.containsMatchIn(this)
}