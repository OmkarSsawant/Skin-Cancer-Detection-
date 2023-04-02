package te.mini_project.skincancerdetection.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import te.mini_project.skincancerdetection.ui.screens.ResultScreen
import te.mini_project.skincancerdetection.ui.theme.Black200
import te.mini_project.skincancerdetection.ui.theme.Black700

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ResultModalBottomSheet(navToResults:()->Unit, btnState: ModalBottomSheetState,title:String, btnText:String){


    ModalBottomSheetLayout(sheetState = btnState,sheetContent = {
        Spacer(modifier = Modifier.height(10.dp))
        LinearProgressIndicator( color = Color.Red, progress = 1.0f, modifier = Modifier.height(10.dp).clip(
            RoundedCornerShape(5.dp)
        ))
        Spacer(modifier = Modifier.height(20.dp))
        Text(modifier = Modifier.align(Alignment.CenterHorizontally),text=title, style = MaterialTheme.typography.h4)
        Spacer(modifier = Modifier.height(20.dp))
        Button(modifier = Modifier.align(Alignment.CenterHorizontally),onClick = navToResults){
            Text(btnText, style = MaterialTheme.typography.h6)
        }
    }) {
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Preview
@Composable
fun BottomSheetPreview(){
    Scaffold {
        it.calculateBottomPadding()
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator( color = Color.Red, progress = 1.0f, modifier = Modifier.height(10.dp).clip(
                RoundedCornerShape(5.dp)
            ))

            Spacer(modifier = Modifier.height(20.dp))
            Text(modifier = Modifier.align(Alignment.CenterHorizontally),text="Title here", style = MaterialTheme.typography.h4)
            Spacer(modifier = Modifier.height(20.dp))
            Button(modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(5.dp)),onClick = {}){
                Text("Done", style = MaterialTheme.typography.h6)
            }
        }
    }
}