package com.medibank.shop.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.medibank.shop.ArticlesHeadlinesApp
import com.medibank.shop.data.Article
import com.medibank.shop.data.NewsResponse
import com.medibank.shop.repository.NewsAppRepository
import com.medibank.shop.utils.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsAppViewModel(
    private val newsRepository: NewsAppRepository, app: Application
) : AndroidViewModel(app) {
    private val TAG = "NewsViewModel"
    val headlineNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    val sourceNews: MutableLiveData<Resource<NewsResponse>> = MutableLiveData()
    var topHeadLinesPage = 1
    var sourceNewsPage = 1
    var headLinesResponse: NewsResponse? = null
    var sourceNewsResponse: NewsResponse? = null

    init {
        getTopHeadLines("au")
        getSourceNews("CNN")
    }

    fun getTopHeadLines(countryCode: String) = viewModelScope.launch {
        safeTopHeadLinesNewsCall(countryCode)
    }

    fun getSourceNews(newsCategory: String) = viewModelScope.launch {
        safeSourceNewsCall(newsCategory)
    }

    private fun handleHeadlineNewsResponse(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                topHeadLinesPage++
                if (headLinesResponse == null) {
                    headLinesResponse = resultResponse
                } else {
                    val oldArticles = headLinesResponse?.articles
                    val newArticles = resultResponse.articles

                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(headLinesResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    private fun handleSourceNews(response: Response<NewsResponse>): Resource<NewsResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                sourceNewsPage++
                if (sourceNewsResponse == null) {
                    sourceNewsResponse = resultResponse
                } else {
                    val oldArticles = sourceNewsResponse?.articles
                    val newArticles = resultResponse.articles

                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(sourceNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

    // Room Database
    fun saveArticle(article: Article) = viewModelScope.launch {
        newsRepository.upsert(article)
    }

    fun getSavedNews() = newsRepository.getSavedNews()

    fun deleteArticle(article: Article) = viewModelScope.launch {
        newsRepository.deleteArticle(article)
    }

    //Fetch Data From Repository
    suspend fun safeTopHeadLinesNewsCall(countryCode: String) {
        headlineNews.postValue(Resource.Loading())
        try {
            if (hasConnection()) {
                val response = newsRepository.getTopHeadLines(countryCode, topHeadLinesPage)
                headlineNews.postValue(handleHeadlineNewsResponse(response))
            } else {
                headlineNews.postValue(Resource.Error("You have no Internet connection"))
            }
        } catch (t: Throwable) {
            Log.d(TAG, t.message.toString())
            when (t) {
                is IOException -> headlineNews.postValue(Resource.Error("Network failure"))
                else -> headlineNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }

    suspend fun safeSourceNewsCall(newsCategory: String) {
        sourceNews.postValue(Resource.Loading())
        try {
            if (hasConnection()) {
                val response = newsRepository.getSourceNews(newsCategory, sourceNewsPage)
                sourceNews.postValue(handleSourceNews(response))
            } else {
                sourceNews.postValue(Resource.Error("You have no active internet network"))
            }
        } catch (t: Throwable) {
            Log.d(TAG, "safeBusinessNewsCall: ${t.message}")
            when (t) {
                is IOException -> sourceNews.postValue(Resource.Error("Network Failure"))
                else -> sourceNews.postValue(Resource.Error("Conversion Error"))
            }
        }

    }

    // Internet Connection Check
    private fun hasConnection(): Boolean {
        val connectivityManager = getApplication<ArticlesHeadlinesApp>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities =
                connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when (type) {
                    TYPE_WIFI -> true
                    TYPE_MOBILE -> true
                    TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }
}
