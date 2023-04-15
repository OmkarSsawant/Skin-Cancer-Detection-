package te.mini_project.skincancerdetection.vm

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import te.mini_project.skincancerdetection.room.SkinCancerDatabase
import te.mini_project.skincancerdetection.room.models.MoleScan

class SkinCancerDetectorVM(app:Application) : AndroidViewModel(app) {

    private val ff:FirebaseFirestore = FirebaseFirestore.getInstance()
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

    fun watchMoleRecord(): Flow<List<MoleScan>> = channelFlow{
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if(networkConnected() && userId!=null){
            ff.collection("v1")
                .document(userId)
                .collection("mole-record")
                .snapshots()
                .collect{ snapshot ->
                    trySend(snapshot.documents.map { it.toObject(MoleScan::class.java)!! })
                }
        }else{
                db.skinCancerDao().observeMolesRecord().collect{
                    trySend(it)
                }
        }


    }



    private fun saveMoleRecordOf(userId: String, ms:MoleScan)=
            ff.collection("v1")
                .document(userId)
                .collection("mole-record")
                .add(ms)
                .addOnSuccessListener {
                    Log.i("SkinCancerDetectorVM", "saveMoleRecordOf: ${it.id} ")
                }

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


    fun updateFirestore() {
        for ((uid,ms) in fsMoleRecordQueue){
            saveMoleRecordOf(uid,ms)
        }
        fsMoleRecordQueue.clear()
    }

}