package ku.kpro.diary_mate.data

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class ChatMessage() : RealmObject(){
    @PrimaryKey
    var index : Int = 0
    var sender : String = ""
    var message : String = ""
    var date : String = ""
}