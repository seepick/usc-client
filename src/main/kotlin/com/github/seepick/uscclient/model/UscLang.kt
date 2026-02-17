package com.github.seepick.uscclient.model

public enum class UscLang(val urlCode: String) {
    English("en"),
    Dutch("nl");
    //    German("de"),
//    French("fr"),
    // PT, ES
    companion object {
        // would otherwise require date parse to use different locale...
        val singleSupported = UscLang.English
    }
}
