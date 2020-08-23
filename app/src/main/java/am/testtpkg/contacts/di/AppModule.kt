package am.testtpkg.contacts.di

import am.testtpkg.contacts.common.ContactsHelper
import am.testtpkg.contacts.domain.repository.ContactsRepositoryImpl
import am.testtpkg.contacts.domain.repository.IContactsRepository
import am.testtpkg.contacts.domain.usecase.ContactsUseCase
import am.testtpkg.contacts.ui.contacts.ContactItemAdapter
import am.testtpkg.contacts.ui.contacts.ContactsAdapter
import am.testtpkg.contacts.ui.contacts.ContactsViewModel
import am.testtpkg.contacts.ui.contacts.ContactsSharedViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object AppModule {

    val viewModelModule = module {
        viewModel {
            ContactsViewModel(get())
        }
        viewModel {
            ContactsSharedViewModel()
        }
    }

    val adapterModule = module {
        single {
            ContactsAdapter()
        }
        single {
            ContactItemAdapter()
        }
    }

    val domainModule = module {
        single {
            androidContext().resources
        }
        single<IContactsRepository> {
            ContactsRepositoryImpl(get())
        }
        single {
            ContactsUseCase(get())
        }
        single {
            ContactsHelper(get())
        }
    }
}