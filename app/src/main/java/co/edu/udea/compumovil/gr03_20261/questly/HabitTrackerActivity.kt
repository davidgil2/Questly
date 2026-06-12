package co.edu.udea.compumovil.gr03_20261.questly

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import co.edu.udea.compumovil.gr03_20261.questly.ui.theme.QuestlyTheme
import java.text.SimpleDateFormat
import java.util.Locale

class HabitTrackerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        PlayerStats.load(this)

        setContent {
            QuestlyTheme {
                HabitTrackerScreen(
                    onNavigateToShop = {
                        val intent = Intent(this, ShopActivity::class.java)
                        startActivity(intent)
                    },
                    onNavigateToEvent = {
                        val intent = Intent(this, DailyEventActivity::class.java)
                        startActivity(intent)
                    },
                    onNavigateToProfile = {
                        val intent = Intent(this, ProfileActivity::class.java).apply {
                            putExtra("USER_NAME", PlayerStats.name)
                            putExtra("USER_CLASS", PlayerStats.userClass)
                        }
                        startActivity(intent)
                    }
                )
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // Recargar stats por si cambió el estado del evento diario
        PlayerStats.load(this)
    }
}

fun sortHabits(habits: MutableList<Habit>) {
    val sdf24 = SimpleDateFormat("HH:mm", Locale.getDefault())
    val sdf12 = SimpleDateFormat("hh:mm a", Locale.getDefault())
    
    habits.sortBy { habit ->
        try {
            val timeStr = habit.time.uppercase()
            if (timeStr.contains("AM") || timeStr.contains("PM")) {
                sdf12.parse(timeStr)?.time ?: 0L
            } else {
                sdf24.parse(timeStr)?.time ?: 0L
            }
        } catch (e: Exception) {
            0L
        }
    }
}

