package donank.amoveowallet.Data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import donank.amoveowallet.Data.Model.Wallet

@TypeConverters(Converters::class)
@Database(entities = [Wallet::class], version = 1,exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun walletDao(): WalletDao
}