package dev.aftly.flags

import android.app.Application
import dev.aftly.flags.data.AppContainer
import dev.aftly.flags.data.AppDataContainer
import dev.aftly.flags.data.DataSource

class FlagsApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = AppDataContainer(context = this)
        DataSource.init(context = this)
    }
}