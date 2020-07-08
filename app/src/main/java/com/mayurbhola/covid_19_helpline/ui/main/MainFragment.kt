package com.mayurbhola.covid_19_helpline.ui.main

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isGone
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.mayurbhola.covid_19_helpline.R
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*

class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private val viewModel: MainViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar.isGone = false
        var counter = 0
        if (checkNetworkConnection()) {
            lifecycleScope.launch {

                viewModel.apiCallGetCaseSummary.observe(viewLifecycleOwner, Observer { result ->
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
                    cardSummary.isGone = false
                    counter++
                    if (counter >= 2) {
                        progressBar.isGone = true
                    }
                })

                viewModel.apiCallGetCaseSummaryFun()
            }

            lifecycleScope.launch {

                viewModel.apiCallHelpline.observe(viewLifecycleOwner, Observer { result ->
                    val jsonObject = JSONObject(result)

                    if (jsonObject.has("success") && jsonObject.getBoolean("success")) {
                        val jsonObjectContact: JSONObject =
                            jsonObject.getJSONObject("data").getJSONObject("contacts")
                                .getJSONObject("primary")
                        if (jsonObjectContact.has("number")) {
                            txtNumber.text = jsonObjectContact.getString("number")
                        } else {
                            txtNumber.isGone = true
                        }
                        if (jsonObjectContact.has("number-tollfree")) {
                            txtTollFree.text = jsonObjectContact.getString("number-tollfree")
                        } else {
                            txtTollFree.isGone = true
                        }
                        if (jsonObjectContact.has("email")) {
                            txtEmail.text = jsonObjectContact.getString("email")
                        } else {
                            txtEmail.isGone = true
                        }

                        if (jsonObjectContact.has("twitter")) {
                            txtTwitter.text = jsonObjectContact.getString("twitter")
                        } else {
                            txtTwitter.isGone = true
                        }

                        if (jsonObjectContact.has("facebook")) {
                            txtFacebook.text = jsonObjectContact.getString("facebook")
                        } else {
                            txtFacebook.isGone = true
                        }
                        cardContact.isGone = false
                        counter++
                        if (counter >= 2) {
                            progressBar.isGone = true
                        }
                    }
                })
                viewModel.apiCallHelplineFun()
            }
        } else {
            progressBar.isGone = true
        }
    }

    private fun checkNetworkConnection(): Boolean {

        val connectivityManager: ConnectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkInfo: NetworkInfo? = connectivityManager.activeNetworkInfo

        val isConnected: Boolean = networkInfo?.isConnected ?: false

        txtNoInternet.isGone = isConnected

        return isConnected
    }
}