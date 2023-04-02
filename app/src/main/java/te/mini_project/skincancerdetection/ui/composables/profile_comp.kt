import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberImagePainter
import com.firebase.ui.auth.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@SuppressLint("RestrictedApi")
@Composable
fun UserProfileDialog(user: FirebaseUser,onDismissRequest :()->Unit) {

    Dialog(onDismissRequest = onDismissRequest) {
        Card(modifier = Modifier.width(280.dp), elevation = 4.dp) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = rememberImagePainter(user.photoUrl),
                    contentDescription = null,
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = user.displayName ?: "", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Spacer(modifier = Modifier.height(8.dp))
                user.email?.let {
                    Text(text = "Email: $it", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                user.phoneNumber?.let {
                    if(it.isEmpty()) return@let
                    Text(text = "Phone: $it", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedButton(onClick = {
                    FirebaseAuth.getInstance().signOut()
                }) {
                    Text("Sign Out")
                }
            }
        }
    }
}
