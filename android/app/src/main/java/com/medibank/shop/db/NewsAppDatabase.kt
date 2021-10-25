package com.medibank.shop.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.medibank.shop.data.Article

@Database(
    entities = [Article::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class NewsAppDatabase : RoomDatabase() {

    abstract fun getArticlesDao(): ArticleDao

    companion object {
        @Volatile
        private var instace: NewsAppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instace ?: synchronized(LOCK) {
            instace ?: createDatabase(context).also {
                instace = it
            }
        }

        private fun createDatabase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            NewsAppDatabase::class.java,
            "article_db.db"
        ).build()

    }
}
