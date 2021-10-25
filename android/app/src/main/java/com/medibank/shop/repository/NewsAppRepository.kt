package com.medibank.shop.repository

import com.medibank.shop.data.Article
import com.medibank.shop.db.NewsAppDatabase
import com.medibank.shop.network.NewsApiInstance

open class NewsAppRepository(val db: NewsAppDatabase) {

    suspend fun getTopHeadLines(countryCode: String, pageNumber: Int) =
        NewsApiInstance.api.getHeadlineNews(countryCode, pageNumber)

    suspend fun getSourceNews(newsCategory: String, pageNumber: Int) =
        NewsApiInstance.api.getSourceNews(newsCategory, pageNumber)

    suspend fun upsert(article: Article) = db.getArticlesDao().upsert(article)

    fun getSavedNews() = db.getArticlesDao().getArticles()

    suspend fun deleteArticle(article: Article) = db.getArticlesDao().deleteArticle(article)

}
