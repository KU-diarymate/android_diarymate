package ku.kpro.diary_mate

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Diary() : RealmObject() {
    @PrimaryKey
    var date : String = ""
    var context : String = ""
    var hashtags = RealmList<String>()
}