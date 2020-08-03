package com.example.chap.application.view.activity

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.net.Uri
import android.os.Bundle
import android.os.IBinder
import android.os.SystemClock
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.NavController
import androidx.viewpager.widget.ViewPager
import com.example.chap.R
import com.example.chap.application.internal.*
import com.example.chap.application.view.fragment.DialogAboutUsFragment
import com.example.chap.application.view.fragment.DialogContactUsFragment
import com.example.chap.application.view.fragment.MainFragment
import com.example.chap.application.viewModel.MainActivityViewModel
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,
    ViewPager.OnPageChangeListener {

    val TAG = "MainActivity"

    private val backStack = Stack<Int>()

    private var mLastClickTime: Long = 0

    lateinit var viewModel: MainActivityViewModel

    lateinit var navController: NavController
    lateinit var navController2: NavController

    var currentFragment = 0

    private val fragments = listOf(
        BaseFragment.newInstance(
            R.layout.content_main_base,
            R.id.nav_host_main
        ),
        BaseFragment.newInstance(
            R.layout.content_comments_base,
            R.id.nav_host_comments
        )
    )

    val indexToPage =
        mapOf(0 to R.id.main, 1 to R.id.comments)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        viewModel =
            ViewModelProviders.of(this, ViewModelsFactory(SharedPref(this)))
                .get(MainActivityViewModel::class.java)

        load()
    }

    fun load() {
        nav_view.setNavigationItemSelectedListener(this)
        iv_content.setOnClickListener {
            drawer_layout.openDrawer(Gravity.RIGHT)
        }

        main_pager.addOnPageChangeListener(this)
        main_pager.adapter = ViewPagerAdapter(supportFragmentManager, fragments)
        main_pager.post(this::checkDeepLink)
        main_pager.offscreenPageLimit = fragments.size

        bottom_nav.setOnNavigationItemSelectedListener { item ->
            val position = indexToPage.values.indexOf(item.itemId)
            if (main_pager.currentItem != position)
                setItem(position)
            currentFragment = position
            return@setOnNavigationItemSelectedListener true
        }

        bottom_nav.setOnNavigationItemReselectedListener { item ->
            // mis-clicking prevention, using threshold of 500 ms
            if (SystemClock.elapsedRealtime() - mLastClickTime > 500) {
                if (item.itemId == R.id.main) {
                    if (!SharedPref(this).hasToken()) {
                        Log.i(
                            TAG,
                            "user didnt loginWithEmail (there was no token found) , redirecting to loginWithEmail page..."
                        )
                        startActivity(Intent(this, AuthActivity::class.java))
                        return@setOnNavigationItemReselectedListener
                    }
                }
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            val position = indexToPage.values.indexOf(item.itemId)
            val fragment = fragments[position]
            fragment.popToRoot()
        }

        if (backStack.empty()) backStack.push(0)

        btn_back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setItem(position: Int) {
        main_pager.currentItem = position
        backStack.push(position)
    }

    private fun checkDeepLink() {
        fragments.forEachIndexed { index, fragment ->
            val hasDeepLink = fragment.handleDeepLink(intent)
            if (hasDeepLink == true) setItem(index)
        }
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            R.id.nav_edit_info -> {

                if (currentFragment == 0)
                    navController.navigate(R.id.action_mainFragment_to_editFragment)
                else
                    navController2.navigate(R.id.action_commentsFragment_to_editFragment)

                bottom_nav.visibility = View.GONE
                iv_content.visibility = View.GONE
                btn_back.visibility = View.VISIBLE
                (layout_basket.layoutParams as Toolbar.LayoutParams).gravity = Gravity.RIGHT
            }
            R.id.nav_addresses -> {
                if (currentFragment == 0)
                    navController.navigate(R.id.action_mainFragment_to_addressListFragment)
                else
                    navController2.navigate(R.id.action_commentsFragment_to_addressListFragment)

                bottom_nav.visibility = View.GONE
                iv_content.visibility = View.GONE
                btn_back.visibility = View.VISIBLE
                tv_address_title.visibility = View.VISIBLE
                (layout_basket.layoutParams as Toolbar.LayoutParams).gravity = Gravity.RIGHT
            }
            R.id.nav_contact_us -> {
                DialogContactUsFragment().show(supportFragmentManager, "ContactUs")
            }
            R.id.nav_about_us -> {
                DialogAboutUsFragment().show(supportFragmentManager, "AboutUs")
            }
            R.id.nav_exit -> {

                SharedPref(this).saveToken("")
                startActivity(Intent(this, AuthActivity::class.java))
                finish()
                viewModel.logout(object : OnError {
                    override fun onError(errMsg: String?) {
                        Log.i(TAG, "logout error:$errMsg")
                    }
                })

            }
        }
        menuItem.isCheckable = false
        drawer_layout.closeDrawer(Gravity.RIGHT)
        return true
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(Gravity.RIGHT))
            drawer_layout.closeDrawer(Gravity.RIGHT)
        else {
            val fragment = fragments[main_pager.currentItem]
            val hadNestedFragments = fragment.onBackPressed()
            // if no fragments were popped
            if (!hadNestedFragments) {
                if (backStack.size > 1) {
                    // remove current position from stack
                    backStack.pop()
                    // set the next item in stack as current
                    main_pager.currentItem = backStack.peek()

                } else super.onBackPressed()
            }
        }
    }

    override fun onPageScrollStateChanged(state: Int) {}

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(page: Int) {
        val itemId = indexToPage[page] ?: R.id.home
        if (bottom_nav.selectedItemId != itemId) bottom_nav.selectedItemId = itemId
    }

}