package com.project.irhs.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.irhs.R
import com.project.irhs.databinding.FragmentHomeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference

    private var previousWaterLevel: Float = 0f
    private var waterUsage: Float = 0f
    private var rainfall: Float = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentHomeBinding.inflate(layoutInflater)

        progressBar = binding.circularProgressBar
        progressText = binding.progressText

        // Initially show progress bar while loading data
        progressBar.visibility = View.VISIBLE
        progressText.text = "Loading..."

        initializeDatabaseListener()

        return binding.root
    }

    private fun initializeDatabaseListener() {
        database = FirebaseDatabase.getInstance().reference

        // Attach ValueEventListener to listen for data changes
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val ph_value = snapshot.child("Sensor/PH").value
                    val currentWaterLevel =
                        snapshot.child("Sensor/Distance").value?.toString()?.toFloatOrNull()
                    val moist = snapshot.child("Sensor/Moisture").value?.toString()?.toIntOrNull()
                    val rain = snapshot.child("Sensor/Rain").value?.toString()?.toIntOrNull()

                    binding.rain.text = rain?.toString() ?: "N/A"
                    binding.phvalue.text = ph_value?.toString() ?: "N/A"
                    binding.progressText.text = currentWaterLevel?.toString() ?: "N/A"
                    binding.moist.text = moist?.toString() ?: "N/A"

                    // Calculate the difference in water level if available
                    if (currentWaterLevel != null) {
                        val waterLevelDifference = currentWaterLevel - previousWaterLevel

                        // Check if the rain value is greater than 4000 for water usage
                        if (rain != null && rain > 4000) {
                            // If rain is greater than 4000, it's water usage
                            waterUsage += waterLevelDifference
                            binding.wateringValue.text = "Water Usage: $waterUsage liters"

                            // Send water usage data to server
                            sendDataToPhpServer(waterUsage, "usage")
                        } else if (rain != null && rain <= 4000) {
                            // If rain is less than or equal to 4000, it's rainfall
                            rainfall += waterLevelDifference
                            binding.wateringValue.text = "Rainfall: $rainfall mm"

                            // Send rainfall data to server
                            sendDataToPhpServer(rainfall, "rain")
                        }

                        // Update the previous water level
                        previousWaterLevel = currentWaterLevel
                    }

                    // Update watering status based on moisture
                    if (moist != null) {
                        binding.wateringValue.text = if (moist > 4000) "Active" else "Disabled"
                    }

                    // Change sun image based on rain value
                    if (rain != null) {
                        binding.sunImg.setImageResource(
                            if (rain > 4000) R.drawable.rain else R.drawable.raining
                        )
                    }

                    // Hide progress bar when data is loaded
                    progressBar.visibility = View.GONE

                } catch (e: Exception) {
                    Log.e("FirebaseError", "Error fetching data: ${e.message}")
                    Toast.makeText(requireContext(), "Error loading data", Toast.LENGTH_SHORT)
                        .show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                Toast.makeText(
                    requireActivity(),
                    "Failed to read sensor data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun sendDataToPhpServer(quantity: Float, type: String) {
        val currentDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        // Prepare the JSON body
        val jsonBody = """
        {
            "quantity": $quantity,
            "type": "$type",
            "date": "$currentDate",
            "time": "$currentTime"
        }
    """.trimIndent()

        scope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://campus.sicsglobal.co.in/Project/rain_water/api/store_sensor_data.php")
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

                // Send the JSON data
                connection.outputStream.use { outputStream ->
                    outputStream.write(jsonBody.toByteArray())
                }

                // Read the response
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("ServerResponse", "Data sent successfully")
                } else {
                    Log.e("ServerResponse", "Failed to send data: $responseCode")
                }

                connection.disconnect()
            } catch (e: Exception) {
                Log.e("HttpError", "Error sending data to PHP server: ${e.message}")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scope.cancel() // Cancel coroutines to avoid memory leaks
    }
}
