package com.sartori.rmnews.di

import android.content.Context
import com.google.firebase.analytics.FirebaseAnalytics
import com.sartori.rmnews.analytics.FirebaseNewsAnalytics
import com.sartori.rmnews.analytics.NewsAnalytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {
    @Binds
    @Singleton
    abstract fun bindNewsAnalytics(analytics: FirebaseNewsAnalytics): NewsAnalytics

    companion object {
        @Provides
        @Singleton
        fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics =
            FirebaseAnalytics.getInstance(context)
    }
}
