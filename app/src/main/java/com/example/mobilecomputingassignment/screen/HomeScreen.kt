package com.example.mobilecomputingassignment.screen

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import com.example.mobilecomputingassignment.R
import androidx.compose.material3.Scaffold
import com.example.mobilecomputingassignment.ui.theme.OrangeNav
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController
import com.example.mobilecomputingassignment.pages.AccountPage
import com.example.mobilecomputingassignment.pages.ClubsPage
import com.example.mobilecomputingassignment.pages.EventPage
import com.example.mobilecomputingassignment.pages.PollsPage

@Composable
fun HomeScreen(modifier: Modifier = Modifier,navController: NavHostController) {

    val navItemList = listOf(
        NavItem(stringResource(id = R.string.navbar_events), Icons.Default.Event),
        NavItem(stringResource(id = R.string.navbar_clubs), Icons.Default.People),
        NavItem(stringResource(id = R.string.navbar_polls), Icons.Default.Poll),
        NavItem(stringResource(id = R.string.navbar_account), Icons.Default.Person)
    )

    var selectedIndex by remember{
        mutableStateOf(0)
    }

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = OrangeNav
            ) {
                navItemList.forEachIndexed { index, navItem->
                    NavigationBarItem(
                        selected = index == selectedIndex,
                        onClick = {
                            selectedIndex = index
                        },
                        icon = {
                            Icon(imageVector = navItem.icon, contentDescription = navItem.label)
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFFE3A370)
                        ),
                        label = {Text(text = navItem.label)}
                    )
                }
            }
        }
    ) {
        ContentScreen(modifier = modifier.padding(it), selectedIndex,navController)
    }
}

@Composable
fun ContentScreen(
    modifier: Modifier = Modifier,
    selectedIndex: Int,
    navController: NavHostController
){
    when(selectedIndex){
        0-> EventPage(modifier)
        1-> ClubsPage(modifier)
        2-> PollsPage(modifier)
        3-> AccountPage(modifier, navController)
    }
}

data class NavItem(
    val label: String,
    val icon: ImageVector
)