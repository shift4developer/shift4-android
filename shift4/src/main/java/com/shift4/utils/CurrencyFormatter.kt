package com.shift4.utils

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.util.*

internal object CurrencyFormatter {
    fun format(
        amount: BigDecimal,
        currency: String,
        divideMinorUnits: Boolean
    ): String {
        val formatter: DecimalFormat =
            NumberFormat.getCurrencyInstance(Locale.US) as DecimalFormat
        val currencyModel = CurrencyModel(currency)

        val symbol = DecimalFormatSymbols(Locale.US)
        symbol.currencySymbol = currencyModel?.symbol
        formatter.decimalFormatSymbols = symbol
        formatter.minimumFractionDigits = currencyModel?.minorUnits ?: 0
        formatter.maximumFractionDigits = currencyModel?.minorUnits ?: 0

        val formatted = if (divideMinorUnits) {
            val dividedAmount = amount.divide(currencyModel?.minorUnitsFactor ?: BigDecimal.ONE)
            formatter.format(dividedAmount)
        } else {
            formatter.format(amount)
        }

        return formatted
    }
}

internal enum class CurrencyModel(val rawValue: String) {
    EUR(rawValue = "EUR"),
    USD(rawValue = "USD"),
    AFN(rawValue = "AFN"),
    ALL(rawValue = "ALL"),
    DZD(rawValue = "DZD"),
    AOA(rawValue = "AOA"),
    ARS(rawValue = "ARS"),
    AMD(rawValue = "AMD"),
    AWG(rawValue = "AWG"),
    AZN(rawValue = "AZN"),
    BSD(rawValue = "BSD"),
    BHD(rawValue = "BHD"),
    BDT(rawValue = "BDT"),
    BBD(rawValue = "BBD"),
    BYR(rawValue = "BYR"),
    BZD(rawValue = "BZD"),
    BMD(rawValue = "BMD"),
    BTN(rawValue = "BTN"),
    BOB(rawValue = "BOB"),
    BOV(rawValue = "BOV"),
    BAM(rawValue = "BAM"),
    BWP(rawValue = "BWP"),
    BRL(rawValue = "BRL"),
    BND(rawValue = "BND"),
    BGN(rawValue = "BGN"),
    BIF(rawValue = "BIF"),
    KHR(rawValue = "KHR"),
    CAD(rawValue = "CAD"),
    CVE(rawValue = "CVE"),
    KYD(rawValue = "KYD"),
    CLF(rawValue = "CLF"),
    CLP(rawValue = "CLP"),
    CNY(rawValue = "CNY"),
    COP(rawValue = "COP"),
    COU(rawValue = "COU"),
    KMF(rawValue = "KMF"),
    CDF(rawValue = "CDF"),
    CRC(rawValue = "CRC"),
    HRK(rawValue = "HRK"),
    CUC(rawValue = "CUC"),
    CUP(rawValue = "CUP"),
    CZK(rawValue = "CZK"),
    DJF(rawValue = "DJF"),
    DOP(rawValue = "DOP"),
    EGP(rawValue = "EGP"),
    SVC(rawValue = "SVC"),
    ERN(rawValue = "ERN"),
    ETB(rawValue = "ETB"),
    FKP(rawValue = "FKP"),
    FJD(rawValue = "FJD"),
    XAF(rawValue = "XAF"),
    GMD(rawValue = "GMD"),
    GEL(rawValue = "GEL"),
    GHS(rawValue = "GHS"),
    GIP(rawValue = "GIP"),
    DKK(rawValue = "DKK"),
    GTQ(rawValue = "GTQ"),
    GNF(rawValue = "GNF"),
    GYD(rawValue = "GYD"),
    HTG(rawValue = "HTG"),
    HNL(rawValue = "HNL"),
    HKD(rawValue = "HKD"),
    HUF(rawValue = "HUF"),
    ISK(rawValue = "ISK"),
    INR(rawValue = "INR"),
    IDR(rawValue = "IDR"),
    IRR(rawValue = "IRR"),
    IQD(rawValue = "IQD"),
    ILS(rawValue = "ILS"),
    JMD(rawValue = "JMD"),
    JPY(rawValue = "JPY"),
    JOD(rawValue = "JOD"),
    KZT(rawValue = "KZT"),
    KES(rawValue = "KES"),
    KPW(rawValue = "KPW"),
    KRW(rawValue = "KRW"),
    KWD(rawValue = "KWD"),
    KGS(rawValue = "KGS"),
    LAK(rawValue = "LAK"),
    LVL(rawValue = "LVL"),
    LBP(rawValue = "LBP"),
    LSL(rawValue = "LSL"),
    LRD(rawValue = "LRD"),
    LYD(rawValue = "LYD"),
    LTL(rawValue = "LTL"),
    MOP(rawValue = "MOP"),
    MKD(rawValue = "MKD"),
    MGA(rawValue = "MGA"),
    MWK(rawValue = "MWK"),
    MYR(rawValue = "MYR"),
    MVR(rawValue = "MVR"),
    MRO(rawValue = "MRO"),
    MUR(rawValue = "MUR"),
    MXN(rawValue = "MXN"),
    MXV(rawValue = "MXV"),
    MDL(rawValue = "MDL"),
    MNT(rawValue = "MNT"),
    MZN(rawValue = "MZN"),
    MMK(rawValue = "MMK"),
    NAD(rawValue = "NAD"),
    NPR(rawValue = "NPR"),
    NIO(rawValue = "NIO"),
    NGN(rawValue = "NGN"),
    OMR(rawValue = "OMR"),
    PKR(rawValue = "PKR"),
    PAB(rawValue = "PAB"),
    PGK(rawValue = "PGK"),
    PYG(rawValue = "PYG"),
    PEN(rawValue = "PEN"),
    PHP(rawValue = "PHP"),
    PLN(rawValue = "PLN"),
    QAR(rawValue = "QAR"),
    RON(rawValue = "RON"),
    RUB(rawValue = "RUB"),
    RWF(rawValue = "RWF"),
    SHP(rawValue = "SHP"),
    XCD(rawValue = "XCD"),
    WST(rawValue = "WST"),
    STD(rawValue = "STD"),
    SAR(rawValue = "SAR"),
    RSD(rawValue = "RSD"),
    SCR(rawValue = "SCR"),
    SLL(rawValue = "SLL"),
    SGD(rawValue = "SGD"),
    ANG(rawValue = "ANG"),
    SBD(rawValue = "SBD"),
    SOS(rawValue = "SOS"),
    ZAR(rawValue = "ZAR"),
    SSP(rawValue = "SSP"),
    LKR(rawValue = "LKR"),
    SDG(rawValue = "SDG"),
    SRD(rawValue = "SRD"),
    NOK(rawValue = "NOK"),
    SZL(rawValue = "SZL"),
    SEK(rawValue = "SEK"),
    CHE(rawValue = "CHE"),
    CHF(rawValue = "CHF"),
    CHW(rawValue = "CHW"),
    SYP(rawValue = "SYP"),
    TWD(rawValue = "TWD"),
    TJS(rawValue = "TJS"),
    TZS(rawValue = "TZS"),
    THB(rawValue = "THB"),
    XOF(rawValue = "XOF"),
    NZD(rawValue = "NZD"),
    TOP(rawValue = "TOP"),
    TTD(rawValue = "TTD"),
    TND(rawValue = "TND"),
    TRY(rawValue = "TRY"),
    TMT(rawValue = "TMT"),
    AUD(rawValue = "AUD"),
    UGX(rawValue = "UGX"),
    UAH(rawValue = "UAH"),
    AED(rawValue = "AED"),
    GBP(rawValue = "GBP"),
    USN(rawValue = "USN"),
    USS(rawValue = "USS"),
    UYI(rawValue = "UYI"),
    UYU(rawValue = "UYU"),
    UZS(rawValue = "UZS"),
    VUV(rawValue = "VUV"),
    VEF(rawValue = "VEF"),
    VND(rawValue = "VND"),
    XPF(rawValue = "XPF"),
    MAD(rawValue = "MAD"),
    YER(rawValue = "YER"),
    ZMK(rawValue = "ZMK"),
    ZWL(rawValue = "ZWL");

