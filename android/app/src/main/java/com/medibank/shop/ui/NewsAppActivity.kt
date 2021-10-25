package com.medibank.shop.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.medibank.shop.R
import com.medibank.shop.db.NewsAppDatabase
import com.medibank.shop.repository.NewsAppRepository
import kotlinx.android.synthetic.main.activity_news.*

class NewsAppActivity : AppCompatActivity() {
    lateinit var viewModel: NewsAppViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val repository = NewsAppRepository(NewsAppDatabase(this))
        val viewModelsProviderFactory = NewsAppViewModelProviderFactory(repository, application)
        viewModel =
            ViewModelProvider(this, viewModelsProviderFactory).get(NewsAppViewModel::class.java)
        bottomNavigationView.setupWithNavController(newsAppNavHostFragment.findNavController())
    }
}
