package ku.kpro.diary_mate.etc

import io.realm.Realm
import io.realm.RealmResults
import ku.kpro.diary_mate.data.Diary
import java.util.Random

class RandomDiaryGenerator {
    private val realm: Realm = Realm.getDefaultInstance()
    private val diaryList: RealmResults<Diary> = realm.where(Diary::class.java).findAll()
    private val random: Random = Random()

    fun getRandomDiary(): Diary? {
        if (diaryList.isEmpty()) {
            return Diary().apply {
                date = date.toString()
                context = "No diary available"
            }
        }
        val index = random.nextInt(diaryList.size)
        return diaryList[index]
    }

}