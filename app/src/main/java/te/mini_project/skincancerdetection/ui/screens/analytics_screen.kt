package te.mini_project.skincancerdetection.ui.screens

import android.util.Log
import android.widget.Space
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.room.Dao
import com.himanshoe.charty.bar.BarChart
import com.himanshoe.charty.bar.config.BarConfig
import com.himanshoe.charty.bar.model.BarData
import com.himanshoe.charty.circle.CircleChart
import com.himanshoe.charty.circle.model.CircleData
import com.himanshoe.charty.common.axis.AxisConfig
import com.himanshoe.charty.common.dimens.ChartDimens
import com.himanshoe.charty.point.PointChart
import com.himanshoe.charty.point.model.PointData
import kotlinx.coroutines.flow.collect
import te.mini_project.skincancerdetection.R
import te.mini_project.skincancerdetection.room.SkinCancerDao
import te.mini_project.skincancerdetection.room.SkinCancerDatabase
import te.mini_project.skincancerdetection.room.models.MoleScan
import te.mini_project.skincancerdetection.ui.theme.Black500
import te.mini_project.skincancerdetection.vm.SkinCancerDetectorVM
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AnalyticsScreen(vm:SkinCancerDetectorVM ,getNavController:()->NavController){
    val moles : List<MoleScan> by  vm.watchMoleRecord().collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar {
                Icon(painter = painterResource(id = R.drawable.baseline_analytics_24), contentDescription = "")
                Text(" Report Analytics")
            }
        },

        bottomBar = {
            BottomAppBar(
                cutoutShape = CircleShape,
                modifier = Modifier.fillMaxWidth(),

                ) {

                IconButton(modifier = Modifier.weight(1f),onClick = {
                    getNavController().popBackStack("home",false)
                }) {
                    Icon(painterResource(id = R.drawable.baseline_home_24), contentDescription = "home")
                }
                IconButton(modifier = Modifier.weight(1f),onClick = {}) {
                    Icon(painterResource(id = R.drawable.baseline_analytics_24), tint = Color.White, contentDescription = "analytics")
                }


            }

        },
    ) {pv->

        pv.calculateBottomPadding()
        val df = SimpleDateFormat("dd-MM")
        val dff = SimpleDateFormat("dd-MM-yyyy")
        if(moles.isEmpty()){
            Box(
                Modifier.fillMaxSize()
            ){
                Text("No Scans Yet !")
            }
        }else{
            Column(
                Modifier
                    .padding(12.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(modifier = Modifier.padding(28.dp),text=" Accuracy")
                //Chart Accuracy
                Card {
                    BarChart(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp)
                            .height(200.dp)
                            .scrollable(rememberScrollableState(
                                consumeScrollDelta = { it }
                            ), orientation = Orientation.Horizontal),
                        onBarClick = { },// handle Click for individual bar},
                        color = Black500,// colors
                        barData = moles.map { ms->
                            BarData(df.format(ms.scanDate),ms.scanResults.maxBy { it.accuracy }.accuracy)
                        }.toList(),

                        )
                }

                Text(modifier = Modifier.padding(28.dp),text="Scan Usage")
                //Scan Usage
                val groupedByDate:List<Pair<String, Int>> = moles.groupBy { dff.format(it.scanDate) }.map { it.key to it.value.size }
                Log.i("TAG", "AnalyticsScreen: $groupedByDate")

                Card{
                    BarChart(
                        modifier = Modifier
                            .padding(32.dp)
                            .fillMaxWidth()
                            .height(200.dp)

                            .scrollable(rememberScrollableState(
                                consumeScrollDelta = { it }
                            ), orientation = Orientation.Horizontal),
                        onBarClick = { },// handle Click for individual bar},
                        color = Black500,// colors
                        barData = groupedByDate.map { gd->
                            BarData(gd.first,gd.second.toFloat())
                        }.toList(),
                        barConfig = BarConfig()
                    )
                }


                Text(modifier = Modifier.padding(12.dp),text="Most Found")
                //Most Found Disease
                val diseaseAndCount = mutableMapOf<String,Int>()
                moles.forEach {mole->
                    val expected = mole.scanResults.maxBy { it.accuracy }
                    val dn = expected.diseaseName.split(',').first()
                    if(diseaseAndCount[dn] == null) {
                        diseaseAndCount[dn] = 1
                    }else{
                        diseaseAndCount[dn]  = diseaseAndCount[dn]!! + 1
                    }
                }

                Log.i("TAG", "AnalyticsScreen: $diseaseAndCount")
                Card{PointChart(
                    modifier = Modifier
                        .padding(32.dp)
                        .fillMaxWidth()
                        .height(200.dp)
                        .scrollable(rememberScrollableState(
                            consumeScrollDelta = { it }
                        ), orientation = Orientation.Horizontal),
                    color =Color.Green, // colors
                    pointData = diseaseAndCount.map {(k,v)->
                        PointData(k,v.toFloat())
                    }.toList() // list of PointData
                )
                }
                Spacer(Modifier.height(30.dp))

            }
        }
        }

}