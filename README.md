# Sugar Charts

A simple and very useful graphics library for use with Jetpack Compose

## Linear
A component for drawing linear graphs

## Initial Setup
Gradle (project)
``` kotlin
allprojects {
    repositories {
        google()
        mavenCentral()
        mavenLocal()
        maven {
            name = "GitHubPackages"

            // Replace GITHUB_USERID with your personal or organisation user ID and
            // REPOSITORY with the name of the repository on GitHub
            url = uri("https://maven.pkg.github.com/evd-evanss/sugarcharts")

            credentials {
                username = "revandro77@yahoo.com.br"
                password = "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"
            }
        }
    }
}
```
Gradle (app)
``` kotlin
depencencies {
    ...
    implementation("com.sugarspoon.library:charts:$latest_version")
    ...
}
```

### Sample

```Kotlin
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
```
    /**
     * SugarCharts Linear
     *
     * @param entries  [GraphEntry]
     *
     * @param enableGrids . default[true]
     *
     * @param enableAnimation Serve para .
     * default[false]
     *
     * @param smoothLines Serve para suavizar a linha de dados. default[true]
     *
     * @param drawValuesOnMarkers 
     *
     * @param aspectRatio .
     * default [3/2f]
     *
     * @param graphicColors . Veja [GraphicColors]
     *
     * @param paddingValues .
     */

| Param               |                                   Function                                    |
|---------------------|:-----------------------------------------------------------------------------:|
| entries             |                         Data entry to build the chart                         |
| enableGrids         |                          Enable or disable grid view                          |
| enableAnimation     |              enable or disable animation when drawing data line               |
| smoothLines         |                            smooths the data line.                             |
| drawValuesOnMarkers |              Enables the display of values on top of the markers              |
| aspectRatio         |        Attempts to scale the content to match a specified aspect ratio        |
| graphicColors       |                           Object with chart colors                            |
| paddingValues       | Describes the spacing to be applied between the edges and the Cartesian plane |