package com.example.androidmvp

import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.example.androidmvp.recycler.News
import com.example.androidmvp.recycler.NewsAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_dialog.view.*


class MainActivity : AppCompatActivity() {
    private val list: ArrayList<News> = getDataSource()
    private var adapter: NewsAdapter? = null
    private var adView: AdView? = null
    private var username: String? = null
    private var firebaseAuth: FirebaseAuth? = null
    private var firebaseUser: FirebaseUser? = null
    private var googleApiClient: GoogleApiClient? = null
    private var firebaseAnalytics: FirebaseAnalytics? = null
    private var firebaseRemoteConfig: FirebaseRemoteConfig? = null
    private var messageEditText: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Fabric.with(this, Crashlytics())

        firebaseAuth = FirebaseAuth.getInstance()
        firebaseUser = firebaseAuth?.getCurrentUser()
        username = "ANONYMOUS"

        if (firebaseUser == null) {
            startActivity( Intent(this, AuthActivity::class.java))
            finish()
        } else {
            username = firebaseUser?.getDisplayName();
            username = firebaseUser?.getEmail();
        }

        googleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, GoogleApiClient.OnConnectionFailedListener {  })
            .addApi(Auth.GOOGLE_SIGN_IN_API)
            .build()

        initAd()
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)

        adapter = NewsAdapter(getDataSource()) { news ->
            delete(news)
        }
        rv_news.adapter = adapter
        setRecyclerViewItemTouchListener()
        btn_show_dialog.setOnClickListener {
            firebaseAnalytics = FirebaseAnalytics.getInstance(this)
            initRemoteConfig()
            showDialog()
        }
        btn_signout.setOnClickListener{
            firebaseAuth?.signOut()
        }
    }

    private fun initRemoteConfig() {
        firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        val firebaseRemoteConfigSettings =
            FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(5)
                .setDeveloperModeEnabled(true)
                .build()
        val defaultConfigMap: MutableMap<String, Any> = HashMap()
        defaultConfigMap["friendly_msg_length"] = 10L
        firebaseRemoteConfig?.setConfigSettings(firebaseRemoteConfigSettings)
        firebaseRemoteConfig?.setDefaults(defaultConfigMap)
        fetchConfig()
    }

    private fun fetchConfig() {
        var cacheExpiration = 3600
        if (firebaseRemoteConfig != null) {
            if (firebaseRemoteConfig!!.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
                cacheExpiration = 0
            }
            firebaseRemoteConfig?.fetch(cacheExpiration.toLong())
                ?.addOnSuccessListener {
                    firebaseRemoteConfig?.activateFetched();
                    applyRetrievedLengthLimit();
                }
                ?.addOnFailureListener {
                    Log.w("Log", "Error fetching config");
                    applyRetrievedLengthLimit();
                };
        }
    }

    private fun applyRetrievedLengthLimit(){
        val friendly_msg_length = firebaseRemoteConfig?.getLong("friendly_msg_length")
        if (friendly_msg_length != null) {
            messageEditText?.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(friendly_msg_length.toInt())))
        }
        Log.d("Log", "FML is:$friendly_msg_length")
    }

    private fun initAd() {
        adView = findViewById(R.id.adView)
        val adRequest =
            AdRequest.Builder().build()
        adView?.loadAd(adRequest)
    }

    private fun setRecyclerViewItemTouchListener() {
        val itemTouchCallback = object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                viewHolder1: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, swipeDir: Int) {
                val index = viewHolder.adapterPosition
                list.removeAt(index)
                adapter?.updateList(list)
            }
        }
        val itemTouchHelper = ItemTouchHelper(itemTouchCallback)
        rv_news.addItemDecoration(itemTouchHelper)
        itemTouchHelper.attachToRecyclerView(rv_news)
    }

    private fun delete(news: News) {
        list.remove(news)
        adapter?.updateList(list)
    }

    private fun showDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.fragment_dialog, null)
        val builder = this.let {
            AlertDialog.Builder(it)
                .setView(dialogView)
        }
        val dialog = builder?.show()
        dialogView.btn_add_dialog.setOnClickListener {
            dialog?.dismiss()
            val name = dialogView.et_name_dialog.text.toString()
            val description = dialogView.et_description.text.toString()
            var index = dialogView.et_index_dialog.text.toString().toInt() - 1
            val listSize = list.size
            if (index > listSize) {
                index = listSize
            }
            list.add(index, News(name, description))
            adapter?.updateList(list)
        }
        dialogView.btn_cancel_dialog.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun getDataSource(): ArrayList<News> = arrayListOf(
        News("Tittle1", "bla-bla"),
        News("Tittle2", "bla-bla"),
        News("Tittle3", "bla-bla"),
        News("Tittle4", "bla-bla"),
        News("Tittle5", "bla-bla"),
        News("Tittle6", "bla-bla")
    )

    override fun onDestroy() {
        if (adView != null) {
            adView?.destroy()
        }
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater:MenuInflater = getMenuInflater()
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        Log.w("Crashlytics", "Crash button clicked");
        causeCrash();
        return true;
    }

    private fun causeCrash() {
        throw NullPointerException("Fake null pointer exception")
    }
}
