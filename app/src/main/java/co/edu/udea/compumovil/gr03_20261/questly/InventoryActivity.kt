package co.edu.udea.compumovil.gr03_20261.questly

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.udea.compumovil.gr03_20261.questly.ui.theme.QuestlyTheme

class InventoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            QuestlyTheme {
                InventoryScreen(onBack = { finish() })
            }
        }
    }
}

@Composable
fun InventoryScreen(onBack: () -> Unit) {
    val backgroundColor = Color(0xFFF3E5F5)
    val context = LocalContext.current
    var showEquipmentPickerType by remember { mutableStateOf<String?>(null) }
    var showSkillPickerIndex by remember { mutableStateOf<Int?>(null) }

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
                Text("Character & Skills", fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                CharacterCard()
            }

            item {
                Text("Equipment Slots", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EquipmentSlot(PlayerStats.equippedWeapon, "Weapon", Modifier.weight(1f)) {
                        showEquipmentPickerType = "Weapon"
                    }
                    EquipmentSlot(PlayerStats.equippedOffHand, "Off-hand", Modifier.weight(1f)) {
                        showEquipmentPickerType = "Off-hand"
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    EquipmentSlot(PlayerStats.equippedBody, "Body", Modifier.weight(1f)) {
                        showEquipmentPickerType = "Body"
                    }
                    EquipmentSlot(PlayerStats.equippedAccessory, "Accessory", Modifier.weight(1f)) {
                        showEquipmentPickerType = "Accessory"
                    }
                }
            }

            item {
                Text("Equipped Skills (Max 2)", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            }

            // Skill Slot 1
            item {
                val skill = PlayerStats.equippedSkills.getOrNull(0)
                SkillSlot(skill, 0) { showSkillPickerIndex = 0 }
            }

            // Skill Slot 2
            item {
                val skill = PlayerStats.equippedSkills.getOrNull(1)
                SkillSlot(skill, 1) { showSkillPickerIndex = 1 }
            }

            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }

    if (showEquipmentPickerType != null) {
        val type = showEquipmentPickerType!!
        ItemPickerDialog(
            title = "Select $type",
            items = PlayerStats.ownedEquipment.filter { it.type == type },
            onItemSelected = { newItem ->
                when (type) {
                    "Weapon" -> PlayerStats.equippedWeapon = newItem
                    "Off-hand" -> PlayerStats.equippedOffHand = newItem
                    "Body" -> PlayerStats.equippedBody = newItem
                    "Accessory" -> PlayerStats.equippedAccessory = newItem
                }
                showEquipmentPickerType = null
            },
            onDismiss = { showEquipmentPickerType = null }
        )
    }

    if (showSkillPickerIndex != null) {
        val index = showSkillPickerIndex!!
        SkillPickerDialog(
            title = "Select Skill",
            skills = PlayerStats.ownedSkills,
            onSkillSelected = { newSkill ->
                val alreadyEquipped = PlayerStats.equippedSkills.any { it.name == newSkill.name }
                if (alreadyEquipped) {
                    Toast.makeText(context, "Skill already equipped!", Toast.LENGTH_SHORT).show()
                } else {
                    if (index < PlayerStats.equippedSkills.size) {
                        PlayerStats.equippedSkills[index] = newSkill
                    } else {
                        PlayerStats.equippedSkills.add(newSkill)
                    }
                }
                showSkillPickerIndex = null
            },
            onDismiss = { showSkillPickerIndex = null }
        )
    }
}

@Composable
fun CharacterCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(24.dp))
            .border(2.dp, Color.Black, RoundedCornerShape(24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(Color(0xFFBA68C8), CircleShape)
                .border(3.dp, Color.Black, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = when(PlayerStats.userClass) {
                    "Warrior" -> Icons.Default.Person
                    "Mage" -> Icons.Default.AutoFixHigh
                    "Scout" -> Icons.AutoMirrored.Filled.DirectionsRun
                    else -> Icons.Default.Face
                },
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = Color.White
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(PlayerStats.userClass, fontWeight = FontWeight.Bold, fontSize = 22.sp)
        Text("Level ${PlayerStats.level} Adventurer", fontSize = 16.sp, color = Color.Gray)
    }
}

@Composable
fun EquipmentSlot(equipment: Equipment?, label: String, modifier: Modifier, onClick: () -> Unit) {
    Card(
        modifier = modifier
            .height(110.dp)
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Spacer(modifier = Modifier.height(4.dp))
            if (equipment != null) {
                Icon(equipment.icon, null, modifier = Modifier.size(32.dp), tint = Color(0xFF7B1FA2))
                Spacer(modifier = Modifier.height(4.dp))
                Text(equipment.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1, textAlign = TextAlign.Center)
            } else {
                Icon(Icons.Default.Add, null, tint = Color.LightGray)
                Text("Empty", fontSize = 12.sp, color = Color.LightGray)
            }
        }
    }
}

@Composable
fun SkillSlot(skill: Skill?, index: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Color.Black, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(if (skill != null) Color(0xFFE1BEE7) else Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(skill?.icon ?: Icons.Default.Add, null, tint = if (skill != null) Color(0xFF4A148C) else Color.Gray)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                if (skill != null) {
                    Text(skill.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(skill.description, fontSize = 12.sp, color = Color.DarkGray)
                } else {
                    Text("Empty Skill Slot ${index + 1}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.Gray)
                    Text("Tap to equip a skill", fontSize = 12.sp, color = Color.Gray)
                }
            }
            if (skill != null) {
                Text("Lv. ${skill.level}", fontWeight = FontWeight.Bold, color = Color(0xFF7B1FA2))
            }
        }
    }
}

@Composable
fun ItemPickerDialog(title: String, items: List<Equipment>, onItemSelected: (Equipment) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            if (items.isEmpty()) {
                Text("No items of this type in inventory.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)) {
                    items(items) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { onItemSelected(item) }.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(item.icon, null, tint = Color(0xFF7B1FA2))
                            Spacer(Modifier.width(16.dp))
                            Text(item.name)
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun SkillPickerDialog(title: String, skills: List<Skill>, onSkillSelected: (Skill) -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            if (skills.isEmpty()) {
                Text("No skills available.")
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 300.dp)) {
                    items(skills) { skill ->
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable { onSkillSelected(skill) }.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(skill.icon, null, tint = Color(0xFF7B1FA2))
                            Spacer(Modifier.width(16.dp))
                            Column {
                                Text(skill.name, fontWeight = FontWeight.Bold)
                                Text("Lv. ${skill.level}", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
