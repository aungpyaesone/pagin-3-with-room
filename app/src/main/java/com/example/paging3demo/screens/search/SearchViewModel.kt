package com.example.paging3demo.screens.search

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.paging3demo.data.repository.Repository
import com.example.paging3demo.model.UnsplashImage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: Repository
) : ViewModel(){

    private val _searchQuery = mutableStateOf("")
    val searchQuery = _searchQuery


    private val _searchImages = MutableStateFlow<PagingData<UnsplashImage>>(PagingData.empty())
    val searchImages = _searchImages

    fun updateSearchQuery(query:String){
        _searchQuery.value = query
    }

    @OptIn(InternalCoroutinesApi::class)
    fun searchHeros(query: String){
        viewModelScope.launch {
            repository.searchImages(query).cachedIn(viewModelScope).collect {
                _searchImages.value = it
            }
        }
    }
}