package com.swapface.fast_variant

import android.content.Context
import android.os.Bundle
import android.view.SurfaceView
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModel
import androidx.media3.common.C.VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.exoplayer.DefaultRenderersFactory
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import com.swapface.fast_variant.ui.theme.Article1ProjectTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@UnstableApi
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var cacheDataSource: CacheDataSource.Factory

    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    val videos = listOf("http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4" , "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4" ,
                        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4" , "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4"
    )

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Article1ProjectTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->


                    val pagerState = rememberPagerState {
                        videos.size
                    }

                    val exoPlayers = remember {
                        buildList{
                            for (item in videos){
                                add(
                                    buildAppExoPlayer(
                                        this@MainActivity,
                                        cacheDataSource,
                                        mediaItem = MediaItem.fromUri(item),
                                    )
                                )
                            }
                        }
                    }

                    var selectedIndex by remember { mutableStateOf(0) }

                    LaunchedEffect(pagerState.currentPage) {
                        selectedIndex = pagerState.currentPage
                    }

                    LaunchedEffect(selectedIndex) {
                        exoPlayers.forEach { it.pause() }
                        exoPlayers[selectedIndex].apply {
                            prepare()
                            play()
                        }
                    }

                    Column(
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(400.dp)
                        ) { index ->
                            val exoPlayer = exoPlayers[index]

                            AndroidView(
                                factory = remember {
                                    { context ->
                                        SurfaceView(context).apply {
                                            layoutParams = ViewGroup.LayoutParams(
                                                /* width = */ ViewGroup.LayoutParams.WRAP_CONTENT,
                                                /* height = */ ViewGroup.LayoutParams.WRAP_CONTENT
                                            )
                                        }.also {
                                            exoPlayer.videoScalingMode = VIDEO_SCALING_MODE_SCALE_TO_FIT_WITH_CROPPING
                                            exoPlayer.setVideoSurfaceView(it)
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clipToBounds()
                            )
                        }

                    }

                }
            }
        }
    }
}

@androidx.annotation.OptIn(UnstableApi::class)
fun buildAppExoPlayer(
    context: Context,
    cacheDataSourceFactory: CacheDataSource.Factory,
    initVolume: Float = 0f,
    mediaItem: MediaItem? = null,
    isPrepareAndPlay: Boolean = true
) = ExoPlayer
    .Builder(context)
    .setRenderersFactory(DefaultRenderersFactory(context).setEnableDecoderFallback(true))
    /* .apply {
         mediaSourceFactory?.let {
             setMediaSourceFactory(it)
         }
     }*/
    .build()
    .apply {
        repeatMode = Player.REPEAT_MODE_ALL
        volume = initVolume
        mediaItem?.let {
            val mediaSourceFactory =
                ProgressiveMediaSource.Factory(cacheDataSourceFactory)
            val mediaSource = mediaSourceFactory.createMediaSource(mediaItem)
            setMediaSource(mediaSource, true)
        }
        if (isPrepareAndPlay) {
            prepare()
            play()
        }
    }

val LocalCacheMediaSourceFactory = staticCompositionLocalOf<CacheDataSource.Factory> {
    error("LocalCacheMediaSourceFactory not present")
}

@HiltViewModel
class MainActivityViewModel @Inject constructor(): ViewModel() {

}
