package com.example.paging3demo.screens.home

import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.paging3demo.navigation.Screen
import com.example.paging3demo.screens.common.ListContent

@Composable
fun HomeScreen(
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
){
    val getAllImages =  homeViewModel.getAllImages.collectAsLazyPagingItems()

    Scaffold(
        topBar = {HomeTopBar(
            onSearchClicked = {
                navController.navigate(Screen.Search.route)
            }
        )},
    ) {
        ListContent(itms = getAllImages)
    }
}