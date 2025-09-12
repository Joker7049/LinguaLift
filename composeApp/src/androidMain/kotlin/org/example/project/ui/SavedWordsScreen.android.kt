package org.example.project.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import org.example.project.R

@Composable
actual fun branchBottomLeftPainter() : Painter {
    return painterResource(R.drawable.branch_bottom_left)
}