package co.edu.udea.compumovil.gr03_20261.questly

data class Choice(
    val id: Int,
    val text: String,
    val requirement: String? = null,
    val successMessage: String,
    val rewardPoints: Int = 0
)

data class StoryEvent(
    val id: Int,
    val title: String,
    val description: String,
    val day: Int,
    val choices: List<Choice>
)
