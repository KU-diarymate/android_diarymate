package ku.kpro.diary_mate.etc

import android.app.Application
import com.renju_note.isoo.util.PreferenceUtil
import io.realm.Realm
import io.realm.RealmConfiguration
import ku.kpro.diary_mate.data.DiaryMateSetting

class DiaryMateApplication : Application() {

    companion object {
        val setting = DiaryMateSetting.getDefaultSetting()
        lateinit var pref : PreferenceUtil
    }

    override fun onCreate() {
        super.onCreate()

        pref = PreferenceUtil(this)
        setting.loadSettingData(pref)

        Realm.init(this)
        val config : RealmConfiguration = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .name("DiaryMate.realm")
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }

}