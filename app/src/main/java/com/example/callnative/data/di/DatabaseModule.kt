package com.example.callnative.data.di

import com.example.callnative.data.repositories.CallRepositoryImpl
import com.example.callnative.domain.irepository.ICallRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindCallRepository(
        impl: CallRepositoryImpl
    ): ICallRepository
}
