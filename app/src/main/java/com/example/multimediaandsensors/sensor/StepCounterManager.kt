package com.example.multimediaandsensors.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue

class StepCounterManager(
    private val context: Context
) : SensorEventListener {

    private val sensorManager =
        context.getSystemService(
            Context.SENSOR_SERVICE
        ) as SensorManager

    private val stepSensor =
        sensorManager.getDefaultSensor(
            Sensor.TYPE_STEP_COUNTER
        )

    val isSensorAvailable: Boolean
        get() = stepSensor != null

    // TYPE_STEP_COUNTER reports the total steps since reboot.
    var steps by mutableIntStateOf(0)
        private set

    fun start() {
        stepSensor?.let {
            sensorManager.registerListener(
                this,
                it,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(
        event: SensorEvent?
    ) {
        if (event?.sensor?.type ==
            Sensor.TYPE_STEP_COUNTER
        ) {
            steps =
                event.values[0].toInt()
        }
    }

    override fun onAccuracyChanged(
        sensor: Sensor?,
        accuracy: Int
    ) {
    }
}
