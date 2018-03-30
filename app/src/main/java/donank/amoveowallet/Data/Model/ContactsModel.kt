package donank.amoveowallet.Data.Model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "contact")
data class ContactsModel(
        var name : String = "",
        var pubkey: String,
        var edit: Boolean = false,
        @PrimaryKey(autoGenerate = true)
        var id : Long = 0
)