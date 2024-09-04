package com.example.fuel_efficiency_predictor

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.io.IOException
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class MainActivity : AppCompatActivity() {
    var mean: FloatArray = floatArrayOf(
        5.477707f,
        195.318471f,
        104.869427f,
        2990.251592f,
        15.559236f,
        75.898089f,
        1.573248f,
        0.624204f,
        0.178344f,
        0.197452f
    )
    var std: FloatArray = floatArrayOf(
        1.699788f,
        104.331589f,
        38.096214f,
        843.898596f,
        2.789230f,
        3.675642f,
        0.800988f,
        0.485101f,
        0.383413f,
        0.398712f
    )


    var interpreter: Interpreter? = null
    lateinit var sv: ScrollView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        //TODO load the model
        try {
            interpreter = Interpreter(loadModelFile())
        } catch (e: IOException) {
            e.printStackTrace()
        }


        //TODO init the views
        sv = findViewById<View>(R.id.sv) as ScrollView
        val cylinders = findViewById<EditText>(R.id.editText)
        val displacement = findViewById<EditText>(R.id.editText2)
        val horsePower = findViewById<EditText>(R.id.editText3)
        val weight = findViewById<EditText>(R.id.editText4)
        val accelration = findViewById<EditText>(R.id.editText5)
        val modelYear = findViewById<EditText>(R.id.editText6)
        val result = findViewById<TextView>(R.id.textView2)
        val btn = findViewById<Button>(R.id.button2)



        //TODO dropdown
        val origin = findViewById<Spinner>(R.id.spinner)
        val arrayAdapter = ArrayAdapter(
            applicationContext,
            android.R.layout.simple_spinner_dropdown_item,
            arrayOf("USA", "Europe", "Japan")
        )
        origin.adapter = arrayAdapter





        //TODO set on click listener on button
        btn.setOnClickListener {
            sv.scrollTo(sv.bottom, 0)
//            var cylindersVal = cylinders.text.toString().toFloat()
//            var displacementVal = displacement.text.toString().toFloat()
//            var horsePowerVal = horsePower.text.toString().toFloat()
//            var weightVal = weight.text.toString().toFloat()
//            var accelrationVal = accelration.text.toString().toFloat()
//            var modelYearVal = modelYear.text.toString().toFloat()
//            var originA = 0f
//            var originB = 0f
//            var originC = 0f
//            when (origin.selectedItemPosition) {
//                0 -> {
//                    originA = 1f
//                    originB = 0f
//                    originC = 0f
//                }
//
//                1 -> {
//                    originA = 0f
//                    originB = 1f
//                    originC = 0f
//                }
//
//                2 -> {
//                    originA = 0f
//                    originB = 0f
//                    originC = 1f
//                }
//            }
//            cylindersVal = (cylindersVal - mean[0]) / std[0]
//            displacementVal = (displacementVal - mean[1]) / std[1]
//            horsePowerVal = (horsePowerVal - mean[2]) / std[2]
//            weightVal = (weightVal - mean[3]) / std[3]
//            accelrationVal = (accelrationVal - mean[4]) / std[4]
//            modelYearVal = (modelYearVal - mean[5]) / std[5]
//            originA = (1 - mean[6]) / std[6]
//            originB = (0 - mean[7]) / std[7]
//            originC = (0 - mean[8]) / std[8]
//            val inputs = Array(1) { FloatArray(9) }
//            inputs[0][0] = cylindersVal
//            inputs[0][1] = displacementVal
//            inputs[0][2] = horsePowerVal
//            inputs[0][3] = weightVal
//            inputs[0][4] = accelrationVal
//            inputs[0][5] = modelYearVal
//            inputs[0][6] = originA
//            inputs[0][7] = originB
//            inputs[0][8] = originC
//
//            val res: Float = doInference(inputs)
//            result.text = String.format("%.2f", res) + " MPG"
        }
    }

    //TODO pass input to model and get output
    fun doInference(input: Array<FloatArray>): Float {
        val output = Array(1) { FloatArray(1) }

        interpreter!!.run(input, output)

        return output[0][0]
    }

    @Throws(IOException::class)
    private fun loadModelFile(): MappedByteBuffer {
        val assetFileDescriptor = this.assets.openFd("automobile.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor.startOffset
        val length = assetFileDescriptor.length
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, length)
    }
}