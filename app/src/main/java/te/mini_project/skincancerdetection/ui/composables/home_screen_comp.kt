package te.mini_project.skincancerdetection.ui.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.gson.Gson
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import te.mini_project.skincancerdetection.data.Result
import te.mini_project.skincancerdetection.mockScans
import te.mini_project.skincancerdetection.room.models.MoleScan
import te.mini_project.skincancerdetection.ui.theme.SkinCancerDetectionTheme
import java.text.SimpleDateFormat


val df = SimpleDateFormat("dd/MM/yyyy")

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MolesHistory(mod: Modifier, moleScans: List<MoleScan> = mockScans,getNavController:()->NavController){
    if(moleScans.isNotEmpty())
    LazyColumn(
        mod.padding(12.dp),
        contentPadding = PaddingValues(12.dp),
    verticalArrangement = Arrangement.spacedBy(12.dp)
    ){

        items(moleScans){ mole ->
            val estimated = mole.scanResults.maxByOrNull { it.accuracy }!!

            Card(
                elevation = 12.dp,
                shape = RoundedCornerShape(12.dp),
                onClick = {
                    val r = Result(estimated.diseaseName,estimated.accuracy)
                    val gson = Gson()
                    val json  =  gson.toJson(r)
                    getNavController().navigate("details/$json")
                }

            ) {
                Column(
                    Modifier.padding(top=7.dp,end=7.dp)
                ) {
                    Text(modifier =  Modifier.padding(7.dp),text = estimated.diseaseName, style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.SemiBold))
                    Text(modifier =  Modifier.padding(horizontal = 7.dp, vertical = 4.dp),text = "Last Scanned On : ${df.format(mole.scanDate)}", style = MaterialTheme.typography.subtitle1.copy(color = Color.Gray))
                    Text(modifier =  Modifier.padding(4.dp).align(Alignment.End),
                        text = "${100 * estimated.accuracy} %", style = MaterialTheme.typography.subtitle2.copy(color = Color.LightGray))
                    Box(modifier = Modifier
                        .padding(top = 12.dp, bottom = 0.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .height(4.dp)
                        .fillMaxWidth(estimated.accuracy)
                        .background(MaterialTheme.colors.primaryVariant)
                       )
                }

            }

        }
    }
    else {
        Box(
            Modifier.fillMaxSize() ,
            contentAlignment = Alignment.Center
        ){
            Text("Click on add button to scan ...")
        }
    }
}


@Composable
@Preview(showBackground = true)
fun DefaultHomeScreenPreview() {
    SkinCancerDetectionTheme {
//            MolesHistory(Modifier.offset(x=0.dp,y=32.dp),)
    }
}