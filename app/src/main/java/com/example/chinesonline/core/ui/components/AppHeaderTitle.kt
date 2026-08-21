package com.example.chinesonline.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chinesonline.BuildConfig
import com.example.chinesonline.R
import com.example.chinesonline.core.ui.theme.LobsterFontFamily
import com.example.chinesonline.core.ui.theme.OffsideFontFamily

@Composable
fun AppHeaderTitle() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = stringResource(id = R.string.app_name),
            fontFamily = LobsterFontFamily,
            color = Color.White
        )
        if (BuildConfig.FLAVOR == "lite") {
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .background(
                        color = Color(0xFF3E64A0),
                        shape = RoundedCornerShape(50)
                    )
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Lite",
                    color = Color.White,
                    fontFamily = OffsideFontFamily,
                    fontSize = 11.sp
                )
            }
        }
    }
}
