package com.medibank.shop.ui

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.medibank.shop.repository.NewsAppRepository

@Suppress("UNCHECKED_CAST")
class NewsAppViewModelProviderFactory(
    val newsRepository: NewsAppRepository,
    val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return NewsAppViewModel(newsRepository, app) as T
    }
}
