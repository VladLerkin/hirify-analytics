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
import hirify.analytics.ui.i18n.LocalAppStrings
import org.koin.compose.koinInject
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.HelpOutline
import kotlinx.coroutines.launch
import androidx.compose.foundation.interaction.MutableInteractionSource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun LeftSidebar(
    filter: VacancyFilter,
    onFilterChanged: (VacancyFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val strings = LocalAppStrings.current

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(320.dp)
            .padding(16.dp)
            .verticalScroll(scrollState)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Text(strings.configureSources, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
            TextButton(onClick = { onFilterChanged(hirify.analytics.core.analytics.VacancyFilter()) }) {
                Text(strings.reset, fontSize = 12.sp, color = MaterialTheme.colorScheme.tertiary)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Work Format
        FilterSection(
            title = strings.workFormat,
            tooltip = strings.workFormatTooltip
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                MultiSelectChip(strings.remote, "remote", filter.workFormat) { onFilterChanged(filter.copy(workFormat = it)) }
                MultiSelectChip(strings.hybrid, "hybrid", filter.workFormat) { onFilterChanged(filter.copy(workFormat = it)) }
                MultiSelectChip(strings.onsite, "onsite", filter.workFormat) { onFilterChanged(filter.copy(workFormat = it)) }
            }
        }

        // Remote Type
        FilterSection(
            title = strings.remoteType,
            tooltip = strings.remoteTypeTooltip
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                MultiSelectChip(strings.global, "global", filter.remoteType) { onFilterChanged(filter.copy(remoteType = it)) }
                MultiSelectChip(strings.russia, "russia", filter.remoteType) { onFilterChanged(filter.copy(remoteType = it)) }
                MultiSelectChip(strings.europe, "europe", filter.remoteType) { onFilterChanged(filter.copy(remoteType = it)) }
                MultiSelectChip(strings.usa, "usa", filter.remoteType) { onFilterChanged(filter.copy(remoteType = it)) }
            }
        }

        val apiClient = koinInject<HirifyApiClient>()

        // Specializations
        FilterSection(
            title = strings.specializations,
            tooltip = strings.specializationsTooltip
        ) {
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
            title = strings.skills,
            tooltip = strings.skillsTooltip,
            action = {
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Text(if (filter.skillsMatchType == "AND") strings.and else strings.or, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        FilterSection(
            title = strings.grade,
            tooltip = strings.gradeTooltip
        ) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                MultiSelectChip(strings.trainee, "trainee", filter.grade) { onFilterChanged(filter.copy(grade = it)) }
                MultiSelectChip(strings.junior, "junior", filter.grade) { onFilterChanged(filter.copy(grade = it)) }
                MultiSelectChip(strings.middle, "middle", filter.grade) { onFilterChanged(filter.copy(grade = it)) }
                MultiSelectChip(strings.senior, "senior", filter.grade) { onFilterChanged(filter.copy(grade = it)) }
                MultiSelectChip(strings.lead, "lead", filter.grade) { onFilterChanged(filter.copy(grade = it)) }
                MultiSelectChip(strings.headGrade, "head", filter.grade) { onFilterChanged(filter.copy(grade = it)) }
                MultiSelectChip(strings.director, "director", filter.grade) { onFilterChanged(filter.copy(grade = it)) }
                MultiSelectChip(strings.cLevel, "c_level", filter.grade) { onFilterChanged(filter.copy(grade = it)) }
            }
        }

        // Company Type
        FilterSection(
            title = strings.companyType,
            tooltip = strings.companyTypeTooltip
        ) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                MultiSelectChip(strings.startup, "startup", filter.companyType) { onFilterChanged(filter.copy(companyType = it)) }
                MultiSelectChip(strings.corporation, "corporation", filter.companyType) { onFilterChanged(filter.copy(companyType = it)) }
                MultiSelectChip(strings.productCompany, "product_company", filter.companyType) { onFilterChanged(filter.copy(companyType = it)) }
                MultiSelectChip(strings.outsourcingCompany, "outsourcing_company", filter.companyType) { onFilterChanged(filter.copy(companyType = it)) }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSection(
    title: String,
    tooltip: String? = null,
    action: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (tooltip != null) {
                    Spacer(modifier = Modifier.width(4.dp))
                    val tooltipState = rememberTooltipState()
                    val scope = rememberCoroutineScope()
                    TooltipBox(
                        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
                        tooltip = {
                            PlainTooltip {
                                Text(tooltip, fontSize = 12.sp)
                            }
                        },
                        state = tooltipState
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.HelpOutline,
                            contentDescription = "Info",
                            modifier = Modifier
                                .size(24.dp)
                                .padding(4.dp)
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null,
                                    onClick = { scope.launch { tooltipState.show() } }
                                ),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
            if (action != null) {
                action()
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}
