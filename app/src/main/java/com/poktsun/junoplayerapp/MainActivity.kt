package com.poktsun.junoplayerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import timber.log.Timber


class MainActivity : AppCompatActivity() {
    private val manager: FragmentManager = supportFragmentManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)
        val viewPager = findViewById<ViewPager>(R.id.view_pager)
//        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
//            override fun onTabReselected(p0: TabLayout.Tab?) {
//                Timber.d("-26, onTabReselected:%s", 1)
//            }
//
//            override fun onTabUnselected(p0: TabLayout.Tab?) {
//                Timber.d("-30, onTabUnselected:%s", 2)
//            }
//
//            override fun onTabSelected(p0: TabLayout.Tab?) {
//                when (p0?.text) {
//                    "VOD" -> replaceFragment(VodPlayerFragment())
//                    "LIVE" -> replaceFragment(LivePlayerFragment())
//                    "AUDIO" -> replaceFragment(AudioPlayerFragment())
//                    else -> replaceFragment(VodPlayerFragment())
//                }
//                Timber.d("-34, onTabSelected:%s", 3)
//            }
//        })
        tabLayout.addTab(tabLayout.newTab().setText("VOD"))
        tabLayout.addTab(tabLayout.newTab().setText("LIVE"))
        tabLayout.addTab(tabLayout.newTab().setText("AUDIO"))
        tabLayout.setupWithViewPager(viewPager)
        viewPager.adapter = MyAdapter(supportFragmentManager)

        //tabLayout.setScrollPosition(0, 0f, true)
//        tabLayout.getTabAt(0)?.select()
//        castContext = CastContext.getSharedInstance(this)
//        Timber.d("-48, onCreate:%s", castContext?.isAppVisible)

        Timber.d("-50, onCreate:%s", 111)

    }

    fun replaceFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = manager.beginTransaction()
        transaction.replace(R.id.container, fragment)
        transaction.commit()
    }

    class MyAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> VodPlayerFragment.netInstance("AAAA")
                1 -> VodPlayerFragment.netInstance("BBBB")
                else -> VodPlayerFragment.netInstance("CCCC")
            }
        }

        override fun getCount(): Int {
            return 3
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return when (position) {
                0 -> "AAAA"
                1 -> "BBBB"
                else -> "CCCC"
            }
        }
    }

}
