package ru.privateclinic.ui.app

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
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
import ru.privateclinic.ui.state.ClinicViewModel
import ru.privateclinic.ui.theme.ClinicTheme
import ru.privateclinic.ui.theme.ThemeChoice

@Composable
internal fun ClinicRoot(
    apiBaseUrl: String?,
    platformModule: Module?,
    fontFamily: FontFamily = FontFamily.SansSerif,
) {
    if (LocalInspectionMode.current) {
        val viewModel = remember { ClinicViewModel() }
        ClinicTheme(ThemeChoice.Light, fontFamily) {
            ClinicContent(viewModel, ThemeChoice.Light, storageUnavailable = false, onTheme = {})
        }
        return
    }

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
        var theme by rememberSaveable { mutableStateOf(ThemeChoice.System) }
        ClinicTheme(theme, fontFamily) {
            ClinicContent(koinViewModel(), theme, storageUnavailable) { theme = it }
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun ClinicRootPreview() {
    ClinicRoot(apiBaseUrl = null, platformModule = null)
}
