package co.edu.udea.compumovil.gr03_20261.questly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.udea.compumovil.gr03_20261.questly.ui.theme.QuestlyTheme

class DailyEventActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuestlyTheme {
                // Mocking a story event for Day 1
                val event = StoryEvent(
                    id = 1,
                    title = "The Dragon of Mount Caly",
                    description = "While trekking through the misty peaks, a massive Emerald Dragon blocks your path. Its scales shimmer like ancient gems. It doesn't look aggressive, but it's curious about your journey.",
                    day = 1,
                    choices = listOf(
                        Choice(1, "Offer a peace gift (Needs 50 pts)", "50 pts", "The dragon accepts your offering and shares a secret path. +20 pts", 20),
                        Choice(2, "Try to communicate (Free)", null, "You speak softly. The dragon seems amused and lets you pass, but you find nothing else.", 0),
                        Choice(3, "Show your Warrior title", "Warrior", "Impressive! The dragon recognizes your valor and gifts you a Dragon Scale. +50 pts", 50)
                    )
                )
                DailyEventScreen(event = event, onFinish = { finish() })
            }
        }
    }
}

@Composable
fun DailyEventScreen(event: StoryEvent, onFinish: () -> Unit) {
    val backgroundColor = Color(0xFF1B5E20) // Dark forest green for RPG feel
    var resultText by remember { mutableStateOf<String?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                IconButton(onClick = onFinish) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = Color(0xFFFFD54F)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Day ${event.day}: ${event.title}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color.White, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = event.description,
                    modifier = Modifier.padding(16.dp),
                    color = Color.White,
                    fontSize = 18.sp,
                    lineHeight = 26.sp,
                    textAlign = TextAlign.Justify
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (resultText == null) {
                Text(
                    text = "What will you do?",
                    color = Color(0xFFFFD54F),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    event.choices.forEach { choice ->
                        Button(
                            onClick = { resultText = choice.successMessage },
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, Color.White, RoundedCornerShape(12.dp)),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.2f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(choice.text, color = Color.White, fontWeight = FontWeight.Bold)
                                if (choice.requirement != null) {
                                    Text("Req: ${choice.requirement}", color = Color(0xFFFFCC80), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color(0xFFFFD54F), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD54F).copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = resultText!!,
                            color = Color.White,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onFinish,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Continue Journey", color = Color.Black, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
