
package com.example.inventory.ui.item

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.inventory.data.Item
import com.example.inventory.data.ItemsRepository
import java.text.NumberFormat

/**
 * ViewModel encargado del registro de productos.
 */
class ItemEntryViewModel(
    private val itemsRepository: ItemsRepository
) : ViewModel() {

    var itemUiState by mutableStateOf(ItemUiState())
        private set

    /**
     * Actualiza los campos del formulario
     * y comprueba que sean válidos.
     */
    fun updateUiState(itemDetails: ItemDetails) {
        itemUiState = ItemUiState(
            itemDetails = itemDetails,
            isEntryValid = validateInput(itemDetails)
        )
    }

    /**
     * Verifica los datos introducidos.
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
     * Guarda el producto en la base de datos Room.
     */
    suspend fun saveItem() {
        if (validateInput()) {
            itemsRepository.insertItem(
                itemUiState.itemDetails.toItem()
            )
        }
    }
}

/**
 * Estado del formulario de productos.
 */
data class ItemUiState(
    val itemDetails: ItemDetails = ItemDetails(),
    val isEntryValid: Boolean = false
)

/**
 * Datos introducidos por el usuario.
 */
data class ItemDetails(
    val id: Int = 0,
    val name: String = "",
    val price: String = "",
    val quantity: String = ""
)

/**
 * Convierte los datos del formulario en un Item.
 */
fun ItemDetails.toItem(): Item = Item(
    id = id,
    name = name,
    price = price.toDoubleOrNull() ?: 0.0,
    quantity = quantity.toIntOrNull() ?: 0
)

/**
 * Da formato monetario al precio.
 */
fun Item.formatedPrice(): String {
    return NumberFormat.getCurrencyInstance().format(price)
}

/**
 * Convierte un Item al estado del formulario.
 */
fun Item.toItemUiState(
    isEntryValid: Boolean = false
): ItemUiState = ItemUiState(
    itemDetails = this.toItemDetails(),
    isEntryValid = isEntryValid
)

/**
 * Convierte un Item a los datos del formulario.
 */
fun Item.toItemDetails(): ItemDetails = ItemDetails(
    id = id,
    name = name,
    price = price.toString(),
    quantity = quantity.toString()
)
