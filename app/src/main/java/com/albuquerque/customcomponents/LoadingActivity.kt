package com.albuquerque.customcomponents

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_loading.*

class LoadingActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading)

        supportActionBar?.let { actionBar ->
            actionBar.apply {
                this.setDisplayHomeAsUpEnabled(true)
                this.title = "Loading"
            }
        }

        setupTextLoading()
    }

    private fun setupTextLoading() {
        var isShowingLoading = true
        loadingTextView.showLoading()

        btnLoadingTextView.text = getString(R.string.button_loadingtextview, "Hide")

        btnLoadingTextView.setOnClickListener {
            isShowingLoading = !isShowingLoading

            if(isShowingLoading) {
                btnLoadingTextView.text = getString(R.string.button_loadingtextview, "Hide")
                loadingTextView.showLoading()
            } else {
                btnLoadingTextView.text = getString(R.string.button_loadingtextview, "Show")
                loadingTextView.hideLoading()
            }
        }
    }

}