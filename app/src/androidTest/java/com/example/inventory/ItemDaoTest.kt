
package com.example.inventory

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.inventory.data.InventoryDatabase
import com.example.inventory.data.Item
import com.example.inventory.data.ItemDao
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ItemDaoTest {

    private lateinit var itemDao: ItemDao
    private lateinit var database: InventoryDatabase

    private val item1 = Item(
        id = 1,
        name = "Apples",
        price = 10.0,
        quantity = 20
    )

    private val item2 = Item(
        id = 2,
        name = "Bananas",
        price = 15.0,
        quantity = 97
    )

    @Before
    fun createDb() {
        val context =
            ApplicationProvider.getApplicationContext<Context>()

        database = Room.inMemoryDatabaseBuilder(
            context,
            InventoryDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        itemDao = database.itemDao()
    }

    @After
    fun closeDb() {
        database.close()
    }

    private suspend fun addOneItemToDb() {
        itemDao.insert(item1)
    }

    private suspend fun addTwoItemsToDb() {
        itemDao.insert(item1)
        itemDao.insert(item2)
    }

    @Test
    fun daoInsert_insertsItemIntoDB() = runBlocking {
        addOneItemToDb()

        val allItems = itemDao.getAllItems().first()

        assertEquals(item1, allItems[0])
    }

    @Test
    fun daoGetAllItems_returnsAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()

        val allItems = itemDao.getAllItems().first()

        assertEquals(2, allItems.size)
        assertEquals(item1, allItems[0])
        assertEquals(item2, allItems[1])
    }

    @Test
    fun daoGetItem_returnsItemFromDB() = runBlocking {
        addOneItemToDb()

        val item = itemDao.getItem(1).first()

        assertEquals(item1, item)
    }

    @Test
    fun daoUpdateItems_updatesItemsInDB() = runBlocking {
        addTwoItemsToDb()

        val updatedItem = item1.copy(
            name = "Green Apples",
            price = 12.0,
            quantity = 10
        )

        itemDao.update(updatedItem)

        val allItems = itemDao.getAllItems().first()

        assertEquals(updatedItem, allItems[1])
    }

    @Test
    fun daoDeleteItems_deletesAllItemsFromDB() = runBlocking {
        addTwoItemsToDb()

        itemDao.delete(item1)
        itemDao.delete(item2)

        val allItems = itemDao.getAllItems().first()

        assertTrue(allItems.isEmpty())
    }
}
