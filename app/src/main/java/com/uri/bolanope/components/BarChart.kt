package com.uri.bolanope.components

import android.content.Context
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

@Composable
fun BarChartComposable(data: List<PieChartData>, context: Context) {
    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        factory = { ctx ->
            BarChart(ctx).apply {

                description.isEnabled = false
                setDrawBarShadow(false)
                setDrawValueAboveBar(true)
                setPinchZoom(false)
                setDrawGridBackground(false)
                setFitBars(true)

                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    setDrawGridLines(false)
                    granularity = 1f
                    labelRotationAngle = -45f
                }

                axisLeft.apply {
                    setDrawGridLines(true)
                    axisMinimum = 0f
                }
                axisRight.isEnabled = false
                legend.isEnabled = false
            }
        },
        update = { chart ->
            val entries = data.mapIndexedNotNull { index, item ->
                item.value?.let { BarEntry(index.toFloat(), it) }
            }

            val dataSet = BarDataSet(entries, "Teams by Tourney").apply {
                setColors(*ColorTemplate.COLORFUL_COLORS)
                valueTextSize = 10f
            }

            chart.data = BarData(dataSet).apply {
                barWidth = 0.9f
            }

            val xAxisLabels = data.map { it.label }
            chart.xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
            chart.xAxis.labelCount = xAxisLabels.size

            chart.invalidate()
        }
    )
}