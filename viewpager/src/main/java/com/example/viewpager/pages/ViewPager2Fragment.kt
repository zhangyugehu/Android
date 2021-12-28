package com.example.viewpager

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class ViewPager2Fragment : Fragment() {

    private lateinit var rootView: View

    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

    private val tabLayout: TabLayout by lazy { findViewById(R.id.tab) }
    private val viewPager2: ViewPager2 by lazy {
        findViewById<ViewPager2>(R.id.view_pager_2).also {
            it.adapter = object : FragmentStateAdapter(this) {
                override fun getItemCount(): Int {
                    return 3
                }

                override fun createFragment(position: Int): Fragment {
                    return generateFragment(position)
                }
            }
        }
    }
    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById<SwipeRefreshLayout>(R.id.refresh_controller).also {
            it.setOnRefreshListener {
                // mock refresh
                it.isRefreshing = true
                Thread {
                    Thread.sleep(1000)
                    handler.post { it.isRefreshing = false }
                }.start()
            }
        }
    }

    private fun generateFragment(position: Int): Fragment {
        return when(position) {
            0 -> MainFragment()
            1 -> CategoryFragment()
            2 -> SettingFragment()
            else -> MainFragment()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_pager2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rootView = view
        TabLayoutMediator(tabLayout, viewPager2, true) { tab, position ->
            tab.text = when(position) {
                0 -> "Main"
                1 -> "Category"
                2 -> "Setting"
                else -> "Default"
            }
        }.attach()
        refreshLayout
    }

    private fun <T: View> findViewById(resId: Int): T {
        return rootView.findViewById(resId)
    }
}