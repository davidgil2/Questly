package co.edu.udea.compumovil.gr03_20261.questly

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import co.edu.udea.compumovil.gr03_20261.questly.ui.theme.QuestlyTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuestlyTheme {
                OnboardingFlow(onFinished = { name, characterClass, wakeTime, sleepTime ->
                    PlayerStats.initialize(name, characterClass, wakeTime, sleepTime)
                    PlayerStats.save(this)
                    PersistenceManager.setOnboardingDone(this, true)

                    val intent = Intent(this, HabitTrackerActivity::class.java)
                    startActivity(intent)
                    finish()
                })
            }
        }
    }
}

data class CharacterOption(val name: String, val icon: ImageVector, val description: String, val color: Color)

@Composable
fun OnboardingFlow(onFinished: (String, String, String, String) -> Unit) {
    var step by remember { mutableStateOf(1) }
    var wakeTime by remember { mutableStateOf("07:00 AM") }
    var sleepTime by remember { mutableStateOf("10:00 PM") }
    var selectedSex by remember { mutableStateOf("") }
    var userName by remember { mutableStateOf("") }
    var selectedClass by remember { mutableStateOf<CharacterOption?>(null) }
    val scrollState = rememberScrollState()

    val backgroundColor = Color(0xFFF3D9C9)

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = backgroundColor
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            LeafDecoration(Modifier.align(Alignment.TopEnd).padding(top = 20.dp))
            if (step >= 3) {
                LeafDecoration(Modifier.align(Alignment.BottomStart).padding(bottom = 100.dp), rotated = true)
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 30.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Spacer(Modifier.height(40.dp))
                when (step) {
                    1 -> TimeSetupScreen(
                        wakeTime = wakeTime,
                        sleepTime = sleepTime,
                        onWakeChange = { wakeTime = it },
                        onSleepChange = { sleepTime = it }
                    )
                    2 -> SexSetupScreen(
                        selectedSex = selectedSex,
                        onSexSelected = { selectedSex = it }
                    )
                    3 -> NameSetupScreen(
                        name = userName,
                        onNameChange = { userName = it },
                        onNext = { if (userName.isNotBlank()) step++ }
                    )
                    4 -> ClassSelectionScreen(
                        selectedClass = selectedClass,
                        onClassSelected = { selectedClass = it }
                    )
                }
                Spacer(Modifier.height(140.dp))
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 60.dp)
                    .size(80.dp)
                    .background(Color(0xFF333333), CircleShape)
                    .clickable {
                        if (step < 4) {
                            if (step == 3 && userName.isBlank()) return@clickable
                            step++
                        } else {
                            if (selectedClass != null) {
                                onFinished(userName, selectedClass!!.name, wakeTime, sleepTime)
                            }
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Next",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeSetupScreen(wakeTime: String, sleepTime: String, onWakeChange: (String) -> Unit, onSleepChange: (String) -> Unit) {
    var showWakePicker by remember { mutableStateOf(false) }
    var showSleepPicker by remember { mutableStateOf(false) }

    Text(
        text = "What time do you wake up and go to sleep?",
        fontSize = 28.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(0xFF333333)
    )

    Spacer(modifier = Modifier.height(60.dp))
    
    if (showWakePicker) {
        OnboardingTimePicker(
            initialTime = wakeTime,
            onTimeSelected = onWakeChange,
            onDismiss = { showWakePicker = false }
        )
    }

    if (showSleepPicker) {
        OnboardingTimePicker(
            initialTime = sleepTime,
            onTimeSelected = onSleepChange,
            onDismiss = { showSleepPicker = false }
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TimeItem("Good Morning", wakeTime, Icons.Default.WbCloudy) { showWakePicker = true }
        TimeItem("Good Night", sleepTime, Icons.Default.NightsStay) { showSleepPicker = true }
    }

    Spacer(modifier = Modifier.height(60.dp))

    Text(
        text = "Remember, you need 7-8 hours of sleep a night!",
        fontSize = 16.sp,
        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
        textAlign = TextAlign.Center,
        color = Color.Gray
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingTimePicker(initialTime: String, onTimeSelected: (String) -> Unit, onDismiss: () -> Unit) {
    val sdf12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
    val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
    
    val date = try {
        if (initialTime.contains("AM") || initialTime.contains("PM")) sdf12.parse(initialTime)
        else sdf24.parse(initialTime)
    } catch (e: Exception) { null }

    val cal = Calendar.getInstance().apply { if (date != null) time = date }
    val state = rememberTimePickerState(
        initialHour = cal.get(Calendar.HOUR_OF_DAY),
        initialMinute = cal.get(Calendar.MINUTE),
        is24Hour = false
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(28.dp), color = Color.White) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Select Time", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 20.dp))
                TimePicker(state = state)
                Row(modifier = Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = {
                        val resultCal = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, state.hour)
                            set(Calendar.MINUTE, state.minute)
                        }
                        onTimeSelected(sdf12.format(resultCal.time))
                        onDismiss()
                    }) { Text("Confirm") }
                }
            }
        }
    }
}

