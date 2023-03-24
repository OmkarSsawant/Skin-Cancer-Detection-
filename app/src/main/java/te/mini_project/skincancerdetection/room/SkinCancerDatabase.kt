package te.mini_project.skincancerdetection.room

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import te.mini_project.skincancerdetection.room.models.MoleScan

@Database(entities = [MoleScan::class], version = 1)
abstract  class SkinCancerDatabase :RoomDatabase(){

   public abstract  fun skinCancerDao(): SkinCancerDao

   companion object{
      @Volatile
      private var INSTANCE: SkinCancerDatabase?=null

      @Synchronized
      fun getInstance(context: Context): SkinCancerDatabase {
            if(INSTANCE ==null){
               INSTANCE = Room.databaseBuilder(context, SkinCancerDatabase::class.java,"reports_db")
                  .fallbackToDestructiveMigration()
                  .build()
            }
         return  INSTANCE!!
      }
   }
}