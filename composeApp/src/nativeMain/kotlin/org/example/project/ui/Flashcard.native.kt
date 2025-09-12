package org.example.project.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import lingualift.composeapp.generated.resources.Res
import lingualift.composeapp.generated.resources.branch_top_right
import org.jetbrains.compose.resources.painterResource

// iosMain / desktopMain
@Composable
actual fun branchTopRightPainter(): Painter {
    return painterResource(Res.drawable.branch_top_right) // SVG
}
