package am.testtpkg.contacts.ui.main

import am.testtpkg.contacts.R
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpNavigation()

    }

    private fun setUpNavigation() {
        setSupportActionBar(toolbar)
        navController = findNavController(R.id.nav_host_fragment)
        appBarConfiguration = AppBarConfiguration(navController.graph)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
    }

    fun setUpToolBar(
        toolbarTitle: String = resources.getString(R.string.app_name),
        isRootPage: Boolean = false
    ) {
        supportActionBar!!.run {
            setDisplayHomeAsUpEnabled(!isRootPage)
            setHomeAsUpIndicator(if (!isRootPage) R.drawable.ic_back else 0)
            toolbarTitleTv.text = toolbarTitle
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
    }

}