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
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lab1_converter.ui.theme.Lab1converterTheme
import java.math.BigDecimal
import java.math.RoundingMode

sealed class ConversionUnit(
    val name: String, 
    val category: String, 
    val toBase: (BigDecimal) -> BigDecimal, 
    val fromBase: (BigDecimal) -> BigDecimal
) {
    // Расстояние (базовая ед. - метр)
    object Centimeters : ConversionUnit("см", "Расстояние", 
        { it.divide(BigDecimal("100"), 20, RoundingMode.HALF_UP) }, 
        { it.multiply(BigDecimal("100")) })
    object Meters : ConversionUnit("м", "Расстояние", { it }, { it })
    object Kilometers : ConversionUnit("км", "Расстояние", 
        { it.multiply(BigDecimal("1000")) }, 
        { it.divide(BigDecimal("1000"), 20, RoundingMode.HALF_UP) })
    object Miles : ConversionUnit("мл", "Расстояние", 
        { it.multiply(BigDecimal("1609.34")) }, 
        { it.divide(BigDecimal("1609.34"), 20, RoundingMode.HALF_UP) })

    // Масса (базовая ед. - килограмм)
    object Grams : ConversionUnit("г", "Масса", 
        { it.divide(BigDecimal("1000"), 20, RoundingMode.HALF_UP) }, 
        { it.multiply(BigDecimal("1000")) })
    object Kilograms : ConversionUnit("кг", "Масса", { it }, { it })
    object Centners : ConversionUnit("ц", "Масса", 
        { it.multiply(BigDecimal("100")) }, 
        { it.divide(BigDecimal("100"), 20, RoundingMode.HALF_UP) })
    object Tonnes : ConversionUnit("т", "Масса", 
        { it.multiply(BigDecimal("1000")) }, 
        { it.divide(BigDecimal("1000"), 20, RoundingMode.HALF_UP) })

    // Время (базовая ед. - минута)
    object Seconds : ConversionUnit("с", "Время", 
        { it.divide(BigDecimal("60"), 20, RoundingMode.HALF_UP) }, 
        { it.multiply(BigDecimal("60")) })
    object Minutes : ConversionUnit("мин", "Время", { it }, { it })
    object Hours : ConversionUnit("ч", "Время", 
        { it.multiply(BigDecimal("60")) }, 
        { it.divide(BigDecimal("60"), 20, RoundingMode.HALF_UP) })
}

val allUnits = listOf(
    ConversionUnit.Centimeters, ConversionUnit.Meters, ConversionUnit.Kilometers, ConversionUnit.Miles,
    ConversionUnit.Grams, ConversionUnit.Kilograms, ConversionUnit.Centners, ConversionUnit.Tonnes,
    ConversionUnit.Seconds, ConversionUnit.Minutes, ConversionUnit.Hours
)

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
    var fromValue by rememberSaveable(stateSaver = TextFieldValue.Saver) { 
        mutableStateOf(TextFieldValue("")) 
    }
    var toValue by remember { mutableStateOf(TextFieldValue("")) }
    
    var fromUnit by rememberSaveable(stateSaver = unitSaver) { mutableStateOf<ConversionUnit>(ConversionUnit.Meters) }
    var toUnit by rememberSaveable(stateSaver = unitSaver) { mutableStateOf<ConversionUnit>(ConversionUnit.Kilometers) }

    LaunchedEffect(fromValue.text, fromUnit, toUnit) {
        if (fromValue.text.isBlank() || fromValue.text == ".") {
            toValue = TextFieldValue("")
            return@LaunchedEffect
        }
        
        try {
            val from = BigDecimal(fromValue.text)
            val result = convert(from, fromUnit, toUnit)
                .setScale(6, RoundingMode.HALF_UP)
                .stripTrailingZeros()
            
            val resultText = result.toPlainString()
            
            toValue = TextFieldValue(
                text = resultText,
                selection = TextRange(resultText.length)
            )
        } catch (e: Exception) {
            toValue = TextFieldValue("Ошибка")
        }
    }

    val onSwap = {
        val tempUnit = fromUnit
        fromUnit = toUnit
        toUnit = tempUnit

        val oldFromText = fromValue.text
        val oldToText = toValue.text
        
        fromValue = TextFieldValue(oldToText, TextRange(oldToText.length))
        toValue = TextFieldValue(oldFromText, TextRange(oldFromText.length))
    }

    val configuration = LocalConfiguration.current
    if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        Row(modifier = Modifier.fillMaxSize().padding(16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            DataFragment(modifier = Modifier.weight(1f), fromValue = fromValue, toValue = toValue, fromUnit = fromUnit, toUnit = toUnit, onFromUnitChange = { fromUnit = it }, onToUnitChange = { toUnit = it }, onSwap = onSwap)
            KeyboardFragment(modifier = Modifier.weight(1f)) { key ->
                val newText = updateFromValue(fromValue.text, key)
                fromValue = TextFieldValue(newText, TextRange(newText.length))
            }
        }
    } else {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            DataFragment(modifier = Modifier.weight(2.5f), fromValue = fromValue, toValue = toValue, fromUnit = fromUnit, toUnit = toUnit, onFromUnitChange = { fromUnit = it }, onToUnitChange = { toUnit = it }, onSwap = onSwap)
            KeyboardFragment(modifier = Modifier.weight(5f)) { key ->
                val newText = updateFromValue(fromValue.text, key)
                fromValue = TextFieldValue(newText, TextRange(newText.length))
            }
        }
    }
}

fun updateFromValue(currentValue: String, key: String): String {
    val maxLength = 18
    return when (key) {
        "AC" -> ""
        "C" -> if (currentValue.isNotEmpty()) currentValue.dropLast(1) else currentValue
        "." -> if (!currentValue.contains(".") && currentValue.length < maxLength) {
            if (currentValue.isEmpty()) "0." else currentValue + key
        } else currentValue
        else -> if (currentValue.length < maxLength) currentValue + key else currentValue
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DataFragment(
    modifier: Modifier = Modifier, 
    fromValue: TextFieldValue, 
    toValue: TextFieldValue, 
    fromUnit: ConversionUnit, 
    toUnit: ConversionUnit, 
    onFromUnitChange: (ConversionUnit) -> Unit, 
    onToUnitChange: (ConversionUnit) -> Unit, 
    onSwap: () -> Unit
) {
    var expandedFrom by remember { mutableStateOf(false) }
    var expandedTo by remember { mutableStateOf(false) }
    val toUnits = allUnits.filter { it.category == fromUnit.category }

    PremiumFeatures(fromValue = fromValue.text, toValue = toValue.text, onSwap = onSwap) { swapButton, copyFromButton, copyToButton ->
        Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = fromValue,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("From") },
                    singleLine = true,
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
                    singleLine = true,
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

fun convert(value: BigDecimal, from: ConversionUnit, to: ConversionUnit): BigDecimal {
    if (from.category != to.category) return BigDecimal.ZERO
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
