package com.shift4.utils

internal class ExpirationDateFormatter {
    data class Result(
        val text: String?,
        val placeholder: String?,
        val resignFocus: Boolean
    )

    fun format(inputText: String, backspace: Boolean): Result {
        var updatedExpiration: String
        var placeholder: String?
        var resignFocus = false

        placeholder = "MM/YY"
        updatedExpiration = inputText.replace("/", "")

        if (updatedExpiration.length == 1) {
            if ((updatedExpiration.toIntOrNull() ?: 0) > 1) {
                updatedExpiration = "0${updatedExpiration}/"
            }
        } else if (updatedExpiration.length == 2) {
            if ((updatedExpiration.toIntOrNull() ?: 0) == 0) {
                updatedExpiration = "0"
            } else if ((updatedExpiration.toIntOrNull() ?: 0) > 12) {
                updatedExpiration = "1"
            } else if (!backspace) {
                updatedExpiration = StringBuilder(updatedExpiration).insert(2, '/').toString()
            }
        } else if (updatedExpiration.length > 2) {
            updatedExpiration = StringBuilder(updatedExpiration).insert(2, '/').toString()

            val components: List<String> = updatedExpiration.split("/")
            val lastString: String? = components.lastOrNull()
            val year: Int? = lastString?.toIntOrNull()

            if (lastString != null && year != null && components.size == 2) {
                if (year <= 1) {
                    updatedExpiration = updatedExpiration.dropLast(1)
                } else if (year == 20) {
                    placeholder = "MM/YYYY"
                } else if (year in 101..999 && (year < 202 || year > 210)) {
                    updatedExpiration = updatedExpiration.dropLast(1)
                    placeholder = "MM/YY"
                } else if (year in 1001..2020) {
                    updatedExpiration = updatedExpiration.dropLast(1)
                    placeholder = "MM/YYYY"
                }
                if (year in 21..99 && updatedExpiration.length >= 5) {
                    resignFocus = true
                }
                if (year == 20 || (year in 202..209)) {
                    placeholder = "MM/YYYY"
                }
            }
        }

        if (updatedExpiration.length >= 7) {
            resignFocus = true
            placeholder = "MM/YYYY"
            updatedExpiration = updatedExpiration.substring(0, 7)
        }

        return Result(updatedExpiration, placeholder, resignFocus)
    }
}