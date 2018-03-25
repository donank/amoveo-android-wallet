package donank.amoveowallet.Data

import android.arch.persistence.room.*
import donank.amoveowallet.Data.Model.Transaction
import donank.amoveowallet.Data.Model.Wallet

@Dao
interface WalletDao {

    @Insert
    fun save(wallet: Wallet)

    @Update
    fun update(wallet: Wallet)

    @Query("select count(*) from wallet")
    fun getWalletCount(): Int

    @Query("select * from `transaction` where from_address == :pubkey or to_address == :pubkey")
    fun getTransactions(pubkey: String):List<Transaction>

    @Query("select * from wallet where id= :id")
    fun getWalletByid(id : Long) : Wallet

    @Query("select * from wallet")
    fun getWallets(): List<Wallet>

}