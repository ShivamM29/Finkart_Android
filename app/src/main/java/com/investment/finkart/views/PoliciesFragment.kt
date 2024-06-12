package com.investment.finkart.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.investment.finkart.databinding.FragmentPoliciesBinding
import com.investment.finkart.utils.Constants
import com.investment.finkart.utils.FinkartLoading

class PoliciesFragment : Fragment() {
    private lateinit var binding: FragmentPoliciesBinding
    private lateinit var navController: NavController
    private lateinit var finkartLoading: FinkartLoading

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentPoliciesBinding.inflate(inflater, container, false)
        initView()
        return binding.root
    }

    private fun initView() {
        navController = findNavController()
        finkartLoading = FinkartLoading()

        var url = ""
        val from = arguments?.getString(Constants.INTENT_FROM)
        Log.i("Policies", "initView: From $from")
        if (from == Constants.INTENT_FROM_PRIVACY){
            binding.tvTitle.text = "Privacy Policy"
            url = "https://finnowinvestment.com/privacy-policy/"
        }else if (from == Constants.INTENT_FROM_TERMS){
            binding.tvTitle.text = "Terms Of Service"
            url = "https://finnowinvestment.com/terms-and-conditions/"
        }
        startWebView(url)
        binding.ibBack.setOnClickListener {
            navController.popBackStack()
        }
    }

    private fun startWebView(url: String) {
        val settings = binding.webView.settings
        settings.javaScriptEnabled = true
        binding.webView.scrollBarStyle = View.SCROLLBARS_OUTSIDE_OVERLAY
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.useWideViewPort = true
        binding.webView.settings.loadWithOverviewMode = true

        finkartLoading.showProgress(requireContext())
        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                finkartLoading.hideProgress()
            }

            override fun onReceivedError(view: WebView, errorCode: Int, description: String, failingUrl: String) {
                Toast.makeText(requireContext(),"Error:$description", Toast.LENGTH_SHORT).show()
            }
        }
        binding.webView.loadUrl(url)
    }
}