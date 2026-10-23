package com.challengeapp.ui.form

import com.challengeapp.MainDispatcherRule
import com.challengeapp.domain.model.ChoiceQuestion
import com.challengeapp.domain.model.Page
import com.challengeapp.domain.model.RefreshError
import com.challengeapp.domain.model.Response
import com.challengeapp.domain.repository.FormRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FormViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val single = ChoiceQuestion(1, "Pick one", multipleSelection = false, responses = responses(10, 11))
    private val multi = ChoiceQuestion(2, "Pick many", multipleSelection = true, responses = responses(20, 21))
    private lateinit var viewModel: FormViewModel

    @Before
    fun setUp() {
        viewModel = FormViewModel(FakeFormRepository(listOf(Page(100, "Page", listOf(single, multi)))))
    }

    @Test
    fun `single select replaces the previous response`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onResponseClick(single, 10)
        viewModel.onResponseClick(single, 11)

        assertEquals(setOf(11L), selectedFor(single))
    }

    @Test
    fun `multi select keeps every tapped response`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onResponseClick(multi, 20)
        viewModel.onResponseClick(multi, 21)

        assertEquals(setOf(20L, 21L), selectedFor(multi))
    }

    @Test
    fun `tapping a selected response clears it`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onResponseClick(single, 10)
        viewModel.onResponseClick(single, 10)
        viewModel.onResponseClick(multi, 20)
        viewModel.onResponseClick(multi, 21)
        viewModel.onResponseClick(multi, 20)

        assertEquals(emptySet<Long>(), selectedFor(single))
        assertEquals(setOf(21L), selectedFor(multi))
    }

    @Test
    fun `selecting in one question leaves the others untouched`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        viewModel.onResponseClick(single, 10)

        assertEquals(emptySet<Long>(), selectedFor(multi))
    }

    @Test
    fun `refresh error is reported and cleared by a successful retry`() = runTest {
        val repository = FakeFormRepository(emptyList(), RefreshError.Offline)
        val viewModel = FormViewModel(repository)
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        assertEquals(RefreshError.Offline, viewModel.uiState.value.refreshError)
        assertFalse(viewModel.uiState.value.isRefreshing)

        repository.refreshError = null
        viewModel.refresh()

        assertNull(viewModel.uiState.value.refreshError)
    }

    @Test
    fun `snackbar error is shown once and the refresh error stays`() = runTest {
        val viewModel = FormViewModel(FakeFormRepository(emptyList(), RefreshError.Unavailable))
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }

        assertEquals(RefreshError.Unavailable, viewModel.uiState.value.snackbarError)

        viewModel.onSnackbarShown()

        assertNull(viewModel.uiState.value.snackbarError)
        assertEquals(RefreshError.Unavailable, viewModel.uiState.value.refreshError)
    }

    private fun selectedFor(question: ChoiceQuestion): Set<Long> =
        viewModel.uiState.value.rows.single { it.item.id == question.id }.selectedResponseIds

    private fun responses(vararg ids: Long) = ids.map { Response(it, "Response $it", null) }
}

private class FakeFormRepository(
    private val pages: List<Page>,
    var refreshError: RefreshError? = null,
) : FormRepository {
    override fun observePages(): Flow<List<Page>> = flowOf(pages)

    override suspend fun refresh(): RefreshError? = refreshError
}
