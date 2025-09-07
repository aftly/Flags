package dev.aftly.flags.model.game

enum class DifficultyMode {
    EASY, /* Unlimited guesses */
    MEDIUM, /* 3 guesses */
    HARD, /* 1 guess */
    SUDDEN_DEATH /* Incorrect guess ends the game */
}