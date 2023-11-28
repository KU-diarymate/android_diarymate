package ku.kpro.diary_mate.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

//open class ChatMessage()  : RealmObject(){
//    @PrimaryKey
//    var index : Int = ,
//    var sender : String,
//    var message : String,
//    var date : String
//}

data class ChatMessage(
    var sender : String,
    var message : String,
)