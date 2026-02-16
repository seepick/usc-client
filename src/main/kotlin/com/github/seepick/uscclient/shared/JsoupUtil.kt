package com.github.seepick.uscclient.shared

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

internal object JsoupUtil {

    fun extractBody(htmlString: String): Element {
        val html = extractHtml(htmlString)
        return html.childNodes().single { it.nodeName() == "body" } as Element
    }

    fun extractHeadAndBody(htmlString: String): Pair<Element, Element> {
        val html = extractHtml(htmlString)
        return (html.childNodes().single { it.nodeName() == "head" } as Element) to
                (html.childNodes().single { it.nodeName() == "body" } as Element)
    }

    private fun extractHtml(htmlString: String): Node =
        Jsoup.parse(htmlString).childNodes().single { it.nodeName() == "html" }
}
