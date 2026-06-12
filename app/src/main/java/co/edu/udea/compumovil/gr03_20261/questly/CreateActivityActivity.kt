package co.edu.udea.compumovil.gr03_20261.questly

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.view.WindowCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import co.edu.udea.compumovil.gr03_20261.questly.ui.theme.QuestlyTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateActivityActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val isEdit = intent.getBooleanExtra("IS_EDIT", false)
        val editHabitId = intent.getLongExtra("HABIT_ID", -1L)
        val editTitle = intent.getStringExtra("HABIT_TITLE") ?: ""
        val editTime = intent.getStringExtra("HABIT_TIME") ?: "07:00 AM"
        val editColorInt = intent.getIntExtra("HABIT_COLOR", Color.Gray.toArgb())
        val editIconName = intent.getStringExtra("HABIT_ICON_NAME") ?: "Add"
        val editQuests = intent.getStringArrayListExtra("HABIT_QUESTS") ?: arrayListOf()
        val existingTimes = intent.getStringArrayListExtra("EXISTING_TIMES") ?: arrayListOf()

        setContent {
            QuestlyTheme {
                var showDetail by remember { mutableStateOf(isEdit) }
                var selectedTitle by remember { mutableStateOf(editTitle) }
                var selectedColor by remember { mutableStateOf(Color(editColorInt)) }
                var selectedIconName by remember { mutableStateOf(editIconName) }
                var initialQuests by remember { mutableStateOf(editQuests.toList()) }
                var initialTime by remember { mutableStateOf(editTime) }

                val selectedIcon = when(selectedIconName) {
                    "WbSunny" -> Icons.Default.WbSunny
                    "NightsStay" -> Icons.Default.NightsStay
                    "FitnessCenter" -> Icons.Default.FitnessCenter
                    "Book" -> Icons.Default.Book
                    "WaterDrop" -> Icons.Default.WaterDrop
                    "Restaurant" -> Icons.Default.Restaurant
                    else -> Icons.Default.Add
                }

                if (!showDetail) {
                    CreateActivityListScreen(
                        onBack = { finish() },
                        onSelectActivity = { title, color, _, iconName ->
                            selectedTitle = title
                            selectedColor = color
                            selectedIconName = iconName
                            initialQuests = emptyList()
                            initialTime = "07:00 AM"
                            showDetail = true
                        }
                    )
                } else {
                    EditActivityDetailScreen(
                        title = selectedTitle,
                        color = selectedColor,
                        icon = selectedIcon,
                        iconName = selectedIconName,
                        habitId = editHabitId,
                        initialQuests = initialQuests,
                        initialTime = initialTime,
                        existingTimes = existingTimes,
                        onBack = { if (isEdit) finish() else showDetail = false },
                        onSave = { habit ->
                            val resultIntent = Intent().apply {
                                putExtra("HABIT_ID", if (isEdit) editHabitId else habit.id)
                                putExtra("HABIT_TITLE", habit.title)
                                putExtra("HABIT_TIME", habit.time)
                                putExtra("HABIT_COLOR", habit.colorValue.toInt())
                                putExtra("HABIT_ICON_NAME", habit.iconName)
                                putStringArrayListExtra("HABIT_QUESTS", ArrayList(habit.quests))
                            }
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        },
                        onDelete = { id ->
                            val resultIntent = Intent().apply {
                                putExtra("HABIT_ID", id)
                                putExtra("HABIT_DELETED", true)
                            }
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }
                    )
                }
            }
        }
    }
}

fun normalizeTime(time: String): String {
    val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
    val sdf12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return try {
        val t = time.uppercase().trim()
        val date = if (t.contains("AM") || t.contains("PM")) {
            sdf12.parse(t)
        } else {
            sdf24.parse(t)
        }
        sdf24.format(date!!)
    } catch (e: Exception) {
        time.uppercase().trim()
    }
}

