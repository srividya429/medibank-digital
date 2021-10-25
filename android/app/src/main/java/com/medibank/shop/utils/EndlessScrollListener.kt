package com.medibank.shop.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessScrollListener : RecyclerView.OnScrollListener() {

    private var isLoading = true
    private var previousTotalItemCount = 0
    private val visibleThreshold = 5
    private var firstVisibleItem = 0
    private var visibleItemCount = 0
    private var totalItemCount = 0
    private val startingPageIndex = 0
    private var currentPage = 0

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
        visibleItemCount = recyclerView.childCount
        totalItemCount = layoutManager?.itemCount ?: 0
        firstVisibleItem = layoutManager?.findFirstVisibleItemPosition() ?: 0

        onScroll(firstVisibleItem, visibleItemCount, totalItemCount)
    }

    private fun onScroll(firstVisibleItem: Int, visibleItemCount: Int, totalItemCount: Int) {
        if (totalItemCount < previousTotalItemCount) {
            currentPage = startingPageIndex
            previousTotalItemCount = totalItemCount
            if (totalItemCount == 0) {
                isLoading = true
            }
        }
        if (isLoading && totalItemCount > previousTotalItemCount) {
            isLoading = false
            previousTotalItemCount = totalItemCount
            currentPage++
        }
        if (!isLoading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
            onLoadMore(currentPage + 1, totalItemCount)
            isLoading = true
        }
    }

    abstract fun onLoadMore(page: Int, totalItemsCount: Int)
}
