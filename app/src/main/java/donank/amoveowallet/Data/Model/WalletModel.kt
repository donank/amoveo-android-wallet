package donank.amoveowallet.Data.Model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "wallet")
data class WalletModel(
        var address: String,
        var value : Long = 0,
        var name: String,
        var type: WalletType,
        var password: String,
        @PrimaryKey(autoGenerate = true)
        var id : Long = 0
)