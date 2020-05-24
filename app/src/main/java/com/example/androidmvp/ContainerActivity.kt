package com.example.androidmvp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.androidmvp.di.App
import com.google.android.gms.ads.MobileAds
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_container.*
import kotlinx.android.synthetic.main.fragment_1.*
import kotlinx.android.synthetic.main.fragment_2.*
import kotlinx.android.synthetic.main.fragment_3.*
import kotlinx.android.synthetic.main.fragment_5.*
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder
import javax.inject.Inject
import ru.terrakok.cicerone.android.support.SupportAppNavigator


class ContainerActivity : AppCompatActivity() {
    lateinit var navController: NavController
    private val navigator : Navigator = SupportAppNavigator(this, R.id.nav_host_fragment)
    val bundle = Bundle()

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.provideContainer()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container)

        navController = Navigation.findNavController(this, R.id.nav_graph)
        val mOnNavigationItemSelectedListener =
            BottomNavigationView.OnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.action_1 -> {
                        navController.navigate(R.id.fragment_1)
                        true
                    }
                    R.id.action_2 -> {
                        navController.navigate(R.id.fragment_2)
                        true
                    }
                    R.id.action_3 -> {
                        navController.navigate(R.id.fragment_3)
                        true
                    }
                    else -> false
                }
            }
        btv_main.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        btn_1.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.action_fragment_1_to_fragment_4)
        }
        btn_3.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.action_fragment_3_to_fragment_5)
        }
        btn_5.setOnClickListener{
            Navigation.findNavController(it).navigate(R.id.action_fragment5_to_authActivity)
        }
    }

    override fun onResume() {
        super.onResume()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }
}
