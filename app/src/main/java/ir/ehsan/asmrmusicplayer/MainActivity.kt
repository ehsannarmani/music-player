package ir.ehsan.asmrmusicplayer

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.with
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import ir.ehsan.asmrmusicplayer.ui.theme.AsmrMusicPlayerTheme
import kotlinx.coroutines.delay
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {

    lateinit var player:ExoPlayer

    @OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        player = ExoPlayer.Builder(this).build()
        setContent {
            AsmrMusicPlayerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val uiController = rememberSystemUiController()

                    val colors = listOf(
                        Color(0xFFFF5A5A),
                        Color(0xFFFFBE3D),
                        Color(0xFFD3FF5A),
                        Color(0xFF5AFFB8),
                        Color(0xFF5AFAFF),
                        Color(0xFF5A9CFF),
                        Color(0xFF6A5AFF),
                        Color(0xFFC55AFF),
                        Color(0xFFFF5A94),
                    )
                    val darkColors = listOf(
                        Color(0xFFBD3030),
                        Color(0xFFAF8024),
                        Color(0xFF83A525),
                        Color(0xFF2B8D63),
                        Color(0xFF288D91),
                        Color(0xFF294E85),
                        Color(0xFF2E248D),
                        Color(0xFF5C1F7E),
                        Color(0xFF812344),
                    )

                    val colorIndex = remember {
                        mutableIntStateOf(0)
                    }
                    LaunchedEffect(Unit) {
                        colorIndex.value += 1
                    }
                    LaunchedEffect(colorIndex.value) {
                        delay(2100)
                        if (colorIndex.value < darkColors.lastIndex) {
                            colorIndex.value += 1
                        } else {
                            colorIndex.value = 0
                        }
                    }
                    val animatedColor by animateColorAsState(
                        targetValue = colors[colorIndex.value],
                        animationSpec = tween(2000)
                    )
                    val animatedDarkColor by animateColorAsState(
                        targetValue = darkColors[colorIndex.value],
                        animationSpec = tween(2000)
                    )
                    uiController.setStatusBarColor(animatedColor, darkIcons = false)
                    uiController.setNavigationBarColor(animatedDarkColor)

                    val musics = listOf(
                        Music(
                            name = "Petrichor",
                            cover = R.drawable.cover1,
                            music = R.raw.music1
                        ),
                        Music(
                            name = "Love Story",
                            cover = R.drawable.cover2,
                            music = R.raw.music2
                        ),
                        Music(
                            name = "GoodBye",
                            cover = R.drawable.cover3,
                            music = R.raw.music3
                        ),
                        Music(
                            name = "Petrichor",
                            cover = R.drawable.cover1,
                            music = R.raw.music1
                        ),
                        Music(
                            name = "Love Story",
                            cover = R.drawable.cover2,
                            music = R.raw.music2
                        ),
                        Music(
                            name = "GoodBye",
                            cover = R.drawable.cover3,
                            music = R.raw.music3
                        ),
                    )

                    val pagerState = rememberPagerState(pageCount = { musics.count() })
                    val playingIndex = remember {
                        mutableIntStateOf(0)
                    }
                    LaunchedEffect(pagerState.currentPage) {
                        playingIndex.value = pagerState.currentPage
                        player.seekTo(pagerState.currentPage,0)
                    }

                    LaunchedEffect(Unit){
                        musics.forEach {
                            val path = "android.resource://"+packageName+"/"+it.music
                            val mediaItem = MediaItem.fromUri(Uri.parse(path))
                            player.addMediaItem(mediaItem)
                        }
                    }
                    player.prepare()

                    val playing = remember{
                        mutableStateOf(false)
                    }
                    val currentPosition  = remember{
                        mutableLongStateOf(0)
                    }
                    val totalDuration  = remember{
                        mutableLongStateOf(0)
                    }
                    val progressSize = remember{
                        mutableStateOf(IntSize(0,0))
                    }
                    LaunchedEffect(player.isPlaying){
                        playing.value = player.isPlaying

                    }
                    LaunchedEffect(player.currentPosition){
                        currentPosition.value = player.currentPosition
                    }
                    LaunchedEffect(player.duration){
                        if (player.duration > 0){
                            totalDuration.value = player.duration
                        }
                    }
                    LaunchedEffect(player.currentMediaItemIndex){
                        playingIndex.value = player.currentMediaItemIndex
                        pagerState.animateScrollToPage(playingIndex.value, animationSpec = tween(500))
                    }
                    var percentReached = currentPosition.value.toFloat()/(if (totalDuration.value > 0) totalDuration.value else 0).toFloat()
                    if (percentReached.isNaN()){
                       percentReached = 0f
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        animatedColor,
                                        animatedDarkColor
                                    )
                                )
                            ), contentAlignment = Alignment.Center
                    ) {
                        val configuration = LocalConfiguration.current

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val textColor by animateColorAsState(
                                targetValue = if (animatedColor.luminance() > .5f) Color(
                                    0xff414141
                                ) else Color.White,
                                animationSpec = tween(2000)
                            )
                            AnimatedContent(targetState = playingIndex.value, transitionSpec = {
                                (scaleIn() + fadeIn()) with (scaleOut() + fadeOut())
                            }) {
                                Text(text = musics[it].name, fontSize = 52.sp, color = textColor)
                            }
                            Spacer(modifier = Modifier.height(32.dp))
                            HorizontalPager(
                                modifier = Modifier.fillMaxWidth(),
                                state = pagerState,
                                pageSize = PageSize.Fixed((configuration.screenWidthDp / (1.7)).dp),
                                contentPadding = PaddingValues(horizontal = 85.dp)
                            ) { page ->
                                Card(
                                    modifier = Modifier
                                        .size((configuration.screenWidthDp / (1.7)).dp)
                                        .graphicsLayer {
                                            val pageOffset = (
                                                    (pagerState.currentPage - page) + pagerState
                                                        .currentPageOffsetFraction
                                                    ).absoluteValue
                                            val alphaLerp = lerp(
                                                start = 0.4f,
                                                stop = 1f,
                                                amount = 1f - pageOffset.coerceIn(0f, 1f)
                                            )
                                            val scaleLerp = lerp(
                                                start = 0.5f,
                                                stop = 1f,
                                                amount = 1f - pageOffset.coerceIn(0f, .5f)
                                            )
                                            alpha = alphaLerp
                                            scaleX = scaleLerp
                                            scaleY = scaleLerp
                                        }
                                        .border(2.dp, Color.White, CircleShape)
                                        .padding(6.dp),
                                    shape = CircleShape
                                ) {
                                    Image(
                                        painter = painterResource(id = musics[page].cover),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(54.dp))
                            Row(
                                modifier = Modifier.padding(horizontal = 24.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = convertLongToText(currentPosition.value),
                                    modifier = Modifier.width(55.dp),
                                    color = textColor,
                                    textAlign = TextAlign.Center
                                )
                                // Progress Box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .height(8.dp)
                                        .padding(horizontal = 8.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .onGloballyPositioned {
                                                              progressSize.value = it.size
                                        }
                                        .pointerInput(Unit){
                                                           detectTapGestures {
                                                               val xPos = it.x
                                                               val whereIClicked = (xPos.toLong()*totalDuration.value)/progressSize.value.width.toLong()
                                                               player.seekTo(whereIClicked)
                                                           }
                                        },
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    // Status Box
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(fraction = if(playing.value) percentReached else 0f)
                                            .fillMaxHeight()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xff414141))
                                    )
                                }
                                Text(
                                    text = convertLongToText(totalDuration.value),
                                    modifier = Modifier.width(55.dp),
                                    color = textColor,
                                    textAlign = TextAlign.Center
                                )
                            }
                            Spacer(modifier = Modifier.height(24.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp),
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Control(icon = R.drawable.ic_fast_rewind, size = 60.dp, onClick = {
                                    player.seekToPreviousMediaItem()
                                })
                                Control(icon = if (playing.value) R.drawable.ic_pause else R.drawable.ic_play, size = 80.dp, onClick = {
                                    if (playing.value){
                                        player.pause()
                                    }else{
                                        player.play()
                                    }
                                })
                                Control(icon = R.drawable.ic_fast_forward, size = 60.dp, onClick = {
                                    player.seekToNextMediaItem()
                                })
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Control(icon: Int, size: Dp, onClick: () -> Unit) {
    Box(modifier = Modifier
        .size(size)
        .clip(CircleShape)
        .background(Color.White)
        .clickable {
            onClick()
        }, contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier.size(size / 2),
            painter = painterResource(id = icon),
            tint = Color(0xff414141),
            contentDescription = null
        )
    }
}

fun convertLongToText(long: Long):String{
    val sec = long/1000
    val minutes = sec/60
    val seconds = sec%60

    val minutesString = if (minutes < 10){
        "0${minutes}"
    }else{
        minutes.toString()
    }
    val secondsString = if (seconds < 10){
        "0${seconds}"
    }else{
        seconds.toString()
    }
    return "$minutesString:$secondsString"
}

data class Music(
    val name: String,
    val music: Int,
    val cover: Int,
)

