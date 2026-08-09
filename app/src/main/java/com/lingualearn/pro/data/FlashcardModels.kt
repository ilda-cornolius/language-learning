package com.lingualearn.pro.data

data class FlashcardDeck(
    val id: Long,
    val languageId: String,
    val name: String,
    val createdAt: Long,
)

data class Flashcard(
    val id: Long,
    val deckId: Long,
    val front: String,
    val back: String,
    val extra: String = "",
    val ease: Double = FlashcardScheduler.DEFAULT_EASE,
    val intervalDays: Int = 0,
    val repetitions: Int = 0,
    val lapses: Int = 0,
    val dueAt: Long = 0L,
    val createdAt: Long = 0L,
)

enum class FlashcardRating(val value: Int) {
    Again(1),
    Hard(2),
    Good(3),
    Easy(4),
}

object FlashcardScheduler {
    const val DEFAULT_EASE = 2.5
    private const val MIN_EASE = 1.3
    private const val TEN_MINUTES_MS = 10L * 60L * 1000L
    private const val DAY_MS = 24L * 60L * 60L * 1000L

    fun schedule(card: Flashcard, rating: FlashcardRating, now: Long = System.currentTimeMillis()): Flashcard {
        return when (rating) {
            FlashcardRating.Again -> card.copy(
                ease = (card.ease - 0.2).coerceAtLeast(MIN_EASE),
                repetitions = 0,
                intervalDays = 0,
                lapses = card.lapses + 1,
                dueAt = now + TEN_MINUTES_MS,
            )
            FlashcardRating.Hard -> {
                val ease = (card.ease - 0.15).coerceAtLeast(MIN_EASE)
                if (card.repetitions == 0) {
                    card.copy(
                        ease = ease,
                        repetitions = 1,
                        intervalDays = 1,
                        dueAt = now + DAY_MS,
                    )
                } else {
                    val nextInterval = maxOf(1, (card.intervalDays * 1.2).toInt())
                    card.copy(
                        ease = ease,
                        repetitions = card.repetitions + 1,
                        intervalDays = nextInterval,
                        dueAt = now + nextInterval * DAY_MS,
                    )
                }
            }
            FlashcardRating.Good -> {
                val (reps, interval) = when (card.repetitions) {
                    0 -> 1 to 1
                    1 -> 2 to 3
                    else -> (card.repetitions + 1) to maxOf(1, kotlin.math.round(card.intervalDays * card.ease).toInt())
                }
                card.copy(
                    repetitions = reps,
                    intervalDays = interval,
                    dueAt = now + interval * DAY_MS,
                )
            }
            FlashcardRating.Easy -> {
                val ease = card.ease + 0.15
                val (reps, interval) = when (card.repetitions) {
                    0 -> 1 to 4
                    1 -> 2 to maxOf(4, kotlin.math.round(3 * ease).toInt())
                    else -> (card.repetitions + 1) to maxOf(
                        1,
                        kotlin.math.round(card.intervalDays * ease * 1.3).toInt(),
                    )
                }
                card.copy(
                    ease = ease,
                    repetitions = reps,
                    intervalDays = interval,
                    dueAt = now + interval * DAY_MS,
                )
            }
        }
    }
}
