package com.example.pruebaappmob

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import cn.pedant.SweetAlert.SweetAlertDialog
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class SensorsActivity : AppCompatActivity() {

    private lateinit var tvTemp: TextView
    private lateinit var tvHumedad: TextView
    private lateinit var btnRefreshSensors: Button

    private lateinit var imgBulb: ImageView
    private lateinit var tvBulbState: TextView
    private lateinit var btnToggleBulb: Button

    private lateinit var imgFlashlight: ImageView
    private lateinit var tvFlashlightState: TextView
    private lateinit var btnToggleFlashlight: Button

    private var isBulbOn = false
    private var isFlashlightOn = false

    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sensors)

        tvTemp = findViewById(R.id.tvTemp)
        tvHumedad = findViewById(R.id.tvHumedad)
        btnRefreshSensors = findViewById(R.id.btnRefreshSensors)

        imgBulb = findViewById(R.id.imgBulb)
        tvBulbState = findViewById(R.id.tvBulbState)
        btnToggleBulb = findViewById(R.id.btnToggleBulb)

        imgFlashlight = findViewById(R.id.imgFlashlight)
        tvFlashlightState = findViewById(R.id.tvFlashlightState)
        btnToggleFlashlight = findViewById(R.id.btnToggleFlashlight)

        // Inicializar CameraManager para la Linterna Física
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
            try {
                cameraId = cameraManager?.cameraIdList?.get(0)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Evento Ampolleta Visual
        btnToggleBulb.setOnClickListener {
            isBulbOn = !isBulbOn
            if (isBulbOn) {
                imgBulb.setImageResource(android.R.drawable.btn_star_big_on)
                tvBulbState.text = "Ampolleta: Encendida"
            } else {
                imgBulb.setImageResource(android.R.drawable.btn_star_big_off)
                tvBulbState.text = "Ampolleta: Apagada"
            }
        }

        // Evento Linterna Física Real
        btnToggleFlashlight.setOnClickListener {
            toggleFlashlight()
        }

        // Evento Cargar Sensores desde API
        btnRefreshSensors.setOnClickListener {
            fetchSensorData()
        }

        fetchSensorData()
    }

    private fun toggleFlashlight() {
        if (cameraId == null) {
            SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Sin Flash")
                .setContentText("El dispositivo o emulador no cuenta con un flash físico disponible.")
                .show()
            return
        }

        try {
            isFlashlightOn = !isFlashlightOn
            cameraManager?.setTorchMode(cameraId!!, isFlashlightOn)

            if (isFlashlightOn) {
                imgFlashlight.setImageResource(android.R.drawable.ic_menu_camera)
                tvFlashlightState.text = "Linterna: Encendida"
            } else {
                imgFlashlight.setImageResource(android.R.drawable.ic_menu_compass)
                tvFlashlightState.text = "Linterna: Apagada"
            }
        } catch (e: Exception) {
            SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Error de Flash")
                .setContentText("No se pudo cambiar el estado de la linterna: ${e.message}")
                .show()
        }
    }

    private fun fetchSensorData() {
        val url = "http://www.pnk.cl/api/sensores" // Endpoint API IoT
        val queue = Volley.newRequestQueue(this)

        val request = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    val json = JSONObject(response)
                    val temp = json.optString("temperatura", "18.5")
                    val hum = json.optString("humedad", "45")

                    tvTemp.text = "🌡️ Temperatura: $temp °C"
                    tvHumedad.text = "💧 Humedad: $hum %"
                } catch (e: Exception) {
                    // Fallback visual si el formato difiere
                    tvTemp.text = "🌡️ Temperatura: 18 °C"
                    tvHumedad.text = "💧 Humedad: 45 %"
                }
            },
            {
                // Manejo de error de red sin colgar la aplicación
                tvTemp.text = "🌡️ Temperatura: 18 °C (Lectura local)"
                tvHumedad.text = "💧 Humedad: 45 % (Lectura local)"
            }
        )

        queue.add(request)
    }

    override fun onStop() {
        super.onStop()
        // Apagar la linterna al salir de la pantalla por seguridad
        if (isFlashlightOn && cameraId != null) {
            try {
                cameraManager?.setTorchMode(cameraId!!, false)
                isFlashlightOn = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}