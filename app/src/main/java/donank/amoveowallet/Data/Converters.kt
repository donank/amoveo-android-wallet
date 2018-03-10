package donank.amoveowallet.Data

import android.arch.persistence.room.TypeConverter
import donank.amoveowallet.Data.Model.TransactionType
import donank.amoveowallet.Data.Model.WalletType

class Converters {

    @TypeConverter
    fun walletTypetoString(walletType: WalletType): String{
        return walletType.name
    }

    @TypeConverter
    fun stringToWalletType(string: String): WalletType {
        return WalletType.valueOf(string)
    }

    @TypeConverter
    fun transactionTypeToString(transactionType: TransactionType): String{
        return transactionType.name
    }

    @TypeConverter
    fun stringToTransactionType(string: String):TransactionType{
        return TransactionType.valueOf(string)
    }
}