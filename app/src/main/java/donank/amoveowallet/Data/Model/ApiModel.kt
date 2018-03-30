package donank.amoveowallet.Data.Model

data class RequestModel(
    var req : List<String>
)


data class ResponseModel(
    var res : List<Pair<String,List<String>>>
)
