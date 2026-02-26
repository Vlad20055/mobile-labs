package com.example.lab1_converter

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab1_converter.ui.theme.Lab1converterTheme

sealed class ConversionUnit(val name: String, val category: String, val toBase: (Double) -> Double, val fromBase: (Double) -> Double) {
    object Meters : ConversionUnit("Meters", "Distance", { it }, { it })
    object Kilometers : ConversionUnit("Kilometers", "Distance", { it * 1000 }, { it / 1000 })
    object Miles : ConversionUnit("Miles", "Distance", { it * 1609.34 }, { it / 1609.34 })

    object Grams : ConversionUnit("Grams", "Weight", { it }, { it })
    object Kilograms : ConversionUnit("Kilograms", "Weight", { it * 1000 }, { it / 1000 })
    object Pounds : ConversionUnit("Pounds", "Weight", { it * 453.592 }, { it / 453.592 })

    object USD : ConversionUnit("USD", "Currency", { it }, { it })
    object EUR : ConversionUnit("EUR", "Currency", { it * 1.1 }, { it / 1.1 }) // Dummy rate
    object RUB : ConversionUnit("RUB", "Currency", { it * 0.011 }, { it / 0.011 }) // Dummy rate
}

val allUnits = listOf(ConversionUnit.Meters, ConversionUnit.Kilometers, ConversionUnit.Miles, ConversionUnit.Grams, ConversionUnit.Kilograms, ConversionUnit.Pounds, ConversionUnit.USD, ConversionUnit.EUR, ConversionUnit.RUB)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainApp()
        }
    }
}

@Composable
fun MainApp() {
    Lab1converterTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            ConverterApp()
        }
    }
}

@Composable
fun ConverterApp() {
    var fromValue by remember { mutableStateOf("") }
    var toValue by remember { mutableStateOf("") }
    var fromUnit by remember { mutableStateOf<ConversionUnit>(ConversionUnit.Meters) }
    var toUnit by remember { mutableStateOf<ConversionUnit>(ConversionUnit.Kilometers) }

    LaunchedEffect(fromValue, fromUnit, toUnit) {
        val from = fromValue.toDoubleOrNull()
        if (from != null) {
            toValue = convert(from, fromUnit, toUnit).toString()
        }
    }

    val onSwap = { 
        val tempUnit = fromUnit
        fromUnit = toUnit
        toUnit = tempUnit

        val tempValue = fromValue
        fromValue = toValue
        toValue = tempValue
    }

    val configuration = LocalConfiguration.current
    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Row(modifier = Modifier.fillMaxSize()) {
            DataFragment(modifier = Modifier.weight(1f), fromValue = fromValue, toValue = toValue, fromUnit = fromUnit, toUnit = toUnit, onFromUnitChange = { fromUnit = it }, onToUnitChange = { toUnit = it }, onSwap = onSwap)
            KeyboardFragment(modifier = Modifier.weight(1f)) { key ->
                    fromValue = updateFromValue(fromValue, key)
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize()) {
            DataFragment(modifier = Modifier.weight(1f), fromValue = fromValue, toValue = toValue, fromUnit = fromUnit, toUnit = toUnit, onFromUnitChange = { fromUnit = it }, onToUnitChange = { toUnit = it }, onSwap = onSwap)
            KeyboardFragment(modifier = Modifier.weight(1f)) { key ->
                fromValue = updateFromValue(fromValue, key)
            }
        }
    }
}

fun updateFromValue(currentValue: String, key: String): String {
    return when (key) {
        "C" -> {
            if (currentValue.isNotEmpty()) {
                currentValue.dropLast(1)
            } else {
                currentValue
            }
        }
        "." -> {
            if (!currentValue.contains(".")) {
                currentValue + key
            } else {
                currentValue
            }
        }
        else -> currentValue + key
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataFragment(modifier: Modifier = Modifier, fromValue: String, toValue: String, fromUnit: ConversionUnit, toUnit: ConversionUnit, onFromUnitChange: (ConversionUnit) -> Unit, onToUnitChange: (ConversionUnit) -> Unit, onSwap: () -> Unit) {
    var expandedFrom by remember { mutableStateOf(false) }
    var expandedTo by remember { mutableStateOf(false) }
    val toUnits = allUnits.filter { it.category == fromUnit.category }

    Column(modifier = modifier.padding(16.dp), verticalArrangement = Arrangement.SpaceEvenly) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = fromValue,
                onValueChange = { },
                readOnly = true,
                label = { Text("From") }
            )

            ExposedDropdownMenuBox(expanded = expandedFrom, onExpandedChange = { expandedFrom = !expandedFrom }) {
                OutlinedTextField(
                    value = fromUnit.name,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedFrom) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = expandedFrom, onDismissRequest = { expandedFrom = false }) {
                    allUnits.forEach {
                        DropdownMenuItem(text = { Text(it.name) }, onClick = { 
                            onFromUnitChange(it)
                            if (it.category != toUnit.category) {
                                onToUnitChange(allUnits.first { u -> u.category == it.category && u != it })
                            }
                            expandedFrom = false
                        })
                    }
                }
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = toValue,
                onValueChange = { },
                readOnly = true,
                label = { Text("To") }
            )

            ExposedDropdownMenuBox(expanded = expandedTo, onExpandedChange = { expandedTo = !expandedTo }) {
                OutlinedTextField(
                    value = toUnit.name,
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedTo) },
                    modifier = Modifier.menuAnchor()
                )
                ExposedDropdownMenu(expanded = expandedTo, onDismissRequest = { expandedTo = false }) {
                    toUnits.forEach {
                        DropdownMenuItem(text = { Text(it.name) }, onClick = { 
                            onToUnitChange(it)
                            expandedTo = false
                        })
                    }
                }
            }
        }
        PremiumFeatures(fromValue = fromValue, toValue = toValue, onSwap = onSwap)
    }
}

@Composable
fun KeyboardFragment(modifier: Modifier = Modifier, onKeyPress: (String) -> Unit) {
    Column(modifier = modifier.padding(16.dp)) {
        Keyboard(onKeyPress)
    }
}

fun convert(value: Double, from: ConversionUnit, to: ConversionUnit): Double {
    if (from.category != to.category) return Double.NaN
    val baseValue = from.toBase(value)
    return to.fromBase(baseValue)
}

@Preview(showBackground = true, device = "spec:width=411dp,height=891dp")
@Composable
fun DefaultPreview() {
    MainApp()
}

@Preview(showBackground = true, device = "spec:width=891dp,height=411dp")
@Composable
fun LandscapePreview() {
    MainApp()
}
