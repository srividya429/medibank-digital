package com.medibank.shop.ui.fragments

import android.os.Bundle
import android.view.View
import android.webkit.WebViewClient
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.medibank.shop.R
import com.medibank.shop.ui.NewsAppActivity
import com.medibank.shop.ui.NewsAppViewModel
import kotlinx.android.synthetic.main.fragment_article.*

class ArticleFragment : Fragment(R.layout.fragment_article) {
    lateinit var viewModel: NewsAppViewModel
    private val args: ArticleFragmentArgs by navArgs()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsAppActivity).viewModel
        val article = args.article
        wvArticle.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url)
        }

        fabSaved.setOnClickListener {
            viewModel.saveArticle(article)
            Snackbar.make(view, "Article saved ", Snackbar.LENGTH_SHORT).show()
        }
    }
}
