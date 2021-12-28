package com.example.viewpager

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class ViewPagerFragment : Fragment() {

    private lateinit var rootView: View

    private val handler: Handler by lazy { Handler(Looper.getMainLooper()) }

    private val tabLayout: TabLayout by lazy {
        findViewById<TabLayout>(R.id.tab).also {
            it.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    viewPager.currentItem = tab?.position ?: 0
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                    println("onTabUnselected")
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                    println("onTabReselected")
                }
            })
        }
    }
    private val viewPager: ViewPager by lazy {
        val lazyView: ViewPager = findViewById(R.id.view_pager)
        lazyView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                tabLayout.setScrollPosition(position, positionOffset, true)
            }

            override fun onPageSelected(position: Int) {
                tabLayout.selectTab(tabLayout.getTabAt(position))
            }

            override fun onPageScrollStateChanged(state: Int) {
                println("onPageScrollStateChanged $state")
            }

        })
        lazyView.adapter = object : PagerAdapter() {

            var transaction: FragmentTransaction? = null

            override fun getCount(): Int {
                return tabLayout.tabCount
            }

            override fun isViewFromObject(view: View, `object`: Any): Boolean {
                if (`object` is Fragment) {
                    return `object`.view == view
                }
                return `object` == view
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                val tag = createFragmentTag(container.id, position)

                val fragment: Fragment = childFragmentManager.findFragmentByTag(tag) ?: getFragment(position)
                transaction = childFragmentManager.beginTransaction()
                if (fragment.isAdded) {
                    transaction?.show(fragment)
                } else {
                    transaction?.add(container.id, fragment, tag)
                }
                return fragment
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                if (`object` is Fragment) {
                    transaction = childFragmentManager.beginTransaction().remove(`object`)
                }
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return tabLayout.getTabAt(position)?.text
            }

            override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
                super.setPrimaryItem(container, position, `object`)
            }

            override fun finishUpdate(container: ViewGroup) {
                super.finishUpdate(container)
                transaction?.commitAllowingStateLoss()
                transaction = null
                childFragmentManager.executePendingTransactions()
            }

            private fun createFragmentTag(container: Int, position: Int): String {
                return "fragment-tag-${container}-$position"
            }
        }
        lazyView
    }
    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById<SwipeRefreshLayout>(R.id.refresh_controller).also {
            it.setOnRefreshListener {
                it.isRefreshing = true
                Thread {
                    Thread.sleep(1000)
                    handler.post { it.isRefreshing = false }
                }.start()
            }
        }
    }

    private val mainFragment: MainFragment by lazy { MainFragment() }
    private val categoryFragment: CategoryFragment by lazy { CategoryFragment() }
    private val settingFragment: SettingFragment by lazy { SettingFragment() }

    private fun getFragment(position: Int): Fragment {
        return when(position) {
            0 -> mainFragment
            1 -> categoryFragment
            2 -> settingFragment
            else -> mainFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        rootView = view
        tabLayout
        viewPager
        refreshLayout
    }

    private fun <T: View> findViewById(resId: Int): T {
        return rootView.findViewById(resId)
    }
}