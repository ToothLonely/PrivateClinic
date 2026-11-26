package ru.privateclinic.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
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
fun ClinicApp(apiBaseUrl: String?) = ClinicApp(apiBaseUrl, platformModule = null)

@Composable
internal fun ClinicApp(apiBaseUrl: String?, platformModule: Module?) {
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
        ClinicTheme { ClinicContent(koinViewModel()) }
    }
}

internal class LocalStorage(val open: suspend () -> Unit)

@Composable
private fun ClinicContent(viewModel: ClinicViewModel) {
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
    Surface(color = MaterialTheme.colorScheme.background) {
        BoxWithConstraints(Modifier.fillMaxSize().safeDrawingPadding().imePadding()) {
            val wide = maxWidth >= WideWindow
            Column(Modifier.fillMaxSize()) {
                if (storageUnavailable) {
                    Text("Локальное хранилище недоступно", color = MaterialTheme.colorScheme.error)
                }
                NavDisplay(
                    backStack = state.backStack,
                    onBack = viewModel::back,
                    modifier = Modifier.weight(1f),
                    entryProvider = entryProvider {
                        entry(ClinicRoute.Catalog) { CatalogScreen(wide, state.search, viewModel::search) }
                        entry(ClinicRoute.Appointments) { AppointmentsScreen(wide, viewModel::open) }
                        entry(ClinicRoute.Profile) { ProfileScreen(wide, viewModel::open) }
                        ClinicRoute.entries.filterNot { it in ClinicTabs }.forEach { destination ->
                            entry(destination) { BackScreen(it.title, wide, viewModel::back) }
                        }
                    },
                )
                if (route in ClinicTabs) {
                    ClinicNavigation(route, viewModel::selectTab)
                }
            }
        }
    }
}