fun parseTime(timeStr: String): Pair<Int, Int> {
    return try {
        val t = timeStr.uppercase().trim()
        val date = if (t.contains("AM") || t.contains("PM")) {
            SimpleDateFormat("hh:mm a", Locale.getDefault()).parse(t)
        } else {
            SimpleDateFormat("HH:mm", Locale.getDefault()).parse(t)
        }
        val cal = Calendar.getInstance().apply { time = date!! }
        cal.get(Calendar.HOUR_OF_DAY) to cal.get(Calendar.MINUTE)
    } catch (e: Exception) {
        7 to 0
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestlyTimePicker(
    initialTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val (initialHour, initialMinute) = parseTime(initialTime)
    val timePickerState = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = false
    )

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier.padding(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Set Time",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.align(Alignment.Start).padding(bottom = 20.dp)
                )
                TimePicker(state = timePickerState)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    TextButton(onClick = {
                        val cal = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                        }
                        onTimeSelected(SimpleDateFormat("hh:mm a", Locale.getDefault()).format(cal.time))
                        onDismiss()
                    }) { Text("Confirm") }
                }
            }
        }
    }
}

@Composable
fun CreateActivityListScreen(onBack: () -> Unit, onSelectActivity: (String, Color, ImageVector, String) -> Unit) {
    val backgroundColor = Color(0xFFE8F5E9)
    val focusRequester = remember { FocusRequester() }
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        delay(300) 
        focusRequester.requestFocus()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Create Activity", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(32.dp))
                }
            }
            
            Spacer(Modifier.height(16.dp))
            Text("What?", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(48.dp).background(Color.White, CircleShape).border(1.dp, Color.LightGray, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
                Spacer(Modifier.width(12.dp))
                TextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search activity...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent, 
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color(0xFF4CAF50)
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    singleLine = true
                )
            }
            
            Spacer(Modifier.height(24.dp))
            Text("Suggested", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            
            Spacer(Modifier.height(12.dp))
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                item { CreateHabitRow("Gym", "Exercise", Color(0xFFCE93D8), Icons.Default.FitnessCenter) { onSelectActivity("Gym", Color(0xFFCE93D8), Icons.Default.FitnessCenter, "FitnessCenter") } }
                item { CreateHabitRow("Study", "Homework", Color(0xFF81C784), Icons.Default.Book) { onSelectActivity("Study", Color(0xFF81C784), Icons.Default.Book, "Book") } }
                item { CreateHabitRow("Hydrate!", "Water", Color(0xFF81D4FA), Icons.Default.WaterDrop) { onSelectActivity("Hydrate!", Color(0xFF81D4FA), Icons.Default.WaterDrop, "WaterDrop") } }
                item { CreateHabitRow("Breakfast", "Meal 1", Color(0xFFFFF176), Icons.Default.Restaurant) { onSelectActivity("Breakfast", Color(0xFFFFF176), Icons.Default.Restaurant, "Restaurant") } }
                item { CreateHabitRow("Meditation", "Mental Health", Color(0xFFA5D6A7), Icons.Default.SelfImprovement) { onSelectActivity("Meditation", Color(0xFFA5D6A7), Icons.Default.SelfImprovement, "SelfImprovement") } }
            }
        }
    }
}

