package com.medibank.shop.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.medibank.shop.R
import com.medibank.shop.adapters.NewsAppAdapter
import com.medibank.shop.ui.NewsAppActivity
import com.medibank.shop.ui.NewsAppViewModel
import com.medibank.shop.utils.Constants.Companion.QUERY_PAGE_SIZE
import com.medibank.shop.utils.EndlessScrollListener
import com.medibank.shop.utils.Resource
import kotlinx.android.synthetic.main.fragment_headline_news.*

class HeadlinesFragment : Fragment(R.layout.fragment_headline_news) {
    lateinit var viewModel: NewsAppViewModel
    lateinit var newsAdapter: NewsAppAdapter

    var isLoading = false
    var isLastPage = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsAppActivity).viewModel
        setUpRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_headlinesFragment_to_articlesFragment,
                bundle
            )
        }
        viewModel.headlineNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse?.articles?.toList())
                        val totalPagesResult = newsResponse!!.totalResults / QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.topHeadLinesPage == totalPagesResult
                        if (isLastPage) {
                            rvHeadlineNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Toast.makeText(context, "Error!! $message", Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAppAdapter()
        rvHeadlineNews.setHasFixedSize(true)
        rvHeadlineNews.apply {
            adapter = newsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            val scrollListener = object : EndlessScrollListener() {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    viewModel.getTopHeadLines("au")
                }
            }
            addOnScrollListener(scrollListener)
        }
    }
}
