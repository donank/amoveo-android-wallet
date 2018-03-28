package donank.amoveowallet.Data

import android.arch.persistence.room.*
import donank.amoveowallet.Data.Model.TransactionModel
import donank.amoveowallet.Data.Model.WalletModel
import io.reactivex.Single

@Dao
interface WalletDao {

    @Insert
    fun save(walletModel: WalletModel)

    @Update
    fun update(walletModel: WalletModel)

    @Query("select count(*) from wallet")
    fun getWalletCount(): Single<Int>

    @Query("select * from `transaction` where from_address == :pubkey or to_address == :pubkey")
    fun getTransactions(pubkey: String):Single<List<TransactionModel>>

    @Query("select * from wallet where id= :id")
    fun getWalletByid(id : Long) : WalletModel

    @Query("select * from wallet")
    fun getWallets(): Single<List<WalletModel>>
}