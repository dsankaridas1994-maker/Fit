package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.OrangeAccent

@Composable
fun OnboardingDialog(
    onComplete: (name: String, goal: Int) -> Unit
) {
    var nameInput by remember { mutableStateOf("") }
    var goalInput by remember { mutableStateOf("20") }
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = { /* Non-cancellable on first launch */ },
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .navigationBarsPadding()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, OrangeAccent.copy(alpha = 0.3f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_dialog")
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🔥",
                        fontSize = 44.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Welcome to INFFIT",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = "High-energy training engineered for real progress.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
                    )

                    // Name Input
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { if (it.length <= 25) nameInput = it },
                        label = { Text("Athlete Name / Nickname") },
                        singleLine = true,
                        placeholder = { Text("e.g. Alex") },
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_name_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangeAccent,
                            focusedLabelColor = OrangeAccent
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Goal Input
                    OutlinedTextField(
                        value = goalInput,
                        onValueChange = { goalInput = it.filter { char -> char.isDigit() }.take(3) },
                        label = { Text("Daily Active Goal (Minutes)") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                focusManager.clearFocus()
                            }
                        ),
                        placeholder = { Text("20") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboarding_goal_input"),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = OrangeAccent,
                            focusedLabelColor = OrangeAccent
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            val parsedGoal = goalInput.toIntOrNull() ?: 20
                            onComplete(nameInput.trim(), parsedGoal)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("onboarding_enter_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = OrangeAccent),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "Enter The Arena 🚀",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}
