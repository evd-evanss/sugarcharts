package com.sugarspoon.sugarcharts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sugarspoon.charts.SugarCharts
import com.sugarspoon.models.GraphEntry
import com.sugarspoon.sugarcharts.ui.theme.SugarChartsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SugarChartsTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val entries = listOf(
                        GraphEntry(
                            xAxisLegend = "jan",
                            yValue = 12f
                        ),
                        GraphEntry(
                            xAxisLegend = "feb",
                            yValue = 22f
                        ),
                        GraphEntry(
                            xAxisLegend = "mar",
                            yValue = 35f
                        )
                    )
                    SugarCharts.Linear(
                        entries = entries,
                        enableGrids = true,
                        enableAnimation = true,
                        smoothLines = true,
                        drawValuesOnMarkers = true,
                        aspectRatio = 3 / 2f,
                        paddingValues = PaddingValues(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    SugarChartsTheme {
        Greeting("Android")
    }
}