@Composable
fun HabitTrackerScreen(
    onNavigateToShop: () -> Unit,
    onNavigateToEvent: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val backgroundColor = Color(0xFFF1F8E9)
    
    val habits = remember {
        val loaded = PersistenceManager.loadHabits(context).toMutableList()
        val goldColor = Color(0xFFFFD600).toArgb().toLong()
        val nightColor = Color(0xFF1A237E).toArgb().toLong()

        if (loaded.isEmpty()) {
            mutableStateListOf(
                Habit(1, "Buenos Días", PlayerStats.wakeTime, "WbSunny", goldColor, listOf("Tiende la cama", "Bebe un vaso de agua")),
                Habit(2, "Buenas Noches", PlayerStats.sleepTime, "NightsStay", nightColor, listOf("Leer 10 páginas", "Planear mañana"))
            ).also { sortHabits(it) }
        } else {
            mutableStateListOf<Habit>().apply { addAll(loaded) }.also { sortHabits(it) }
        }
    }

    val createActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val isDeleted = data?.getBooleanExtra("HABIT_DELETED", false) ?: false
            val id = data?.getLongExtra("HABIT_ID", -1L) ?: -1L
            
            if (isDeleted) {
                if (id != -1L) {
                    habits.removeAll { it.id == id }
                    PersistenceManager.saveHabits(context, habits.toList())
                }
                return@rememberLauncherForActivityResult
            }

            val title = data?.getStringExtra("HABIT_TITLE") ?: ""
            val time = data?.getStringExtra("HABIT_TIME") ?: ""
            val colorLong = data?.getIntExtra("HABIT_COLOR", Color.Gray.toArgb())?.toLong() ?: Color.Gray.toArgb().toLong()
            val iconName = data?.getStringExtra("HABIT_ICON_NAME") ?: "Add"
            val quests = data?.getStringArrayListExtra("HABIT_QUESTS") ?: arrayListOf<String>()

            if (id != -1L) {
                val index = habits.indexOfFirst { it.id == id }
                if (index != -1) {
                    val updatedHabit = habits[index].copy(
                        title = title,
                        time = time,
                        colorValue = colorLong,
                        iconName = iconName,
                        quests = quests
                    )
                    habits[index] = updatedHabit
                    HabitNotificationManager.scheduleNotification(context, updatedHabit)
                }
            } else {
                val newHabit = Habit(
                    title = title,
                    time = time,
                    iconName = iconName,
                    colorValue = colorLong,
                    quests = quests
                )
                habits.add(newHabit)
                HabitNotificationManager.scheduleNotification(context, newHabit)
            }
            sortHabits(habits)
            PersistenceManager.saveHabits(context, habits.toList())
        }
    }

    LaunchedEffect(habits.size) {
        PersistenceManager.saveHabits(context, habits.toList())
    }

    val isEventDone = PlayerStats.isEventDoneToday()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(
                    onClick = onNavigateToEvent,
                    containerColor = if (isEventDone) Color.Gray else Color(0xFF2E7D32),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.border(2.dp, Color.White, RoundedCornerShape(16.dp)).size(64.dp)
                ) {
                    Icon(
                        imageVector = if (isEventDone) Icons.Default.EventAvailable else Icons.Default.AutoAwesome, 
                        contentDescription = "Daily Event", 
                        modifier = Modifier.size(32.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))

                FloatingActionButton(
                    onClick = onNavigateToShop,
                    containerColor = Color(0xFFFFD54F),
                    contentColor = Color.Black,
                    shape = CircleShape,
                    modifier = Modifier.border(2.dp, Color.Black, CircleShape)
                ) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Shop")
                }
            }
        }
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
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text("Today", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = Color(0xFF33691E))
                    val today = remember {
                        val cal = java.util.Calendar.getInstance()
                        val month = cal.getDisplayName(java.util.Calendar.MONTH, java.util.Calendar.LONG, java.util.Locale("es"))
                            ?.replaceFirstChar { it.uppercase() } ?: ""
                        val day = cal.get(java.util.Calendar.DAY_OF_MONTH)
                        "$month $day"
                    }
                    Text(today, fontSize = 28.sp, color = Color(0xFF558B2F))
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Button(
                        onClick = {
                            val intent = Intent(context, CreateActivityActivity::class.java).apply {
                                putStringArrayListExtra("EXISTING_TIMES", ArrayList(habits.map { it.time }))
                            }
                            createActivityLauncher.launch(intent)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFE082)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                    ) {
                        Text("Crear/Editar", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    Surface(
                        color = Color(0xFFFFD54F),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .border(1.dp, Color.Black, RoundedCornerShape(12.dp))
                            .clickable { onNavigateToProfile() }
                    ) {
                        Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Stars, null, modifier = Modifier.size(16.dp), tint = Color.Black)
                            Spacer(Modifier.width(4.dp))
                            Text("${PlayerStats.shopPoints}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(24.dp))

            val days = remember {
                val cal = java.util.Calendar.getInstance()
                val todayNum = cal.get(java.util.Calendar.DAY_OF_MONTH)
                val dayLetters = listOf("D", "L", "M", "M", "J", "V", "S")
                (-3..3).map { offset ->
                    val c = java.util.Calendar.getInstance()
                    c.add(java.util.Calendar.DAY_OF_YEAR, offset)
                    val letter = dayLetters[c.get(java.util.Calendar.DAY_OF_WEEK) - 1]
                    letter to c.get(java.util.Calendar.DAY_OF_MONTH).toString()
                }
            }
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(days) { (day, date) ->
                    val todayDay = remember { java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH).toString() }
                    DayCard(day, date, isSelected = date == todayDay)
                }
            }
            
            Spacer(Modifier.height(32.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(habits) { habit ->
                    HabitItem(
                        habit = habit,
                        onQuestCompleted = {questName ->
                            val index = habits.indexOfFirst { it.id == habit.id }
                            if (index != -1) {
                                val updated = habits[index].copy(
                                    completedQuests = habits[index].safeCompletedQuests + questName
                                )
                                habits[index] = updated
                                PlayerStats.shopPoints += 10
                                PlayerStats.save(context)
                                PersistenceManager.saveHabits(context, habits.toList())
                            }
                        },
                        onDoubleClick = {
                            val intent = Intent(context, CreateActivityActivity::class.java).apply {
                                putExtra("HABIT_ID", habit.id)
                                putExtra("HABIT_TITLE", habit.title)
                                putExtra("HABIT_TIME", habit.time)
                                putExtra("HABIT_COLOR", habit.colorValue.toInt())
                                putExtra("HABIT_ICON_NAME", habit.iconName)
                                putStringArrayListExtra("HABIT_QUESTS", ArrayList(habit.quests))
                                putExtra("IS_EDIT", true)
                                putStringArrayListExtra("EXISTING_TIMES", ArrayList(habits.filter { it.id != habit.id }.map { it.time }))
                            }
                            createActivityLauncher.launch(intent)
                        }
                    )
                }
                
                item {
                    Button(
                        onClick = {
                            val intent = Intent(context, CreateActivityActivity::class.java).apply {
                                putStringArrayListExtra("EXISTING_TIMES", ArrayList(habits.map { it.time }))
                            }
                            createActivityLauncher.launch(intent)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .border(2.dp, Color.Black, RoundedCornerShape(20.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFCFD8DC)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Add, null, tint = Color.Black, modifier = Modifier.size(32.dp))
                            Spacer(Modifier.width(16.dp))
                            Text("Añadir", color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitItem(habit: Habit, onQuestCompleted: (String) -> Unit, onDoubleClick: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f)
    val bgColor = Color(habit.colorValue.toInt())
    val contentColor = if (bgColor.luminance() < 0.4f) Color.White else Color.Black
    
    val habitIcon = when(habit.iconName) {
        "WbSunny" -> Icons.Default.WbSunny
        "NightsStay" -> Icons.Default.NightsStay
        "FitnessCenter" -> Icons.Default.FitnessCenter
        "Book" -> Icons.Default.Book
        "WaterDrop" -> Icons.Default.WaterDrop
        "Restaurant" -> Icons.Default.Restaurant
        else -> Icons.Default.Add
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.Black, RoundedCornerShape(20.dp))
            .background(bgColor, RoundedCornerShape(20.dp))
            .combinedClickable(
                onClick = { expanded = !expanded },
                onDoubleClick = onDoubleClick
            )
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(habitIcon, null, modifier = Modifier.size(36.dp), tint = contentColor)
            Spacer(Modifier.width(16.dp))
            Text(habit.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = contentColor, modifier = Modifier.weight(1f))
            Text(habit.time, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = contentColor)
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.Default.ExpandMore,
                null,
                modifier = Modifier.rotate(rotation).size(24.dp),
                tint = contentColor
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = 16.dp, start = 8.dp)) {
                HorizontalDivider(color = contentColor.copy(alpha = 0.2f), thickness = 1.dp)
                Spacer(Modifier.height(12.dp))
                Text("Quests (+10 pts cada una):", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = contentColor)
                habit.quests.forEach { quest ->
                    val isDone = quest in habit.safeCompletedQuests
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable { 
                                if (!isDone) {
                                    onQuestCompleted(quest)
                                }
                            }
                    ) {
                        Icon(
                            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline, 
                            contentDescription = null, 
                            modifier = Modifier.size(24.dp), 
                            tint = if (isDone) Color(0xFF4CAF50) else contentColor
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = quest, 
                            fontSize = 16.sp, 
                            color = if (isDone) contentColor.copy(alpha = 0.6f) else contentColor,
                            style = if (isDone) LocalTextStyle.current.copy(textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough) else LocalTextStyle.current
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayCard(day: String, date: String, isSelected: Boolean) {
    val bgColor = if (isSelected) Color(0xFFFB8C00) else Color(0xFFFFCCBC)
    Column(
        modifier = Modifier.size(70.dp, 85.dp).background(bgColor, RoundedCornerShape(16.dp)).border(2.dp, Color.Black, RoundedCornerShape(16.dp)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(day, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Text(date, fontSize = 20.sp)
    }
}
