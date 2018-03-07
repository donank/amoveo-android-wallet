package donank.amoveowallet.Data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Update

@Dao
interface AddressDao {

    @Insert
    fun save(addressModel: AddressModel)

    @Update
    fun update(addressModel: AddressModel)

}