package com.example.chinesonline.feature_auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import com.example.chinesonline.core.ui.theme.*
import com.example.chinesonline.core.domain.model.Country
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.res.stringResource
import com.example.chinesonline.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    viewModel: AuthViewModel = viewModel(),
    onNavigateToLogin: () -> Unit
) {
    val errorState by viewModel.loginState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val registerSuccess by viewModel.registerSuccess.collectAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var birthDateApi by remember { mutableStateOf("") } // RFC-3339
    var birthDateDisplay by remember { mutableStateOf("") } // DD/MM/YYYY
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    // Country Dropdown state
    var expanded by remember { mutableStateOf(false) }
    val countries = Country.values()
    var selectedCountry by remember { mutableStateOf(Country.BRASIL) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(errorState) {
        errorState?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearState()
        }
    }

    LaunchedEffect(registerSuccess) {
        if (registerSuccess) {
            delay(3500) // Aguarda 3.5 segundos para o usuário ler a mensagem longa
            onNavigateToLogin()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { padding ->
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
                .padding(padding)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.displayLarge,
                    fontFamily = LobsterFontFamily,
                    color = Color.White
                )
                Text(
                    text = stringResource(id = R.string.register_button),
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 32.dp, top = 8.dp)
                )

                val textFieldColors = TextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.White,
                    unfocusedIndicatorColor = Color.White
                )

                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(stringResource(id = R.string.name_label), color = Color.White) },
                    colors = textFieldColors,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                TextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(stringResource(id = R.string.email_label), color = Color.White) },
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                showDatePicker = false
                                datePickerState.selectedDateMillis?.let { millis ->
                                    val sdfApi = SimpleDateFormat("yyyy-MM-dd'T'00:00:00'Z'", Locale.getDefault())
                                    sdfApi.timeZone = java.util.TimeZone.getTimeZone("UTC")
                                    birthDateApi = sdfApi.format(Date(millis))

                                    val sdfDisplay = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                    birthDateDisplay = sdfDisplay.format(Date(millis))
                                }
                            }) { Text(stringResource(id = R.string.ok_button), color = Color(0xFF2A7FFF)) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) { Text(stringResource(id = R.string.cancel_button), color = Color(0xFF2A7FFF)) }
                        },
                        colors = DatePickerDefaults.colors(containerColor = Color.White)
                    ) {
                        DatePicker(state = datePickerState)
                    }
                }

                Box(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    TextField(
                        value = birthDateDisplay,
                        onValueChange = { },
                        readOnly = true,
                        label = { Text(stringResource(id = R.string.dob_label), color = Color.White) },
                        colors = textFieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clickable { showDatePicker = true }
                    )
                }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                ) {
                    TextField(
                        readOnly = true,
                        value = "${selectedCountry.flagEmoji} ${selectedCountry.nativeName}",
                        onValueChange = {},
                        label = { Text(stringResource(id = R.string.country_label), color = Color.White) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        colors = textFieldColors.copy(
                            focusedTrailingIconColor = Color.White,
                            unfocusedTrailingIconColor = Color.White
                        ),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color(0xFF450606))
                    ) {
                        countries.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text("${selectionOption.flagEmoji} ${selectionOption.nativeName}", color = Color.White) },
                                onClick = {
                                    selectedCountry = selectionOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                TextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(stringResource(id = R.string.password_label), color = Color.White) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text(stringResource(id = R.string.confirm_password_label), color = Color.White) },
                    visualTransformation = PasswordVisualTransformation(),
                    colors = textFieldColors,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
                )

                Button(
                    onClick = {
                        if (password == confirmPassword) {
                            viewModel.doRegister(name, email, password, selectedCountry.id, birthDateApi)
                        } else {
                            // Erro de senha
                        }
                    },
                    enabled = !isLoading && !registerSuccess,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2A7FFF),
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 48.dp, vertical = 12.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Text(stringResource(id = R.string.register_button), fontSize = 18.sp)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(id = R.string.has_account_text), color = Color.White, fontSize = 14.sp)
                    Text(
                        text = stringResource(id = R.string.login_link),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // Overlay de Sucesso
            if (registerSuccess) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 20.dp, start = 16.dp, end = 16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF388E3C)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 10.dp,
                                shape = RoundedCornerShape(12.dp),
                                spotColor = Color.Black.copy(alpha = 0.45f)
                            )
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.padding(20.dp).fillMaxWidth()
                        ) {
                            Text(stringResource(id = R.string.success_title), color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(stringResource(id = R.string.success_message), color = Color.White, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(stringResource(id = R.string.redirect_message), color = Color.White, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }
}
