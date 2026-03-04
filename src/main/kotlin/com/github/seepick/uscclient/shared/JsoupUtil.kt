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
        return (html.childNodes().single { it.nodeName() == "head" } as Element) to (html.childNodes()
            .single { it.nodeName() == "body" } as Element)
    }

    private fun extractHtml(htmlString: String): Node {
        val doc = Jsoup.parse(htmlString)
        doc.outputSettings().prettyPrint(false) // to preserve whitespace (line breaks) in text nodes
        return doc.childNodes().single { it.nodeName() == "html" }
    }
}
