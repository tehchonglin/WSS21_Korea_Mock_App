package com.example.wss_mock_app.data

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(
    val name: String,
    val route:String,
    val icon : ImageVector,
    val badgeCount : Int = 0
)
