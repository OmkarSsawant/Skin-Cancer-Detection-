package te.mini_project.skincancerdetection.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import te.mini_project.skincancerdetection.room.models.MoleScan

@Dao
interface SkinCancerDao {

    @Insert
    suspend fun insertRecord(record: MoleScan)

    @Query("SELECT * FROM mole_record")
    fun observeMolesRecord():Flow<List<MoleScan>>
}