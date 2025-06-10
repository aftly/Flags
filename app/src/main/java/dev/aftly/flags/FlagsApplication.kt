package dev.aftly.flags

import android.app.Application
import dev.aftly.flags.data.room.AppContainer
import dev.aftly.flags.data.room.AppDataContainer

class FlagsApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(context = this)
    }
}