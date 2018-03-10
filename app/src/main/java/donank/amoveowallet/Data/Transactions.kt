package donank.amoveowallet.Data

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import donank.amoveowallet.Data.Model.Wallet

@Entity(tableName = "transactions", foreignKeys = [ForeignKey(
        entity = Wallet::class,parentColumns = arrayOf("id"),childColumns = arrayOf("id")
)])
data class Transactions (
    var from : String,
    var to : String,
    @ColumnInfo(name = "tx_id")
    var txId : String,
    @PrimaryKey(autoGenerate = true)
    var id : Long = 0
)