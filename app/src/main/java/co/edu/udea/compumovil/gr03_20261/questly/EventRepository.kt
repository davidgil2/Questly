package co.edu.udea.compumovil.gr03_20261.questly

import java.util.Calendar
import java.util.Random

object EventRepository {
    private val events = listOf(
        StoryEvent(
            id = 1,
            title = "The Emerald Dragon",
            description = "A massive dragon blocks your path. It demands a tribute or a show of worthiness.",
            day = 1,
            choices = listOf(
                Choice(1, "Intimidate the dragon", "STR", 5, 15, null, "You roar so loud the dragon steps aside in respect!", "The dragon laughs at your attempt and blows smoke in your face.", 50, 100),
                Choice(2, "Solve its riddle", "INT", 4, 12, null, "Your clever answer impresses the ancient beast.", "You stutter the wrong answer. The dragon looks disappointed.", 30, 80),
                Choice(3, "Offer a precious gem", null, 0, 5, "Lucky Gem", "The dragon loves the shiny gift!", "You don't have anything shiny enough to appease it.", 40, 60)
            )
        ),
        StoryEvent(
            id = 2,
            title = "The Mysterious Forest Trap",
            description = "You are caught in a magical vine trap. It's tightening quickly!",
            day = 2,
            choices = listOf(
                Choice(1, "Break free with brute force", "STR", 4, 14, null, "You snap the vines like dry twigs!", "The vines are stronger than you thought.", 40, 90),
                Choice(2, "Wriggle out skillfully", "AGI", 5, 12, null, "You slip through the gaps like water.", "You get even more tangled.", 30, 80),
                Choice(3, "Use a sharp tool", null, 0, 8, "Steel Sword", "You cut your way out easily.", "Your tools are too dull for these magical vines.", 20, 70)
            )
        ),
        StoryEvent(
            id = 3,
            title = "The Old Library's Secret",
            description = "You find a locked chest in a forgotten library. It's protected by a complex arcane seal.",
            day = 3,
            choices = listOf(
                Choice(1, "Analyze the seal", "INT", 6, 16, null, "You decode the arcane symbols and the chest opens.", "The symbols make your head spin.", 60, 120),
                Choice(2, "Pick the lock carefully", "AGI", 4, 14, null, "Click! The mechanism yields to your touch.", "The lock is jammed.", 40, 100),
                Choice(3, "Smash it open", "STR", 5, 18, null, "Wood splinters everywhere, but you found the loot!", "You just hurt your hand.", 20, 50)
            )
        )
    )

    fun getEventForToday(): StoryEvent {
        val calendar = Calendar.getInstance()
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val year = calendar.get(Calendar.YEAR)
        val seed = (year * 1000 + dayOfYear).toLong()
        val random = Random(seed)
        return events[random.nextInt(events.size)]
    }
}
