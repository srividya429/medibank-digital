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
import com.medibank.shop.utils.Constants
import com.medibank.shop.utils.EndlessScrollListener
import com.medibank.shop.utils.Resource
import kotlinx.android.synthetic.main.fragment_source_news.*

class SourceNewsFragment : Fragment(R.layout.fragment_source_news) {

    lateinit var viewModel: NewsAppViewModel
    lateinit var newsAdapter: NewsAppAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as NewsAppActivity).viewModel
        setUpRecyclerView()
        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article", it)
            }
            findNavController().navigate(
                R.id.action_sourceNewsFragment_to_articlesFragment,
                bundle
            )
        }
        viewModel.sourceNews.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data.let { newsResponse ->
                        newsAdapter.differ.submitList(newsResponse?.articles?.toList())
                        val totalPagesResult =
                            newsResponse!!.totalResults / Constants.QUERY_PAGE_SIZE + 2
                        isLastPage = viewModel.sourceNewsPage == totalPagesResult
                        if (isLastPage) {
                            rvSourceNews.setPadding(0, 0, 0, 0)
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

    //Add Pagination
    var isLoading = false
    var isLastPage = false

    private fun hideProgressBar() {
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }

    private fun showProgressBar() {
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }

    private fun setUpRecyclerView() {
        newsAdapter = NewsAppAdapter()
        rvSourceNews.setHasFixedSize(true)
        rvSourceNews.apply {
            adapter = newsAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

            val scrollListener = object : EndlessScrollListener() {
                override fun onLoadMore(page: Int, totalItemsCount: Int) {
                    viewModel.getSourceNews("CNN")
                }
            }
            addOnScrollListener(scrollListener)
        }
    }
}
