package com.example.wss_mock_app

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Normal Font",
    group = "Normal Font Group",
    uiMode = UI_MODE_NIGHT_NO,
    showBackground = true
)

annotation class FontScalePreview

@Preview(name = "Pixel 2", group = "Devices", device = Devices.PIXEL_2, showSystemUi = false, showBackground = true )
annotation class Pixel2Preview

@Preview(name = "Pixel C", group = "Devices", device = Devices.PIXEL_2, showSystemUi = false, showBackground = true )
annotation class PixelCPreview

