package com.challengeapp.di

import com.challengeapp.data.repository.FormRepositoryImpl
import com.challengeapp.domain.repository.FormRepository
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
    abstract fun bindFormRepository(impl: FormRepositoryImpl): FormRepository
}
