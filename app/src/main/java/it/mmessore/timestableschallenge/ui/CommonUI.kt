package it.mmessore.timestableschallenge.ui

import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.Window
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import it.mmessore.timestableschallenge.R
import it.mmessore.timestableschallenge.data.persistency.AppPreferencesImpl
import kotlinx.coroutines.delay
import java.util.Locale
import kotlin.math.max
import kotlin.math.min


@Composable
fun RoundButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    text: String,
    enabled: Boolean = true,
    uppercase: Boolean = true
) {
    val btnText = if (uppercase)
        text.uppercase(Locale.getDefault())
    else
        text.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        modifier = modifier
            .padding(8.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primary)
            .padding(4.dp)
    ) {
        Text(
            text = btnText,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RoundButtonPreview() {
    RoundButton(onClick = {}, text = "hello")
}

@Composable
fun DelayedFadeInContent(
    startDelayMillis: Long = 1000,
    endDelayMillis: Long = 1000,
    fadeInDurationMillis: Int = 1000,
    onAnimationEnd: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = fadeInDurationMillis)
    )

    LaunchedEffect(Unit) {
        delay(startDelayMillis)
        visible = true
        delay(endDelayMillis)
        onAnimationEnd()
    }

    Box(modifier = modifier.alpha(alpha)) {
        content()
    }
}

