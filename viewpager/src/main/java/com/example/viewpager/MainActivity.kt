package com.example.viewpager

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private val tabLayout: TabLayout by lazy {
        val view: TabLayout = findViewById(R.id.tab)
        view.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                println("onTabSelected")
                viewPager.currentItem = tab?.position ?: 0
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                println("onTabUnselected")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                println("onTabReselected")
            }

        })
        view
    }

    private val viewPager: ViewPager by lazy {
        val lazyView: ViewPager = findViewById(R.id.view_pager)
        lazyView.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                println("onPageScrolled: $position $positionOffset $positionOffsetPixels")
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

                val fragment: Fragment = supportFragmentManager.findFragmentByTag(tag) ?: getFragment(position)
                transaction = supportFragmentManager.beginTransaction()
                if (fragment.isAdded) {
                    transaction?.show(fragment)
                } else {
                    transaction?.add(container.id, fragment, tag)
                }
                return fragment
            }

            override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
                if (`object` is Fragment) {
                    transaction = supportFragmentManager.beginTransaction().remove(`object`)
                }
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return tabLayout.getTabAt(position)?.text
            }

            private fun createFragmentTag(container: Int, position: Int): String {
                return "fragment-tag-${container}-$position"
            }

            override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
                super.setPrimaryItem(container, position, `object`)
            }

            override fun finishUpdate(container: ViewGroup) {
                super.finishUpdate(container)
                transaction?.commitAllowingStateLoss()
                transaction = null
                supportFragmentManager.executePendingTransactions()
            }
        }
        lazyView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewPager.currentItem = 0
    }
}