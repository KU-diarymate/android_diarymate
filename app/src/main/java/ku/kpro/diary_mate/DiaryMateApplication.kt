package ku.kpro.diary_mate

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class DiaryMateApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        val config : RealmConfiguration = RealmConfiguration.Builder()
            .allowWritesOnUiThread(true)
            .name("scrapIT.realm")
            .deleteRealmIfMigrationNeeded()
            .build()
        Realm.setDefaultConfiguration(config)
    }

}