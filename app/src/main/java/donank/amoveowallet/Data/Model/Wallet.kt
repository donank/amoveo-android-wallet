package donank.amoveowallet.Data.Model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import donank.amoveowallet.Data.WalletType

@Entity(tableName = "wallets")
data class Wallet(
        var address: String,
        var value : Long = 0,
        var name: String,
        var type: WalletType,
        var password: String,
        @PrimaryKey(autoGenerate = true)
        var id : Long = 0
)