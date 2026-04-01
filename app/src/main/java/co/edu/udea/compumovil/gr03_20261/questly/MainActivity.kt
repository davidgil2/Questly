package co.edu.udea.compumovil.gr03_20261.questly

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
    val backgroundColor = Color(0xFFF3D9C9)
    val primaryGreen = Color(0xFF81B692)
    val darkGray = Color(0xFF333333)

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
                        withStyle(style = SpanStyle(color = darkGray)) {
                            append("ly")
                        }
                    },
                    fontSize = 72.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-2).sp
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "A daily planner and habit tracker for students and young adults.",
                    fontSize = 18.sp,
                    color = darkGray,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 24.sp
                )
            }
            
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Grass,
                    contentDescription = null,
                    tint = primaryGreen,
                    modifier = Modifier.size(240.dp)
                )
            }
            
            Box(
                modifier = Modifier
                    .padding(bottom = 60.dp)
                    .size(80.dp)
                    .background(darkGray, CircleShape)
                    .clickable { onNavigateNext() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "Get Started",
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}
