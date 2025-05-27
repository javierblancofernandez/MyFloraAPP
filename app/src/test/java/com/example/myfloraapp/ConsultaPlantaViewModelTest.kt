package com.example.myfloraapp

import android.util.Log
import com.example.myfloraapp.ViewModel.ConsultaPlantaViewModel
import com.example.myfloraapp.api.OpenAIApi
import com.example.myfloraapp.api.OpenAIApiClient
import com.example.myfloraapp.models.ChatCompletionRequest
import com.example.myfloraapp.models.ChatCompletionResponse
import com.example.myfloraapp.models.Choice
import com.example.myfloraapp.models.ChatMessage
import com.example.myfloraapp.models.ConsultaUiState
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class ConsultaPlantaViewModelTest {

    // Mock del API
    @MockK
    //private lateinit var mockOpenAIApi: OpenAIApi
    val mockOpenAIApi = mockk<OpenAIApi>()
    // Test dispatcher para corrutinas
    private val testDispatcher = StandardTestDispatcher()
    private val testScope = TestScope(testDispatcher)

    // ViewModel bajo test
    private lateinit var viewModel: ConsultaPlantaViewModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this) // Inicializar @MockK

        // Configurar MockK para reemplazar OpenAIApiClient.openAIApi
        mockkObject(OpenAIApiClient)
        every { OpenAIApiClient.openAIApi } returns mockOpenAIApi

        // Configurar corrutinas de test
        Dispatchers.setMain(testDispatcher)

        // Inicializar ViewModel
        viewModel = ConsultaPlantaViewModel() // Usará el mock por defecto
    }

    @After
    fun tearDown() {
        // Limpiar mocks y resetear dispatcher
        unmockkAll()
        Dispatchers.resetMain()
    }

    @Test
    fun `submitQuery con query válida debe retornar respuesta`() = testScope.runTest {
        // 1. Configurar mock
        val mockResponse = ChatCompletionResponse(
            choices = listOf(
                Choice(
                    message = ChatMessage(role = "assistant", content = "Riégalas poco..."),

                )
            )
        )
        coEvery{ mockOpenAIApi.getChatCompletion(any()) } returns mockResponse

        // 2. Ejecutar función bajo test
        viewModel.updateQuery("¿Cómo cuidar una suculenta?")
        viewModel.submitQuery()

        // 3. Avanzar corrutinas
        advanceUntilIdle()

        // 4. Verificar resultados
        assertEquals("Riégalas poco...", viewModel.uiState.value.response)
        assertFalse(viewModel.uiState.value.isLoading)
        assertNull(viewModel.uiState.value.error)
    }

    @Test
    fun `submitQuery con query vacía debe retornar error`() = testScope.runTest {
        // 1. No es necesario mockear el API porque no debe llamarse

        // 2. Ejecutar función bajo test
        viewModel.updateQuery("")
        viewModel.submitQuery()

        // 3. Avanzar corrutinas
        advanceUntilIdle()

        // 4. Verificar resultados
        assertEquals("La consulta no puede estar vacía", viewModel.uiState.value.error)
        assertNull(viewModel.uiState.value.response)
    }

    @Test
    fun `submitQuery con error en API debe actualizar uiState correctamente`() = testScope.runTest {
        // 1. Configurar mock para lanzar excepción
        val testException = RuntimeException("Error simulado")
        coEvery { mockOpenAIApi.getChatCompletion(any()) } throws testException

        // 2. Crear un collector de estados
        val states = mutableListOf<ConsultaUiState>()
        val job = viewModel.uiState
            .onEach { states.add(it.copy()) }
            .launchIn(this)

        try {
            // 3. Ejecutar la acción
            viewModel.updateQuery("test query")
            viewModel.submitQuery()

            // 4. Avanzar solo hasta el estado de loading
            testDispatcher.scheduler.runCurrent()

            // 5. Verificar que se activó el loading
            //assertEquals(2, states.size) // Estado inicial + loading
            //assertTrue("Debería estar en estado loading", states[1].isLoading)

            // 5. Avanzar hasta completar (incluyendo el error)
            advanceUntilIdle()

            // 6. Verificar estados finales
            assertEquals(2, states.size)
            with(states.last()) {

                assertEquals("Error al obtener la respuesta. Intenta de nuevo.", error)
                assertNull(response)
            }

            // 8. Verificar llamadas a la API
            coVerify(exactly = 1) { mockOpenAIApi.getChatCompletion(any()) }
        } finally {
            job.cancel()
        }
    }
}