package donank.amoveowallet.Dagger

import android.app.Application
import android.arch.persistence.room.Room
import dagger.Module
import dagger.Provides
import donank.amoveowallet.Api.RESTInterface
import donank.amoveowallet.Data.AppDatabase
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class AppModule(private val app: Application) {

    @Singleton
    @Provides
    @ForApplication
    fun provideApplication() = app

    @Provides
    @Singleton
    fun providesAppDatabase(context: Application): AppDatabase =
            Room.databaseBuilder(context, AppDatabase::class.java, "my-amoveo-db")
                    .build()

    @Provides
    @Singleton
    fun providesDao(database: AppDatabase) = database.walletDao()

    @Singleton
    @Provides
    fun provideHttp() = OkHttpClient.Builder().connectTimeout(20,TimeUnit.SECONDS).build()

    @Singleton
    @Provides
    fun ProvideRESTInterface(httpClient: OkHttpClient) = RESTInterface.create(httpClient)
}