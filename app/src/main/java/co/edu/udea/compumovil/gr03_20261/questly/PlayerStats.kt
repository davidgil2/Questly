package co.edu.udea.compumovil.gr03_20261.questly

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// Modelos globales para el juego
data class Equipment(val name: String, val type: String, val iconName: String)
data class Skill(val name: String, val description: String, val iconName: String, val level: Int)

object PlayerStats {
    var name by mutableStateOf("Hero")
    var userClass by mutableStateOf("Warrior")
    var level by mutableIntStateOf(1)
    var experience by mutableIntStateOf(0)
    var experienceToNextLevel by mutableIntStateOf(100)
    var statPoints by mutableIntStateOf(0)
    var shopPoints by mutableIntStateOf(150)
    
    var str by mutableIntStateOf(5)
    var int by mutableIntStateOf(1)
    var agi by mutableIntStateOf(2)
    var luk by mutableIntStateOf(2)

    var wakeTime by mutableStateOf("05:00")
    var sleepTime by mutableStateOf("22:00")
    
    // Slots de equipamiento específicos
    var equippedWeapon by mutableStateOf<Equipment?>(null)
    var equippedOffHand by mutableStateOf<Equipment?>(null)
    var equippedBody by mutableStateOf<Equipment?>(null)
    var equippedAccessory by mutableStateOf<Equipment?>(null)
    
    // Habilidades equipadas (máximo 2)
    val equippedSkills = mutableStateListOf<Skill>()
    
    // Inventario total
    val ownedEquipment = mutableStateListOf<Equipment>()
    val ownedSkills = mutableStateListOf<Skill>()

    var pendingRewards by mutableStateOf(false)
    
    var lastEventDate by mutableStateOf("")

    fun initialize(chosenName: String, chosenClass: String, wake: String, sleep: String) {
        name = chosenName
        userClass = chosenClass
        wakeTime = wake
        sleepTime = sleep
        shopPoints = 150
        lastEventDate = ""
        when (chosenClass) {
            "Warrior" -> { str = 5; int = 1; agi = 2; luk = 2 }
            "Mage" -> { str = 1; int = 5; agi = 2; luk = 2 }
            "Scout" -> { str = 2; int = 2; agi = 5; luk = 1 }
            else -> { str = 3; int = 3; agi = 3; luk = 3 }
        }
        
        ownedEquipment.clear()
        ownedSkills.clear()
        equippedSkills.clear()
        equippedWeapon = null
        equippedOffHand = null
        equippedBody = null
        equippedAccessory = null

        val starterWeapon = when(userClass) {
            "Warrior" -> Equipment("Espada Oxidada", "Weapon", "Gavel")
            "Mage" -> Equipment("Vara de Aprendiz", "Weapon", "AutoFixHigh")
            else -> Equipment("Honda de Cuero", "Weapon", "AdsClick")
        }
        
        val starterSkill = when(userClass) {
            "Warrior" -> Skill("Golpe Pesado", "Daño físico fuerte", "FlashOn", 1)
            "Mage" -> Skill("Rayo Mágico", "Descarga de energía", "AutoAwesome", 1)
            else -> Skill("Paso Veloz", "Aumenta evasión", "DirectionsRun", 1)
        }

        ownedEquipment.add(starterWeapon)
        equippedWeapon = starterWeapon
        
        ownedSkills.add(starterSkill)
        equippedSkills.add(starterSkill)
        
        ownedEquipment.add(Equipment("Escudo de Madera", "Off-hand", "Shield"))
        ownedEquipment.add(Equipment("Túnica Vieja", "Body", "Checkroom"))
        ownedSkills.add(Skill("Bloqueo", "Postura defensiva", "Security", 1))
    }

    fun addExperience(amount: Int): Boolean {
        experience += amount
        if (experience >= experienceToNextLevel) {
            level++
            experience -= experienceToNextLevel
            experienceToNextLevel = (experienceToNextLevel * 1.5).toInt()
            statPoints += 3
            pendingRewards = true
            return true
        }
        return false
    }
    
    fun getStatValue(statName: String): Int {
        return when (statName.uppercase()) {
            "STR" -> str
            "INT" -> int
            "AGI" -> agi
            "LUK" -> luk
            else -> 0
        }
    }

    fun markEventAsDone() {
        lastEventDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }

    fun isEventDoneToday(): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return lastEventDate == today
    }

    fun save(context: Context) {
        val data = PlayerData(
            name, userClass, level, experience, experienceToNextLevel,
            statPoints, shopPoints, str, int, agi, luk, wakeTime, sleepTime,
            equippedWeapon, equippedOffHand, equippedBody, equippedAccessory,
            equippedSkills.toList(), ownedEquipment.toList(), ownedSkills.toList(),
            lastEventDate
        )
        PersistenceManager.savePlayerStats(context, data)
    }

    fun load(context: Context) {
        val data = PersistenceManager.loadPlayerStats(context) ?: return
        name = data.name
        userClass = data.userClass
        level = data.level
        experience = data.experience
        experienceToNextLevel = data.experienceToNextLevel
        statPoints = data.statPoints
        shopPoints = data.shopPoints
        str = data.str
        int = data.int
        agi = data.agi
        luk = data.luk
        wakeTime = data.wakeTime
        sleepTime = data.sleepTime

        equippedWeapon = data.equippedWeapon
        equippedOffHand = data.equippedOffHand
        equippedBody = data.equippedBody
        equippedAccessory = data.equippedAccessory
        
        equippedSkills.clear()
        equippedSkills.addAll(data.equippedSkills)
        
        ownedEquipment.clear()
        ownedEquipment.addAll(data.ownedEquipment)
        
        ownedSkills.clear()
        ownedSkills.addAll(data.ownedSkills)
        
        lastEventDate = data.lastEventDate ?: ""
    }
}
