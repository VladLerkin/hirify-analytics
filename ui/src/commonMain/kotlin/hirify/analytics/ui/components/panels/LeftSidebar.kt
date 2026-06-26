package hirify.analytics.ui.components.panels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.clickable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.scale
import hirify.analytics.core.analytics.VacancyFilter
import hirify.analytics.core.analytics.HirifyApiClient
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LeftSidebar(
    filter: VacancyFilter,
    onFilterChanged: (VacancyFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text("Настроить источники", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            TextButton(onClick = { onFilterChanged(VacancyFilter()) }) {
                Text("Сбросить", fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Work Format
        FilterSection("Формат работы") {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                MultiSelectChip("Удаленно", "remote", filter.workFormat) { onFilterChanged(filter.copy(workFormat = it)) }
                MultiSelectChip("Гибрид", "hybrid", filter.workFormat) { onFilterChanged(filter.copy(workFormat = it)) }
                MultiSelectChip("В офисе", "onsite", filter.workFormat) { onFilterChanged(filter.copy(workFormat = it)) }
            }
        }

        // Remote Type
        FilterSection("Тип удаленки") {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                MultiSelectChip("Глобал", "global", filter.remoteType) { onFilterChanged(filter.copy(remoteType = it)) }
                MultiSelectChip("РФ", "russia", filter.remoteType) { onFilterChanged(filter.copy(remoteType = it)) }
                MultiSelectChip("Европа", "europe", filter.remoteType) { onFilterChanged(filter.copy(remoteType = it)) }
                MultiSelectChip("США", "usa", filter.remoteType) { onFilterChanged(filter.copy(remoteType = it)) }
            }
        }

        val apiClient = koinInject<HirifyApiClient>()

        // Specializations
        FilterSection("Специализации") {
            AutocompleteDictionaryField(
                apiClient = apiClient,
                dictionary = "specializations",
                currentValue = filter.specializations,
                onValueChanged = { onFilterChanged(filter.copy(specializations = it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }
        
        // Skills
        FilterSection(
            title = "Навыки",
            action = {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text(if (filter.skillsMatchType == "AND") "И" else "ИЛИ", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Switch(
                        checked = filter.skillsMatchType == "AND",
                        onCheckedChange = { isAnd ->
                            onFilterChanged(filter.copy(skillsMatchType = if (isAnd) "AND" else "OR"))
                        },
                        modifier = Modifier.padding(start = 4.dp).scale(0.8f)
                    )
                }
            }
        ) {
            AutocompleteDictionaryField(
                apiClient = apiClient,
                dictionary = "skills",
                currentValue = filter.skills,
                onValueChanged = { onFilterChanged(filter.copy(skills = it)) },
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Grade
        FilterSection("Грейд") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                MultiSelectChip("Стажер", "trainee", filter.grade) { onFilterChanged(filter.copy(grade = it)) }
                MultiSelectChip("Джуниор", "junior", filter.grade) { onFilterChanged(filter.copy(grade = it)) }
                MultiSelectChip("Мидл", "middle", filter.grade) { onFilterChanged(filter.copy(grade = it)) }
                MultiSelectChip("Сеньор", "senior", filter.grade) { onFilterChanged(filter.copy(grade = it)) }
                MultiSelectChip("Лид", "lead", filter.grade) { onFilterChanged(filter.copy(grade = it)) }
                MultiSelectChip("Head", "head", filter.grade) { onFilterChanged(filter.copy(grade = it)) }
                MultiSelectChip("Директор", "director", filter.grade) { onFilterChanged(filter.copy(grade = it)) }
                MultiSelectChip("C-level", "c_level", filter.grade) { onFilterChanged(filter.copy(grade = it)) }
            }
        }

        // Company Type
        FilterSection("Тип компании") {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                MultiSelectChip("Стартап", "startup", filter.companyType) { onFilterChanged(filter.copy(companyType = it)) }
                MultiSelectChip("Корпорация", "corporation", filter.companyType) { onFilterChanged(filter.copy(companyType = it)) }
                MultiSelectChip("Продуктовая компания", "product_company", filter.companyType) { onFilterChanged(filter.copy(companyType = it)) }
                MultiSelectChip("Аутсорс компания", "outsourcing_company", filter.companyType) { onFilterChanged(filter.copy(companyType = it)) }
            }
        }
    }
}

@Composable
fun MultiSelectChip(
    label: String,
    value: String,
    currentSelection: String?,
    onSelectionChanged: (String?) -> Unit
) {
    val selectedItems = currentSelection?.split(",")?.map { it.trim() }?.filter { it.isNotEmpty() } ?: emptyList()
    val isSelected = selectedItems.contains(value)
    CompactFilterChip(
        selected = isSelected,
        onClick = {
            val newItems = if (isSelected) {
                selectedItems - value
            } else {
                selectedItems + value
            }
            onSelectionChanged(if (newItems.isEmpty()) null else newItems.joinToString(","))
        },
        label = label
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactFilterChip(
    selected: Boolean,
    onClick: () -> Unit,
    label: String
) {
    Surface(
        shape = MaterialTheme.shapes.small,
        color = if (selected) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent,
        contentColor = if (selected) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.height(32.dp)
    ) {
        Box(
            contentAlignment = androidx.compose.ui.Alignment.Center,
            modifier = Modifier.clickable(onClick = onClick).padding(horizontal = 8.dp).fillMaxHeight()
        ) {
            Text(text = label, fontSize = 13.sp)
        }
    }
}

@Composable
private fun FilterSection(title: String, action: (@Composable () -> Unit)? = null, content: @Composable () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            if (action != null) {
                action()
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}