    companion object {
        operator fun invoke(rawValue: String): CurrencyModel? =
            values().firstOrNull { it.rawValue == rawValue }
    }

    val readable: String
        get() {
            return when (this) {
                EUR -> "Euro"
                USD -> "US Dollar"
                AFN -> "Afghani"
                ALL -> "Lek"
                DZD -> "Algerian Dinar"
                AOA -> "Kwanza"
                ARS -> "Argentine Peso"
                AMD -> "Armenian Dram"
                AWG -> "Aruban Florin"
                AZN -> "Azerbaijanian Manat"
                BSD -> "Bahamian Dollar"
                BHD -> "Bahraini Dinar"
                BDT -> "Taka"
                BBD -> "Barbados Dollar"
                BYR -> "Belarussian Ruble"
                BZD -> "Belize Dollar"
                BMD -> "Bermudian Dollar"
                BTN -> "Ngultrum"
                BOB -> "Boliviano"
                BOV -> "Mvdol"
                BAM -> "Convertible Mark"
                BWP -> "Pula"
                BRL -> "Brazilian Real"
                BND -> "Brunei Dollar"
                BGN -> "Bulgarian Lev"
                BIF -> "Burundi Franc"
                KHR -> "Riel"
                CAD -> "Canadian Dollar"
                CVE -> "Cape Verde Escudo"
                KYD -> "Cayman Islands Dollar"
                CLF -> "Unidades de fomento"
                CLP -> "Chilean Peso"
                CNY -> "Yuan Renminbi"
                COP -> "Colombian Peso"
                COU -> "Unidad de Valor Real"
                KMF -> "Comoro Franc"
                CDF -> "Congolese Franc"
                CRC -> "Costa Rican Colon"
                HRK -> "Croatian Kuna"
                CUC -> "Peso Convertible"
                CUP -> "Cuban Peso"
                CZK -> "Czech Koruna"
                DJF -> "Djibouti Franc"
                DOP -> "Dominican Peso"
                EGP -> "Egyptian Pound"
                SVC -> "El Salvador Colon"
                ERN -> "Nakfa"
                ETB -> "Ethiopian Birr"
                FKP -> "Falkland Islands Pound"
                FJD -> "Fiji Dollar"
                XAF -> "CFA Franc BEAC"
                GMD -> "Dalasi"
                GEL -> "Lari"
                GHS -> "Ghana Cedi"
                GIP -> "Gibraltar Pound"
                DKK -> "Danish Krone"
                GTQ -> "Quetzal"
                GNF -> "Guinea Franc"
                GYD -> "Guyana Dollar"
                HTG -> "Gourde"
                HNL -> "Lempira"
                HKD -> "Hong Kong Dollar"
                HUF -> "Forint"
                ISK -> "Iceland Krona"
                INR -> "Indian Rupee"
                IDR -> "Rupiah"
                IRR -> "Iranian Rial"
                IQD -> "Iraqi Dinar"
                ILS -> "New Israeli Sheqel"
                JMD -> "Jamaican Dollar"
                JPY -> "Yen"
                JOD -> "Jordanian Dinar"
                KZT -> "Tenge"
                KES -> "Kenyan Shilling"
                KPW -> "North Korean Won"
                KRW -> "Won"
                KWD -> "Kuwaiti Dinar"
                KGS -> "Som"
                LAK -> "Kip"
                LVL -> "Latvian Lats"
                LBP -> "Lebanese Pound"
                LSL -> "Loti"
                LRD -> "Liberian Dollar"
                LYD -> "Libyan Dinar"
                LTL -> "Lithuanian Litas"
                MOP -> "Pataca"
                MKD -> "Denar"
                MGA -> "Malagasy Ariary"
                MWK -> "Kwacha"
                MYR -> "Malaysian Ringgit"
                MVR -> "Rufiyaa"
                MRO -> "Ouguiya"
                MUR -> "Mauritius Rupee"
                MXN -> "Mexican Peso"
                MXV -> "Mexican Unidad de Inversion (UDI)"
                MDL -> "Moldovan Leu"
                MNT -> "Tugrik"
                MZN -> "Mozambique Metical"
                MMK -> "Kyat"
                NAD -> "Namibia Dollar"
                NPR -> "Nepalese Rupee"
                NIO -> "Cordoba Oro"
                NGN -> "Naira"
                OMR -> "Rial Omani"
                PKR -> "Pakistan Rupee"
                PAB -> "Balboa"
                PGK -> "Kina"
                PYG -> "Guarani"
                PEN -> "Nuevo Sol"
                PHP -> "Philippine Peso"
                PLN -> "Złoty"
                QAR -> "Qatari Rial"
                RON -> "New Romanian Leu"
                RUB -> "Russian Ruble"
                RWF -> "Rwanda Franc"
                SHP -> "Saint Helena Pound"
                XCD -> "East Caribbean Dollar"
                WST -> "Tala"
                STD -> "Dobra"
                SAR -> "Saudi Riyal"
                RSD -> "Serbian Dinar"
                SCR -> "Seychelles Rupee"
                SLL -> "Leone"
                SGD -> "Singapore Dollar"
                ANG -> "Netherlands Antillean Guilder"
                SBD -> "Solomon Islands Dollar"
                SOS -> "Somali Shilling"
                ZAR -> "Rand"
                SSP -> "South Sudanese Pound"
                LKR -> "Sri Lanka Rupee"
                SDG -> "Sudanese Pound"
                SRD -> "Surinam Dollar"
                NOK -> "Norwegian Krone"
                SZL -> "Lilangeni"
                SEK -> "Swedish Krona"
                CHE -> "WIR Euro"
                CHF -> "Swiss Franc"
                CHW -> "WIR Franc"
                SYP -> "Syrian Pound"
                TWD -> "New Taiwan Dollar"
                TJS -> "Somoni"
                TZS -> "Tanzanian Shilling"
                THB -> "Baht"
                XOF -> "CFA Franc BCEAO"
                NZD -> "New Zealand Dollar"
                TOP -> "Pa’anga"
                TTD -> "Trinidad and Tobago Dollar"
                TND -> "Tunisian Dinar"
                TRY -> "Turkish Lira"
                TMT -> "Turkmenistan New Manat"
                AUD -> "Australian Dollar"
                UGX -> "Uganda Shilling"
                UAH -> "Hryvnia"
                AED -> "UAE Dirham"
                GBP -> "Pound Sterling"
                USN -> "US Dollar (Next day)"
                USS -> "US Dollar (Same day)"
                UYI -> "Uruguay Peso en Unidades Indexadas (URUIURUI)"
                UYU -> "Peso Uruguayo"
                UZS -> "Uzbekistan Sum"
                VUV -> "Vatu"
                VEF -> "Bolivar Fuerte"
                VND -> "Dong"
                XPF -> "CFP Franc"
                MAD -> "Moroccan Dirham"
                YER -> "Yemeni Rial"
                ZMK -> "Zambian Kwacha"
                ZWL -> "Zimbabwe Dollar"
            }
        }
    val minorUnits: Int
        get() {
            return when (this) {
                EUR -> 2
                USD -> 2
                AFN -> 2
                ALL -> 2
                DZD -> 2
                AOA -> 2
                ARS -> 2
                AMD -> 2
                AWG -> 2
                AZN -> 2
                BSD -> 2
                BHD -> 3
                BDT -> 2
                BBD -> 2
                BYR -> 0
                BZD -> 2
                BMD -> 2
                BTN -> 2
                BOB -> 2
                BOV -> 2
                BAM -> 2
                BWP -> 2
                BRL -> 2
                BND -> 2
                BGN -> 2
                BIF -> 0
                KHR -> 2
                CAD -> 2
                CVE -> 2
                KYD -> 2
                CLF -> 0
                CLP -> 0
                CNY -> 2
                COP -> 2
                COU -> 2
                KMF -> 0
                CDF -> 2
                CRC -> 2
                HRK -> 2
                CUC -> 2
                CUP -> 2
                CZK -> 2
                DJF -> 0
                DOP -> 2
                EGP -> 2
                SVC -> 2
                ERN -> 2
                ETB -> 2
                FKP -> 2
                FJD -> 2
                XAF -> 0
                GMD -> 2
                GEL -> 2
                GHS -> 2
                GIP -> 2
                DKK -> 2
                GTQ -> 2
                GNF -> 0
                GYD -> 2
                HTG -> 2
                HNL -> 2
                HKD -> 2
                HUF -> 2
                ISK -> 0
                INR -> 2
                IDR -> 2
                IRR -> 2
                IQD -> 3
                ILS -> 2
                JMD -> 2
                JPY -> 0
                JOD -> 3
                KZT -> 2
                KES -> 2
                KPW -> 2
                KRW -> 0
                KWD -> 3
                KGS -> 2
                LAK -> 2
                LVL -> 2
                LBP -> 2
                LSL -> 2
                LRD -> 2
                LYD -> 3
                LTL -> 2
                MOP -> 2
                MKD -> 2
                MGA -> 2
                MWK -> 2
                MYR -> 2
                MVR -> 2
                MRO -> 2
                MUR -> 2
                MXN -> 2
                MXV -> 2
                MDL -> 2
                MNT -> 2
                MZN -> 2
                MMK -> 2
                NAD -> 2
                NPR -> 2
                NIO -> 2
                NGN -> 2
                OMR -> 3
                PKR -> 2
                PAB -> 2
                PGK -> 2
                PYG -> 0
                PEN -> 2
                PHP -> 2
                PLN -> 2
                QAR -> 2
                RON -> 2
                RUB -> 2
                RWF -> 0
                SHP -> 2
                XCD -> 2
                WST -> 2
                STD -> 2
                SAR -> 2
                RSD -> 2
                SCR -> 2
                SLL -> 2
                SGD -> 2
                ANG -> 2
                SBD -> 2
                SOS -> 2
                ZAR -> 2
                SSP -> 2
                LKR -> 2
                SDG -> 2
                SRD -> 2
                NOK -> 2
                SZL -> 2
                SEK -> 2
                CHE -> 2
                CHF -> 2
                CHW -> 2
                SYP -> 2
                TWD -> 2
                TJS -> 2
                TZS -> 2
                THB -> 2
                XOF -> 0
                NZD -> 2
                TOP -> 2
                TTD -> 2
                TND -> 3
                TRY -> 2
                TMT -> 2
                AUD -> 2
                UGX -> 2
                UAH -> 2
                AED -> 2
                GBP -> 2
                USN -> 2
                USS -> 2
                UYI -> 0
                UYU -> 2
                UZS -> 2
                VUV -> 0
                VEF -> 2
                VND -> 0
                XPF -> 0
                MAD -> 2
                YER -> 2
                ZMK -> 2
                ZWL -> 2
            }
        }
    val minorUnitsFactor: BigDecimal
        get() {
            return when (minorUnits) {
                0 -> BigDecimal.ONE
                2 -> BigDecimal(100)
                3 -> BigDecimal(1000)
                else -> BigDecimal.ONE
            }
        }
    val symbol: String
        get() {
            return when (this) {
                EUR -> "€"
                USD -> "$"
                AFN -> "؋"
                ALL -> "L"
                DZD -> "د.‏"
                AOA -> "Kz"
                ARS -> "$"
                AMD -> "դր."
                AWG -> "ƒ"
                AZN -> "₼"
                BSD -> "$"
                BHD -> "د.ب.‏"
                BDT -> "৳"
                BBD -> "$"
                BYR -> "р."
                BZD -> "BZ$"
                BMD -> "$"
                BTN -> "Nu."
                BOB -> "Bs."
                BOV -> "BOV"
                BAM -> "КМ"
                BWP -> "P"
                BRL -> "R$"
                BND -> "$"
                BGN -> "лв."
                BIF -> "FBu"
                KHR -> "៛"
                CAD -> "$"
                CVE -> "$"
                KYD -> "$"
                CLF -> "UF"
                CLP -> "$"
                CNY -> "¥"
                COP -> "$"
                KMF -> "CF"
                CDF -> "FC"
                CRC -> "₡"
                HRK -> "kn"
                CUP -> "\$MN"
                CZK -> "Kč"
                DJF -> "Fdj"
                DOP -> "RD\$"
                EGP -> "ج.م.‏"
                SVC -> "₡"
                ERN -> "Nfk"
                ETB -> "Br"
                FKP -> "£"
                FJD -> "\$"
                XAF -> "F"
                GMD -> "D"
                GEL -> "Lari"
                GHS -> "₵"
                GIP -> "£"
                DKK -> "kr."
                GTQ -> "Q"
                GNF -> "FG"
                GYD -> "\$"
                HTG -> "G"
                HNL -> "L."
                HKD -> "HK\$"
                HUF -> "Ft"
                ISK -> "kr."
                INR -> "₹"
                IDR -> "Rp"
                IRR -> "﷼"
                IQD -> "د.ع.‏"
                ILS -> "₪"
                JMD -> "J\$"
                JPY -> "¥"
                JOD -> "د.ا."
                KZT -> "₸"
                KES -> "S"
                KPW -> "₩"
                KRW -> "₩"
                KWD -> "د.ك.‏"
                KGS -> "сом"
                LAK -> "₭"
                LVL -> "LVL"
                LBP -> "ل.ل.‏"
                LSL -> "M"
                LRD -> "\$"
                LYD -> "د.ل.‏"
                LTL -> "LTL"
                MOP -> "MOP\$"
                MKD -> "ден."
                MGA -> "Ar"
                MWK -> "MK"
                MYR -> "RM"
                MVR -> "MV"
                MRO -> "UM"
                MUR -> "₨"
                MXN -> "\$"
                MDL -> "lei"
                MNT -> "₮"
                MZN -> "MT"
                MMK -> "K"
                NAD -> "\$"
                NPR -> "₨"
                NIO -> "C\$"
                NGN -> "₦"
                OMR -> "﷼"
                PKR -> "₨"
                PAB -> "B/."
                PGK -> "K"
                PYG -> "₲"
                PEN -> "S/."
                PHP -> "₱"
                PLN -> "zł"
                QAR -> "﷼"
                RON -> "lei"
                RUB -> "₽"
                RWF -> "RWF"
                SHP -> "£"
                XCD -> "\$"
                WST -> "WS\$"
                STD -> "Db"
                SAR -> "﷼"
                RSD -> "Дин."
                SCR -> "₨"
                SLL -> "Le"
                SGD -> "\$"
                ANG -> "ƒ"
                SBD -> "\$"
                SOS -> "S"
                ZAR -> "R"
                LKR -> "₨"
                SDG -> "£‏"
                SRD -> "\$"
                NOK -> "kr"
                SZL -> "E"
                SEK -> "kr"
                CHF -> "CHF"
                SYP -> "£"
                TWD -> "NT\$"
                TJS -> "TJS"
                TZS -> "TSh"
                THB -> "฿"
                XOF -> "F"
                NZD -> "\$"
                TOP -> "T\$"
                TTD -> "TT\$"
                TND -> "د.ت.‏"
                TRY -> "TL"
                TMT -> "m"
                AUD -> "\$"
                UGX -> "USh"
                UAH -> "₴"
                AED -> "د.إ.‏"
                GBP -> "£"
                UYU -> "\$U"
                UZS -> "сўм"
                VUV -> "VT"
                VEF -> "Bs. F."
                VND -> "₫"
                XPF -> "F"
                MAD -> "د.م.‏"
                YER -> "﷼"
                else -> ""
            }
        }
}