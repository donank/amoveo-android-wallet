package donank.amoveowallet.Data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import donank.amoveowallet.Data.Model.Wallet

@Dao
interface WalletDao {

    @Insert
    fun save(wallet: Wallet)

    @Update
    fun update(wallet: Wallet)

    @Query("select count(*) from wallets")
    fun getWalletCount(): Int

}