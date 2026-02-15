package com.github.seepick.uscclient

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node

// TODO group by using object Jsoup as a namespace
fun jsoupBody(htmlString: String): Element {
    val html = jsoupHtml(htmlString)
    return html.childNodes().single { it.nodeName() == "body" } as Element
}

fun jsoupHeadAndBody(htmlString: String): Pair<Element, Element> {
    val html = jsoupHtml(htmlString)
    return (html.childNodes().single { it.nodeName() == "head" } as Element) to
            (html.childNodes().single { it.nodeName() == "body" } as Element)
}

private fun jsoupHtml(htmlString: String): Node =
    Jsoup.parse(htmlString).childNodes().single { it.nodeName() == "html" }
