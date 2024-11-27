package com.swapface.benchmark

import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.BySelector
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
@RunWith(AndroidJUnit4::class)
class ExampleStartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()

    @Test
    fun startupSlowVariant() = benchmarkRule.measureRepeated(
        packageName = "com.swapface.article1project",
        metrics = listOf(StartupTimingMetric(), FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD
    ) {
        pressHome()
        startActivityAndWait()
        //todo wait for exo player show first frame

        //todo open next screen4
        val button = device.findObject(By.res("button"))
        val buttonSearchCondition = Until.hasObject(By.res("button"))
        button.wait(buttonSearchCondition, 5000)
        button.click()

        val nextScreen = device.findObject(By.res("next_screen"))
        val searchCondition = Until.hasObject(By.res("next_screen"))
        nextScreen.wait(searchCondition, 5000)
    }

    @Test
    fun startupFastVariant() = benchmarkRule.measureRepeated(
        packageName = "com.swapface.fast_variant",
        metrics = listOf(StartupTimingMetric() , FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD
    ) {
        pressHome()
        startActivityAndWait()
        //todo wait for exo player show first frame
        //todo open next screen
    }
}
