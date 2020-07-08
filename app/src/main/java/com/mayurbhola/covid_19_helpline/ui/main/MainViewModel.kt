package com.mayurbhola.covid_19_helpline.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainViewModel : ViewModel() {

    private val _apiCallGetCaseSummary = MutableLiveData<String>()
    private val _apiCallHelpline = MutableLiveData<String>()

    val apiCallGetCaseSummary: LiveData<String> = _apiCallGetCaseSummary
    val apiCallHelpline: LiveData<String> = _apiCallHelpline

    suspend fun apiCallGetCaseSummaryFun() {
        _apiCallGetCaseSummary.value = httpGet("https://coronavirus-19-api.herokuapp.com/all")
    }

    suspend fun apiCallHelplineFun() {
        _apiCallHelpline.value = httpGet("https://api.rootnet.in/covid19-in/contacts")
    }

    private fun convertInputStreamToString(inputStream: InputStream): String {
        val bufferedReader: BufferedReader? = BufferedReader(InputStreamReader(inputStream))

        var line: String? = bufferedReader?.readLine()
        var result = ""

        while (line != null) {
            result += line
            line = bufferedReader?.readLine()
        }

        inputStream.close()
        return result
    }

    private suspend fun httpGet(myURL: String): String? {

        return withContext(Dispatchers.IO) {
            val inputStream: InputStream

            val httpURLConnection: HttpURLConnection =
                URL(myURL).openConnection() as HttpURLConnection

            httpURLConnection.connect()

            inputStream = httpURLConnection.inputStream

            if (inputStream != null) {
                convertInputStreamToString(inputStream)
            } else {
                "{}"
            }
        }
    }
}