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
package com.linkdev.easylocation.core.models

import android.location.Location

sealed class LocationResult(
    val status: Status,
    val location: Location? = null,
    val locationResultError: LocationResultError? = null
) {

    /**
     * Returns the [location] of the device.
     *
     * @param location the location result
     */
    class Success(location: Location) : LocationResult(Status.SUCCESS, location)

    /**
     * Returned when there is an error while retrieving the location.
     *
     * @param locationResultError with the error returning
     */
    class Error(locationResultError: LocationResultError? = LocationResultError.UnknownError()) :
        LocationResult(Status.ERROR, locationResultError = locationResultError)
}
