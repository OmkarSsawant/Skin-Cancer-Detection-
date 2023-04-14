package te.mini_project.skincancerdetection.vm

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import te.mini_project.skincancerdetection.room.SkinCancerDatabase
import te.mini_project.skincancerdetection.room.models.MoleScan

class SkinCancerDetectorVM(app:Application) : AndroidViewModel(app) {

    private val ff:FirebaseFirestore = FirebaseFirestore.getInstance()
    private var close : (() -> Boolean)? = null
    private val db: SkinCancerDatabase by lazy {
        SkinCancerDatabase.getInstance(app)
    }
    private val fsMoleRecordQueue = mutableMapOf<String,MoleScan>()
    private val cm by lazy {
        getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    private fun networkConnected():Boolean{
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.activeNetwork!=null
        }else cm.isActiveNetworkMetered
    }

    fun watchMoleRecord(): Flow<List<MoleScan>> = callbackFlow{
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if(networkConnected() && userId!=null){
            ff.collection("v1")
                .document(userId)
                .collection("mole-record")
                .addSnapshotListener{snap,e->
                    if(e==null && snap!=null){
                        trySend(
                            snap.documents.map { it.toObject(MoleScan::class.java)!! }.toList()
                        )
                    }
                }
        }else{
                trySend( db.skinCancerDao().getMolesRecord())
        }

        close = {
            close(null)
        }
    }



    private fun saveMoleRecordOf(userId: String, ms:MoleScan)=
            ff.collection("v1")
                .document(userId)
                .collection("mole-record")
                .add(ms)

    fun saveMoleRecord(ms:MoleScan) {
        if(FirebaseAuth.getInstance().currentUser == null){
            Toast.makeText(getApplication(),"Login to save information",Toast.LENGTH_SHORT).show()
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            db.skinCancerDao().insertRecord(ms)
            fsMoleRecordQueue[FirebaseAuth.getInstance().currentUser!!.uid] = ms
        }
    }

    override fun onCleared() {
        super.onCleared()
        close?.invoke()
        close = null
    }

    fun updateFirestore() {
        for ((uid,ms) in fsMoleRecordQueue){
            saveMoleRecordOf(uid,ms)
        }
        fsMoleRecordQueue.clear()
    }

}