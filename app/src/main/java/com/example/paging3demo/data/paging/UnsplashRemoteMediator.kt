package com.example.paging3demo.data.paging

import androidx.compose.runtime.key
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.paging3demo.data.local.UnsplashDatabase
import com.example.paging3demo.data.remote.UnsplashApi
import com.example.paging3demo.model.UnsplashImage
import com.example.paging3demo.model.UnsplashRemoteKeys
import com.example.paging3demo.util.Constants.ITEMS_PER_PAGE
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class UnsplashRemoteMediator @Inject constructor(
    private val unsplashDatabase: UnsplashDatabase,
    private val unsplashApi: UnsplashApi
): RemoteMediator<Int,UnsplashImage>() {

    private val unsplashImageDao = unsplashDatabase.unsplashImageDao()
    private val unsplashRemoteKeyDao = unsplashDatabase.unsplashRemoteKeysDao()



    override suspend fun load(loadType: LoadType, state: PagingState<Int, UnsplashImage>): MediatorResult {

        return try {
            val currentPage = when(loadType){
                LoadType.REFRESH ->{
                    val remoteKeys = getRemoteKeyClosetToCurrentPosition(state)
                    remoteKeys?.nextPage?.minus(1) ?: 1
                }
                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevPage = remoteKeys?.prevPage ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                    prevPage
                }
                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextPage = remoteKeys?.nextPage ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys!=null)
                    nextPage
                }
            }

            val response = unsplashApi.getAllImages(page = currentPage, perPage = ITEMS_PER_PAGE)
            val endOfpaginationReached = response.isEmpty()

            val prevPage = if(currentPage == 1) null else currentPage -1
            val nextPage = if(endOfpaginationReached) null else currentPage +1

            unsplashDatabase.withTransaction {
                if(loadType == LoadType.REFRESH){
                   unsplashImageDao.deleteAllImages()
                   unsplashRemoteKeyDao.deleteAllRemoteKeys()
                }
                val keys = response.map {
                    UnsplashRemoteKeys(
                        id = it.id,
                        prevPage = prevPage,
                        nextPage = nextPage
                    )
                }
                unsplashRemoteKeyDao.addAllRemoteKeys(keys)
                unsplashImageDao.addImages(response)
            }
            MediatorResult.Success(endOfPaginationReached = endOfpaginationReached)
        }catch (e: Exception){
            return  MediatorResult.Error(e)
        }

    }

    private suspend fun getRemoteKeyClosetToCurrentPosition(
        state: PagingState<Int, UnsplashImage>
    ):UnsplashRemoteKeys?{
        return state.anchorPosition?.let {position ->
            state.closestItemToPosition(position)?.id?.let {
                unsplashRemoteKeyDao.getRemoteKeys(it)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(
        state: PagingState<Int, UnsplashImage>
    ): UnsplashRemoteKeys? {
        return state.pages.firstOrNull {it.data.isNotEmpty()}?.data?.firstOrNull()
            ?.let { unsplashImage ->
                unsplashRemoteKeyDao.getRemoteKeys(id = unsplashImage.id)
            }
    }

    private suspend fun getRemoteKeyForLastItem(
        state: PagingState<Int, UnsplashImage>
    ) : UnsplashRemoteKeys?{
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { unsplashImage ->
                unsplashRemoteKeyDao.getRemoteKeys(unsplashImage.id)
            }
    }
}