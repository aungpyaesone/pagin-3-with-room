package com.example.paging3demo.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.paging3demo.data.remote.UnsplashApi
import com.example.paging3demo.model.UnsplashImage
import com.example.paging3demo.util.Constants.ITEMS_PER_PAGE
import javax.inject.Inject

class SearchPagingSource (
    private val unsplashApi: UnsplashApi,
    private val query : String
) : PagingSource<Int,UnsplashImage>(){

    override fun getRefreshKey(state: PagingState<Int, UnsplashImage>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, UnsplashImage> {
        val currentPage = params.key ?: 1
        return try {
            val response = unsplashApi.searchImages(query, perPage = ITEMS_PER_PAGE)
            val endofPagination = response.images.isEmpty()
            if(response.images.isNotEmpty()){
                LoadResult.Page(
                    data = response.images,
                    prevKey = if(currentPage == 1) null else currentPage -1,
                    nextKey = if(endofPagination) null else currentPage + 1
                )
            }else{
                LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )
            }
        }catch (e: Exception){
            LoadResult.Error(e)
        }
    }
}