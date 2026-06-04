package com.brindawan.mobile.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import brindawan.shared.generated.resources.Res
import brindawan.shared.generated.resources.ice_cream_hero_png_fallback
import org.jetbrains.compose.resources.painterResource

@Composable
fun defaultIceCreamPainter(): Painter {
    return painterResource(Res.drawable.ice_cream_hero_png_fallback)
}



