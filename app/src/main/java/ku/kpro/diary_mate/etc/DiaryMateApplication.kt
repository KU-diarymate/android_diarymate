package ku.kpro.diary_mate.etc

import android.app.Application
import com.renju_note.isoo.util.PreferenceUtil
import io.realm.Realm
import io.realm.RealmConfiguration
import ku.kpro.diary_mate.data.DiaryMateSetting
import ku.kpro.diary_mate.fragment.ChattingFragment

class DiaryMateApplication : Application() {

    var currentChattingFragment: ChattingFragment? = null

    companion object {
        val setting = DiaryMateSetting.getDefaultSetting()
        lateinit var pref : PreferenceUtil

        private var msgId = 0
        fun addNewMessage(pref : PreferenceUtil) : Int {
            msgId += 1
            pref.setInt("msgID", msgId)
            return msgId
        }
    }

    override fun onCreate() {
        super.onCreate()

        pref = PreferenceUtil(this)
        setting.loadSettingData(pref)
        msgId = pref.getInt("msgID", 0)

        Realm.init(this)
        val config : RealmConfiguration = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .name("DiaryMate.realm")
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }

}