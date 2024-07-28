package com.hoppers.tawk.di

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.hoppers.tawk.core.local.IRoomDataBase
import com.hoppers.tawk.core.local.UserDatabase
import com.hoppers.tawk.core.local.UserEntity
import com.hoppers.tawk.core.remote.DefaultDispatchers
import com.hoppers.tawk.core.remote.DispatcherProvider
import com.hoppers.tawk.core.remote.UserRemoteMediator
import com.hoppers.tawk.core.remote.ktorHttpClient
import com.hoppers.tawk.home.repositories.HomeRepository
import com.hoppers.tawk.home.repositories.IHomeRepository
import com.hoppers.tawk.home.viewmodels.HomeViewModel
import com.hoppers.tawk.profile.repositories.IProfileRepository
import com.hoppers.tawk.profile.repositories.ProfileRepository
import com.hoppers.tawk.profile.viewmodels.ProfileViewModel
import io.ktor.client.HttpClient
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

@OptIn(ExperimentalPagingApi::class)
val appModule = module {

    single<DispatcherProvider> { DefaultDispatchers() }
    single<IProfileRepository> { ProfileRepository(get()) }

    /**
     * Provides a singleton instance of [IRoomDataBase].
     * Builds the Room database with the name "user.db".
     */

    single<IRoomDataBase> {
        Room.databaseBuilder(
            androidContext(),
            UserDatabase::class.java,
            "user.db"
        ).build()
    }
    single<HttpClient> { ktorHttpClient }
    single<IHomeRepository> { HomeRepository(client = get()) }
    /**
     * Provides a factory for creating instances of [ProfileViewModel].
     * @param params Parameters needed to create the [ProfileViewModel].
     */
    factory { params ->
        ProfileViewModel(
            dispatchers = get(),
            userRepository = get(),
            userDatabase = get(),
            user = params.get()
        )
    }
    single { MutableStateFlow("") }

    factory { _ -> HomeViewModel(pager = get(), dispatcherProvider = get()) }

    /**
     * Provides a singleton instance of [Pager] for paging through [UserEntity].
     * Configures the pager with page size, initial load size, and prefetch distance.
     */

    single<Pager<Int, UserEntity>> {
        Pager(
            config = PagingConfig(
                pageSize = 30,
                initialLoadSize = 30,
                prefetchDistance = 0
            ),
            remoteMediator = UserRemoteMediator(userDb = get(), repository = get()),
            initialKey = 0,
            pagingSourceFactory = {
                get<IRoomDataBase>().dao.pagingSource()
            }
        )
    }
}
