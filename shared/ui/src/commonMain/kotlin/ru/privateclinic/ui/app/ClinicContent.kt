package ru.privateclinic.ui.app

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import ru.privateclinic.ui.components.layout.WideWindow
import ru.privateclinic.ui.navigation.ClinicNavigation
import ru.privateclinic.ui.navigation.ClinicRoute
import ru.privateclinic.ui.navigation.ClinicTabs
import ru.privateclinic.ui.platform.isWeb
import ru.privateclinic.ui.screens.appointments.AppointmentsScreen
import ru.privateclinic.ui.screens.appointments.BookingNotice
import ru.privateclinic.ui.screens.appointments.MedicalRecordScreen
import ru.privateclinic.ui.screens.catalog.CatalogScreen
import ru.privateclinic.ui.screens.catalog.CatalogScrollState
import ru.privateclinic.ui.screens.profile.FavoritesScreen
import ru.privateclinic.ui.screens.profile.PersonalDataScreen
import ru.privateclinic.ui.screens.profile.ProfileScreen
import ru.privateclinic.ui.state.ClinicViewModel
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun ClinicContent(
    viewModel: ClinicViewModel,
    theme: ThemeChoice,
    storageUnavailable: Boolean,
    onTheme: (ThemeChoice) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val route = state.backStack.last()
    var bookingNotice by remember(route) { mutableStateOf(false) }
    var restoreBookingFocus by remember(route) { mutableStateOf(false) }
    val bookingFocus = remember { FocusRequester() }
    val compactMobileCatalog = if (isWeb) null else remember { CatalogScrollState() }
    val dismissBookingNotice = {
        restoreBookingFocus = true
        bookingNotice = false
    }
    LaunchedEffect(route, bookingNotice) {
        if (route == ClinicRoute.Appointments && !bookingNotice && restoreBookingFocus) {
            bookingFocus.requestFocus()
            restoreBookingFocus = false
        }
    }
    Surface(color = MaterialTheme.colorScheme.background) {
        val systemInsets = WindowInsets.safeDrawing
        val headerColor = MaterialTheme.colorScheme.primaryContainer
        BoxWithConstraints(Modifier.fillMaxSize().drawBehind {
            if (route in ClinicTabs) drawRect(headerColor, size = Size(size.width, systemInsets.getTop(this).toFloat()))
        }.safeDrawingPadding().imePadding()) {
            val wide = maxWidth >= WideWindow
            val limitSavedCards = maxWidth >= 1000.dp
            Column(Modifier.fillMaxSize().then(if (bookingNotice) Modifier.clearAndSetSemantics {} else Modifier)) {
                if (storageUnavailable) {
                    Text("Локальное хранилище недоступно", color = MaterialTheme.colorScheme.error)
                }
                NavDisplay(
                    backStack = state.backStack,
                    onBack = { if (bookingNotice) dismissBookingNotice() else viewModel.back() },
                    modifier = Modifier.weight(1f),
                    entryProvider = entryProvider {
                        entry(ClinicRoute.Catalog) {
                            CatalogScreen(wide, state.search, state.branches,
                                compactScrollState = compactMobileCatalog.takeUnless { wide },
                                onSearch = viewModel::search)
                        }
                        entry(ClinicRoute.Appointments) {
                            AppointmentsScreen(wide, viewModel::open, bookingFocus) { bookingNotice = true }
                        }
                        entry(ClinicRoute.Profile) {
                            ProfileScreen(wide, state.profile, state.favorites.size, theme, onTheme, viewModel::open)
                        }
                        entry(ClinicRoute.Favorites) {
                            FavoritesScreen(state.favorites, wide, limitSavedCards, viewModel::back) {
                                viewModel.selectTab(ClinicRoute.Catalog)
                            }
                        }
                        entry(ClinicRoute.PersonalData) {
                            PersonalDataScreen(state.profile, wide, viewModel::back)
                        }
                        ClinicRoute.entries.filterNot {
                            it in ClinicTabs || it == ClinicRoute.Favorites || it == ClinicRoute.PersonalData
                        }.forEach { destination ->
                            entry(destination) {
                                MedicalRecordScreen(destination.title, wide, viewModel::back)
                            }
                        }
                    },
                )
                if (route in ClinicTabs) {
                    ClinicNavigation(wide, route, viewModel::selectTab)
                }
            }
            if (bookingNotice) {
                BookingNotice(onDismiss = dismissBookingNotice, onCatalog = {
                    bookingNotice = false
                    viewModel.selectTab(ClinicRoute.Catalog)
                })
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ClinicContentPreview() {
    ClinicTheme(ThemeChoice.Light) {
        ClinicContent(remember { ClinicViewModel() }, ThemeChoice.Light, storageUnavailable = false, onTheme = {})
    }
}
