package donank.amoveowallet.Dagger

import android.app.Application
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {

    @Singleton
    @Provides
    @ForApplication
    fun provideApplication() = app

    @Singleton
    @Provides
    fun provideHttp() = OkHttpClient.Builder().connectTimeout(20,TimeUnit.SECONDS).build()

}