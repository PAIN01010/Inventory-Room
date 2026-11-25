
package com.example.inventory.ui

import android.app.Application
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.inventory.InventoryApplication
import com.example.inventory.ui.home.HomeViewModel
import com.example.inventory.ui.item.ItemDetailsViewModel
import com.example.inventory.ui.item.ItemEditViewModel
import com.example.inventory.ui.item.ItemEntryViewModel

/**
 * Proporciona los ViewModels y sus dependencias
 * para la aplicación Inventory.
 */
object AppViewModelProvider {

    val Factory = viewModelFactory {

        // ViewModel para editar productos
        initializer {
            ItemEditViewModel(
                this.createSavedStateHandle(),
                inventoryApplication()
                    .container
                    .itemsRepository
            )
        }

        // ViewModel para registrar productos
        initializer {
            ItemEntryViewModel(
                inventoryApplication()
                    .container
                    .itemsRepository
            )
        }

        // ViewModel para consultar, vender y eliminar
        initializer {
            ItemDetailsViewModel(
                this.createSavedStateHandle(),
                inventoryApplication()
                    .container
                    .itemsRepository
            )
        }

        // ViewModel de la pantalla principal
        initializer {
            HomeViewModel(
                inventoryApplication()
                    .container
                    .itemsRepository
            )
        }
    }
}

/**
 * Obtiene InventoryApplication desde CreationExtras.
 */
fun CreationExtras.inventoryApplication(): InventoryApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as InventoryApplication)
