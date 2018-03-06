package donank.amoveowallet.Dagger

import android.app.Application

class MainApplication : Application() {
    val component: AppComponent by lazy {
        DaggerAppComponent
                .builder()
                .appModule(AppModule(this))
                .build()
    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
        instance = this
    }

    companion object {
        lateinit var instance: MainApplication
    }
}

