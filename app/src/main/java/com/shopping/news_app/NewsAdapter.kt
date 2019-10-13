package com.shopping.news_app

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.annotation.Nullable

class NewsAdapter(@NonNull context: Context, @NonNull objects: List<News>) : ArrayAdapter<News> (context, 0, objects){

    @NonNull
    override fun getView(position: Int, @Nullable convertView: View?, @NonNull parent: ViewGroup): View {
        var convertView = convertView

        if (convertView == null) {
            convertView =
                LayoutInflater.from(context).inflate(R.layout.newselement, parent, false)
        }
        val currentNews = getItem(position)

        val title = convertView!!.findViewById(R.id.TitleId) as TextView
        title.text = currentNews!!.getTitle()

        val date = convertView.findViewById(R.id.tagId) as TextView
        date.text = currentNews.getTag()

        val year = convertView.findViewById(R.id.yearId) as TextView
        year.text = currentNews.getDate()

        return convertView
    }
}