package com.uri.bolanope.components

import android.content.Context
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.uri.bolanope.R

data class PieChartData(
    var label: String?,
    var value: Float?
)


@Composable
fun PieChart(
    title: String,
    data: List<PieChartData>,
    context: Context
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = TextStyle.Default,
                fontFamily = FontFamily.Default,
                fontStyle = FontStyle.Normal,
                fontSize = 20.sp
            )

            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .size(320.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Crossfade(targetState = data) { pieChartData ->
                    AndroidView(factory = { context ->
                        PieChart(context).apply {
                            layoutParams = LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.MATCH_PARENT,
                            )
                            this.description.isEnabled = false
                            this.isDrawHoleEnabled = false
                            this.legend.isEnabled = true
                            this.legend.textSize = 14F
                            this.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
                            ContextCompat.getColor(context, R.color.white)
                        }
                    },
                        modifier = Modifier
                            .wrapContentSize()
                            .padding(5.dp), update = {
                            updatePieChartWithData(it, pieChartData, context)
                        })
                }
            }
        }
    }
}
fun updatePieChartWithData(
    chart: PieChart,
    data: List<PieChartData>,
    context: Context
) {
    val entries = ArrayList<PieEntry>()

    for (i in data.indices) {
        val item = data[i]
        entries.add(PieEntry(item.value ?: 0.toFloat(), item.label ?: ""))
    }

    val ds = PieDataSet(entries, "")

    val greenColor = Color(0xFF0F9D58)
    val blueColor = Color(0xFF2196F3)
    val yellowColor = Color(0xFFFFC107)
    val redColor = Color(0xFFF44336)

    ds.colors = arrayListOf(
        greenColor.toArgb(),
        blueColor.toArgb(),
        redColor.toArgb(),
        yellowColor.toArgb(),
    )
    ds.yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE

    ds.xValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE

    ds.sliceSpace = 2f

    ContextCompat.getColor(context, R.color.white)

    ds.valueTextSize = 18f

    ds.valueTypeface = android.graphics.Typeface.DEFAULT_BOLD

    val d = PieData(ds)

    chart.data = d

    chart.invalidate()
}
