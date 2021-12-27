package com.java.nearbyplaces.ui.home.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.java.nearbyplaces.R
import com.java.nearbyplaces.data.model.LocationDetails
import com.java.nearbyplaces.data.model.PlaceDetails
import com.java.nearbyplaces.data.repository.AppRepository
import com.java.nearbyplaces.data.retrofit.LocationApiService
import com.java.nearbyplaces.databinding.ActivityHomeBinding
import com.java.nearbyplaces.ui.home.ui.adapter.PlacesAdapter
import com.java.nearbyplaces.ui.home.ui.viewmodels.LocationViewModel
import com.java.nearbyplaces.ui.home.ui.viewmodels.PlacesViewModel
import com.java.nearbyplaces.ui.home.ui.viewmodels.PlacesViewModelFactory
import com.java.nearbyplaces.ui.home.ui.viewmodels.RxSearchObservable.fromView
import com.java.nearbyplaces.utils.isLocationEnabled
import com.java.nearbyplaces.utils.showAppPermissionSettings
import com.java.nearbyplaces.utils.showSnackbar
import com.java.nearbyplaces.utils.updateVisiblity
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class HomeActivity : AppCompatActivity() {

    private val REQUESTPERMISSIONSREQUESTCODE = 125

    private val TIME_INTERVAL_DEBOUNCE = 1000L

    private lateinit var context: Context

    //adapter
    private var placesAdapter:PlacesAdapter ?= null

    //viewmodels
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var placesViewModel: PlacesViewModel

    private var currentLocationDetails: LocationDetails? = null

    private lateinit var binding: ActivityHomeBinding

    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        setSupportActionBar(findViewById(R.id.toolbar))

        context = this
        val locationApiService = LocationApiService()
        val appRepository = AppRepository(locationApiService)
        val placesViewModelFactory = PlacesViewModelFactory(appRepository)
        placesViewModel =
            ViewModelProviders.of(this, placesViewModelFactory).get(PlacesViewModel::class.java)

        settingUpSearchView()
        settingUpRecyclerView()
    }

    private fun settingUpRecyclerView() {
        placesAdapter = PlacesAdapter(mutableListOf<PlaceDetails>())
        var placesRecyclerView:RecyclerView = binding.mainContentView.placesRecyclerView
        placesRecyclerView.layoutManager = LinearLayoutManager(context)
        placesRecyclerView.setHasFixedSize(false)
        placesRecyclerView.adapter = placesAdapter
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        if (!checkLocationPermissions()) {
            requestPermissions()
        } else if(isLocationEnabled()){
            showSnackbar(binding.rootLayout,
                "Location should be enabled to get location based results",
                "OK",
                View.OnClickListener {
                    // Request to enable location
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
        } else{
            observeLocationUpdates()
        }
        observePlacesUpdates()
    }

    private fun settingUpSearchView(){
        val disposable: Disposable = fromView(binding.mainContentView.searchView)
            .debounce(TIME_INTERVAL_DEBOUNCE, TimeUnit.MILLISECONDS)
            .filter { text: String -> !text.isEmpty() && text.length >= 3 }
            .map { text: String ->
                text.toLowerCase().trim { it <= ' ' }
            }
            .distinctUntilChanged()
            .switchMap<Any> { s: String? ->
                Observable.just(
                    s
                )
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { query: Any? ->
                placesViewModel.getPlaces(query.toString(), currentLocationDetails)
            }
        compositeDisposable.add(disposable)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkLocationPermissions(): Boolean {
        val permissionCoarseState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        val permissionFineState = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        return permissionCoarseState == PackageManager.PERMISSION_GRANTED
                && permissionFineState == PackageManager.PERMISSION_GRANTED
    }


    private fun startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(
            this@HomeActivity,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            REQUESTPERMISSIONSREQUESTCODE
        )
    }

    private fun observePlacesUpdates() {
        placesViewModel.placesList.observe(this, {
            placesAdapter?.updateList(it.toMutableList())
        })

        placesViewModel.errorMessage.observe(this, {
            binding.mainContentView.errorTextView.updateVisiblity(true)
            binding.mainContentView.errorTextView.text = it
        })

        placesViewModel.loadingStatus.observe(this, {
            if(it)
                binding.mainContentView.errorTextView.updateVisiblity(false)

            placesAdapter?.run{  updateList(mutableListOf()) }

            binding.mainContentView.progressBar.updateVisiblity(it)
        })
    }

    private fun requestPermissions() {
        val shouldProvideCoarseRationale = ActivityCompat.shouldShowRequestPermissionRationale(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (!shouldProvideCoarseRationale) {
            startLocationPermissionRequest()
        } else {
            showSnackbar(binding.rootLayout,
                "Enable location permissions as they are required to get the user current location",
                "OK",
                View.OnClickListener {
                    // Request permission
                    showAppPermissionSettings()
                })
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUESTPERMISSIONSREQUESTCODE) {
            if (checkLocationPermissions()) {
                observeLocationUpdates()
            } else {
                requestPermissions()
            }
        }
    }

    private fun observeLocationUpdates() {
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel::class.java)
        locationViewModel.getLocationLiveData().observe(this, {
            currentLocationDetails = it
        })
    }


    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}