@Composable
fun EditActivityDetailScreen(
    title: String, 
    color: Color, 
    icon: ImageVector, 
    iconName: String, 
    habitId: Long = -1L,
    initialQuests: List<String> = emptyList(),
    initialTime: String = "07:00 AM",
    existingTimes: List<String> = emptyList(),
    onBack: () -> Unit, 
    onSave: (Habit) -> Unit,
    onDelete: (Long) -> Unit = {}
) {
    val backgroundColor = Color(0xFFE8F5E9)
    var questText by remember { mutableStateOf("") }
    val quests = remember { mutableStateListOf<String>().apply { addAll(initialQuests) } }
    var time by remember { mutableStateOf(initialTime) }
    var showTimePicker by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val focusRequester = remember { FocusRequester() }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val isDefaultHabit = habitId == 1L || habitId == 2L || title == "Buenos Días" || title == "Buenas Noches"

    if (showTimePicker) {
        QuestlyTimePicker(
            initialTime = time,
            onTimeSelected = { time = it },
            onDismiss = { showTimePicker = false }
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize().imePadding(),
        containerColor = backgroundColor,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(if (habitId != -1L) "Edit Activity" else "Add Activity", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(64.dp).background(color, RoundedCornerShape(16.dp)).border(2.dp, Color.Black, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, null, modifier = Modifier.size(36.dp), tint = Color.Black)
                }
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(title, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text("Habit(s)", fontSize = 18.sp, color = Color.Gray)
                }
            }

            Spacer(Modifier.height(32.dp))

            Text("Quests (Farm Points!)", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .border(2.dp, Color.Black, RoundedCornerShape(20.dp))
                    .padding(12.dp)
            ) {
                quests.forEachIndexed { index, quest ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.RadioButtonUnchecked, null, tint = Color.Gray)
                        Spacer(Modifier.width(8.dp))
                        Text(quest, fontSize = 18.sp, modifier = Modifier.weight(1f))
                        IconButton(onClick = { quests.removeAt(index) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Eliminar sub-tarea", tint = Color.Red.copy(alpha = 0.7f))
                        }
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = questText,
                        onValueChange = { questText = it },
                        placeholder = { Text("Nueva sub-misión...") },
                        modifier = Modifier.weight(1f).focusRequester(focusRequester),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent, 
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color(0xFF4CAF50)
                        ),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Sentences,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (questText.isNotBlank()) {
                                    quests.add(questText)
                                    questText = ""
                                }
                            }
                        ),
                        singleLine = true
                    )
                    IconButton(onClick = { if (questText.isNotBlank()) { quests.add(questText); questText = "" } }) {
                        Icon(Icons.Default.AddCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            
            Text("When?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(Color.White, RoundedCornerShape(12.dp))
                    .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                    .clickable { showTimePicker = true }
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, null, tint = Color.Gray)
                    Spacer(Modifier.width(12.dp))
                    Text(time, fontSize = 18.sp)
                }
            }

            Spacer(Modifier.height(40.dp))
            
            Button(
                onClick = { 
                    val normalizedNew = normalizeTime(time)
                    val isDuplicate = existingTimes.any { normalizeTime(it) == normalizedNew }
                    
                    if (isDuplicate) {
                        scope.launch {
                            snackbarHostState.showSnackbar("Error: Ya tienes una actividad a las $time. ¡Elige otro minuto!")
                        }
                    } else {
                        onSave(Habit(id = if (habitId != -1L) habitId else System.currentTimeMillis(), title = title, time = time, iconName = iconName, colorValue = color.toArgb().toLong(), quests = quests.toList()))
                    }
                },
                modifier = Modifier.fillMaxWidth().height(60.dp).border(2.dp, Color.Black, RoundedCornerShape(30.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = color),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text(if (habitId != -1L) "Update Activity" else "Save Activity", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
            
            if (habitId != -1L && !isDefaultHabit) {
                Spacer(Modifier.height(16.dp))
                OutlinedButton(
                    onClick = { onDelete(habitId) },
                    modifier = Modifier.fillMaxWidth().height(60.dp).border(2.dp, Color.Red, RoundedCornerShape(30.dp)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
                    shape = RoundedCornerShape(30.dp)
                ) {
                    Text("Eliminar Actividad", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun CreateHabitRow(title: String, category: String, color: Color, icon: ImageVector, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(75.dp).border(2.dp, Color.Black, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(44.dp).background(Color.White.copy(alpha = 0.4f), CircleShape).border(1.dp, Color.Black, CircleShape), contentAlignment = Alignment.Center) {
                Icon(icon, null, modifier = Modifier.size(24.dp), tint = Color.Black)
            }
            Spacer(Modifier.width(16.dp))
            Column {
                Text(title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                Text("Habit(s) - $category", fontSize = 16.sp, color = Color.DarkGray)
            }
        }
    }
}
