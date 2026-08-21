package com.example.chinesonline.feature_home.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import android.app.Activity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chinesonline.R
import com.example.chinesonline.core.ui.theme.LobsterFontFamily
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chinesonline.ChinesOnlineApplication
import com.example.chinesonline.feature_home.ui.HomeViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToQuiz: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val appContainer = (context.applicationContext as ChinesOnlineApplication).container
    val viewModel: HomeViewModel = viewModel(
        factory = HomeViewModel.provideFactory(appContainer.userPreferencesRepository)
    )
    
    val userLevel by viewModel.userLevel.collectAsState()
    val userPoints by viewModel.userXp.collectAsState()
    val storedName by viewModel.userName.collectAsState()
    val displayName = storedName?.takeIf { it.isNotBlank() } ?: "Player"

    val view = LocalView.current
    if (!view.isInEditMode) {
        DisposableEffect(view) {
            val window = (view.context as Activity).window
            
            // Fundo preto
            window.statusBarColor = android.graphics.Color.BLACK
            // Ícones brancos
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false

            onDispose {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontFamily = LobsterFontFamily,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black
                ),
                actions = {
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = stringResource(id = R.string.exit_content_desc), tint = Color.White)
                    }
                }
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Imagem de fundo customizada
            Image(
                painter = painterResource(id = R.drawable.homebg),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Camada escurecida para ajudar na legibilidade (opcional, pode ser ajustado)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
            
            // Cabeçalho com Bem-vindo e Nome do Usuário
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 48.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.welcome_message),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )
                Text(
                    text = displayName,
                    style = MaterialTheme.typography.displaySmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }

            // Cartão com Nível e Pontos Acumulados
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Nível
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(id = R.string.level_label),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = userLevel.toString(),
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    // Pontos
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = stringResource(id = R.string.xp_accumulated_label),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = userPoints.toString(),
                            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }

            // Botão de iniciar jogo
            Button(
                onClick = onNavigateToQuiz,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.start_game_button),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            val message = androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf("") }
            val appConfig = com.example.chinesonline.core.config.LocalAppConfig.current
            
            if (message.value.isNotEmpty()) {
                Text(
                    text = message.value, 
                    color = Color.White, 
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp).testTag("status_message")
                )
            }

            Button(
                onClick = {
                    if (appConfig.subscriptionStatus().hasAdvancedAccess()) {
                        message.value = "Abrindo configurações avançadas..."
                    } else {
                        message.value = "Funcionalidade exclusiva da versão Premium"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(28.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Text(
                    text = "Configurações Avançadas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        }
    }
}
