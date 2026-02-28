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
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab1_converter.ui.theme.Lab1converterTheme

sealed class ConversionUnit(val name: String, val category: String, val toBase: (Double) -> Double, val fromBase: (Double) -> Double) {
    // Расстояние (базовая ед. - метр)
    object Centimeters : ConversionUnit("см", "Расстояние", { it / 100.0 }, { it * 100.0 })
    object Meters : ConversionUnit("м", "Расстояние", { it }, { it })
    object Kilometers : ConversionUnit("км", "Расстояние", { it * 1000.0 }, { it / 1000.0 })
    object Miles : ConversionUnit("мл", "Расстояние", { it * 1609.34 }, { it / 1609.34 })

    // Масса (базовая ед. - килограмм)
    object Grams : ConversionUnit("г", "Масса", { it / 1000.0 }, { it * 1000.0 })
    object Kilograms : ConversionUnit("кг", "Масса", { it }, { it })
    object Centners : ConversionUnit("ц", "Масса", { it * 100.0 }, { it / 100.0 })
    object Tonnes : ConversionUnit("т", "Масса", { it * 1000.0 }, { it / 1000.0 })

    // Время (базовая ед. - минута)
    object Seconds : ConversionUnit("с", "Время", { it / 60.0 }, { it * 60.0 })
    object Minutes : ConversionUnit("мин", "Время", { it }, { it }) // ИСПРАВЛЕНО: "м" -> "мин"
    object Hours : ConversionUnit("ч", "Время", { it * 60.0 }, { it / 60.0 })
}

val allUnits = listOf(
    ConversionUnit.Centimeters, ConversionUnit.Meters, ConversionUnit.Kilometers, ConversionUnit.Miles,
    ConversionUnit.Grams, ConversionUnit.Kilograms, ConversionUnit.Centners, ConversionUnit.Tonnes,
    ConversionUnit.Seconds, ConversionUnit.Minutes, ConversionUnit.Hours
)

// "Сохранитель" для ConversionUnit, чтобы rememberSaveable знал, как с ним работать
val unitSaver = Saver<ConversionUnit, String>(
    save = { it.name },
    restore = { name -> allUnits.first { it.name == name } }
)

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
    // remember заменен на rememberSaveable
    var fromValue by rememberSaveable { mutableStateOf("") }
    var toValue by remember { mutableStateOf("") } // Это значение вычисляется, его сохранять не нужно
    var fromUnit by rememberSaveable(stateSaver = unitSaver) { mutableStateOf<ConversionUnit>(ConversionUnit.Meters) }
    var toUnit by rememberSaveable(stateSaver = unitSaver) { mutableStateOf<ConversionUnit>(ConversionUnit.Kilometers) }

    LaunchedEffect(fromValue, fromUnit, toUnit) {
        if (fromValue.isBlank()) {
            toValue = ""
            return@LaunchedEffect
        }
        val from = fromValue.toDoubleOrNull()
        if (from != null) {
            toValue = "%.4f".format(convert(from, fromUnit, toUnit))
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
        Row(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DataFragment(modifier = Modifier.weight(1f), fromValue = fromValue, toValue = toValue, fromUnit = fromUnit, toUnit = toUnit, onFromUnitChange = { fromUnit = it }, onToUnitChange = { toUnit = it }, onSwap = onSwap)
            KeyboardFragment(modifier = Modifier.weight(1f)) { key ->
                    fromValue = updateFromValue(fromValue, key)
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DataFragment(modifier = Modifier.weight(2.5f), fromValue = fromValue, toValue = toValue, fromUnit = fromUnit, toUnit = toUnit, onFromUnitChange = { fromUnit = it }, onToUnitChange = { toUnit = it }, onSwap = onSwap)
            KeyboardFragment(modifier = Modifier.weight(5f)) { key ->
                fromValue = updateFromValue(fromValue, key)
            }
        }
    }
}

fun updateFromValue(currentValue: String, key: String): String {
    val maxLength = 10
    return when (key) {
        "C" -> {
            if (currentValue.isNotEmpty()) {
                currentValue.dropLast(1)
            } else {
                currentValue
            }
        }
        "." -> {
            if (!currentValue.contains(".") && currentValue.length < maxLength) {
                currentValue + key
            } else {
                currentValue
            }
        }
        else -> {
            if (currentValue.length < maxLength) {
                currentValue + key
            } else {
                currentValue
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataFragment(
    modifier: Modifier = Modifier, 
    fromValue: String, 
    toValue: String, 
    fromUnit: ConversionUnit, 
    toUnit: ConversionUnit, 
    onFromUnitChange: (ConversionUnit) -> Unit, 
    onToUnitChange: (ConversionUnit) -> Unit, 
    onSwap: () -> Unit
) {
    var expandedFrom by remember { mutableStateOf(false) }
    var expandedTo by remember { mutableStateOf(false) }
    val toUnits = allUnits.filter { it.category == fromUnit.category }

    PremiumFeatures(fromValue = fromValue, toValue = toValue, onSwap = onSwap) { swapButton, copyFromButton, copyToButton ->
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fromValue,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("From") },
                    modifier = Modifier.weight(3f)
                )

                ExposedDropdownMenuBox(expanded = expandedFrom, onExpandedChange = { expandedFrom = !expandedFrom }, modifier = Modifier.weight(1.7f)) {
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
                copyFromButton()
            }

            swapButton()

            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = toValue,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("To") },
                    modifier = Modifier.weight(3f)
                )

                ExposedDropdownMenuBox(expanded = expandedTo, onExpandedChange = { expandedTo = !expandedTo }, modifier = Modifier.weight(1.7f)) {
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
                copyToButton()
            }
        }
    }
}

@Composable
fun KeyboardFragment(modifier: Modifier = Modifier, onKeyPress: (String) -> Unit) {
    Column(modifier = modifier) {
        Keyboard(onKeyPress = onKeyPress, modifier = Modifier.fillMaxSize())
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
