
package com.example.inventory.ui.item

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.inventory.data.ItemsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel para consultar, vender
 * y eliminar productos de Room.
 */
class ItemDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    private val itemId: Int = checkNotNull(
        savedStateHandle[ItemDetailsDestination.itemIdArg]
    )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

    val uiState: StateFlow<ItemDetailsUiState> =
        itemsRepository.getItemStream(itemId)
            .filterNotNull()
            .map { item ->
                ItemDetailsUiState(
                    outOfStock = item.quantity <= 0,
                    itemDetails = item.toItemDetails()
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(
                    TIMEOUT_MILLIS
                ),
                initialValue = ItemDetailsUiState()
            )

    /**
     * Reduce una unidad del inventario
     * cuando se registra una venta.
     */
    suspend fun reduceQuantityByOne() {
        val currentItem = uiState.value.itemDetails.toItem()

        if (currentItem.id != 0 && currentItem.quantity > 0) {
            itemsRepository.updateItem(
                currentItem.copy(
                    quantity = currentItem.quantity - 1
                )
            )
        }
    }

    /**
     * Elimina el producto de la base de datos.
     */
    suspend fun deleteItem() {
        val currentItem = uiState.value.itemDetails.toItem()

        if (currentItem.id != 0) {
            itemsRepository.deleteItem(currentItem)
        }
    }
}

/**
 * Estado de la pantalla de detalles.
 */
data class ItemDetailsUiState(
    val outOfStock: Boolean = true,
    val itemDetails: ItemDetails = ItemDetails()
)
