package co.edu.udea.compumovil.gr03_20261.questly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.udea.compumovil.gr03_20261.questly.ui.theme.QuestlyTheme
import kotlin.random.Random

class DailyEventActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuestlyTheme {
                val event = remember { EventRepository.getEventForToday() }
                DailyEventScreen(event = event, onFinish = { finish() })
            }
        }
    }
}

@Composable
fun DailyEventScreen(event: StoryEvent, onFinish: () -> Unit) {
    val backgroundColor = Color(0xFF1B5E20)
    var selectedChoice by remember { mutableStateOf<Choice?>(null) }
    var selectedSkill by remember { mutableStateOf<Skill?>(null) }
    var rollResult by remember { mutableIntStateOf(0) }
    var isRolling by remember { mutableStateOf(false) }
    var eventResult by remember { mutableStateOf<Boolean?>(null) }
    var showLevelUp by remember { mutableStateOf(false) }

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

            Text(
                text = event.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFD54F),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(2.dp, Color.White, RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = event.description,
                    modifier = Modifier.padding(20.dp),
                    color = Color.White,
                    fontSize = 18.sp,
                    lineHeight = 24.sp,
                    textAlign = TextAlign.Justify
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (eventResult == null) {
                if (selectedChoice == null) {
                    Text("Choose your action:", color = Color.White, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    event.choices.forEach { choice ->
                        ChoiceButton(choice) { selectedChoice = choice }
                    }
                } else {
                    // Choice selected, now pick a skill and roll
                    ActionPhase(
                        choice = selectedChoice!!,
                        selectedSkill = selectedSkill,
                        onSkillSelect = { selectedSkill = it },
                        onRoll = {
                            isRolling = true
                            // Simulate roll delay
                        },
                        isRolling = isRolling,
                        onRollComplete = { result ->
                            rollResult = result
                            isRolling = false
                            val total = result + (selectedSkill?.level ?: 0)
                            eventResult = total >= selectedChoice!!.difficultyClass
                            if (eventResult == true) {
                                val leveledUp = PlayerStats.addExperience(selectedChoice!!.experienceReward)
                                if (leveledUp) showLevelUp = true
                            }
                        }
                    )
                }
            } else {
                ResultPhase(
                    choice = selectedChoice!!,
                    success = eventResult!!,
                    roll = rollResult,
                    skillBonus = selectedSkill?.level ?: 0,
                    onFinish = onFinish,
                    showLevelUp = showLevelUp
                )
            }
        }
    }
}

@Composable
fun ChoiceButton(choice: Choice, onSelect: () -> Unit) {
    val statValue = choice.requiredStat?.let { PlayerStats.getStatValue(it) } ?: 0
    val hasItem = choice.requiredItem?.let { reqItem -> PlayerStats.ownedEquipment.any { it.name == reqItem } } ?: true
    val isLocked = (choice.requiredStat != null && statValue < choice.minStatValue) || !hasItem

    Button(
        onClick = onSelect,
        enabled = !isLocked,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(1.dp, if (isLocked) Color.Gray else Color.White, RoundedCornerShape(12.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isLocked) Color.Black.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f),
            disabledContainerColor = Color.Black.copy(alpha = 0.4f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (isLocked) {
                Icon(Icons.Default.Lock, null, tint = Color.Gray, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(choice.text, color = if (isLocked) Color.Gray else Color.White, fontWeight = FontWeight.Bold)
                if (choice.requiredStat != null) {
                    Text(
                        "Req: ${choice.requiredStat} ${choice.minStatValue} (You: $statValue)",
                        fontSize = 12.sp,
                        color = if (statValue >= choice.minStatValue) Color(0xFFAED581) else Color(0xFFEF9A9A)
                    )
                }
                if (choice.requiredItem != null) {
                    Text(
                        "Needs: ${choice.requiredItem}",
                        fontSize = 12.sp,
                        color = if (hasItem) Color(0xFFAED581) else Color(0xFFEF9A9A)
                    )
                }
            }
        }
    }
}

@Composable
fun ActionPhase(
    choice: Choice,
    selectedSkill: Skill?,
    onSkillSelect: (Skill) -> Unit,
    onRoll: () -> Unit,
    isRolling: Boolean,
    onRollComplete: (Int) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Target: DC ${choice.difficultyClass}", color = Color(0xFFFFD54F), fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        
        Text("Apply a Skill bonus?", color = Color.White, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(PlayerStats.equippedSkills) { skill ->
                val isSelected = selectedSkill == skill
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(if (isSelected) Color(0xFFFFD54F) else Color.White.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                        .border(2.dp, if (isSelected) Color.Black else Color.White, RoundedCornerShape(8.dp))
                        .clickable { onSkillSelect(skill) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(skill.icon, null, tint = if (isSelected) Color.Black else Color.White)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        if (isRolling) {
            var displayValue by remember { mutableIntStateOf(1) }
            LaunchedEffect(Unit) {
                repeat(15) {
                    displayValue = Random.nextInt(1, 21)
                    kotlinx.coroutines.delay(100)
                }
                onRollComplete(Random.nextInt(1, 21))
            }
            Text(
                text = "$displayValue",
                fontSize = 80.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )
        } else {
            Button(
                onClick = onRoll,
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F))
            ) {
                Text("ROLL\nd20", color = Color.Black, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
            }
        }
    }
}

@Composable
fun ResultPhase(
    choice: Choice,
    success: Boolean,
    roll: Int,
    skillBonus: Int,
    onFinish: () -> Unit,
    showLevelUp: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = if (success) "SUCCESS!" else "FAILURE",
            fontSize = 40.sp,
            fontWeight = FontWeight.ExtraBold,
            color = if (success) Color(0xFFAED581) else Color(0xFFEF9A9A)
        )
        Text("Roll: $roll + Bonus: $skillBonus = ${roll + skillBonus}", color = Color.White)
        Text("Target DC: ${choice.difficultyClass}", color = Color.LightGray, fontSize = 12.sp)
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth().border(2.dp, if (success) Color(0xFFAED581) else Color(0xFFEF9A9A), RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.1f))
        ) {
            Text(
                text = if (success) choice.successMessage else choice.failureMessage,
                modifier = Modifier.padding(20.dp),
                color = Color.White,
                textAlign = TextAlign.Center
            )
        }
        
        if (showLevelUp) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF006064))) {
                Text("LEVEL UP!", modifier = Modifier.padding(8.dp), color = Color.Cyan, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onFinish,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD54F))
        ) {
            Text("Finish Event", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
