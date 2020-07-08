package com.mayurbhola.instant

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.instantapps.InstantApps
import com.mayurbhola.covid_19_helpline.ui.main.MainViewModel
import kotlinx.android.synthetic.main.instant_activity.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*

class InstantActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    private fun checkNetworkConnection(): Boolean {

        val connectivityManager: ConnectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

        val isConnected: Boolean = networkInfo?.isConnected ?: false

        if (isConnected) {
            txtNoInternet.visibility = View.GONE
        } else {
            txtNoInternet.visibility = View.VISIBLE
        }
        return isConnected
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.instant_activity)

        progressBar.visibility = View.VISIBLE

        val isInstantApp = InstantApps.getPackageManagerCompat(this).isInstantApp
        btnInstallFullApp.apply {
            // isEnabled = isInstantApp

            // Show the installation prompt only for an instant app.
            if (isInstantApp) {
                visibility = View.VISIBLE
                setOnClickListener {
                    InstantApps.showInstallPrompt(
                        this@InstantActivity,
                        postInstallIntent,
                        123,
                        "InstantMainActivity"
                    )
                }
            } else {
                visibility = View.GONE
                /*startActivity(Intent(this@PayActivity, PayActivity::class.java))
                finish()*/
            }
        }

        if (checkNetworkConnection()) {
            lifecycleScope.launch {

                viewModel.apiCallGetCaseSummary.observe(this@InstantActivity, Observer { result ->
                    val jsonObject = JSONObject(result)
                    val numberFormat: NumberFormat =
                        NumberFormat.getNumberInstance(Locale.getDefault())

                    if (jsonObject.has("cases")) {
                        txtTotalCasesValue.text =
                            numberFormat.format(jsonObject.getString("cases").toDouble())
                    }
                    if (jsonObject.has("recovered")) {
                        txtTotalCuredValue.text =
                            numberFormat.format(jsonObject.getString("recovered").toDouble())
                    }
                    if (jsonObject.has("deaths")) {
                        txtTotalDeathsValue.text =
                            numberFormat.format(jsonObject.getString("deaths").toDouble())
                    }

                    cardSummary.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                })

                viewModel.apiCallGetCaseSummaryFun()
            }
        }
    }

    private val postInstallIntent = Intent(
        Intent.ACTION_VIEW,
        Uri.parse("https://www.walletdoc.com")
    ).addCategory(Intent.CATEGORY_BROWSABLE).putExtras(Bundle().apply {
        putString("The key to", "sending data via intent")
    })
}