package co.edu.udea.compumovil.gr03_20261.questly

data class Choice(
    val id: Int,
    val text: String,
    val requiredStat: String? = null,
    val minStatValue: Int = 0,
    val difficultyClass: Int = 10, // DC target for the d20 roll
    val requiredItem: String? = null,
    val successMessage: String,
    val failureMessage: String,
    val rewardPoints: Int = 0,
    val experienceReward: Int = 50
)

data class StoryEvent(
    val id: Int,
    val title: String,
    val description: String,
    val day: Int,
    val choices: List<Choice>
)
