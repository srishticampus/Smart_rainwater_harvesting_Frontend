package com.project.irhs.fragments

<<<<<<< HEAD
import android.app.AlertDialog
=======
>>>>>>> dea137607b8f09b7cdf920506f050450eafa8766
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
<<<<<<< HEAD
import com.google.firebase.database.*
import com.project.irhs.R
import com.project.irhs.databinding.FragmentHomeBinding
import kotlinx.coroutines.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
=======
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
>>>>>>> dea137607b8f09b7cdf920506f050450eafa8766

class HomeFragment : Fragment() {

    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private val scope = CoroutineScope(Dispatchers.Main + Job())
<<<<<<< HEAD
=======

>>>>>>> dea137607b8f09b7cdf920506f050450eafa8766
    lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference

    private var previousWaterLevel: Float = 0f
    private var waterUsage: Float = 0f
<<<<<<< HEAD
    private var isPumpAlertShown = false
    private var isTankWaterAlertShown = false
=======
    private var rainfall: Float = 0f
>>>>>>> dea137607b8f09b7cdf920506f050450eafa8766

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
<<<<<<< HEAD
=======
        // Inflate the layout for this fragment
>>>>>>> dea137607b8f09b7cdf920506f050450eafa8766
        binding = FragmentHomeBinding.inflate(layoutInflater)

        progressBar = binding.circularProgressBar
        progressText = binding.progressText

<<<<<<< HEAD
=======
        // Initially show progress bar while loading data
>>>>>>> dea137607b8f09b7cdf920506f050450eafa8766
        progressBar.visibility = View.VISIBLE
        progressText.text = "Loading..."

        initializeDatabaseListener()

        return binding.root
    }

    private fun initializeDatabaseListener() {
        database = FirebaseDatabase.getInstance().reference

<<<<<<< HEAD
=======
        // Attach ValueEventListener to listen for data changes
>>>>>>> dea137607b8f09b7cdf920506f050450eafa8766
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    val ph_value = snapshot.child("Sensor/PH").value
                    val currentWaterLevel =
                        snapshot.child("Sensor/Distance").value?.toString()?.toFloatOrNull()
                    val moist = snapshot.child("Sensor/Moisture").value?.toString()?.toIntOrNull()
                    val rain = snapshot.child("Sensor/Rain").value?.toString()?.toIntOrNull()

<<<<<<< HEAD
                    // Check rain status: < 4000 = Raining, >= 4000 = Not Raining
                    val isRaining = rain != null && rain < 4000
                    binding.rain.text = if (isRaining) "Raining" else "Not Raining"

                    // Check soil moisture: < 4000 = Wet, >= 4000 = Dry
                    val isSoilDry = moist != null && moist >= 4000
                    binding.moist.text = if (isSoilDry) "Dry" else "Wet"

                    // Show soil dry alert once
                    if (isSoilDry && !isPumpAlertShown) {
                        isPumpAlertShown = true
                        showAlert(
                            "Water Pump Alert",
                            "Soil is dry! The water pump has been turned ON."
                        ) {}
                    } else if (!isSoilDry) {
                        isPumpAlertShown = false
                    }
                    // Display pH value and water level
                    binding.phvalue.text = ph_value?.toString() ?: "N/A"
                    binding.progressText.text = currentWaterLevel?.toString() ?: "N/A"

                    // Show tank water alert once if level > 10
                    if (currentWaterLevel != null) {
                        if (currentWaterLevel > 10 && !isTankWaterAlertShown) {
                            isTankWaterAlertShown = true
                            showAlert(
                                "Tank Water Alert",
                                "Water level is Low! Use household water."
                            ) {}
                        } else if (currentWaterLevel <= 10) {
                            isTankWaterAlertShown = false
                        }
                    }
                    // Track water usage when not raining
                    if (currentWaterLevel != null) {
                        val diff = currentWaterLevel - previousWaterLevel
                        if (!isRaining) {
                            waterUsage += diff
                            binding.wateringValue.text = "Water Usage: $waterUsage liters"
                            sendDataToPhpServer(waterUsage, "usage")
                        }
                        previousWaterLevel = currentWaterLevel
                    }
                    // Update watering status
                    val isPumpOn = isSoilDry
                    binding.wateringValue.text = if (isPumpOn) "Active" else "Disabled"

                    // Update sun image
                    rain?.let {
                        binding.sunImg.setImageResource(
                            if (it >= 4000) R.drawable.rain else R.drawable.mazha
                        )
                        binding.lidImg.setImageResource(
                            if (it >= 4000) R.drawable.closelid else R.drawable.openlid
                        )
                    }
=======
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
>>>>>>> dea137607b8f09b7cdf920506f050450eafa8766
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

<<<<<<< HEAD
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
                val url =
                    URL("http://campus.sicsglobal.co.in/Project/rain_water/api/store_sensor_data.php")
=======
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
>>>>>>> dea137607b8f09b7cdf920506f050450eafa8766
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.setRequestProperty("Content-Type", "application/json")
                connection.doOutput = true

<<<<<<< HEAD
                connection.outputStream.use { it.write(jsonBody.toByteArray()) }

=======
                // Send the JSON data
                connection.outputStream.use { outputStream ->
                    outputStream.write(jsonBody.toByteArray())
                }

                // Read the response
>>>>>>> dea137607b8f09b7cdf920506f050450eafa8766
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Log.d("ServerResponse", "Data sent successfully")
                } else {
                    Log.e("ServerResponse", "Failed to send data: $responseCode")
                }
<<<<<<< HEAD
=======

>>>>>>> dea137607b8f09b7cdf920506f050450eafa8766
                connection.disconnect()
            } catch (e: Exception) {
                Log.e("HttpError", "Error sending data to PHP server: ${e.message}")
            }
        }
    }
<<<<<<< HEAD
    override fun onDestroyView() {
        super.onDestroyView()
        scope.cancel()
    }

    private fun showAlert(title: String, message: String, onDismiss: () -> Unit) {
        AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { dialog, _ ->
                onDismiss()
                dialog.dismiss()
            }
            .show()
=======

    override fun onDestroyView() {
        super.onDestroyView()
        scope.cancel() // Cancel coroutines to avoid memory leaks
>>>>>>> dea137607b8f09b7cdf920506f050450eafa8766
    }
}
