package com.shopping.news_app

import android.content.Context
import androidx.loader.content.AsyncTaskLoader

class NewsLoader(context: Context, private var url: String) : AsyncTaskLoader<List<News>>(context) {

    override fun onStartLoading() {
        forceLoad()
    }

    override fun loadInBackground(): List<News>? {
        return Query.fetchNewsData(url)
    }
}