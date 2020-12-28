package com.vanpra.amblor.ui.layouts.scrobble

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
@Composable
fun ScrobbleLayout() {
    // val viewModel: ScrobbleViewModel = viewModel()
    // val viewState by viewModel.viewState.collectAsState()
    //
    // LazyColumn(
    //     modifier = Modifier.fillMaxSize()
    // ) {
    //     itemsIndexed(viewState.scrobbles) { index, item ->
    //         Column {
    //             val currentDate = Instant.fromEpochSeconds(item.time.toLong()).toJavaDate()
    //             val previousDate = if (index != 0) {
    //                 Instant.fromEpochSeconds(viewState.scrobbles[index - 1].time.toLong())
    //                     .toJavaDate()
    //             } else {
    //                 null
    //             }
    //             val topPadding = if (index == 0) 0.dp else 12.dp
    //
    //             if (currentDate != previousDate) {
    //                 ScrobleDateTitle(
    //                     Modifier.padding(start = 8.dp, bottom = 4.dp, top = topPadding),
    //                     currentDate
    //                 )
    //             }
    //
    //             ScrobbleItem(
    //                 Modifier.fillMaxWidth().clickable(
    //                     onClick = {
    //                         viewModel.openScrobbleView(item)
    //                     }
    //                 ),
    //                 item
    //             )
    //         }
    //     }
    // }
}

// @ExperimentalAnimationApi
// @Composable
// fun ScrobbleDetailView() {
//     val viewModel = viewModel<ScrobbleViewModel>()
//     val viewState by viewModel.viewState.collectAsState()
//
//     AnimatedVisibility(
//         visible = viewState.showingScrobble,
//         modifier = Modifier.fillMaxSize().clickable(onClick = {}, indication = null),
//         enter = slideInVertically(initialOffsetY = { it }),
//         exit = slideOutVertically(targetOffsetY = { it })
//     ) {
//         Column(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
//             BackButton {
//                 viewModel.closeScrobbleView()
//             }
//             Spacer(Modifier.height(8.dp))
//             CoilImage(
//                 data = viewState.selectedScrobble.image,
//                 Modifier.padding(horizontal = 24.dp).fillMaxWidth()
//             )
//             Spacer(Modifier.height(16.dp))
//             Text(
//                 "Artists",
//                 modifier = Modifier.padding(start = 12.dp),
//                 color = MaterialTheme.colors.onBackground,
//                 style = MaterialTheme.typography.h6,
//                 fontSize = 18.sp,
//                 maxLines = 1
//             )
//             Spacer(Modifier.height(4.dp))
//             LazyColumn {
//                 items(Artist.from(viewState.selectedScrobble)) {
//                     ArtistView(it)
//                 }
//             }
//         }
//     }
// }
//
// @Composable
// private fun ArtistView(artist: Artist, onClick: () -> Unit = {}) {
//     Column {
//         Row(
//             modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
//                 .padding(top = 8.dp, bottom = 8.dp, start = 8.dp),
//             verticalAlignment = Alignment.CenterVertically
//         ) {
//             CoilImage(data = artist.image, Modifier.size(40.dp).clip(CircleShape))
//             Spacer(Modifier.width(16.dp))
//             Text(
//                 artist.name,
//                 color = MaterialTheme.colors.onBackground,
//                 style = MaterialTheme.typography.body2,
//                 fontSize = 16.sp,
//                 maxLines = 1
//             )
//         }
//     }
// }
//
// @Composable
// private fun ScrobleDateTitle(modifier: Modifier = Modifier, date: LocalDate) {
//     val dateFormat = DateTimeFormatter.ofPattern("EEE, dd/MM/yyyy")
//     Text(
//         date.format(dateFormat),
//         modifier = modifier,
//         color = MaterialTheme.colors.onBackground,
//         style = MaterialTheme.typography.h6,
//         fontSize = 18.sp,
//         maxLines = 1
//     )
// }
//
// @Composable
// private fun ScrobbleItem(modifier: Modifier = Modifier, scrobble: ScrobbleData) {
//     ConstraintLayout(modifier) {
//         val (content, time) = createRefs()
//         Row(
//             verticalAlignment = Alignment.CenterVertically,
//             modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 8.dp)
//                 .constrainAs(content) {
//                     centerVerticallyTo(parent)
//                     start.linkTo(parent.start)
//                     end.linkTo(time.start, 8.dp)
//                     width = Dimension.fillToConstraints
//                     height = Dimension.wrapContent
//                 }
//         ) {
//             CoilImage(data = scrobble.image, modifier = Modifier.size(50.dp))
//             Column(Modifier.padding(start = 8.dp)) {
//                 Text(scrobble.name, color = MaterialTheme.colors.onBackground, maxLines = 1)
//                 Spacer(modifier = Modifier.height(2.dp))
//                 Text(
//                     scrobble.artist_names,
//                     color = MaterialTheme.colors.onBackground,
//                     style = MaterialTheme.typography.body2,
//                     fontSize = 14.sp,
//                     maxLines = 1
//                 )
//             }
//         }
//
//         val scrobbleDateTime = Instant.fromEpochSeconds(scrobble.time.toLong())
//             .toLocalDateTime(TimeZone.currentSystemDefault())
//         val timeFormat = DateTimeFormatter.ofPattern("HH:mm")
//
//         Text(
//             scrobbleDateTime.toJavaLocalDateTime().format(timeFormat),
//             modifier = Modifier.constrainAs(time) {
//                 centerVerticallyTo(parent)
//                 end.linkTo(parent.end, 16.dp)
//                 width = Dimension.wrapContent
//                 height = Dimension.wrapContent
//             },
//             color = MaterialTheme.colors.onBackground,
//             style = MaterialTheme.typography.h6,
//             fontSize = 16.sp
//         )
//     }
// }
