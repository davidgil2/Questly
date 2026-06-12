package co.edu.udea.compumovil.gr03_20261.questly

data class Habit(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val time: String,
    val iconName: String,
    val colorValue: Long,
    val quests: List<String> = emptyList()
)
