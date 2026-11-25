
package com.example.inventory.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.ItemsRepository
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * ViewModel encargado de editar productos
 * almacenados en la base de datos Room.
 */
class ItemEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    var itemUiState by mutableStateOf(ItemUiState())
        private set

    private val itemId: Int = checkNotNull(
        savedStateHandle[ItemEditDestination.itemIdArg]
    )

    /**
     * Recupera el producto seleccionado
     * desde la base de datos.
     */
    init {
        viewModelScope.launch {
            itemUiState = itemsRepository
                .getItemStream(itemId)
                .filterNotNull()
                .first()
                .toItemUiState(isEntryValid = true)
        }
    }

    /**
     * Actualiza los datos del formulario.
     */
    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState = ItemUiState(
            itemDetails = itemDetails,
            isEntryValid = validateInput(itemDetails)
        )
    }

    /**
     * Comprueba que los campos sean válidos.
     */
    private fun validateInput(
        uiState: ItemDetails = itemUiState.itemDetails
    ): Boolean {
        return with(uiState) {
            name.isNotBlank() &&
                    price.toDoubleOrNull()?.let { it > 0 } == true &&
                    quantity.toIntOrNull()?.let { it >= 0 } == true
        }
    }

    /**
     * Guarda las modificaciones en Room.
     */
    suspend fun updateItem() {
        if (validateInput() && itemUiState.itemDetails.id == itemId) {
            itemsRepository.updateItem(
                itemUiState.itemDetails.toItem()
            )
        }
    }
}
