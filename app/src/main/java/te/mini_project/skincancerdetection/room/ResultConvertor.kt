package te.mini_project.skincancerdetection.room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import te.mini_project.skincancerdetection.data.Result
import java.lang.reflect.Type
import java.util.Date

object ResultConvertor {
    @TypeConverter
    fun resultToString(result:List<Result>) : String{
       return Gson().toJson(result)
    }

    @TypeConverter
    fun resultFromString(string: String):List<Result>{
        val tt = object: TypeToken<List<Result>>(){}.type
        return  Gson().fromJson(string,tt)
    }

    @TypeConverter
    fun dateToString(date: Date):String{
        return date.toString()
    }

    @TypeConverter
    fun stringToDate(string: String):Date{
        return  Date(string)
    }
}