/* Credits: https://gist.github.com/sinasamaki/daa825d96235a18822177a2b1b323f49?ref=sinasamaki.com */
@Composable
fun SFXDialog(
    showDialog: Boolean,
    audioResource: Int? = null,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {

    var showAnimatedDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val appPreferences = remember { AppPreferencesImpl(context) }

    LaunchedEffect(showDialog) {
        if (showDialog) showAnimatedDialog = true
    }

    if (showAnimatedDialog) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            val dialogWindow = getDialogWindow()

            SideEffect {
                dialogWindow.let { window ->
                    window?.setDimAmount(0f)
                    window?.setWindowAnimations(-1)
                }
            }

            if (appPreferences.playSounds) {
                audioResource?.let {
                    LaunchedEffect(Unit) {
                        try {
                            MediaPlayer.create(context, audioResource).apply {
                                start()
                                setOnCompletionListener { release() }
                            }
                        } catch (e: Exception) {
                            Log.e("ExpandingDialog", "Error in playing dialog sfx", e)
                        }
                    }
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                var animateIn by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { animateIn = true }
                AnimatedVisibility(
                    visible = animateIn && showDialog,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Box(
                        modifier = Modifier
                            .pointerInput(Unit) { detectTapGestures { onDismissRequest() } }
                            .background(Color.Black.copy(alpha = .80f))
                            .fillMaxSize()
                    )
                }
                AnimatedVisibility(
                    visible = animateIn && showDialog,
                    enter = fadeIn(spring(stiffness = Spring.StiffnessHigh)) + scaleIn(
                        initialScale = .8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
                    exit = slideOutVertically { it / 8 } + fadeOut() + scaleOut(targetScale = .95f)
                ) {
                    Box(
                        Modifier
                            .pointerInput(Unit) { detectTapGestures { } }
                            .shadow(8.dp, shape = RoundedCornerShape(16.dp))
                            .width(320.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                MaterialTheme.colorScheme.surface,
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        content()
                    }

                    DisposableEffect(Unit) {
                        onDispose {
                            showAnimatedDialog = false
                        }
                    }
                }
            }
        }
    }
}

@ReadOnlyComposable
@Composable
fun getDialogWindow(): Window? = (LocalView.current.parent as? DialogWindowProvider)?.window

@Composable
fun DialogScaffold(
    painter: Painter? = null,
    contentDescription: String? = null,
    content: @Composable () -> Unit = {},
    okBtnText: String? = null,
    onOkButtonClick: () -> Unit = {},
    closeBtnText: String = stringResource(id = R.string.close),
    onCloseButtonClick: () -> Unit = {}
) {
    Column(Modifier.background(MaterialTheme.colorScheme.surface)) {
        painter?.let {
            var graphicVisible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) { graphicVisible = true }

            AnimatedVisibility(
                visible = graphicVisible,
                enter = expandVertically(
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    expandFrom = Alignment.CenterVertically,
                )
            ) {
                Image(
                    painter = painter,
                    contentDescription = contentDescription,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
        content()

        Row(
            modifier = Modifier.height(IntrinsicSize.Min)
        ) {
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onCloseButtonClick() }
                    .weight(1f)
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = closeBtnText.uppercase(), fontWeight = FontWeight.Bold)
            }
            okBtnText?.let {
                Box(
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                        .width(2.dp)
                        .fillMaxHeight()
                        .background(
                            MaterialTheme.colorScheme.onSurface.copy(alpha = .08f),
                            shape = RoundedCornerShape(10.dp)
                        )
                )

                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            onCloseButtonClick()
                            onOkButtonClick()
                        }
                        .weight(1f)
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = okBtnText.uppercase(),
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DialogPager(
    pages: List<@Composable () -> Unit>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { pages.size })

    Column(modifier = modifier.padding(16.dp)) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.wrapContentHeight()
        ) { page ->
            val scrollState = rememberScrollState()
            Box (
                Modifier
                    .height(250.dp)
                    .verticalScroll(scrollState)
                    .verticalScrollbar(scrollState)){
                pages[page]()
            }
        }

        // Pager indicators
        if (pages.size > 1) {
            Row(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val color =
                        if (pagerState.currentPage == iteration)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = .2f)

                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(color)
                            .size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ClickableTextWithUrl(text: String, textUrl: String, url: String, style: TextStyle = MaterialTheme.typography.bodyLarge) {
    val context = LocalContext.current
    val annotatedString = buildAnnotatedString {
        withStyle(style = SpanStyle(
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = style.fontSize,
            fontFamily = style.fontFamily
        )) {
            append("$text ")
        }
        pushStringAnnotation(tag = "URL", annotation = url)
        withStyle(
            style = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                fontSize = style.fontSize,
                fontFamily = style.fontFamily
            )
        ) {
            append(textUrl)
        }
        pop()
    }

    ClickableText(
        text = annotatedString,
        style = style,
        onClick = { offset ->
            annotatedString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                    context.startActivity(intent)
                }
        }
    )
}

@Composable
fun ScreenContainer(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        modifier = modifier
            .padding(16.dp)
            .fillMaxSize()
            .verticalScroll(scrollState)
            .fadingEdges(scrollState)
    ) {
        content()
    }
}

/*
    Credits to: https://gist.github.com/XFY9326/2067efcc3c5899557cc6a334d76a92c8
 */
@Composable
fun Modifier.verticalScrollbar(
    scrollState: ScrollState,
    scrollBarWidth: Dp = 4.dp,
    minScrollBarHeight: Dp = 5.dp,
    scrollBarColor: Color = MaterialTheme.colorScheme.primary,
    cornerRadius: Dp = 2.dp
): Modifier = composed {
    val targetAlpha = if (scrollState.isScrollInProgress) 1f else .1f
    val duration = if (scrollState.isScrollInProgress) 150 else 500

    val alpha by animateFloatAsState(
        targetValue = targetAlpha,
        animationSpec = tween(durationMillis = duration)
    )

    drawWithContent {
        drawContent()

        val needDrawScrollbar = scrollState.isScrollInProgress || alpha > 0.0f

        if (needDrawScrollbar && scrollState.maxValue > 0) {
            val visibleHeight: Float = this.size.height - scrollState.maxValue
            val scrollBarHeight: Float = max(visibleHeight * (visibleHeight / this.size.height), minScrollBarHeight.toPx())
            val scrollPercent: Float = scrollState.value.toFloat() / scrollState.maxValue
            val scrollBarOffsetY: Float = scrollState.value + (visibleHeight - scrollBarHeight) * scrollPercent

            drawRoundRect(
                color = scrollBarColor,
                topLeft = Offset(this.size.width - scrollBarWidth.toPx(), scrollBarOffsetY),
                size = Size(scrollBarWidth.toPx(), scrollBarHeight),
                alpha = alpha,
                cornerRadius = CornerRadius(cornerRadius.toPx())
            )
        }
    }
}

// Credits to: https://medium.com/@helmersebastian/fading-edges-modifier-in-jetpack-compose-af94159fdf1f
fun Modifier.fadingEdges(
    scrollState: ScrollState,
    topEdgeHeight: Dp = 16.dp,
    bottomEdgeHeight: Dp = 16.dp
): Modifier = this.then(
    Modifier
        // adding layer fixes issue with blending gradient and content
        .graphicsLayer { alpha = 0.99F }
        .drawWithContent {
            drawContent()

            val topColors = listOf(Color.Transparent, Color.Black)
            val topStartY = scrollState.value.toFloat()
            val topGradientHeight = min(topEdgeHeight.toPx(), topStartY)
            drawRect(
                brush = Brush.verticalGradient(
                    colors = topColors,
                    startY = topStartY,
                    endY = topStartY + topGradientHeight
                ),
                blendMode = BlendMode.DstIn
            )

            val bottomColors = listOf(Color.Black, Color.Transparent)
            val bottomEndY = size.height - scrollState.maxValue + scrollState.value
            val bottomGradientHeight = min(bottomEdgeHeight.toPx(), scrollState.maxValue.toFloat() - scrollState.value)
            if (bottomGradientHeight != 0f) drawRect(
                brush = Brush.verticalGradient(
                    colors = bottomColors,
                    startY = bottomEndY - bottomGradientHeight,
                    endY = bottomEndY
                ),
                blendMode = BlendMode.DstIn
            )
        }
)