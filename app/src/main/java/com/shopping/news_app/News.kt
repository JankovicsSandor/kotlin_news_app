package com.shopping.news_app

class News (private var title: String, private var tag: String, private var date: String,
            private var url: String) {

    //Setting up gettters
    fun getTitle(): String {
        return title
    }

    fun getTag(): String {
        return tag
    }

    fun getDate(): String {
        return date
    }

    fun getUrl(): String {
        return url
    }
}