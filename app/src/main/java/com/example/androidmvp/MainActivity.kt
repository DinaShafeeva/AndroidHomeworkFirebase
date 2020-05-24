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
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.crashlytics.android.Crashlytics
import com.example.androidmvp.di.App
import com.example.androidmvp.presenters.MainPresenter
import com.example.androidmvp.recycler.News
import com.example.androidmvp.recycler.NewsAdapter
import com.example.androidmvp.views.MainView
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
import moxy.MvpAppCompatActivity
import moxy.ktx.moxyPresenter
import javax.inject.Inject
import javax.inject.Provider


class MainActivity : MvpAppCompatActivity(), MainView {
    lateinit var dialog: AlertDialog

    @Inject
    lateinit var presenterProvider: Provider<MainPresenter>

    private val presenter: MainPresenter by moxyPresenter {
        presenterProvider.get()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        App.appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initListeners()
        initRecycler()
        initAdd()
        presenter.authUser()
    }

    private fun initListeners() {
        btn_signout.setOnClickListener { presenter.signOut() }
        btn_show_dialog.setOnClickListener{
            presenter.openDialog()
        }
        btn_to_graph.setOnClickListener{presenter.goToGraph()        }
    }

    override fun onDestroy() {
        presenter.destroyAd()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater:MenuInflater = getMenuInflater()
        inflater.inflate(R.menu.toolbar, menu);
        return true;
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean {
        presenter.causeCrash();
        return true;
    }

    override fun openDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.fragment_dialog, null)
        val builder = this.let {
            AlertDialog.Builder(it)
                .setView(dialogView)
        }
        dialog = builder.show()
        dialogView.btn_add_dialog.setOnClickListener {
            presenter.closeDialog()
            val name = dialogView.et_name_dialog.text.toString()
            val description = dialogView.et_description.text.toString()
            val index = dialogView.et_index_dialog.text.toString().toInt() - 1

            presenter.changesFromDialog(name,description,index)
        }
        dialogView.btn_cancel_dialog.setOnClickListener {
           presenter.closeDialog()
        }
    }

    override fun closeDialog(){
        dialog.dismiss()
    }

    override fun initRecycler() {
        presenter.initRecycler(rv_news)
    }

    override fun goToAuth() {
        startActivity(Intent(this, AuthActivity::class.java))
        finish()
    }

    override fun goToGraph() {
        startActivity(Intent(this, ContainerActivity::class.java))
        finish()
    }

    override fun initAdd() {
        presenter.initAd(findViewById(R.id.adView))
    }
}
