package te.mini_project.skincancerdetection

import android.util.Log
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import te.mini_project.skincancerdetection.room.SkinCancerDatabase
import te.mini_project.skincancerdetection.room.models.MoleScan
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @OptIn(DelicateCoroutinesApi::class)
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
       val db = Room.inMemoryDatabaseBuilder(appContext, SkinCancerDatabase::class.java)
           .fallbackToDestructiveMigration()
           .build()
        GlobalScope.launch {
         val i =    db.skinCancerDao().insertRecord(MoleScan().apply { id = 1
                scanDate = Date(System.currentTimeMillis())
                scanResults = mockResults
            })
          db.skinCancerDao().observeMolesRecord().collect{
              Log.i("Android.Test", "useAppContext: $it")

          }
        }

    }
}