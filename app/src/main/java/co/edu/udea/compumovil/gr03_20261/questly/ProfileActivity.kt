package co.edu.udea.compumovil.gr03_20261.questly

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.udea.compumovil.gr03_20261.questly.ui.theme.QuestlyTheme
import com.google.firebase.auth.FirebaseAuth

data class StatDisplay(val name: String, val value: Int, val icon: ImageVector, val color: Color, val onIncrease: () -> Unit)
data class Achievement(val title: String, val description: String, val icon: ImageVector, val unlocked: Boolean)

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            QuestlyTheme {
                ProfileScreen(
                    onBack = { finish() },
                    onNavigateToInventory = {
                        val intent = Intent(this, InventoryActivity::class.java).apply {
                            putExtra("USER_CLASS", PlayerStats.userClass)
                        }
                        startActivity(intent)
                    },
                    onLogout = {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val backgroundColor = Color(0xFFE8F5E9)
    var showRewardDialog by remember { mutableStateOf(false) }

    val stats = listOf(
        StatDisplay("STR", PlayerStats.str, Icons.Default.FitnessCenter, Color(0xFFEF9A9A)) { 
            PlayerStats.str++
            PlayerStats.statPoints--
            PlayerStats.save(context)
        },
        StatDisplay("INT", PlayerStats.int, Icons.AutoMirrored.Filled.MenuBook, Color(0xFFCE93D8)) { 
            PlayerStats.int++
            PlayerStats.statPoints--
            PlayerStats.save(context)
        },
        StatDisplay("AGI", PlayerStats.agi, Icons.AutoMirrored.Filled.DirectionsRun, Color(0xFF81D4FA)) { 
            PlayerStats.agi++
            PlayerStats.statPoints--
            PlayerStats.save(context)
        },
        StatDisplay("LUK", PlayerStats.luk, Icons.Default.Casino, Color(0xFFFFF176)) { 
            PlayerStats.luk++
            PlayerStats.statPoints--
            PlayerStats.save(context)
        }
    )

    val achievements = listOf(
        Achievement("First Quest", "Completed your first daily habit.", Icons.Default.CheckCircle, true),
        Achievement("Dragon Tamer", "Survived the encounter with the Emerald Dragon.", Icons.Default.AutoAwesome, true)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Character Profile", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
                
                IconButton(onClick = onLogout) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Logout,
                        contentDescription = "Cerrar Sesión",
                        tint = Color(0xFFD32F2F),
                        modifier = Modifier.size(28.dp)
                    )
                }
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
                        .border(2.dp, Color.Black, RoundedCornerShape(24.dp))
                        .padding(20.dp)
                ) {
                    Row(
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
                                imageVector = when(PlayerStats.userClass) {
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
                            Text(PlayerStats.name, fontSize = 28.sp, fontWeight = FontWeight.Bold)
                            Text("Class: ${PlayerStats.userClass}", fontSize = 18.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.height(4.dp))
                            val progress = PlayerStats.experience.toFloat() / PlayerStats.experienceToNextLevel.toFloat()
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier.fillMaxWidth().height(8.dp).border(1.dp, Color.Black, CircleShape),
                                color = Color(0xFF4CAF50),
                                trackColor = Color.White,
                                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
                            )
                            Text("Level ${PlayerStats.level} Adventurer (${PlayerStats.experience}/${PlayerStats.experienceToNextLevel} XP)", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = onNavigateToInventory,
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(2.dp, Color.Black, RoundedCornerShape(12.dp)),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Inventory, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("View Equipment & Skills", fontWeight = FontWeight.Bold)
                    }

                    if (PlayerStats.pendingRewards) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showRewardDialog = true },
                            modifier = Modifier.fillMaxWidth().border(2.dp, Color.Black, RoundedCornerShape(12.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Open Reward Chest!", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Base Statistics", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    if (PlayerStats.statPoints > 0) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(color = Color.Red, shape = CircleShape) {
                            Text("${PlayerStats.statPoints} points", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    stats.forEach { stat ->
                        StatCard(stat, Modifier.weight(1f), PlayerStats.statPoints > 0)
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

    if (showRewardDialog) {
        RewardChestDialog(onDismiss = { 
            showRewardDialog = false 
            PlayerStats.pendingRewards = false
            PlayerStats.save(context)
        })
    }
}

@Composable
fun StatCard(stat: StatDisplay, modifier: Modifier, canIncrease: Boolean) {
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
        if (canIncrease) {
            IconButton(onClick = stat.onIncrease, modifier = Modifier.size(24.dp)) {
                Icon(Icons.Default.AddCircle, contentDescription = "Increase", tint = Color.Black)
            }
        }
    }
}

@Composable
fun RewardChestDialog(onDismiss: () -> Unit) {
    val reward = remember {
        val options = listOf(
            "New Skill: Whirlwind",
            "Magic Wand (Weapon)",
            "500 Shop Points",
            "Mystery Potion"
        )
        options.random()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Level Up Reward!", fontWeight = FontWeight.Bold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color(0xFFFFD54F))
                Spacer(modifier = Modifier.height(16.dp))
                Text("You found:", fontSize = 16.sp)
                Text(reward, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Collect")
            }
        }
    )
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
        }
    }
}
