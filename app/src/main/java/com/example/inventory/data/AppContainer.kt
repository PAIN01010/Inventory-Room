
package com.example.inventory.data

import android.content.Context

/**
 * Contenedor de dependencias de la aplicación.
 */
interface AppContainer {
    val itemsRepository: ItemsRepository
}

/**
 * Proporciona el repositorio conectado
 * a la base de datos local de Room.
 */
class AppDataContainer(
    private val context: Context
) : AppContainer {

    override val itemsRepository: ItemsRepository by lazy {
        OfflineItemsRepository(
            InventoryDatabase.getDatabase(context).itemDao()
        )
    }
}
