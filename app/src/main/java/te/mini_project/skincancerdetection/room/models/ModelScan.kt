package te.mini_project.skincancerdetection.room.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import te.mini_project.skincancerdetection.data.Result
import te.mini_project.skincancerdetection.room.ResultConvertor
import java.util.Date

@Entity(tableName = "mole_record")
@TypeConverters(ResultConvertor::class)
 class MoleScan {
    @ColumnInfo
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    @ColumnInfo
     var scanDate: Date = Date(System.currentTimeMillis())

    @ColumnInfo
     var scanResults: List<Result> = emptyList()
}
