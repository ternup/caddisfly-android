package com.ternup

import android.app.Activity
import android.app.Dialog
import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Button
import android.location.LocationProvider
import android.location.LocationManager
import android.location.Location
import android.location.LocationListener

import java.net.URL

class CaddisflyReader < Activity

    def onCreate(state)
        super state
        setContentView R.layout.main

        Log.d 'CaddisflyReader', 'init'
        @waterQualityReadings = EditText findViewById(R.id.water_quality_txt)
        @submit = Button findViewById(R.id.submit_btn)
        @locationProvider = LocationManager getSystemService(Context.LOCATION_SERVICE)
        if not @locationProvider.isProviderEnabled(LocationManager.GPS_PROVIDER)
           showAlert "GPS is not enabled!" 
        end

        setListeners
    end

    def updateLocation(location:Location)
        puts "Location: #{location.getLatitude()},#{location.getLongitude()}"
    end


    def setListeners
        @submit.setOnClickListener do |v|
            Log.d 'CaddisflyReader', 'submit clicked'
        end
        listener = LocationUpdater.new(self)
        LocationManager.requestLocationUpdates Location.GPS_PROVIDER,
            10000,
            10,
            listener
    end

    def showAlert(message:String)
        alert = AlertDialog.Builder.new(self)
        alert.setTitle 'Caddisfly'
        alert.setMessage message
        alert.SetPositiveButton('OK') do |dialog, which|
            dialog.dismiss
        end
    end
end
