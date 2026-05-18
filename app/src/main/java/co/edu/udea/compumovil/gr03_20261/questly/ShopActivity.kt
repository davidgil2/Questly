package co.edu.udea.compumovil.gr03_20261.questly

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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

data class ShopItem(
    val id: Int,
    val name: String,
    val cost: Int,
    val icon: ImageVector,
    val category: String,
    val color: Color,
    val description: String = ""
)

class ShopActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuestlyTheme {
                ShopScreen(onBack = { finish() })
            }
        }
    }
}

@Composable
fun ShopScreen(onBack: () -> Unit) {
    val backgroundColor = Color(0xFFE8F5E9)
    val context = LocalContext.current

    val items = listOf(
        ShopItem(1, "Steel Sword", 100, Icons.Default.Gavel, "Weapon", Color(0xFFEF9A9A), "A sturdy blade for warriors."),
        ShopItem(2, "Magic Wand", 150, Icons.Default.AutoFixHigh, "Weapon", Color(0xFFCE93D8), "Focuses arcane energy."),
        ShopItem(3, "Iron Shield", 120, Icons.Default.Shield, "Off-hand", Color(0xFF90CAF9), "Provides great protection."),
        ShopItem(4, "Lucky Coin", 80, Icons.Default.Casino, "Accessory", Color(0xFFFFF176), "Increases luck slightly."),
        ShopItem(5, "Fireball", 200, Icons.Default.Whatshot, "Skill", Color(0xFFFFB300), "Deals area fire damage."),
        ShopItem(6, "Whirlwind", 180, Icons.Default.Cyclone, "Skill", Color(0xFFAED581), "Spinning attack for warriors.")
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 20.dp, end = 20.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", modifier = Modifier.size(32.dp))
                }
                Text("Questly Shop", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Surface(
                    color = Color(0xFFFFD54F),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.border(2.dp, Color.Black, RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Stars, null, modifier = Modifier.size(20.dp), tint = Color.Black)
                        Spacer(Modifier.width(4.dp))
                        Text("${PlayerStats.shopPoints}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).padding(horizontal = 20.dp)) {
            Text("Unlock new rewards!", fontSize = 18.sp, color = Color.Gray)
            Spacer(Modifier.height(20.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(items) { item ->
                    ShopCard(item) {
                        if (PlayerStats.shopPoints >= item.cost) {
                            PlayerStats.shopPoints -= item.cost
                            
                            // Add to inventory or skills based on category
                            if (item.category == "Skill") {
                                val newSkill = Skill(item.name, item.description, item.icon, 1)
                                if (!PlayerStats.ownedSkills.any { it.name == item.name }) {
                                    PlayerStats.ownedSkills.add(newSkill)
                                    Toast.makeText(context, "Purchased ${item.name}!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "You already own this skill!", Toast.LENGTH_SHORT).show()
                                    PlayerStats.shopPoints += item.cost // Refund
                                }
                            } else {
                                val newEquip = Equipment(item.name, item.category, item.icon)
                                if (!PlayerStats.ownedEquipment.any { it.name == item.name }) {
                                    PlayerStats.ownedEquipment.add(newEquip)
                                    Toast.makeText(context, "Purchased ${item.name}!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "You already own this item!", Toast.LENGTH_SHORT).show()
                                    PlayerStats.shopPoints += item.cost // Refund
                                }
                            }
                        } else {
                            Toast.makeText(context, "Not enough points!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShopCard(item: ShopItem, onBuy: () -> Unit) {
    val alreadyOwned = if (item.category == "Skill") {
        PlayerStats.ownedSkills.any { it.name == item.name }
    } else {
        PlayerStats.ownedEquipment.any { it.name == item.name }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = item.color),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(item.category, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black.copy(alpha = 0.6f))
            Spacer(Modifier.height(8.dp))
            Box(
                modifier = Modifier.size(50.dp).background(Color.White.copy(alpha = 0.5f), CircleShape).border(1.dp, Color.Black, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(item.icon, null, modifier = Modifier.size(30.dp), tint = Color.Black)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(item.name, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(modifier = Modifier.height(12.dp))
            
            Button(
                onClick = onBuy,
                enabled = !alreadyOwned,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(36.dp).border(1.dp, Color.Black, RoundedCornerShape(10.dp)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
            ) {
                if (alreadyOwned) {
                    Text("Owned", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Stars, null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFB300))
                        Spacer(Modifier.width(4.dp))
                        Text("${item.cost}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
