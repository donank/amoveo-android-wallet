package donank.amoveowallet.Data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class AddressModel(
        var address: String,
        var value : Long = 0,
        @PrimaryKey(autoGenerate = true)
        var id : Long = 0
)