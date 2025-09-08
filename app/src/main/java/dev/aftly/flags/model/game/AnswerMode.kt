package dev.aftly.flags.model.game

import androidx.annotation.StringRes
import dev.aftly.flags.R
import kotlinx.serialization.Serializable

@Serializable
enum class AnswerMode(@param:StringRes val title: Int) {
    NAMES (title = R.string.game_mode_answer_names_title),
    DATES (title = R.string.game_mode_answer_dates_title)
}