package com.shift4.data.model

import android.content.res.Resources
import android.graphics.drawable.Drawable
import com.shift4.R
import com.shift4.data.model.lookup.Card
import com.shift4.utils.empty
import com.shift4.utils.sanitized

internal class CreditCard(
    val type: Type,
    val number: String?,
    private val last2: String?,
    private val last4: String?
) {
    companion object {
        var empty: CreditCard = CreditCard(Type.UNKNOWN, null, null, null)
    }

    enum class Type(val rawValue: String) {
        VISA(rawValue = "visa"),
        MAESTRO(rawValue = "maestro"),
        MASTERCARD(rawValue = "mastercard"),
        AMERICAN_EXPRESS(rawValue = "americanExpress"),
        DISCOVER(rawValue = "discover"),
        DINERS(rawValue = "diners"),
        JCB(rawValue = "jcb"),
        UNKNOWN(rawValue = "unknown");

        companion object {
            operator fun invoke(rawValue: String?): Type =
                values().firstOrNull { it.rawValue == rawValue } ?: UNKNOWN
        }
    }

    constructor(number: String?) : this(
        type = Type.cardTypeForCreditCardNumber(cardNumber = number),
        number = number?.sanitized,
        last2 = null,
        last4 = null
    )

    constructor(card: Card?) : this(
        type = Type(card?.brand),
        number = null,
        last2 = card?.last2,
        last4 = card?.last4
    )

    val numberLength: Int
        get() {
            return when (type) {
                Type.AMERICAN_EXPRESS -> 15
                Type.DINERS -> 14
                else -> 16
            }
        }

    val cvcLength: Int
        get() =
            if (type == Type.AMERICAN_EXPRESS) {
                4
            } else {
                3
            }

    val cvcPlaceholder: String
        get() =
            if (type == Type.AMERICAN_EXPRESS) {
                "0000"
            } else {
                "000"
            }

    val correct: Boolean get() = numberLength == (number?.length ?: 0)

    val expPlaceholder = "••/••"

    val readable: String
        get() {
            var result = String.empty
            var currentCharacterIndex = 0
            var patternIndex = 0
            val endIndex = if (numberLength > cardNumber.length) {
                cardNumber.length
            } else {
                numberLength
            }
            val charArray = cardNumber.toCharArray(endIndex = endIndex)
            charArray.forEach {
                result += it
                currentCharacterIndex += 1
                if (separationPattern[patternIndex] == currentCharacterIndex) {
                    patternIndex += 1
                    currentCharacterIndex = 0
                    if (patternIndex < separationPattern.size) {
                        result += " "
                    }
                }
            }

            return if (result.lastOrNull() == ' ') {
                result.dropLast(1)
            } else {
                result
            }
        }

    fun image(resources: Resources): Drawable {
        return when (type) {
            Type.VISA -> resources.getDrawable(R.drawable.com_shift4_ic_logo_visa, null)
            Type.MASTERCARD -> resources.getDrawable(R.drawable.com_shift4_ic_logo_mastercard, null)
            Type.AMERICAN_EXPRESS -> resources.getDrawable(R.drawable.com_shift4_ic_logo_americanexpress, null)
            Type.DISCOVER -> resources.getDrawable(R.drawable.com_shift4_ic_logo_discover, null)
            Type.UNKNOWN -> resources.getDrawable(R.drawable.com_shift4_ic_unknown_card, null)
            Type.DINERS -> resources.getDrawable(R.drawable.com_shift4_ic_logo_diners, null)
            Type.JCB -> resources.getDrawable(R.drawable.com_shift4_ic_logo_jcb, null)
            Type.MAESTRO -> resources.getDrawable(R.drawable.com_shift4_ic_logo_maestro, null)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CreditCard) return false

        if (type != other.type) return false
        if (number != other.number) return false
        if (last2 != other.last2) return false
        if (last4 != other.last4) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + (number?.hashCode() ?: 0)
        result = 31 * result + (last2?.hashCode() ?: 0)
        result = 31 * result + (last4?.hashCode() ?: 0)
        return result
    }

    private val cardNumber: String
        get() {
            return this.number ?: when {
                last2 != null -> {
                    "•".repeat(numberLength - 2) + last2
                }
                last4 != null -> {
                    "•".repeat(numberLength - 4) + last4
                }
                else -> {
                    "•".repeat(numberLength)
                }
            }
        }

    private val separationPattern: List<Int>
        get() {
            return when (type) {
                Type.AMERICAN_EXPRESS -> listOf(4, 6, 5)
                Type.DINERS -> listOf(4, 6, 4)
                else -> listOf(4, 4, 4, 4)
            }
        }
}

internal data class ValidationRegex(
    val type: CreditCard.Type,
    val prefix: IntRange,
    val patterns: List<IntRange>
) {
    companion object {
        val unknown: ValidationRegex = ValidationRegex(CreditCard.Type.UNKNOWN, 0..0, listOf())
    }
}

internal fun CreditCard.Type.validationRegex(): ValidationRegex {
    return when (this) {
        CreditCard.Type.VISA -> ValidationRegex(CreditCard.Type.VISA, 1..1, listOf(4..4))
        CreditCard.Type.MASTERCARD -> ValidationRegex(
            CreditCard.Type.MASTERCARD,
            2..4,
            listOf(51..55, 2221..2720)
        )
        CreditCard.Type.AMERICAN_EXPRESS -> ValidationRegex(
            CreditCard.Type.AMERICAN_EXPRESS,
            2..2,
            listOf(34..34, 37..37)
        )
        CreditCard.Type.DISCOVER -> ValidationRegex(
            CreditCard.Type.DISCOVER,
            2..2,
            listOf(60..60, 64..65)
        )
        CreditCard.Type.UNKNOWN -> ValidationRegex.unknown
        CreditCard.Type.DINERS -> ValidationRegex(
            CreditCard.Type.DINERS,
            2..3,
            listOf(300..305, 309..309, 36..36, 38..39)
        )
        CreditCard.Type.JCB -> ValidationRegex(
            CreditCard.Type.JCB,
            4..4,
            listOf(2131..2131, 1800..1800, 3528..3689)
        )
        CreditCard.Type.MAESTRO -> ValidationRegex(CreditCard.Type.MAESTRO, 2..2, listOf(67..67))
    }
}

internal fun CreditCard.Type.Companion.cardTypeForCreditCardNumber(cardNumber: String?): CreditCard.Type {
    CreditCard.Type.values().forEach { type ->
        type.validationRegex().prefix.forEach {
            val prefix: Int? = cardNumber?.sanitized?.take(it)?.toIntOrNull()
            prefix ?: return CreditCard.Type.UNKNOWN
            for (pattern in type.validationRegex().patterns) {
                if (pattern.contains(prefix)) {
                    return type
                }
            }
        }
    }
    return CreditCard.Type.UNKNOWN
}