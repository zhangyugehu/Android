package com.thssh.recyclerview

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainHolder(val view: TextView) : RecyclerView.ViewHolder(view)

class MainAdapter(private val context: Context, private val data: List<String>): RecyclerView.Adapter<MainHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
        val textView = TextView(context)
        textView.height = 100
        textView.setTextColor(Color.BLACK)
        textView.gravity = Gravity.CENTER_VERTICAL
        return MainHolder(textView)
    }

    override fun onBindViewHolder(holder: MainHolder, position: Int) {
        holder.view.text = data[position]
    }

    override fun getItemCount(): Int {
        return data.size
    }

}
//class MainAdapter(private val context: Context, private val data: List<String>): LoadMoreRecyclerView.Adapter<String, MainHolder>(context, data) {
//    override fun onFixedCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder {
//        return MainHolder(TextView(context))
//    }
//
//    override fun onFixedBindViewHolder(holder: MainHolder, position: Int) {
//        holder.view.text = data[position]
//    }
//
//}

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
        view.adapter = AdapterWrapper(MainAdapter(this, (0..20).map { it1 -> "item-$it1" }))
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
