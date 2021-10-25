package com.scheme.di

import android.app.Application
import androidx.room.Room
import com.scheme.data.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LectureModule {
    @Provides
    @Singleton
    fun provideDatabase(application: Application) =
        Room.databaseBuilder(application, AppDatabase::class.java, "lecture_database")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideLectureDao(db: AppDatabase) = db.lectureDao()


    @Provides
    fun provideEventDao(db: AppDatabase) = db.eventDao()

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope