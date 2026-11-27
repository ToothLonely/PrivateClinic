package ru.privateclinic.ui

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import ru.privateclinic.ui.components.header.HeaderPull
import ru.privateclinic.ui.navigation.ClinicRoute
import ru.privateclinic.ui.state.ClinicViewModel

class ClientFoundationTest {
    @Test
    fun retainsSearchOnTabSwitchAndReturnsToTheParent() {
        val viewModel = ClinicViewModel()
        viewModel.search("Ленина")
        viewModel.selectTab(ClinicRoute.Profile)
        viewModel.open(ClinicRoute.PersonalData)
        viewModel.open(ClinicRoute.PersonalData)

        assertEquals(
            listOf(ClinicRoute.Catalog, ClinicRoute.Profile, ClinicRoute.PersonalData),
            viewModel.state.value.backStack,
        )
        viewModel.back()
        assertEquals(ClinicRoute.Profile, viewModel.state.value.backStack.last())
        viewModel.selectTab(ClinicRoute.Catalog)
        viewModel.back()
        assertEquals(listOf(ClinicRoute.Catalog), viewModel.state.value.backStack)
        assertEquals("Ленина", viewModel.state.value.search)
    }

    @Test
    fun compactTitleFinishesDroppingBeforeRefreshAppears() {
        val halfway = HeaderPull(40f, wide = false)
        assertEquals(0.5f, halfway.titleProgress)
        assertFalse(halfway.showsRefresh)
        assertFalse(halfway.canRefresh)
        assertEquals(1f, HeaderPull(80f, wide = false).titleProgress)
        assertFalse(HeaderPull(80f, wide = false).showsRefresh)
        assertTrue(HeaderPull(81f, wide = false).showsRefresh)
        assertFalse(HeaderPull(129f, wide = false).canRefresh)
        assertTrue(HeaderPull(130f, wide = false).canRefresh)
        assertEquals(0f, HeaderPull(130f, wide = true).titleProgress)
    }
}
