package com.vanpra.amblor.ui.layouts.scrobble

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Dimension
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumnFor
import androidx.compose.foundation.lazy.LazyColumnForIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.viewModel
import com.vanpra.amblor.models.ScrobbleData
import com.vanpra.amblor.util.BackButton
import com.vanpra.amblor.util.toJavaDate
import dev.chrisbanes.accompanist.coil.CoilImageWithCrossfade
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaLocalDateTime
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@ExperimentalCoroutinesApi
@Composable
fun ScrobbleLayout() {
    val viewModel: ScrobbleViewModel = viewModel()
    val viewState by viewModel.viewState.collectAsState()

    LazyColumnForIndexed(
        viewState.scrobbles,
        modifier = Modifier.fillMaxSize()
    ) { index, item ->
        Column {
            val currentDate = Instant.fromEpochSeconds(item.time.toLong()).toJavaDate()
            val previousDate = if (index != 0) {
                Instant.fromEpochSeconds(viewState.scrobbles[index - 1].time.toLong())
                    .toJavaDate()
            } else {
                null
            }
            val topPadding = if (index == 0) 0.dp else 12.dp

            if (currentDate != previousDate) {
                ScrobleDateTitle(
                    Modifier.padding(start = 8.dp, bottom = 4.dp, top = topPadding),
                    currentDate
                )
            }

            ScrobbleItem(
                Modifier.fillMaxWidth().clickable(
                    onClick = {
                        viewModel.openScrobbleView(item)
                    }
                ),
                item
            )
        }
    }
}

@ExperimentalAnimationApi
@Composable
fun ScrobbleDetailView() {
    val viewModel = viewModel<ScrobbleViewModel>()
    val viewState by viewModel.viewState.collectAsState()

    AnimatedVisibility(
        visible = viewState.showingScrobble,
        modifier = Modifier.fillMaxSize().clickable(onClick = {}, indication = null),
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it })
    ) {
        Column(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
            BackButton {
                viewModel.closeScrobbleView()
            }
            Spacer(Modifier.height(8.dp))
            CoilImageWithCrossfade(
                data = viewState.selectedScrobble.image,
                Modifier.padding(horizontal = 24.dp).fillMaxWidth()
            )
            Spacer(Modifier.height(16.dp))
            Text(
                "Artists",
                modifier = Modifier.padding(start = 12.dp),
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.h6,
                fontSize = 18.sp,
                maxLines = 1
            )
            Spacer(Modifier.height(4.dp))
            LazyColumnFor(Artist.from(viewState.selectedScrobble)) {
                ArtistView(it)
            }
        }
    }
}

@Composable
private fun ArtistView(artist: Artist, onClick: () -> Unit = {}) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
                .padding(top = 8.dp, bottom = 8.dp, start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CoilImageWithCrossfade(data = artist.image, Modifier.size(40.dp).clip(CircleShape))
            Spacer(Modifier.width(16.dp))
            Text(
                artist.name,
                color = MaterialTheme.colors.onBackground,
                style = MaterialTheme.typography.body2,
                fontSize = 16.sp,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ScrobleDateTitle(modifier: Modifier = Modifier, date: LocalDate) {
    val dateFormat = DateTimeFormatter.ofPattern("EEE, dd/MM/yyyy")
    Text(
        date.format(dateFormat),
        modifier = modifier,
        color = MaterialTheme.colors.onBackground,
        style = MaterialTheme.typography.h6,
        fontSize = 18.sp,
        maxLines = 1
    )
}

@Composable
private fun ScrobbleItem(modifier: Modifier = Modifier, scrobble: ScrobbleData) {
    ConstraintLayout(modifier) {
        val (content, time) = createRefs()
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 8.dp)
                .constrainAs(content) {
                    centerVerticallyTo(parent)
                    start.linkTo(parent.start)
                    end.linkTo(time.start, 8.dp)
                    width = Dimension.fillToConstraints
                    height = Dimension.wrapContent
                }
        ) {
            CoilImageWithCrossfade(data = scrobble.image, modifier = Modifier.size(50.dp))
            Column(Modifier.padding(start = 8.dp)) {
                Text(scrobble.name, color = MaterialTheme.colors.onBackground, maxLines = 1)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    scrobble.artist_names,
                    color = MaterialTheme.colors.onBackground,
                    style = MaterialTheme.typography.body2,
                    fontSize = 14.sp,
                    maxLines = 1
                )
            }
        }

        val scrobbleDateTime = Instant.fromEpochSeconds(scrobble.time.toLong())
            .toLocalDateTime(TimeZone.currentSystemDefault())
        val timeFormat = DateTimeFormatter.ofPattern("HH:mm")

        Text(
            scrobbleDateTime.toJavaLocalDateTime().format(timeFormat),
            modifier = Modifier.constrainAs(time) {
                centerVerticallyTo(parent)
                end.linkTo(parent.end, 16.dp)
                width = Dimension.wrapContent
                height = Dimension.wrapContent
            },
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.h6,
            fontSize = 16.sp
        )
    }
}
