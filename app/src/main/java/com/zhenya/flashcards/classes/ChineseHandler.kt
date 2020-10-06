package com.zhenya.flashcards.classes

import java.util.regex.Matcher
import java.util.regex.Pattern

class ChineseHandler {
    private fun getTonePosition(r: String): Int {
        val lowerCase = r.toLowerCase()

        // exception to the rule
        if (lowerCase == "ou") return 0

        // higher precedence, both never go together
        var preferencePosition = lowerCase.indexOf('a')
        if (preferencePosition >= 0) return preferencePosition
        preferencePosition = lowerCase.indexOf('e')
        return if (preferencePosition >= 0) preferencePosition else lowerCase.length - 1

        // otherwise the last one takes the tone mark
    }

    fun getCharacter(string: String?, position: Int): String? {
        val characters = string!!.toCharArray()
        return characters[position].toString()
    }

    fun toPinyin(asciiPinyin: String): String? {
        val pinyinToneMarks: MutableMap<String, String> = HashMap()
        pinyinToneMarks["a"] = "āáǎà"
        pinyinToneMarks["e"] = "ēéěè"
        pinyinToneMarks["i"] = "īíǐì"
        pinyinToneMarks["o"] = "ōóǒò"
        pinyinToneMarks["u"] = "ūúǔù"
        pinyinToneMarks["ü"] = "ǖǘǚǜ"
        pinyinToneMarks["A"] = "ĀÁǍÀ"
        pinyinToneMarks["E"] = "ĒÉĚÈ"
        pinyinToneMarks["I"] = "ĪÍǏÌ"
        pinyinToneMarks["O"] = "ŌÓǑÒ"
        pinyinToneMarks["U"] = "ŪÚǓÙ"
        pinyinToneMarks["Ü"] = "ǕǗǙǛ"
        val pattern = Pattern.compile("([aeiouüvÜ]{1,3})(n?g?r?)([012345])")
        val matcher: Matcher = pattern.matcher(asciiPinyin)
        val s = StringBuilder()
        var start = 0
        while (matcher.find(start)) {
            s.append(asciiPinyin, start, matcher.start(1))
            val tone: Int = matcher.group(3).toInt() % 5
            val r: String = matcher.group(1).replace("v", "ü").replace("V", "Ü")
            if (tone != 0) {
                val pos = getTonePosition(r)
                s.append(r, 0, pos)
                    .append(getCharacter(pinyinToneMarks[getCharacter(r, pos)], tone - 1))
                    .append(r, pos + 1, r.length)
            } else {
                s.append(r)
            }
            s.append(matcher.group(2))
            start = matcher.end(3)
        }
        if (start != asciiPinyin.length) {
            s.append(asciiPinyin, start, asciiPinyin.length)
        }
        return s.toString()
    }
}