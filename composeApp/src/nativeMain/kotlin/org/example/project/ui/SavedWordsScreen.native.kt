package org.example.project.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import lingualift.composeapp.generated.resources.Res
import lingualift.composeapp.generated.resources.branch_bottom_left
import org.jetbrains.compose.resources.painterResource

@Composable
actual fun branchBottomLeftPainter(): Painter {
    return painterResource(Res.drawable.branch_bottom_left)
}