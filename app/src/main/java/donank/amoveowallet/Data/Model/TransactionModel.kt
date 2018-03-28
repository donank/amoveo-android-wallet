package donank.amoveowallet.Data.Model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "transaction")
data class TransactionModel(
        @ColumnInfo(name = "from_address")
        var fromAddress: String,
        @ColumnInfo(name = "to_address")
        var toAddress: String,
        @ColumnInfo(name = "tx_id")
        var txId: String,
        var confirmations: Int,
        var value : Long,
        var type : TransactionType,
        @PrimaryKey(autoGenerate = true)
        var id: Long = 0
)