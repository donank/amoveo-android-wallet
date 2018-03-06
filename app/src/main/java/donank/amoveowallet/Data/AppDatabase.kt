package donank.amoveowallet.Data

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = [AddressModel::class], version = 1,exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun walletDao(): AddressDao
}