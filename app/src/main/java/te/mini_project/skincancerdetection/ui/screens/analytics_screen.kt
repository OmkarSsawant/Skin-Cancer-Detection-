package te.mini_project.skincancerdetection.ui.screens

import android.util.Log
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.*
import te.mini_project.skincancerdetection.R
import te.mini_project.skincancerdetection.room.models.MoleScan
import te.mini_project.skincancerdetection.vm.SkinCancerDetectorVM
import java.text.SimpleDateFormat

private val monthDF = SimpleDateFormat("dd-MM")

class Entry(
    val localDate: String,
    override val x: Float,
    override val y: Float,
) : ChartEntry {
    override fun withY(y: Float) = Entry(localDate, x, y)
}


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

                val accuracyEntryModelProducer = moles
                    .mapIndexed { index, ms -> Entry(monthDF.format(ms.scanDate), index.toFloat(), ms.scanResults.maxBy { it.accuracy }.accuracy) }
                    .let { ChartEntryModelProducer(it) }
//                val accuracyModel = entryModelOf(
//                    moles.map { it.scanResults.maxBy { it.accuracy }.accuracy }.mapIndexed {i,a-> FloatEntry(i.toFloat(),a * 16f) }
//                )

                Card(Modifier.scrollable(rememberScrollState(),Orientation.Horizontal)) {
                    Chart(modifier=Modifier.scrollable(rememberScrollState(),Orientation.Horizontal),chart = columnChart(), model = accuracyEntryModelProducer.getModel() ,startAxis = startAxis(),
                        bottomAxis = bottomAxis(),)
                }
                Spacer(Modifier.height(30.dp))
                Text(modifier = Modifier.padding(28.dp),text="Scan Usage")

                val scanCountEntryModelProducer = moles
                    .groupBy { dff.format(it.scanDate) }.map { it.key to it.value.size }
                    .mapIndexed {i,v-> entryOf(i,v.second) }

                    .let { ChartEntryModelProducer(it) }
                Log.i("TAG", "AnalyticsScreen: ${moles
                    .groupBy { dff.format(it.scanDate) }.map { it.key to it.value.size }} ")
//                val useCountModel = entryModelOf(moles.groupBy{ dff.format(it.scanDate) }.map { it.key to it.value.size }.mapIndexed { index, (d,c) ->  FloatEntry(index.toFloat(),c.toFloat()) })
                Card(Modifier.scrollable(rememberScrollState(),Orientation.Horizontal)) {
                    Chart(modifier = Modifier.scrollable(rememberScrollState(),Orientation.Horizontal),chart = columnChart(), model = scanCountEntryModelProducer.getModel(), startAxis = startAxis(),
                        bottomAxis = bottomAxis(),)
                }

                //Chart Accuracy
//                Card {
//                    BarChart(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(32.dp)
//                            .height(200.dp)
//                            .scrollable(rememberScrollableState(
//                                consumeScrollDelta = { it }
//                            ), orientation = Orientation.Horizontal),
//                        onBarClick = { },// handle Click for individual bar},
//                        color = Black500,// colors
//                        barData = moles.map { ms->
//                            BarData(df.format(ms.scanDate),ms.scanResults.maxBy { it.accuracy }.accuracy)
//                        }.toList(),
//
//                        )
//                }


//                Log.i("TAG", "AnalyticsScreen: $groupedByDate")
//
//                Card{
//                    BarChart(
//                        modifier = Modifier
//                            .padding(32.dp)
//                            .fillMaxWidth()
//                            .height(200.dp)
//
//                            .scrollable(rememberScrollableState(
//                                consumeScrollDelta = { it }
//                            ), orientation = Orientation.Horizontal),
//                        onBarClick = { },// handle Click for individual bar},
//                        color = Black500,// colors
//                        barData = groupedByDate.map { gd->
//                            BarData(gd.first,gd.second.toFloat())
//                        }.toList(),
//                        barConfig = BarConfig()
//                    )
//                }


//                Text(modifier = Modifier.padding(12.dp),text="Most Found")
//                //Most Found Disease
//                val diseaseAndCount = mutableMapOf<String,Int>()
//                moles.forEach {mole->
//                    val expected = mole.scanResults.maxBy { it.accuracy }
//                    val dn = expected.diseaseName.split(',').first()
//                    if(diseaseAndCount[dn] == null) {
//                        diseaseAndCount[dn] = 1
//                    }else{
//                        diseaseAndCount[dn]  = diseaseAndCount[dn]!! + 1
//                    }
//                }
//
//                Log.i("TAG", "AnalyticsScreen: $diseaseAndCount")
//                Card{PointChart(
//                    modifier = Modifier
//                        .padding(32.dp)
//                        .fillMaxWidth()
//                        .height(200.dp)
//                        .scrollable(rememberScrollableState(
//                            consumeScrollDelta = { it }
//                        ), orientation = Orientation.Horizontal),
//                    color =Color.Green, // colors
//                    pointData = diseaseAndCount.map {(k,v)->
//                        PointData(k,v.toFloat())
//                    }.toList() // list of PointData
//                )
//                }
                Spacer(Modifier.height(90.dp))

            }
        }
        }

}