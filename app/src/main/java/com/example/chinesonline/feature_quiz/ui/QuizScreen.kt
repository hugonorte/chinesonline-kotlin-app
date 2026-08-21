package com.example.chinesonline.feature_quiz.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chinesonline.core.ui.theme.*
import androidx.compose.ui.platform.LocalView
import android.app.Activity
import androidx.core.view.WindowCompat
import androidx.compose.ui.graphics.toArgb
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.alpha
import com.example.chinesonline.R

import com.example.chinesonline.ChinesOnlineApplication
import com.example.chinesonline.feature_quiz.data.QuestionResponse
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val appContainer = (context.applicationContext as ChinesOnlineApplication).container
    val viewModel: QuizViewModel = viewModel(
        factory = QuizViewModel.provideFactory(
            appContainer.quizRepository,
            appContainer.userPreferencesRepository
        )
    )
    
    val uiState by viewModel.uiState.collectAsState()
    val feedbackState by viewModel.feedbackState.collectAsState()
    val currentXp by viewModel.currentXp.collectAsState()
    val currentScore by viewModel.currentScore.collectAsState()
    val currentLevel by viewModel.currentLevel.collectAsState()
    val levelUp by viewModel.levelUp.collectAsState()

    val storedName by viewModel.userName.collectAsState()
    val displayName = storedName?.takeIf { it.isNotBlank() } ?: "Player"

    val view = LocalView.current
    if (!view.isInEditMode) {
        DisposableEffect(Unit) {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Black.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            onDispose {}
        }
    }

    val tts = remember(context) {
        var ttsInstance: android.speech.tts.TextToSpeech? = null
        ttsInstance = android.speech.tts.TextToSpeech(context) { status ->
            if (status == android.speech.tts.TextToSpeech.SUCCESS) {
                ttsInstance?.language = java.util.Locale.CHINESE
            }
        }
        ttsInstance!!
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    LaunchedEffect(feedbackState) {
        if (feedbackState == FeedbackState.CORRECT) {
            val mp = android.media.MediaPlayer.create(context, com.example.chinesonline.R.raw.som_acerto)
            mp?.start()
            mp?.setOnCompletionListener { it.release() }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.startGame()
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
        containerColor = QuizBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                QuizState.LOADING -> LoadingState()
                QuizState.GAMEPLAY -> {
                    val question by viewModel.currentQuestion.collectAsState()
                    question?.let { q ->
                        GameplayState(
                            xp = currentXp,
                            score = currentScore,
                            level = currentLevel,
                            userName = displayName,
                            question = q,
                            feedbackState = feedbackState,
                            pointsPerCorrectAnswer = viewModel.pointsPerCorrectAnswer,
                            onSubmitAnswer = { viewModel.submitAnswer(it) },
                            onSpeakRequest = {
                                tts.speak(q.character, android.speech.tts.TextToSpeech.QUEUE_FLUSH, null, null)
                            }
                        )
                    }
                }
                QuizState.END_GAME -> EndGameState(
                    levelUp = levelUp,
                    level = currentLevel,
                    xpGained = currentScore,
                    onNewRound = { viewModel.startGame() }
                )
                QuizState.ERROR -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                    Text(stringResource(id = R.string.game_load_error), color = Color.White)
                }
            }
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameplayState(
    xp: Int,
    score: Int,
    level: Int,
    userName: String,
    question: QuestionResponse,
    feedbackState: FeedbackState,
    pointsPerCorrectAnswer: Int,
    onSubmitAnswer: (String) -> Unit,
    onSpeakRequest: () -> Unit
) {
    var answerText by remember { mutableStateOf("") }
    
    // Clear answer text when round resets
    LaunchedEffect(feedbackState) {
        if (feedbackState == FeedbackState.NONE) {
            answerText = ""
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Sessão de Pontuação (Header Superior)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Lado Esquerdo (Player Info)
            Column {
                Text(
                    text = userName,
                    fontFamily = VendSansFontFamily,
                    color = TextXpValue,
                    fontWeight = FontWeight.W400,
                    fontSize = 16.sp
                )
                Text(
                    text = stringResource(id = R.string.xp_accumulated_label),
                    fontFamily = SansationFontFamily,
                    color = TextXpLabel,
                    fontWeight = FontWeight.W300,
                    fontSize = 10.sp
                )
                Text(
                    text = "$xp",
                    fontFamily = VendSansFontFamily,
                    color = TextXpValue,
                    fontWeight = FontWeight.W500,
                    fontSize = 18.sp
                )
            }
            
            // Lado Direito (Score e Nível)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(id = R.string.score_label),
                        fontFamily = SansationFontFamily,
                        color = TextScoreLabel,
                        fontWeight = FontWeight.W300,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "$score",
                        fontFamily = VendSansFontFamily,
                        color = TextScoreValue,
                        fontWeight = FontWeight.W600,
                        fontSize = 40.sp
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = stringResource(id = R.string.level_label),
                        fontFamily = SansationFontFamily,
                        color = TextScoreLabel,
                        fontWeight = FontWeight.W300,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "$level",
                        fontFamily = VendSansFontFamily,
                        color = TextScoreValue,
                        fontWeight = FontWeight.W600,
                        fontSize = 40.sp
                    )
                }
            }
        }

        // Título da Pergunta
        Text(
            text = stringResource(id = R.string.question_title),
            fontFamily = SansationFontFamily,
            color = Color.White.copy(alpha = 0.7f),
            fontWeight = FontWeight.W400,
            fontSize = 14.sp,
            modifier = Modifier.padding(top = 16.dp, start = 16.dp, end = 16.dp)
        )

        // Card do Ideograma Central
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .height(160.dp)
                .shadow(
                    elevation = 8.dp, // Simula o offset Y=4 blur 4
                    shape = RoundedCornerShape(12.dp),
                    ambientColor = Color.Black.copy(alpha = 0.26f),
                    spotColor = Color.Black.copy(alpha = 0.26f)
                )
                .background(Color.White, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = question.character,
                fontSize = 80.sp,
                color = Color.Black
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))

        // Barra de Input (Resposta do Usuário)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(56.dp)
        ) {
            TextField(
                value = answerText,
                onValueChange = { answerText = it },
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.pinyin_placeholder),
                        fontFamily = VendSansFontFamily,
                        color = Color.Black.copy(alpha = 0.38f)
                    )
                },
                enabled = feedbackState == FeedbackState.NONE,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    disabledTextColor = Color.Black,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
            
            Button(
                onClick = { onSubmitAnswer(answerText) },
                enabled = feedbackState == FeedbackState.NONE,
                shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = QuizSubmitBlue,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.fillMaxHeight()
            ) {
                Text(
                    text = stringResource(id = R.string.submit_button),
                    fontFamily = VendSansFontFamily,
                    fontWeight = FontWeight.W400,
                    fontSize = 18.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))

        // Card Dinâmico de Feedback
        if (feedbackState != FeedbackState.NONE) {
            val isCorrect = feedbackState == FeedbackState.CORRECT
            val cardColor = if (isCorrect) QuizCorrectCard else QuizWrongCard
            val titleText = if (isCorrect) stringResource(id = R.string.correct_feedback) else stringResource(id = R.string.incorrect_feedback)
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isCorrect) {
                    // Chip de pontos
                    Box(
                        modifier = Modifier
                            .border(1.5.dp, QuizCorrectChip, RoundedCornerShape(20.dp))
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = stringResource(id = R.string.points_earned, pointsPerCorrectAnswer),
                            color = QuizCorrectChip,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Card de Detalhes
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, Color.Black, RoundedCornerShape(8.dp))
                        .background(cardColor, RoundedCornerShape(8.dp))
                        .padding(16.dp)
                ) {
                    IconButton(
                        onClick = onSpeakRequest,
                        modifier = Modifier.align(Alignment.TopEnd)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = stringResource(id = R.string.listen_content_desc),
                            tint = Color.Black
                        )
                    }
                    
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = titleText,
                            fontFamily = VendSansFontFamily,
                            fontWeight = FontWeight.W600,
                            fontSize = 20.sp,
                            color = Color.Black.copy(alpha = 0.87f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = question.character,
                            fontSize = 28.sp,
                            color = Color.Black.copy(alpha = 0.87f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = question.pinyin,
                            fontFamily = VendSansFontFamily,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.W600,
                            fontSize = 20.sp,
                            color = Color.Black.copy(alpha = 0.87f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = question.translation,
                            fontFamily = VendSansFontFamily,
                            fontStyle = FontStyle.Italic,
                            fontWeight = FontWeight.W300,
                            fontSize = 14.sp,
                            color = Color.Black.copy(alpha = 0.87f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EndGameState(
    levelUp: Boolean,
    level: Int,
    xpGained: Int,
    onNewRound: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(
                        SplashGradientStart,
                        SplashGradientCenter,
                        SplashGradientEnd
                    )
                )
            )
            .padding(10.dp)
    ) {
        Image(
            painter = painterResource(id = R.drawable.corner_inset),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopStart)
        )
        Image(
            painter = painterResource(id = R.drawable.corner_inset),
            contentDescription = null,
            modifier = Modifier.align(Alignment.TopEnd).rotate(90f)
        )
        Image(
            painter = painterResource(id = R.drawable.corner_inset),
            contentDescription = null,
            modifier = Modifier.align(Alignment.BottomEnd).rotate(180f)
        )
        Image(
            painter = painterResource(id = R.drawable.corner_inset),
            contentDescription = null,
            modifier = Modifier.align(Alignment.BottomStart).rotate(270f)
        )

        Image(
            painter = painterResource(id = R.drawable.dragon_circle),
            contentDescription = null,
            modifier = Modifier.align(Alignment.Center).alpha(0.15f)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
        if (levelUp) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Level Up",
                tint = Color(0xFFFFC107),
                modifier = Modifier
                    .size(64.dp)
                    .padding(bottom = 16.dp)
            )
            Text(
                text = stringResource(id = R.string.level_up_message, level),
                fontFamily = VendSansFontFamily,
                color = Color(0xFFFFD54F),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 32.dp)
            )
        } else {
            Text(
                text = stringResource(id = R.string.round_finished_title),
                color = Color.White,
                fontSize = 24.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = stringResource(id = R.string.xp_gained_message, xpGained),
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 18.sp,
                modifier = Modifier.padding(bottom = 24.dp)
            )
        }

        Button(
            onClick = onNewRound,
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = QuizOrangeButton,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.new_round_button),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
    }
}
