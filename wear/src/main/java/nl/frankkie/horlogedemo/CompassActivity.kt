package nl.frankkie.horlogedemo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.wearable.activity.WearableActivity
import android.util.Log
import android.view.View
import kotlinx.android.synthetic.main.activity_compass.*

class CompassActivity : WearableActivity(), SensorEventListener {

    private var showDebug = true
    private var rot = 0f

    //
    private lateinit var sensorManager: SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        setContentView(R.layout.activity_compass)
        // Enables Always-on
        setAmbientEnabled()

        compass_root.setOnClickListener {
            showDebug = !showDebug
            compass_tv.visibility = if (showDebug) View.VISIBLE else View.GONE
            compass_tv.invalidate()
        }

        Log.v("Horloge", "compass act, onCreate")
    }

    override fun onResume() {
        super.onResume()
        //Start sensor

        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
            sensorManager.registerListener(
                this,
                accelerometer,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
            sensorManager.registerListener(
                this,
                magneticField,
                SensorManager.SENSOR_DELAY_NORMAL,
                SensorManager.SENSOR_DELAY_UI
            )
        }

    }

    override fun onPause() {
        super.onPause()
        Log.v("Horloge", "compass act, onPause")

    }

    override fun onStop() {
        super.onStop()
        //Stop sensor
        sensorManager.unregisterListener(this)
        Log.v("Horloge", "compass act, onStop")
    }

    private fun updateCompass() {
        //rot++
        val degrees = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
//        val degrees = rot % 360
        compass_view?.updateCompass(degrees)

        if (showDebug) {
            compass_tv.text = "degrees: $degrees"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("Horloge C", "onAccuracyChanged $sensor - $accuracy")
    }

    override fun onSensorChanged(event: SensorEvent?) {
        Log.v("Horloge C", "onSensorChanged $event")
        event?.let {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(
                    event.values,
                    0,
                    accelerometerReading,
                    0,
                    accelerometerReading.size
                )
            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
            }
        }

        updateOrientationAngles()
    }

    // Compute the three orientation angles based on the most recent readings from
    // the device's accelerometer and magnetometer.
    private fun updateOrientationAngles() {
        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )

        // "mRotationMatrix" now has up-to-date information.
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        // "mOrientationAngles" now has up-to-date information.

        updateCompass()
    }
}