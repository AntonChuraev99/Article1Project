package com.swapface.article1project

import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

const val NextScreenRoute = "next_screen"

@Composable
fun TestNextScreen() {
    ReportDrawnWhen {
        TODO()
    }

    Column {
        Text(
            "Next Screen",
            modifier = Modifier.testTag("next_screen")
        )
    }

}
