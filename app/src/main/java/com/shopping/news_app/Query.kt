package com.shopping.news_app

import android.text.TextUtils
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset
import java.util.ArrayList

class Query {
    companion object {
        private val LOG_TAG = Query::class.java.simpleName

        fun createUrl(stringUrl: String): URL? {
            var url: URL? = null
            try {
                url = URL(stringUrl)
            } catch (e: MalformedURLException) {
                Log.e(LOG_TAG, "Error with creating url", e)
            }

            return url
        }

        //Gathering up the infromations needed for the query
        fun fetchNewsData(requestUrl: String): ArrayList<News>? {
            val url = createUrl(requestUrl)
            var response: String? = null
            try {
                response = makeHttpRequest(url)
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Error with fetching data", e)
            }

            return extractFeatures(response)
        }

        //Requesting for a response
        @Throws(IOException::class)
        public fun makeHttpRequest(url: URL?): String {
            var response = ""
            if (url == null) {
                return response
            }
            var urlConnection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            try {
                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.connect()

                if (urlConnection.responseCode == 200) {
                    inputStream = urlConnection.inputStream
                    response = readFromStream(inputStream)
                } else {
                    Log.e(LOG_TAG, "Error response code: " + urlConnection.responseCode)
                }
            } catch (e: IOException) {
                Log.e(LOG_TAG, "Problem retrieving the News JSON results.", e)
            } finally {
                urlConnection?.disconnect()
                inputStream?.close()
            }
            return response
        }

        // Reading from the response
        @Throws(IOException::class)
        fun readFromStream(inputStream: InputStream?): String {
            val output = StringBuilder()
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream, Charset.forName("UTF-8"))
                val reader = BufferedReader(inputStreamReader)
                var line: String? = reader.readLine()
                while (line != null) {
                    output.append(line)
                    line = reader.readLine()
                }
            }
            return output.toString()
        }

        // Setting up news fields
        private var titleName: String = ""
        private var publishDate: String = ""
        private var tag: String = ""
        private var url: String = ""

        fun extractFeatures(json: String?): ArrayList<News>? {
            val news = ArrayList<News>()
            if (TextUtils.isEmpty(json)) {
                return null
            }
            try {
                // Try to parse the json responses
                val baseJson = JSONObject(json!!)
                val response = baseJson.getJSONObject("response")
                val items = response.getJSONArray("results")
                for (i in 0 until items.length()) {
                    val item = items.getJSONObject(i)

                    if (item.has("webTitle")) {
                        titleName = item.getString("webTitle")
                    } else {
                        titleName = "No title"
                    }
                    if (item.has("sectionName")) {
                        tag = item.getString("sectionName")
                    } else {
                        tag = " "
                    }
                    if (item.has("webPublicationDate")) {
                        publishDate = item.getString("webPublicationDate")
                    } else {
                        publishDate = ""
                    }
                    url = item.getString("webUrl")
                    news.add(News(titleName, tag, publishDate, url))
                }
            } catch (e: JSONException) {
                Log.e(LOG_TAG, "Problem parsing the Book JSON result", e)
            }

            return news
        }
    }
}