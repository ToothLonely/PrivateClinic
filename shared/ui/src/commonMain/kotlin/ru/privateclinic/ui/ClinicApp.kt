package ru.privateclinic.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.CancellationException
import org.koin.compose.KoinApplication
import org.koin.compose.getKoin
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.koinConfiguration
import org.koin.dsl.module
import org.koin.dsl.onClose
import ru.privateclinic.network.ClinicApi

@Composable
fun ClinicApp(apiBaseUrl: String?, fontFamily: FontFamily = FontFamily.SansSerif) =
    ClinicApp(apiBaseUrl, platformModule = null, fontFamily = fontFamily)

@Composable
internal fun ClinicApp(apiBaseUrl: String?, platformModule: Module?, fontFamily: FontFamily = FontFamily.SansSerif) {
    val configuration = remember(apiBaseUrl, platformModule) {
        koinConfiguration {
            modules(module {
                single(createdAtStart = true) { ClinicApi(apiBaseUrl) } onClose { it?.close() }
                viewModelOf(::ClinicViewModel)
            })
            platformModule?.let { modules(it) }
        }
    }
    KoinApplication(configuration, logLevel = Level.ERROR) {
        var theme by rememberSaveable { mutableStateOf(ThemeChoice.System) }
        ClinicTheme(theme, fontFamily) { ClinicContent(koinViewModel(), theme) { theme = it } }
    }
}

internal class LocalStorage(val open: suspend () -> Unit)

@Composable
private fun ClinicContent(viewModel: ClinicViewModel, theme: ThemeChoice, onTheme: (ThemeChoice) -> Unit) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val koin = getKoin()
    var storageUnavailable by remember(koin) { mutableStateOf(false) }
    LaunchedEffect(koin) {
        try {
            koin.getOrNull<LocalStorage>()?.open?.invoke()
        } catch (error: CancellationException) {
            throw error
        } catch (_: Exception) {
            storageUnavailable = true
        }
    }

    val route = state.backStack.last()
    var bookingNotice by remember(route) { mutableStateOf(false) }
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
                    onBack = { if (bookingNotice) bookingNotice = false else viewModel.back() },
                    modifier = Modifier.weight(1f),
                    entryProvider = entryProvider {
                        entry(ClinicRoute.Catalog) { CatalogScreen(wide, state.search, state.branches, viewModel::search) }
                        entry(ClinicRoute.Appointments) { AppointmentsScreen(wide, viewModel::open) { bookingNotice = true } }
                        entry(ClinicRoute.Profile) {
                            ProfileScreen(wide, state.profile, state.favorites.size, theme, onTheme, viewModel::open)
                        }
                        ClinicRoute.entries.filterNot { it in ClinicTabs }.forEach { destination ->
                            entry(destination) {
                                val saved = destination == ClinicRoute.Favorites
                                BackScreen(destination.title, wide, viewModel::back,
                                    maxContentWidth = if (saved) 1280.dp else Dp.Infinity) {
                                    when (destination) {
                                        ClinicRoute.Favorites -> {
                                            if (state.favorites.isEmpty()) {
                                                Text("Сохраняйте клиники из их карточек.",
                                                    Modifier.padding(top = 10.dp, bottom = 16.dp))
                                                CatalogLink { viewModel.selectTab(ClinicRoute.Catalog) }
                                            } else {
                                                val cardWidth = if (limitSavedCards) 720.dp else Dp.Infinity
                                                state.favorites.forEach {
                                                    ClinicRow(it, Modifier.widthIn(max = cardWidth).padding(bottom = 10.dp))
                                                }
                                            }
                                        }
                                        ClinicRoute.PersonalData -> PersonalData(state.profile)
                                        else -> {
                                            val center = if (wide) Modifier.fillMaxWidth().weight(1f) else Modifier
                                            Box(center.padding(top = 10.dp, bottom = 16.dp), contentAlignment = Alignment.Center) {
                                                Text("Функция появится позже.", color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = if (wide) 20.sp else 16.sp,
                                                    lineHeight = if (wide) 28.sp else 22.4.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    },
                )
                if (route in ClinicTabs) {
                    ClinicNavigation(wide, route, viewModel::selectTab)
                }
            }
            if (bookingNotice) {
                BookingNotice(onDismiss = { bookingNotice = false }, onCatalog = {
                    bookingNotice = false
                    viewModel.selectTab(ClinicRoute.Catalog)
                })
            }
        }
    }
}
