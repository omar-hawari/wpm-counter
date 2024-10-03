package com.omarhawari.wpm_counter.di

import android.content.Context
import androidx.room.Room
import com.omarhawari.wpm_counter.database.DATABASE_NAME
import com.omarhawari.wpm_counter.database.WPMDatabase
import com.omarhawari.wpm_counter.database.WPMRepositoryDBImpl
import com.omarhawari.wpm_counter.domain.WPMRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class) // This module will be installed in the SingletonComponent, ensuring the provided dependencies have a singleton lifecycle throughout the application's lifetime
object WPMModule {


    /**
     * Provides a singleton instance of the WPMDatabase
     *
     * @param appContext The application context used to create the database
     * @return A singleton instance of WPMDatabase
     */
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context) = Room.databaseBuilder(
        appContext,
        WPMDatabase::class.java,
        DATABASE_NAME
    ).build()

    /**
     * Provides a singleton instance of the WPMRepository implementation
     *
     * @param database The WPMDatabase instance used to create the repository
     * @return A singleton instance of WPMRepositoryDBImpl
     */
    @Provides
    @Singleton
    fun provideRepository(database: WPMDatabase): WPMRepository = WPMRepositoryDBImpl(database)



    /**
     * Provides a singleton instance of WPMText with a predefined string
     *
     * @return A singleton instance of WPMText initialized with a specific text
     */
    @Provides
    @Singleton
    fun provideText(): WPMText = WPMText(
        "He thought he would light the fire when " +
                "he got inside, and make himself some " +
                "breakfast, just to pass away the time; " +
                "but he did not seem able to handle anything " +
                "from a scuttleful of coals to a"
    )

}