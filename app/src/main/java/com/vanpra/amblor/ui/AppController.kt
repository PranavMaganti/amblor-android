package com.vanpra.amblor.ui

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.Box
import androidx.compose.foundation.ContentColorAmbient
import androidx.compose.foundation.Image
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ConstraintLayout
import androidx.compose.foundation.layout.Dimension
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Stack
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.navigation.NavHost
import androidx.compose.navigation.composable
import androidx.compose.navigation.navigate
import androidx.compose.navigation.rememberNavController
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.savedinstancestate.savedInstanceState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.VectorAsset
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.vanpra.amblor.ui.layouts.profile.ProfileLayout
import com.vanpra.amblor.ui.layouts.scrobble.ScrobbleLayout
import com.vanpra.amblor.ui.layouts.stats.StatsLayout

enum class BottomNavigationState {
    Scrobbles,
    Stats,
    Profile
}

@ExperimentalAnimationApi
@Composable
fun AppController() {
    Stack {
        val bottomNavController = rememberNavController()
        AmblorScaffold(bottomNavController) {
            NavHost(
                navController = bottomNavController,
                startDestination = BottomNavigationState.Scrobbles
            ) {
                composable(BottomNavigationState.Scrobbles) { ScrobbleLayout() }
                composable(BottomNavigationState.Stats) { StatsLayout() }
                composable(BottomNavigationState.Profile) { ProfileLayout() }
            }
        }
    }
}

@Composable
fun AmblorScaffold(bottomNavController: NavHostController, children: @Composable () -> Unit) {
    ConstraintLayout(Modifier.fillMaxSize().background(MaterialTheme.colors.background)) {
        val (title, body, bottomNav) = createRefs()
        AmblorTitle(
            Modifier.constrainAs(title) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
                width = Dimension.fillToConstraints
            }
        )

        Box(
            Modifier.constrainAs(body) {
                linkTo(top = title.bottom, bottom = bottomNav.top)
                linkTo(start = parent.start, end = parent.end)
                width = Dimension.fillToConstraints
                height = Dimension.fillToConstraints
            }
        ) {
            children()
        }

        AmblorNavigation(
            Modifier.constrainAs(bottomNav) {
                bottom.linkTo(parent.bottom)
                linkTo(parent.start, parent.end)
                width = Dimension.fillToConstraints
            }, bottomNavController
        )
    }
}

@Composable
fun AmblorTitle(modifier: Modifier = Modifier) {
    Row(
        modifier.fillMaxWidth().height(56.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            "Amblor",
            color = MaterialTheme.colors.onBackground,
            fontSize = 20.sp,
            fontWeight = FontWeight.W700
        )
    }
}

data class NavigationItem(val name: String, val icon: VectorAsset, val state: BottomNavigationState)

@Composable
fun AmblorNavigation(modifier: Modifier = Modifier, bottomNavController: NavHostController) {
    var selectedIndex by savedInstanceState { 0 }

    val items = remember {
        listOf(
            NavigationItem("Scrobbles", Icons.Default.Album, BottomNavigationState.Scrobbles),
            NavigationItem("Stats", Icons.Default.BarChart, BottomNavigationState.Stats),
            NavigationItem("Profile", Icons.Default.AccountCircle, BottomNavigationState.Profile)
        )
    }

    BottomNavigation(modifier) {
        items.forEachIndexed { index, it ->
            BottomNavigationItem(
                modifier = Modifier.background(MaterialTheme.colors.background),
                icon = {
                    Image(
                        it.icon,
                        colorFilter = ColorFilter.tint(ContentColorAmbient.current)
                    )
                },
                label = { Text(it.name, color = ContentColorAmbient.current) },
                alwaysShowLabels = false,
                selected = index == selectedIndex,
                onClick = {
                    bottomNavController.navigate(it.state)
                    selectedIndex = index
                },
                selectedContentColor = MaterialTheme.colors.primaryVariant,
                unselectedContentColor = Color.Gray
            )
        }
    }
}
