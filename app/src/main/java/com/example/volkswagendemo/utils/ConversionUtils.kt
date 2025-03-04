package com.example.volkswagendemo.utils

import android.util.Log
import javax.inject.Inject

private val EXTENDED_ASCII_CHAR = charArrayOf(
    0x00C7.toChar(), 0x00FC.toChar(), 0x00E9.toChar(), 0x00E2.toChar(),
    0x00E4.toChar(), 0x00E0.toChar(), 0x00E5.toChar(), 0x00E7.toChar(),
    0x00EA.toChar(), 0x00EB.toChar(), 0x00E8.toChar(), 0x00EF.toChar(),
    0x00EE.toChar(), 0x00EC.toChar(), 0x00C4.toChar(), 0x00C5.toChar(),
    0x00C9.toChar(), 0x00E6.toChar(), 0x00C6.toChar(), 0x00F4.toChar(),
    0x00F6.toChar(), 0x00F2.toChar(), 0x00FB.toChar(), 0x00F9.toChar(),
    0x00FF.toChar(), 0x00D6.toChar(), 0x00DC.toChar(), 0x00A2.toChar(),
    0x00A3.toChar(), 0x00A5.toChar(), 0x20A7.toChar(), 0x0192.toChar(),
    0x00E1.toChar(), 0x00ED.toChar(), 0x00F3.toChar(), 0x00FA.toChar(),
    0x00F1.toChar(), 0x00D1.toChar(), 0x00AA.toChar(), 0x00BA.toChar(),
    0x00BF.toChar(), 0x2310.toChar(), 0x00AC.toChar(), 0x00BD.toChar(),
    0x00BC.toChar(), 0x00A1.toChar(), 0x00AB.toChar(), 0x00BB.toChar(),
    0x2591.toChar(), 0x2592.toChar(), 0x2593.toChar(), 0x2502.toChar(),
    0x2524.toChar(), 0x2561.toChar(), 0x2562.toChar(), 0x2556.toChar(),
    0x2555.toChar(), 0x2563.toChar(), 0x2551.toChar(), 0x2557.toChar(),
    0x255D.toChar(), 0x255C.toChar(), 0x255B.toChar(), 0x2510.toChar(),
    0x2514.toChar(), 0x2534.toChar(), 0x252C.toChar(), 0x251C.toChar(),
    0x2500.toChar(), 0x253C.toChar(), 0x255E.toChar(), 0x255F.toChar(),
    0x255A.toChar(), 0x2554.toChar(), 0x2569.toChar(), 0x2566.toChar(),
    0x2560.toChar(), 0x2550.toChar(), 0x256C.toChar(), 0x2567.toChar(),
    0x2568.toChar(), 0x2564.toChar(), 0x2565.toChar(), 0x2559.toChar(),
    0x2558.toChar(), 0x2552.toChar(), 0x2553.toChar(), 0x256B.toChar(),
    0x256A.toChar(), 0x2518.toChar(), 0x250C.toChar(), 0x2588.toChar(),
    0x2584.toChar(), 0x258C.toChar(), 0x2590.toChar(), 0x2580.toChar(),
    0x03B1.toChar(), 0x00DF.toChar(), 0x0393.toChar(), 0x03C0.toChar(),
    0x03A3.toChar(), 0x03C3.toChar(), 0x00B5.toChar(), 0x03C4.toChar(),
    0x03A6.toChar(), 0x0398.toChar(), 0x03A9.toChar(), 0x03B4.toChar(),
    0x221E.toChar(), 0x03C6.toChar(), 0x03B5.toChar(), 0x2229.toChar(),
    0x2261.toChar(), 0x00B1.toChar(), 0x2265.toChar(), 0x2264.toChar(),
    0x2320.toChar(), 0x2321.toChar(), 0x00F7.toChar(), 0x2248.toChar(),
    0x00B0.toChar(), 0x2219.toChar(), 0x00B7.toChar(), 0x221A.toChar(),
    0x207F.toChar(), 0x00B2.toChar(), 0x25A0.toChar(), 0x00A0.toChar()
)

class ConversionUtils @Inject constructor() {

    fun hexToAscii(tag: String): String? {
        return hex2ascii(tag)
    }

    private fun hex2ascii(tagID: String?): String? {
        if (tagID.isNullOrEmpty()) return tagID ?: ""

        val n = tagID.length
        if (n % 2 != 0) return tagID

        return runCatching {
            buildString {
                for (i in 0 until n step 2) {
                    val a = tagID[i]
                    val b = tagID[i + 1]
                    val c = ((hexToInt(a) shl 4) or hexToInt(b)).toChar()

                    when {
                        hexToInt(a) <= 7 && hexToInt(b) <= 0xf && c in 0x21.toChar()..0x7E.toChar() -> append(
                            c
                        )

                        hexToInt(a) >= 8 && hexToInt(b) <= 0xf && c in 0x80.toChar()..0xFF.toChar() ->
                            append(EXTENDED_ASCII_CHAR[c.code - 0x7F])

                        else -> append(' ')
                    }
                }
            }
        }.onFailure { e ->
            Log.e("HexToAscii", "⚠️ Error converting HEX to ASCII: ${e.message}", e)
        }.getOrNull()
    }

    private fun hexToInt(ch: Char): Int {
        return when (ch) {
            in 'a'..'f' -> ch - 'a' + 10
            in 'A'..'F' -> ch - 'A' + 10
            in '0'..'9' -> ch - '0'
            else -> throw IllegalArgumentException(ch.toString())
        }
    }

    fun AsciiToHex(tag: String): String {
        val trimmedData = tag.substring(1, tag.length - 1)
        val bytes = trimmedData.toByteArray()
        val builder = StringBuilder()
        for (c in bytes) {
            builder.append(c.toInt().toString(16))
        }
        return builder.toString()
    }

}