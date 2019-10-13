package com.shopping.news_app

import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.loader.app.LoaderManager
import androidx.loader.content.Loader
import androidx.preference.PreferenceManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import java.util.ArrayList

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<List<News>>,
    SharedPreferences.OnSharedPreferenceChangeListener, SwipeRefreshLayout.OnRefreshListener  {

    private val requestUrl = "https://content.guardianapis.com/search?&api-key=test"
    private val loaderId = 1
    private var mAdapter: NewsAdapter? = null
    private var mEmptyStateTextView: TextView? = null
    private var swipeRefreshLayout: SwipeRefreshLayout? = null
    private var mProgressBar: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cm = applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val active = cm.activeNetworkInfo
        // setting up the swipe refresh layout
        swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe)
        swipeRefreshLayout?.setOnRefreshListener(this)

        // Setting the networkstatus textview
        val isConnnected = active != null && active.isConnectedOrConnecting
        mEmptyStateTextView = findViewById<TextView>(R.id.empty_view)
        val newsList = findViewById<ListView>(R.id.list)
        newsList.emptyView = mEmptyStateTextView
        mProgressBar = findViewById<ProgressBar>(R.id.progressBar)
        if (isConnnected) {
            supportLoaderManager.initLoader(loaderId, null, this)
        } else {
            mProgressBar?.visibility = View.GONE
            mEmptyStateTextView?.text = getString(R.string.noInternet)
        }

        mAdapter = NewsAdapter(this, ArrayList())
        newsList.adapter = mAdapter
        newsList.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id ->
                val current = mAdapter?.getItem(position)
                val url = current?.getUrl()
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_settings) {
            val setting = Intent(this, SettingsActivity::class.java)
            startActivity(setting)
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        if (key == getString(R.string.setting_orberby_key) || key == getString(R.string.setting_category_key)) {
            mAdapter?.clear()

            mEmptyStateTextView?.setVisibility(View.GONE)

            val load = findViewById<ProgressBar>(R.id.progressBar)
            load?.setVisibility(View.VISIBLE)
            supportLoaderManager.restartLoader(loaderId, null, this)
        }
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<List<News>> {
        val shared = PreferenceManager.getDefaultSharedPreferences(this)
        val category = shared.getString(
            getString(R.string.setting_category_key),
            getString(R.string.setting_category_default)
        )
        val order = shared.getString(
            getString(R.string.setting_orberby_key),
            getString(R.string.setting_orderby_default)
        )

        val uri = Uri.parse(requestUrl)
        val builder = uri.buildUpon()
        builder.appendQueryParameter("q", category)
        builder.appendQueryParameter("order-by", order)
        Log.e("Value of link", builder.toString())
        return NewsLoader(this, builder.toString())
    }



    override fun onLoadFinished(loader: Loader<List<News>>, data: List<News>?) {
        mProgressBar?.visibility = View.GONE
        mEmptyStateTextView?.setText(R.string.emptyText)
        if (data != null && data.isNotEmpty()) {
            mAdapter?.addAll(data)
        }
    }

    override fun onLoaderReset(loader: Loader<List<News>>) {
        mAdapter?.clear()
    }

    override fun onRefresh() {
        mAdapter?.clear()
        supportLoaderManager?.restartLoader(loaderId, null, this)
        swipeRefreshLayout?.isRefreshing = false
    }
}
