package co.edu.udea.compumovil.gr03_20261.questly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.udea.compumovil.gr03_20261.questly.ui.theme.QuestlyTheme

class CreateActivityActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuestlyTheme {
                var showDetail by remember { mutableStateOf(false) }
                var selectedTitle by remember { mutableStateOf("") }
                var selectedColor by remember { mutableStateOf(Color.Gray) }
                var selectedIcon by remember { mutableStateOf(Icons.Default.Add) }

                if (!showDetail) {
                    CreateActivityListScreen(
                        onBack = { finish() },
                        onSelectActivity = { title, color, icon ->
                            selectedTitle = title
                            selectedColor = color
                            selectedIcon = icon
                            showDetail = true
                        }
                    )
                } else {
                    EditActivityDetailScreen(
                        title = selectedTitle,
                        color = selectedColor,
                        icon = selectedIcon,
                        onBack = { showDetail = false },
                        onSave = { 
                            // In a real app, save to DB. For now, just go back.
                            finish() 
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun CreateActivityListScreen(onBack: () -> Unit, onSelectActivity: (String, Color, ImageVector) -> Unit) {
    val backgroundColor = Color(0xFFE8F5E9)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Search activity...") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                )
            }
            
            Spacer(Modifier.height(24.dp))
            Text("Suggested", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            
            Spacer(Modifier.height(12.dp))
            
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                item { CreateHabitRow("Gym", "Exercise", Color(0xFFAED581), Icons.Default.FitnessCenter) { onSelectActivity("Gym", Color(0xFFCE93D8), Icons.Default.FitnessCenter) } }
                item { CreateHabitRow("Study", "Homework", Color(0xFF81C784), Icons.Default.Book) { onSelectActivity("Study", Color(0xFF81C784), Icons.Default.Book) } }
                item { CreateHabitRow("Hydrate!", "Water", Color(0xFF81D4FA), Icons.Default.WaterDrop) { onSelectActivity("Hydrate!", Color(0xFF81D4FA), Icons.Default.WaterDrop) } }
                item { CreateHabitRow("Breakfast", "Meal 1", Color(0xFFFFF176), Icons.Default.Restaurant) { onSelectActivity("Breakfast", Color(0xFFFFF176), Icons.Default.Restaurant) } }
            }
        }
    }
}

@Composable
fun EditActivityDetailScreen(title: String, color: Color, icon: ImageVector, onBack: () -> Unit, onSave: (Habit) -> Unit) {
    val backgroundColor = Color(0xFFE8F5E9)
    var questText by remember { mutableStateOf("") }
    val quests = remember { mutableStateListOf<String>() }
    var time by remember { mutableStateOf("07:00 AM") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
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
                Text("Add Activity", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
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
                quests.forEach { quest ->
                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.RadioButtonUnchecked, null, tint = Color.Gray)
                        Spacer(Modifier.width(8.dp))
                        Text(quest, fontSize = 18.sp)
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextField(
                        value = questText,
                        onValueChange = { questText = it },
                        placeholder = { Text("Nueva sub-misión...") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent)
                    )
                    IconButton(onClick = { if (questText.isNotBlank()) { quests.add(questText); questText = "" } }) {
                        Icon(Icons.Default.AddCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(32.dp))
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
            
            Text("When?", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            TextField(
                value = time,
                onValueChange = { time = it },
                modifier = Modifier.fillMaxWidth().border(1.dp, Color.Black, RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White)
            )

            Spacer(Modifier.weight(1f))
            
            Button(
                onClick = { onSave(Habit(title = title, time = time, icon = icon, color = color, quests = quests.toList())) },
                modifier = Modifier.fillMaxWidth().height(60.dp).border(2.dp, Color.Black, RoundedCornerShape(30.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = color),
                shape = RoundedCornerShape(30.dp)
            ) {
                Text("Save Activity", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            }
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
