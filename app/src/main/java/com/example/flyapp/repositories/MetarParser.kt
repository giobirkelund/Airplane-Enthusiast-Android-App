package com.example.flyapp.repositories

import android.util.Xml
import com.example.flyapp.models.AirportMetar
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream

private val ns: String? = null

class MetarParser {
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): MutableList<AirportMetar> {
        inputStream.use {
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(it, null)
            parser.nextTag()
            return readFeed(parser)
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun readFeed(parser: XmlPullParser): MutableList<AirportMetar> {
        val metarList = mutableListOf<AirportMetar>()

        parser.require(XmlPullParser.START_TAG, ns, "metno:aviationProducts")
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "metno:meteorologicalAerodromeReport") {
                metarList.add(readEntry(parser))
            } else {
                skip(parser)
            }
        }
        return metarList
    }

    private fun readEntry(parser: XmlPullParser): AirportMetar {
        parser.require(XmlPullParser.START_TAG, ns, "metno:meteorologicalAerodromeReport")
        val time: String? = null // We decided not to handle this
        var metarText: String? = null
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            when (parser.name) {
                "metno:metarText" -> metarText = readAttribute(parser, parser.name)
                else -> skip(parser)
            }
        }
        return AirportMetar(time, metarText)
    }


    @Throws(IOException::class, XmlPullParserException::class)
    private fun readAttribute(parser: XmlPullParser, tag: String): String {
        parser.require(XmlPullParser.START_TAG, ns, tag)
        val id = readText(parser)
        parser.require(XmlPullParser.END_TAG, ns, tag)
        return id.trim()
    }

    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }
}