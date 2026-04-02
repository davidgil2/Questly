package co.edu.udea.compumovil.gr03_20261.questly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.MenuBook
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

data class Stat(val name: String, val value: Int, val icon: ImageVector, val color: Color)
data class Achievement(val title: String, val description: String, val icon: ImageVector, val unlocked: Boolean)

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val userName = intent.getStringExtra("USER_NAME") ?: "Hero"
        val userClass = intent.getStringExtra("USER_CLASS") ?: "Warrior"
        
        setContent {
            QuestlyTheme {
                ProfileScreen(
                    userName = userName,
                    userClass = userClass,
                    onBack = { finish() }
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(userName: String, userClass: String, onBack: () -> Unit) {
    val backgroundColor = Color(0xFFE8F5E9)
    
    val stats = listOf(
        Stat("STR", 15, Icons.Default.FitnessCenter, Color(0xFFEF9A9A)),
        Stat("INT", 12, Icons.AutoMirrored.Filled.MenuBook, Color(0xFFCE93D8)),
        Stat("AGI", 10, Icons.AutoMirrored.Filled.DirectionsRun, Color(0xFF81D4FA)),
        Stat("LUK", 5, Icons.Default.Casino, Color(0xFFFFF176))
    )
    
    val achievements = listOf(
        Achievement("First Quest", "Completed your first daily habit.", Icons.Default.CheckCircle, true),
        Achievement("Dragon Tamer", "Survived the encounter with the Emerald Dragon.", Icons.Default.AutoAwesome, true),
        Achievement("Wealthy Traveler", "Saved up more than 500 points.", Icons.Default.Stars, false),
        Achievement("Early Bird", "Completed all morning habits before 8 AM.", Icons.Default.WbSunny, false)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(32.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("Character Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                        .border(2.dp, Color.Black, RoundedCornerShape(24.dp))
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(Color(0xFF81B692), CircleShape)
                            .border(2.dp, Color.Black, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when(userClass) {
                                "Warrior" -> Icons.Default.Shield
                                "Mage" -> Icons.Default.AutoFixHigh
                                else -> Icons.Default.Explore
                            },
                            contentDescription = null,
                            modifier = Modifier.size(48.dp),
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(20.dp))
                    Column {
                        Text(userName, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                        Text("Class: $userClass", fontSize = 18.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { 0.6f },
                            modifier = Modifier.fillMaxWidth().height(8.dp).border(1.dp, Color.Black, CircleShape),
                            color = Color(0xFF4CAF50),
                            trackColor = Color.White,
                            strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                        )
                        Text("Level 5 Adventurer", fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            item {
                Text("Base Statistics", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    stats.forEach { stat ->
                        StatCard(stat, Modifier.weight(1f))
                    }
                }
            }

            item {
                Text("Achievements", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }
            
            items(achievements) { achievement ->
                AchievementRow(achievement)
            }
            
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }
}

@Composable
fun StatCard(stat: Stat, modifier: Modifier) {
    Column(
        modifier = modifier
            .background(stat.color, RoundedCornerShape(16.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(stat.icon, null, modifier = Modifier.size(24.dp), tint = Color.Black)
        Text(stat.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Text("${stat.value}", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
    }
}

@Composable
fun AchievementRow(achievement: Achievement) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.unlocked) Color.White else Color.LightGray.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = achievement.icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = if (achievement.unlocked) Color(0xFFFFD54F) else Color.Gray
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = achievement.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (achievement.unlocked) Color.Black else Color.Gray
                )
                Text(
                    text = achievement.description,
                    fontSize = 14.sp,
                    color = if (achievement.unlocked) Color.DarkGray else Color.Gray
                )
            }
            if (!achievement.unlocked) {
                Spacer(modifier = Modifier.weight(1f))
                Icon(Icons.Default.Lock, null, tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
        }
    }
}
