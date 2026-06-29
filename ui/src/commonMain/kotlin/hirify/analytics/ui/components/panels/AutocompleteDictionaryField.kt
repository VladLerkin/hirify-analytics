package hirify.analytics.ui.components.panels

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import hirify.analytics.core.analytics.DictionaryItem
import hirify.analytics.core.analytics.HirifyApiClient
import hirify.analytics.ui.i18n.LocalAppStrings
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AutocompleteDictionaryField(
    apiClient: HirifyApiClient,
    dictionary: String,
    currentValue: String?,
    onValueChanged: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedItems = remember(currentValue) { 
        currentValue?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList() 
    }
    
    val codeToNameCache = remember { mutableStateMapOf<String, String>() }
    var query by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var suggestions by remember { mutableStateOf<List<DictionaryItem>>(emptyList()) }
    var searchJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val strings = LocalAppStrings.current

    LaunchedEffect(dictionary) {
        val result = apiClient.searchDictionary(dictionary, "")
        result.onSuccess { list ->
            list.forEach { item -> 
                item.name?.let { codeToNameCache[item.code] = it }
            }
        }
    }

    fun performSearch(text: String, immediate: Boolean = false) {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            if (!immediate) delay(300) // debounce
            val result = apiClient.searchDictionary(dictionary, text)
            result.onSuccess { list ->
                list.forEach { item -> 
                    item.name?.let { codeToNameCache[item.code] = it }
                }
                val lowerQuery = text.lowercase()
                val filteredList = if (lowerQuery.isNotBlank()) {
                    list.filter { item ->
                        item.name?.lowercase()?.contains(lowerQuery) == true ||
                        item.code.lowercase().contains(lowerQuery)
                    }
                } else {
                    list
                }
                suggestions = filteredList
                if (filteredList.isNotEmpty()) {
                    expanded = true
                }
            }
        }
    }

    Column(modifier = modifier) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { 
                expanded = it 
                if (it && suggestions.isEmpty()) {
                    performSearch(query, immediate = true)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { newValue ->
                    query = newValue
                    performSearch(newValue)
                    expanded = true
                },
                placeholder = { Text(strings.searchPlaceholder) },
                modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryEditable, true).fillMaxWidth(),
                singleLine = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
            )

            if (suggestions.isNotEmpty()) {
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    suggestions.forEach { item ->
                        val isSelected = selectedItems.contains(item.code)
                        DropdownMenuItem(
                            text = { Text(item.name ?: item.code) },
                            onClick = {
                                val newItems = if (isSelected) {
                                    selectedItems - item.code
                                } else {
                                    selectedItems + item.code
                                }
                                onValueChanged(if (newItems.isEmpty()) null else newItems.joinToString(","))
                                // Do not close the dropdown so user can select multiple
                            },
                            trailingIcon = {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = "Selected")
                                }
                            }
                        )
                    }
                }
            }
        }

        if (selectedItems.isNotEmpty()) {
            FlowRow(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                selectedItems.forEach { code ->
                    InputChip(
                        selected = true,
                        onClick = {
                            val newItems = selectedItems - code
                            onValueChanged(if (newItems.isEmpty()) null else newItems.joinToString(","))
                        },
                        label = { 
                            val displayName = codeToNameCache[code] ?: code.replace("_", " ").replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                            Text(displayName) 
                        },
                        trailingIcon = {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = strings.delete,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    )
                }
            }
        }
    }
}
