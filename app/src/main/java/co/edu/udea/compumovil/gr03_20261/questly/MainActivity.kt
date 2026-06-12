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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Grass
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.udea.compumovil.gr03_20261.questly.ui.theme.QuestlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (PersistenceManager.isOnboardingDone(this)) {
            startActivity(Intent(this, HabitTrackerActivity::class.java))
            finish()
            return
        }

        enableEdgeToEdge()
        setContent {
            QuestlyTheme {
                WelcomeScreen(onNavigateNext = {
                    val intent = Intent(this, OnboardingActivity::class.java)
                    startActivity(intent)
                    finish()
                })
            }
        }
    }
}

@Composable
fun WelcomeScreen(onNavigateNext: () -> Unit) {
    // Colores RPG: Verde pradera, Oro aventura y Marrón orgánico
    val backgroundColor = Color(0xFFF1F8E9) // Verde muy claro (tranquilidad)
    val primaryGreen = Color(0xFF4CAF50)    // Verde brillante (aventura)
    val adventureGold = Color(0xFFFFC107)   // Oro (recompensas/RPG)
    val woodBrown = Color(0xFF5D4037)       // Marrón madera (texto/estabilidad)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = backgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(top = 60.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Icon(
                    imageVector = Icons.Default.Spa,
                    contentDescription = null,
                    tint = primaryGreen,
                    modifier = Modifier.size(80.dp)
                )
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = primaryGreen)) {
                            append("Quest")
                        }
                        withStyle(style = SpanStyle(color = adventureGold)) {
                            append("ly")
                        }
                    },
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-2).sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Tu aventura diaria comienza aquí. Convierte tus hábitos en misiones épicas.",
                    fontSize = 18.sp,
                    color = woodBrown,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 24.sp
                )
            }
            
            Box(contentAlignment = Alignment.Center) {
                // Icono central con un resplandor suave
                Icon(
                    imageVector = Icons.Default.Grass,
                    contentDescription = null,
                    tint = primaryGreen.copy(alpha = 0.7f),
                    modifier = Modifier.size(240.dp)
                )
            }
            
            // Botón de inicio tipo "Start Quest"
            Box(
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .size(85.dp)
                    .background(adventureGold, CircleShape)
                    .border(3.dp, woodBrown, CircleShape)
                    .clickable { onNavigateNext() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Empezar Aventura",
                    tint = woodBrown,
                    modifier = Modifier.size(45.dp)
                )
            }
        }
    }
}
