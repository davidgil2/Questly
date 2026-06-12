package co.edu.udea.compumovil.gr03_20261.questly

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object PersistenceManager {
    private const val PREFS_NAME = "questly_prefs"
    private const val KEY_HABITS = "habits"
    private const val KEY_PLAYER_STATS = "player_stats"
    private const val KEY_ONBOARDING_DONE = "onboarding_done"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveHabits(context: Context, habits: List<Habit>) {
        val json = Gson().toJson(habits)
        getPrefs(context).edit().putString(KEY_HABITS, json).apply()
    }

    fun loadHabits(context: Context): List<Habit> {
        val json = getPrefs(context).getString(KEY_HABITS, null) ?: return emptyList()
        val type = object : TypeToken<List<Habit>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun savePlayerStats(context: Context, stats: PlayerData) {
        val json = Gson().toJson(stats)
        getPrefs(context).edit().putString(KEY_PLAYER_STATS, json).apply()
    }

    fun loadPlayerStats(context: Context): PlayerData? {
        val json = getPrefs(context).getString(KEY_PLAYER_STATS, null) ?: return null
        return Gson().fromJson(json, PlayerData::class.java)
    }

    fun setOnboardingDone(context: Context, done: Boolean) {
        getPrefs(context).edit().putBoolean(KEY_ONBOARDING_DONE, done).apply()
    }

    fun isOnboardingDone(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_ONBOARDING_DONE, false)
    }
}

data class PlayerData(
    val name: String,
    val userClass: String,
    val level: Int,
    val experience: Int,
    val experienceToNextLevel: Int,
    val statPoints: Int,
    val shopPoints: Int,
    val str: Int,
    val int: Int,
    val agi: Int,
    val luk: Int,
    val wakeTime: String,
    val sleepTime: String,
    val equippedWeapon: Equipment?,
    val equippedOffHand: Equipment?,
    val equippedBody: Equipment?,
    val equippedAccessory: Equipment?,
    val equippedSkills: List<Skill>,
    val ownedEquipment: List<Equipment>,
    val ownedSkills: List<Skill>
)
