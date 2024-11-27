package com.swapface.article1project

import android.content.Context
import androidx.annotation.OptIn
import androidx.media3.common.util.UnstableApi
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @OptIn(UnstableApi::class)
    @Provides
    @Singleton
    fun provideCache(
        @ApplicationContext context: Context
    ): CacheDataSource.Factory {
        val CACHE_DIR_NAME = "cached_video"
        val MAX_CACHE_SIZE = 512 * 1024 * 1024L //256MB

        val cacheEvictor = LeastRecentlyUsedCacheEvictor(MAX_CACHE_SIZE)
        val databaseProvider = StandaloneDatabaseProvider(context)
        val cache = SimpleCache(
            File(context.cacheDir, CACHE_DIR_NAME),
            cacheEvictor, databaseProvider
        )
        val upstreamFactory = DefaultDataSource.Factory(context)
        return CacheDataSource.Factory().apply {
            setCache(cache)
            setUpstreamDataSourceFactory(upstreamFactory)
            setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)
        }
    }

}



