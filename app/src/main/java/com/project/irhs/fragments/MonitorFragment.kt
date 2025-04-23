package com.project.irhs.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.project.irhs.analysis.model.AnalysisByDateYear
import com.project.irhs.analysis.model.DataX
import com.project.irhs.api.ApiUtilities
import com.project.irhs.databinding.FragmentMonitorBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MonitorFragment : Fragment() {
    private lateinit var binding: FragmentMonitorBinding
    private lateinit var barChart: BarChart
    private var selectedYear = ""
    private var selectedMonth = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMonitorBinding.inflate(inflater, container, false)
        barChart = binding.barChart

        setupSpinners()
        return binding.root
    }

    private fun setupSpinners() {
        fetchSpinnerData { data ->
            val years = getYears(data)

            // Setup Year Spinner
            setupSpinner(binding.spinnerYear, years) { year ->
                selectedYear = year
                Toast.makeText(context, "Selected Year: $year", Toast.LENGTH_SHORT).show()

                // Fetch months for the selected year and update the month spinner
                val monthsForYear = getMonthsForYear(data, year)
                setupSpinner(binding.spinnerMonth, monthsForYear) { month ->
                    selectedMonth = month
                    Toast.makeText(context, "Selected Month: $month", Toast.LENGTH_SHORT).show()

                    // Fetch analysis data when both year and month are selected
                    if (selectedYear.isNotEmpty() && selectedMonth.isNotEmpty()) {
                        fetchAnalysisData(
                            selectedYear,
                            getMonthNumber(data, selectedYear, selectedMonth)
                        )
                    }
                }
            }
        }
    }

    private fun fetchSpinnerData(callback: (List<DataX>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiUtilities.getInstance().selectDateAndMonth()
                if (response.isSuccessful && response.body() != null) {
                    val data = response.body()!!.data
                    withContext(Dispatchers.Main) {
                        callback(data)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to fetch spinner data", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupSpinner(
        spinner: Spinner,
        items: List<String>,
        onItemSelected: (String) -> Unit
    ) {
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, items).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }
        spinner.adapter = adapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                onItemSelected(items[position])
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun fetchAnalysisData(year: String, month: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = ApiUtilities.getInstance().getAnalysisByMonthYear(month, year)
                if (response.isSuccessful && response.body() != null) {
                    val analysisData = response.body()!!
                    withContext(Dispatchers.Main) {
                        setupBarChart(analysisData)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Failed to fetch analysis data", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupBarChart(analysisData: AnalysisByDateYear) {
        val usageEntries = mutableListOf<BarEntry>()
        val rainEntries = mutableListOf<BarEntry>()

        analysisData.data.forEachIndexed { index, data ->
            if (data.type == "usage") {
                usageEntries.add(BarEntry(index.toFloat(), data.average_quantity.toFloat()))
            } else if (data.type == "rain") {
                rainEntries.add(BarEntry(index.toFloat(), data.average_quantity.toFloat()))
            }
        }
        // Water Usage color - Light Blue (#ADD8E6)
        val usageDataSet = BarDataSet(usageEntries, "Water Usage").apply {
            color = android.graphics.Color.parseColor("#ADD8E6") // Light Blue Color
            valueTextColor = android.graphics.Color.BLACK
            setDrawValues(false) // Disable the value labels
        }
        // Rainfall color - Light Cyan (#E0FFFF)
        val rainDataSet = BarDataSet(rainEntries, "Rainfall").apply {
            color = android.graphics.Color.parseColor("#E0FFFF") // Light Cyan Color
            valueTextColor = android.graphics.Color.BLACK
            setDrawValues(false) // Disable the value labels
        }

        val barData = BarData(usageDataSet, rainDataSet)
        barChart.data = barData

        // Setting X Axis properties
        barChart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            granularity = 1f
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    return analysisData.data.getOrNull(value.toInt())?.date ?: ""
                }
            }
        }
        // Setting Y Axis properties to prevent negative values
        barChart.axisLeft.apply {
            axisMinimum = 0f // Ensuring the Y axis starts from 0
            setDrawGridLines(true)
        }
        // Disabling the right Y Axis as it's usually not needed
        barChart.axisRight.isEnabled = false

        // Setting the legend to show the line form
        barChart.legend.form = Legend.LegendForm.LINE

        // Disable bar highlight (selected color change)
        barChart.setHighlightPerTapEnabled(false)

        // Enable zooming and scrolling
        barChart.setTouchEnabled(true) // Keep touch enabled for zoom and scrolling
        barChart.setDragEnabled(true)  // Allow dragging (scrolling)
        barChart.setScaleEnabled(true) // Allow zooming

        // Refresh the chart
        barChart.invalidate()
    }

    private fun getYears(data: List<DataX>): List<String> {
        return data.map { it.year }.distinct()
    }

    private fun getMonthsForYear(data: List<DataX>, year: String): List<String> {
        return data.filter { it.year == year }.map { it.month_name }
    }

    private fun getMonthNumber(data: List<DataX>, year: String, monthName: String): String {
        return data.firstOrNull { it.year == year && it.month_name == monthName }?.month ?: "1"
    }
}