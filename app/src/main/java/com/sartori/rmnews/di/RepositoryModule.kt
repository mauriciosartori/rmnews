package com.sartori.rmnews.di

import com.sartori.rmnews.data.repository.DefaultNewsRepository
import com.sartori.rmnews.domain.repository.NewsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindNewsRepository(repository: DefaultNewsRepository): NewsRepository
}
