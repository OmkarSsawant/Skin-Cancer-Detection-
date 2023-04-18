package te.mini_project.skincancerdetection.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.Navigator
import com.google.gson.Gson
import te.mini_project.skincancerdetection.mockResults
import te.mini_project.skincancerdetection.data.Result as UiResult

@OptIn(ExperimentalMaterialApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ResultScreen(results:List<UiResult> = mockResults,getNavigator:()->NavController){

        Scaffold(
            topBar = {
                    Text(modifier = Modifier.padding(12.dp),text="Detected Results",style=MaterialTheme.typography.h3)
            }
        ) {
            LazyColumn(
                Modifier.padding(12.dp),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){
                items(results.sortedByDescending { it.accuracy } .subList(0,3)){
                    Card(elevation = 12.dp){
                        ListItem(text =  {
                            Button(
                                onClick = {
                                    getNavigator().navigate("details/${Gson().toJson(it)}")
                                }) {
                                Text("Details")
                            }
                                         }, secondaryText = { LinearProgressIndicator( modifier = Modifier.padding(12.dp),
                            progress = it.accuracy
                        )}, trailing = { Text("${it.accuracy/1f * 100f}  %")}, overlineText = {
                            Text(it.diseaseName, style = MaterialTheme.typography.h5)
                        })
                    }

                }
            }
        }
}

