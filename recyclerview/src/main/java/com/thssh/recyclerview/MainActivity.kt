package com.thssh.recyclerview

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainHolder(val view: TextView) : FixedHolder(view)

class MainAdapter(private val context: Context, private val data: List<String>): LoadMoreRecyclerView.Adapter<String, MainHolder>(context, data) {
    override fun onFixedCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        return MainHolder(TextView(context))
    }

    override fun onFixedBindViewHolder(holder: MainHolder, position: Int) {
        holder.view.text = data[position]
    }

}

class MainActivity : AppCompatActivity() {

    private val onRefresh = {
        println("refresh")
        delay(1000) {
            listView.loadCompleted()
        }
    }

    private val onLoadMore = {
        println("load more")
        delay(1500) {
            listView.loadCompleted()
        }
    }

    private val listView: LoadMoreRecyclerView by lazy {
        val view:LoadMoreRecyclerView = findViewById(R.id.list_view)
        view.layoutManager = LinearLayoutManager(this)
        view.adapter = MainAdapter(this, (0..100).map { it1 -> "item-$it1" })
        view.setOnRefreshListener(onRefresh)
        view.setOnLoadMoreListener(onLoadMore)
        view
    }

    private val handler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        listView.let {
        }
    }

    private fun delay(timeout: Long, runnable: Runnable) {
        Thread {
            Thread.sleep(timeout)
            handler.post(runnable)
        }.start()
    }
}
