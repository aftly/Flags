package dev.aftly.flags.model.game

import androidx.annotation.StringRes

enum class DifficultyMode(val guessLimit: Int?) {
    EASY (guessLimit = null), /* Unlimited guesses */
    MEDIUM (guessLimit = 3), /* 3 guesses */
    HARD (guessLimit = 1), /* 1 guess */
    SUDDEN_DEATH (guessLimit = 0) /* Incorrect guess ends the game */
}