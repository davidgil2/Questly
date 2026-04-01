package co.edu.udea.compumovil.gr03_20261.questly

import android.os.Bundle
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

data class ShopItem(
    val id: Int,
    val name: String,
    val cost: Int,
    val icon: ImageVector,
    val category: String,
    val color: Color
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
    val userPoints = remember { mutableIntStateOf(150) } // Example points

    val items = listOf(
        ShopItem(1, "The Scholar", 100, Icons.Default.AutoStories, "Title", Color(0xFFAED581)),
        ShopItem(2, "Night Owl", 150, Icons.Default.Bedtime, "Title", Color(0xFF90CAF9)),
        ShopItem(3, "Warrior", 500, Icons.Default.Shield, "Character", Color(0xFFEF9A9A)),
        ShopItem(4, "Speedy", 200, Icons.Default.DirectionsRun, "Effect", Color(0xFFFFF176)),
        ShopItem(5, "Golden Icon", 300, Icons.Default.Stars, "Icon", Color(0xFFFFD54F)),
        ShopItem(6, "Wizard", 750, Icons.Default.AutoFixHigh, "Character", Color(0xFFCE93D8))
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
                    Icon(Icons.Default.ArrowBack, "Back", modifier = Modifier.size(32.dp))
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
                        Text("${userPoints.intValue}", fontWeight = FontWeight.Bold)
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
                        if (userPoints.intValue >= item.cost) {
                            userPoints.intValue -= item.cost
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShopCard(item: ShopItem, onBuy: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
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
            Spacer(Modifier.height(8.dp))
            Text(item.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(36.dp).border(1.dp, Color.Black, RoundedCornerShape(10.dp)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Stars, null, modifier = Modifier.size(14.dp), tint = Color(0xFFFFB300))
                    Spacer(Modifier.width(4.dp))
                    Text("${item.cost}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
