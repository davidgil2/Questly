package co.edu.udea.compumovil.gr03_20261.questly

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.udea.compumovil.gr03_20261.questly.ui.theme.QuestlyTheme
import java.text.SimpleDateFormat
import java.util.Locale

class HabitTrackerActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val userName = intent.getStringExtra("USER_NAME") ?: "John Doe"
        val userClass = intent.getStringExtra("USER_CLASS") ?: "Warrior"
        val wakeTime = intent.getStringExtra("WAKE_TIME") ?: "05:00"
        val sleepTime = intent.getStringExtra("SLEEP_TIME") ?: "22:00"
        
        setContent {
            QuestlyTheme {
                HabitTrackerScreen(
                    userName = userName,
                    userClass = userClass,
                    wakeTime = wakeTime,
                    sleepTime = sleepTime,
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
                            putExtra("USER_NAME", userName)
                            putExtra("USER_CLASS", userClass)
                        }
                        startActivity(intent)
                    }
                )
            }
        }
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
    userName: String, 
    userClass: String,
    wakeTime: String,
    sleepTime: String,
    onNavigateToShop: () -> Unit, 
    onNavigateToEvent: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val backgroundColor = Color(0xFFE8F5E9)
    var points by remember { mutableIntStateOf(150) }
    
    val habits = remember { 
        mutableStateListOf(
            Habit(1, "Buenos Días", wakeTime, Icons.Default.WbSunny, Color(0xFFFFF176), listOf("Tiende la cama", "Bebe un vaso de agua")),
            Habit(2, "Buenas Noches", sleepTime, Icons.Default.NightsStay, Color(0xFF90CAF9), listOf("Leer 10 páginas", "Planear mañana"))
        ).also { sortHabits(it) }
    }

    val createActivityLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val title = data?.getStringExtra("HABIT_TITLE") ?: ""
            val time = data?.getStringExtra("HABIT_TIME") ?: ""
            val colorInt = data?.getIntExtra("HABIT_COLOR", Color.Gray.value.toInt()) ?: Color.Gray.value.toInt()
            val iconName = data?.getStringExtra("HABIT_ICON_NAME") ?: "Add"
            val quests = data?.getStringArrayListExtra("HABIT_QUESTS") ?: arrayListOf<String>()

            val icon = when (iconName) {
                "FitnessCenter" -> Icons.Default.FitnessCenter
                "Book" -> Icons.Default.Book
                "WaterDrop" -> Icons.Default.WaterDrop
                "Restaurant" -> Icons.Default.Restaurant
                else -> Icons.Default.Add
            }

            val newHabit = Habit(
                title = title,
                time = time,
                icon = icon,
                color = Color(colorInt.toLong()),
                quests = quests
            )
            habits.add(newHabit)
            sortHabits(habits)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        floatingActionButton = {
            Column(horizontalAlignment = Alignment.End) {
                FloatingActionButton(
                    onClick = onNavigateToEvent,
                    containerColor = Color(0xFF1B5E20),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.border(2.dp, Color.White, RoundedCornerShape(16.dp)).size(64.dp)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = "Daily Event", modifier = Modifier.size(32.dp))
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
                    Text("Today", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Text("Marzo 24", fontSize = 28.sp)
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Button(
                        onClick = {
                            val intent = Intent(context, CreateActivityActivity::class.java)
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
                            Text("$points", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
            
            Spacer(Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToProfile() }
                    .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = when(userClass) {
                        "Warrior" -> Icons.Default.Shield
                        "Mage" -> Icons.Default.AutoFixHigh
                        else -> Icons.Default.Explore
                    },
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = Color(0xFF1B5E20)
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("Bienvenido, $userName", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("Clase: $userClass | Ver Perfil", fontSize = 14.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
            }
            
            Spacer(Modifier.height(24.dp))
            
            val days = listOf("L" to "20", "M" to "21", "M" to "22", "J" to "23", "V" to "24", "S" to "25", "D" to "26")
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(days) { (day, date) ->
                    DayCard(day, date, isSelected = date == "24")
                }
            }
            
            Spacer(Modifier.height(32.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(habits) { habit ->
                    HabitItem(habit, onQuestCompleted = { points += 10 })
                }
                
                item {
                    Button(
                        onClick = {
                            val intent = Intent(context, CreateActivityActivity::class.java)
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

@Composable
fun HabitItem(habit: Habit, onQuestCompleted: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (expanded) 180f else 0f)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.Black, RoundedCornerShape(20.dp))
            .background(habit.color, RoundedCornerShape(20.dp))
            .clickable { expanded = !expanded }
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(habit.icon, null, modifier = Modifier.size(36.dp), tint = Color.Black)
            Spacer(Modifier.width(16.dp))
            Text(habit.title, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black, modifier = Modifier.weight(1f))
            Text(habit.time, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.Black)
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.Default.ExpandMore,
                null,
                modifier = Modifier.rotate(rotation).size(24.dp),
                tint = Color.Black
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = 16.dp, start = 8.dp)) {
                HorizontalDivider(color = Color.Black.copy(alpha = 0.2f), thickness = 1.dp)
                Spacer(Modifier.height(12.dp))
                Text("Quests (+10 pts cada una):", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                habit.quests.forEach { quest ->
                    var isDone by remember { mutableStateOf(false) }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(vertical = 4.dp)
                            .clickable { 
                                if (!isDone) {
                                    isDone = true
                                    onQuestCompleted()
                                }
                            }
                    ) {
                        Icon(
                            imageVector = if (isDone) Icons.Default.CheckCircle else Icons.Default.CheckCircleOutline, 
                            contentDescription = null, 
                            modifier = Modifier.size(24.dp), 
                            tint = if (isDone) Color(0xFF4CAF50) else Color.Black
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = quest, 
                            fontSize = 16.sp, 
                            color = if (isDone) Color.Gray else Color.Black,
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
