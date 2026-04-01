package co.edu.udea.compumovil.gr03_20261.questly

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class Habit(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val time: String,
    val icon: ImageVector,
    val color: Color,
    val quests: List<String> = emptyList()
)
