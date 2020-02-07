package com.tntkhang.coronavirus.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import com.tntkhang.coronavirus.R
import kotlinx.android.synthetic.main.fragment_info.*


class InfoFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_info, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        webview.loadUrl("https://www.who.int/news-room/q-a-detail/q-a-coronaviruses")

        // Enable Javascript
        // Enable Javascript
        val webSettings: WebSettings = webview.getSettings()
        webSettings.javaScriptEnabled = true

        // Force links and redirects to open in the WebView instead of in a browser
        // Force links and redirects to open in the WebView instead of in a browser
        webview.setWebViewClient(WebViewClient())

    }

}