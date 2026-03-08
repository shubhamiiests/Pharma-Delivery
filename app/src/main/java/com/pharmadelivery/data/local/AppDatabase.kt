package com.pharmadelivery.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.pharmadelivery.data.local.dao.*
import com.pharmadelivery.data.local.entities.*

@Database(
    entities = [
        UserEntity::class,
        OrderEntity::class,
        MedicineEntity::class,
        SessionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun orderDao(): OrderDao
    abstract fun medicineDao(): MedicineDao
    abstract fun sessionDao(): SessionDao
}
