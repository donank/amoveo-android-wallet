package donank.amoveowallet.Dagger

import android.app.Application
import dagger.Component
import donank.amoveowallet.Activity.MainActivity
import donank.amoveowallet.Fragments.*
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
}