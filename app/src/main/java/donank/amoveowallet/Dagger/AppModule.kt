package donank.amoveowallet.Dagger

import android.app.Application
import android.arch.lifecycle.ViewModelProvider
import android.arch.persistence.room.Room
import com.commonsware.cwac.saferoom.SafeHelperFactory
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import donank.amoveowallet.Api.RESTInterface
import donank.amoveowallet.Repositories.CryptoRepository
import donank.amoveowallet.Data.AppDatabase
import donank.amoveowallet.Data.Model.ViewModels.WalletListViewModelFactory
import donank.amoveowallet.Data.WalletDao
import donank.amoveowallet.Repositories.DBRepository
import donank.amoveowallet.Repositories.MainRepository
import donank.amoveowallet.Repositories.NetworkRepository
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
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


    @Provides
    @Singleton
    fun provideJson(): Moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()


    @Provides
    @Singleton
    fun provideHttp(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        return OkHttpClient.Builder().connectTimeout(30, TimeUnit.SECONDS).readTimeout(30, TimeUnit.SECONDS).addInterceptor(interceptor).build()
    }

    @Provides
    @Singleton
    fun ProvideRESTInterface(httpClient: OkHttpClient, moshi: Moshi) = RESTInterface.create(httpClient, moshi)

    @Provides
    fun provideWalletListViewModelFactory(factory: WalletListViewModelFactory):
            ViewModelProvider.Factory = factory
}