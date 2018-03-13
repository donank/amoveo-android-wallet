package donank.amoveowallet.Data.Model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import donank.amoveowallet.Data.Model.Wallet

@Entity(tableName = "transaction")
data class Transaction(
        @ColumnInfo(name = "from_address")
        var fromAddress: String,
        @ColumnInfo(name = "to_address")
        var toAddress: String,
        @ColumnInfo(name = "tx_id")
        var txId: String,
        var value : Long,
        var type : TransactionType,
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0
)