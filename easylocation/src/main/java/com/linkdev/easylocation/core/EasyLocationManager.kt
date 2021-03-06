/**
 * Copyright (c) 2020-present Link Development
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.linkdev.easylocation.core

import android.Manifest
import android.content.Context
import android.location.Location
import androidx.annotation.RequiresPermission
import com.linkdev.easylocation.core.location_providers.ILocationProvider
import com.linkdev.easylocation.core.location_providers.LocationProvidersFactory
import com.linkdev.easylocation.core.location_providers.LocationResultListener
import com.linkdev.easylocation.core.location_providers.fused.FusedLocationProvider
import com.linkdev.easylocation.core.location_providers.fused.options.LocationOptions
import com.linkdev.easylocation.core.models.*
import com.linkdev.easylocation.core.utils.EasyLocationTimeoutHelper

/**
 * This class Manages the location timeout updates request,
 * Also manages the request [LocationRequestType], and
 * manages the [ILocationProvider] and the fetch and stop location updates.
 *
 * @param mContext [Context]
 * @param mLocationRequestTimeout Optional = [EasyLocationConstants.DEFAULT_LOCATION_REQUEST_TIMEOUT] - The max wait time for the location update after the request is made, If exceeded the request will stop.
 * @param mLocationRequestType Optional = [LocationRequestType.UPDATES] - One of [LocationRequestType.ONE_TIME_REQUEST] or [LocationRequestType.UPDATES]
 */
internal class EasyLocationManager(
    private val mContext: Context,
    private var mLocationRequestTimeout: Long = EasyLocationConstants.DEFAULT_LOCATION_REQUEST_TIMEOUT,
    private var mLocationRequestType: LocationRequestType = LocationRequestType.UPDATES
) {

    /**
     * Callback to emit location updates.
     */
    private lateinit var mLocationResultListener: LocationResultListener

    /**
     * Location Provider to request location updates,
     * currently there is only [FusedLocationProvider]
     */
    private lateinit var mILocationProvider: ILocationProvider

    private val mTimeoutHelper = EasyLocationTimeoutHelper(mLocationRequestTimeout, onTimeout())

    /**
     * Requests location updates using [locationOptions].
     *
     * @param locationOptions The specs required for retrieving location info:
     *      + [DisplacementFusedLocationOptions]
     *      + [TimeFusedLocationOptions]
     *
     * @param locationResultListener This listener will be invoked with the updates from the location provider.
     *
     * @throws IllegalArgumentException If the [locationOptions] does not correspond to the selected [LocationProvidersTypes] mentioned above.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    fun requestLocationUpdates(
        locationOptions: LocationOptions,
        locationResultListener: LocationResultListener,
    ) {
        mLocationResultListener = locationResultListener

        if (mLocationRequestType == LocationRequestType.FETCH_LAST_KNOWN_LOCATION)
            fetchLastKnownLocation()
        else
            requestLocationUpdates(locationOptions)

        mTimeoutHelper.startLocationRequestTimer()
    }

    /**
     * Cancels the location updates from the factory and stops the timeout timer.
     */
    fun stopLocationUpdates() {
        if (::mILocationProvider.isInitialized)
            mILocationProvider.stopLocationUpdates()

        mTimeoutHelper.stop()
    }

    /**
     * Requests location updates [locationOptions].
     *
     * @param locationOptions The specs required for retrieving location info:
     *      + [DisplacementFusedLocationOptions]
     *      + [TimeFusedLocationOptions]
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun requestLocationUpdates(locationOptions: LocationOptions) {
        mILocationProvider = LocationProvidersFactory(mContext)
            .getLocationProvider(LocationProvidersTypes.FUSED_LOCATION_PROVIDER, locationOptions)

        mILocationProvider.requestLocationUpdates(onLocationRetrieved())
    }

    /**
     * fetch the latest known location.
     */
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION])
    private fun fetchLastKnownLocation() {
        mILocationProvider = LocationProvidersFactory(mContext)
            .getLocationProvider(LocationProvidersTypes.FUSED_LOCATION_PROVIDER)

        mILocationProvider.fetchLatestKnownLocation(onLocationRetrieved())
    }

    /**
     * Callback for when the location is retrieved to manage the timer and the location updates and update the consumer [mLocationResultListener].
     */
    private fun onLocationRetrieved(): LocationResultListener {
        return object : LocationResultListener {
            override fun onLocationRetrieved(location: Location) {
                if (mLocationRequestType == LocationRequestType.ONE_TIME_REQUEST)
                    stopLocationUpdates()
                else
                    mTimeoutHelper.restartTimer()

                mLocationResultListener.onLocationRetrieved(location)
            }

            override fun onLocationRetrievalError(locationResult: LocationResult?) {
                mLocationResultListener.onLocationRetrievalError(locationResult)

            }
        }
    }

    private fun onTimeout(): Runnable = Runnable {
        stopLocationUpdates()
        if (::mLocationResultListener.isInitialized)
            mLocationResultListener.onLocationRetrievalError(
                LocationResult.Error(LocationResultError.TimeoutError())
            )
    }
}