@Composable
fun TimeItem(label: String, time: String, icon: ImageVector, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .border(1.dp, Color.White, RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Icon(icon, null, modifier = Modifier.size(48.dp), tint = Color(0xFFFFD54F))
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(time, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
    }
}

@Composable
fun SexSetupScreen(selectedSex: String, onSexSelected: (String) -> Unit) {
    Text(
        text = "Choose your sex",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF333333)
    )

    Spacer(modifier = Modifier.height(40.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SexCard("Male", Icons.Default.Face, selectedSex == "Male", Modifier.weight(1f)) { onSexSelected("Male") }
        SexCard("Female", Icons.Default.Face3, selectedSex == "Female", Modifier.weight(1f)) { onSexSelected("Female") }
    }
}

@Composable
fun SexCard(label: String, icon: ImageVector, isSelected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val bgColor = if (isSelected) Color(0xFFAED581).copy(alpha = 0.4f) else Color(0xFFFFE0B2).copy(alpha = 0.4f)
    val textColor = if (isSelected) Color(0xFF4CAF50) else Color(0xFF333333)

    Column(
        modifier = modifier
            .height(200.dp)
            .background(bgColor, RoundedCornerShape(100.dp))
            .clickable { onClick() }
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(80.dp).background(if (isSelected) Color(0xFFAED581) else Color.White, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, modifier = Modifier.size(50.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(label, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = textColor)
    }
}

@Composable
fun NameSetupScreen(name: String, onNameChange: (String) -> Unit, onNext: () -> Unit) {
    Text(
        text = "What's your\nname?",
        fontSize = 36.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        color = Color(0xFF333333)
    )

    Spacer(modifier = Modifier.height(40.dp))

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(Unit) {
        delay(300)
        focusRequester.requestFocus()
    }

    TextField(
        value = name,
        onValueChange = onNameChange,
        placeholder = { Text("Dennis, Frank, Mac...") },
        modifier = Modifier
            .fillMaxWidth()
            .focusRequester(focusRequester)
            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            focusedIndicatorColor = Color(0xFF81B692),
            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
        ),
        keyboardActions = KeyboardActions(
            onNext = { onNext() }
        ),
        singleLine = true
    )
}

@Composable
fun ClassSelectionScreen(selectedClass: CharacterOption?, onClassSelected: (CharacterOption) -> Unit) {
    val options = listOf(
        CharacterOption("Warrior", Icons.Default.Shield, "High strength and durability. Focuses on physical habits.", Color(0xFFEF9A9A)),
        CharacterOption("Mage", Icons.Default.AutoFixHigh, "Master of wisdom. Focuses on study and focus habits.", Color(0xFFCE93D8)),
        CharacterOption("Scout", Icons.Default.Explore, "Fast and agile. Focuses on hydration and movement.", Color(0xFF81D4FA))
    )

    Text(
        text = "Choose your Class",
        fontSize = 32.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF333333)
    )

    Spacer(modifier = Modifier.height(30.dp))

    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp)
    ) {
        items(options) { option ->
            val isSelected = selectedClass?.name == option.name
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onClassSelected(option) }
                    .border(2.dp, if (isSelected) Color.Black else Color.Transparent, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = option.color.copy(alpha = if (isSelected) 1f else 0.4f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(option.icon, null, modifier = Modifier.size(40.dp), tint = Color.Black)
                    Spacer(Modifier.width(16.dp))
                    Column {
                        Text(option.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(option.description, fontSize = 14.sp, color = Color.Black.copy(alpha = 0.7f))
                    }
                }
            }
        }
    }
}

@Composable
fun LeafDecoration(modifier: Modifier, rotated: Boolean = false) {
    Icon(
        imageVector = Icons.Default.Eco,
        contentDescription = null,
        tint = Color(0xFF81B692),
        modifier = modifier.size(150.dp).let { if (rotated) it.rotate(180f) else it }
    )
}
