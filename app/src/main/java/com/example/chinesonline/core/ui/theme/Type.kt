package com.example.chinesonline.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.text.googlefonts.Font
import com.example.chinesonline.R

val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val LobsterFontFamily = FontFamily(
    androidx.compose.ui.text.font.Font(R.font.lobster)
)

val OffsideFontFamily = FontFamily(
    androidx.compose.ui.text.font.Font(R.font.offside_regular)
)

// Nota: "Vend Sans" e "Sansation" não estão nativamente na lista standard do Google Fonts Provider para todos os dispositivos.
// Usaremos fallback temporário do Google Fonts com nomes similares, e a fonte final poderá ser baixada no formato TTF.
// Substituindo Vend Sans por Varela Round e Sansation por pt sans como fallback até o carregamento local.
val VendSansFont = GoogleFont("Varela Round")
val VendSansFontFamily = FontFamily(
    Font(googleFont = VendSansFont, fontProvider = provider)
)

val SansationFont = GoogleFont("PT Sans")
val SansationFontFamily = FontFamily(
    Font(googleFont = SansationFont, fontProvider = provider)
)

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = LobsterFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 48.sp,
        lineHeight = 56.sp,
        letterSpacing = 0.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = VendSansFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    labelMedium = TextStyle(
        fontFamily = SansationFontFamily,
        fontWeight = FontWeight.Light,
        fontSize = 10.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
