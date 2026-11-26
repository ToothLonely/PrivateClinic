package ru.privateclinic.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

internal val ClinicTabs = listOf(ClinicRoute.Appointments, ClinicRoute.Catalog, ClinicRoute.Profile)

private val TabIcons = listOf(Icons.Outlined.DateRange, Icons.Outlined.Search, Icons.Outlined.Person)

@Composable
internal fun ClinicNavigation(selected: ClinicRoute, onSelect: (ClinicRoute) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Box(Modifier.fillMaxWidth().background(colors.primaryContainer), contentAlignment = Alignment.Center) {
        NavigationBar(
            modifier = Modifier.widthIn(max = 480.dp),
            containerColor = colors.primaryContainer,
            windowInsets = WindowInsets(0, 0, 0, 0),
        ) {
            ClinicTabs.zip(TabIcons).forEach { (tab, icon) ->
                NavigationBarItem(
                    selected = tab == selected,
                    onClick = { onSelect(tab) },
                    icon = { Icon(icon, contentDescription = null) },
                    label = { Text(tab.title) },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = colors.secondaryContainer,
                        selectedIconColor = colors.onSecondaryContainer,
                        selectedTextColor = colors.onPrimaryContainer,
                        unselectedIconColor = colors.onPrimaryContainer,
                        unselectedTextColor = colors.onPrimaryContainer,
                    ),
                )
            }
        }
    }
}

@Composable
internal fun CatalogScreen(wide: Boolean, search: String, onSearch: (String) -> Unit) {
    val colors = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().background(colors.primaryContainer), contentAlignment = Alignment.Center) {
            if (wide) {
                Row(
                    Modifier.widthIn(max = 1280.dp).fillMaxWidth().padding(24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    Brand(Modifier.weight(1f))
                    SearchField(search, onSearch, Modifier.weight(2f))
                    Box(Modifier.weight(1f), contentAlignment = Alignment.CenterEnd) { City() }
                }
            } else {
                Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Brand(Modifier.weight(1f))
                        City()
                    }
                    SearchField(search, onSearch, Modifier.fillMaxWidth())
                }
            }
        }
        MainScreen(ClinicRoute.Catalog.title, wide) {
            Text("Каталог пока недоступен", color = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun Brand(modifier: Modifier = Modifier) {
    Text(
        text = "Частный приём",
        modifier = modifier,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
    )
}

@Composable
private fun City() {
    Text("Ставрополь", color = MaterialTheme.colorScheme.onPrimaryContainer)
}

@Composable
private fun SearchField(value: String, onSearch: (String) -> Unit, modifier: Modifier) {
    val colors = MaterialTheme.colorScheme
    val label = "Название или адрес"
    OutlinedTextField(
        value = value,
        onValueChange = onSearch,
        modifier = modifier.semantics { contentDescription = label },
        label = { Text(label) },
        singleLine = true,
        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = colors.surface,
            unfocusedContainerColor = colors.surface,
            focusedBorderColor = colors.onSurface,
            unfocusedBorderColor = colors.outline,
            focusedLabelColor = colors.onPrimaryContainer,
            unfocusedLabelColor = colors.onSurfaceVariant,
        ),
    )
}

@Composable
internal fun AppointmentsScreen(wide: Boolean, onOpen: (ClinicRoute) -> Unit) {
    MainScreen(ClinicRoute.Appointments.title, wide) {
        ScreenSections(wide, first = { Text("У вас нет записей") }) {
            SectionTitle("Медкарта")
            RouteCard(ClinicRoute.Visits, onOpen)
            RouteCard(ClinicRoute.Studies, onOpen)
            RouteCard(ClinicRoute.LabResults, onOpen)
        }
    }
}

@Composable
internal fun ProfileScreen(wide: Boolean, onOpen: (ClinicRoute) -> Unit) {
    MainScreen(ClinicRoute.Profile.title, wide) {
        ScreenSections(wide, first = { RouteCard(ClinicRoute.PersonalData, onOpen) }) {
            RouteCard(ClinicRoute.Favorites, onOpen)
        }
    }
}

@Composable
private fun MainScreen(title: String, wide: Boolean, content: @Composable () -> Unit) {
    Box(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), contentAlignment = Alignment.TopCenter) {
        Column(
            modifier = Modifier.widthIn(max = 1280.dp).fillMaxWidth().padding(if (wide) 32.dp else 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(title, Modifier.semantics { heading() }, style = MaterialTheme.typography.headlineLarge)
            content()
        }
    }
}

@Composable
private fun ScreenSections(wide: Boolean, first: @Composable () -> Unit, second: @Composable () -> Unit) {
    if (wide) {
        Row(horizontalArrangement = Arrangement.spacedBy(32.dp)) {
            Column(Modifier.weight(1f), content = { first() })
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) { second() }
        }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            first()
            second()
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, Modifier.semantics { heading() }, style = MaterialTheme.typography.titleLarge)
}

@Composable
private fun RouteCard(route: ClinicRoute, onOpen: (ClinicRoute) -> Unit) {
    Card(
        onClick = { onOpen(route) },
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
    ) {
        Text(route.title, Modifier.padding(16.dp))
    }
}
