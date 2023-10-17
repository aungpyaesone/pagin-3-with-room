package com.example.paging3demo.screens.search

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.paging3demo.screens.common.ListContent

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
){

    val searchQuery by viewModel.searchQuery
    val searchImages = viewModel.searchImages.collectAsLazyPagingItems()

    Scaffold (
        topBar = {
            SearchWidget(
                text = searchQuery,
                onSearchClicked = {
                    viewModel.searchHeros(query = it)
                },
                onTextChange = {
                    viewModel.updateSearchQuery(query = it)
                },
                onClosedClicked = {
                    navController.popBackStack()
                }
            )
        }
    ) {
        ListContent(
            searchImages
        )
    }
}