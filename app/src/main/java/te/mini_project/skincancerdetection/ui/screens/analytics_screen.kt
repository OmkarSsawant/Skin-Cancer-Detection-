package te.mini_project.skincancerdetection.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.util.toRange
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.column.columnChart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.chart.line.lineSpec
import com.patrykandpatrick.vico.compose.component.shape.shader.verticalGradient
import com.patrykandpatrick.vico.core.chart.line.LineChart
import com.patrykandpatrick.vico.core.chart.values.AxisValuesOverrider
import com.patrykandpatrick.vico.core.entry.ChartEntryModel
import com.patrykandpatrick.vico.core.entry.ChartEntryModelProducer
import com.patrykandpatrick.vico.core.entry.FloatEntry
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import te.mini_project.skincancerdetection.room.SkinCancerDatabase
import te.mini_project.skincancerdetection.room.models.MoleScan
import java.nio.file.WatchEvent.Modifier
import kotlin.random.Random

@Composable
fun AnalyticsScreen(){
    val context = LocalContext.current.applicationContext
    var moleScans by remember {
        mutableStateOf(listOf<MoleScan>())
    }
    LaunchedEffect(key1 = "" ){
        withContext(Dispatchers.IO){
            val db = SkinCancerDatabase.getInstance(context)
            db.skinCancerDao().observeMolesRecord().collect{
                moleScans = it
            }
        }
    }
    fun getRandomEntries()  = moleScans.mapIndexed<MoleScan,FloatEntry> { i,mole->
        val estimated = mole.scanResults.maxByOrNull { it.accuracy }!!
        FloatEntry(x=i.toFloat(),y=16f * estimated.accuracy)
    }.toList()
    val chartEntryModelProducer = ChartEntryModelProducer(getRandomEntries())
    Scaffold{
        LazyColumn(
            androidx.compose.ui.Modifier.offset(y = it.calculateTopPadding()),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(12.dp)
        ){
            item {
                Text(" \uD83D\uDCCA Report Analytics ",style= TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold))
            }
            item {
                Card(elevation = 12.dp) {
                    Chart(
                        chart = columnChart(),
                        chartModelProducer = chartEntryModelProducer,
                        startAxis = startAxis(),
                        bottomAxis = bottomAxis(),
                    )
                }
            }
            item {
                Card(elevation = 12.dp) {
                    Chart(
                        chart = lineChart(),
                        chartModelProducer = chartEntryModelProducer,
                        startAxis = startAxis(),
                        bottomAxis = bottomAxis(),
                    )
                }
            }

            item {
                Card(elevation = 12.dp) {
                    Chart(
                        chart = lineChart(),
                        chartModelProducer = chartEntryModelProducer,
                        startAxis = startAxis(),
                        bottomAxis = bottomAxis(),
                    )
                }
            }

            item {
                Card(elevation = 12.dp) {
                    LineChartDark(model = chartEntryModelProducer.getModel())
                }
            }

        }

    }


}

@Composable
public fun LineChartDark(model:ChartEntryModel) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = Color.Black,
    ) {
        val yellow = Color(0xFFFFAA4A)
        val pink = Color(0xFFFF4AAA)

        Chart(
            modifier = androidx.compose.ui.Modifier.padding(8.dp),
            chart = lineChart(
                lines = listOf(
                    lineSpec(
                        lineColor = yellow,
                        lineBackgroundShader = verticalGradient(
                            arrayOf(yellow.copy(0.5f), yellow.copy(alpha = 0f)),
                        ),
                    ),
                    lineSpec(
                        lineColor = pink,
                        lineBackgroundShader = verticalGradient(
                            arrayOf(pink.copy(0.5f), pink.copy(alpha = 0f)),
                        ),
                    ),
                ),
                axisValuesOverrider = AxisValuesOverrider.fixed(maxY = 4f),
            ),
            model = model,
        )
    }
}