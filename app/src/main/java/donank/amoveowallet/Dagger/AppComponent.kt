package donank.amoveowallet.Dagger

import android.app.Application
import dagger.Component
import donank.amoveowallet.Activity.MainActivity
import donank.amoveowallet.Activity.SplashActivity
import donank.amoveowallet.Fragments.*
import donank.amoveowallet.Repositories.DBRepository
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun inject(app: Application)
    fun inject(mainActivity: MainActivity)
    fun inject(dashboard: Dashboard)
    fun inject(explorer: Explorer)
    fun inject(settings: Settings)
    fun inject(wallet: Wallet)
    fun inject(peer: Peer)
    fun inject(contacts: Contacts)
    fun inject(participate: Participate)
    fun inject(generateWallet: GenerateWallet)
    fun inject(importWallet: ImportWallet)
    fun inject(watchWallet: WatchWallet)
    fun inject(dbRepository: DBRepository)
    fun inject(receive: Receive)
    fun inject(selectedContact: SelectedContact)
    fun inject(send: Send)
    fun inject(splashActivity: SplashActivity)
    fun inject(splashFragment: SplashFragment) {

    }
}