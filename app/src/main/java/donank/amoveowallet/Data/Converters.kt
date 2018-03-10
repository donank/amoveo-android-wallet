package donank.amoveowallet.Data

import android.arch.persistence.room.TypeConverter

class Converters {

    @TypeConverter
    fun walletTypetoString(walletType: WalletType): String{
        return walletType.name
    }

    @TypeConverter
    fun stringToWalletType(string: String): WalletType{
        return WalletType.valueOf(string)
    }
}