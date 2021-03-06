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

enum class EasyLocationPriority(val value: Int) {
    /**
     * Used to request the most accurate locations available.
     */
    PRIORITY_HIGH_ACCURACY(100),
    /**
     * Used to request "block" level accuracy.
     */
    PRIORITY_BALANCED_POWER_ACCURACY(102),
    /**
     * Used to request "city" level accuracy.
     */
    PRIORITY_LOW_POWER(104),
    /**
     * Used to request the best accuracy possible with zero additional power consumption.
     */
    PRIORITY_NO_POWER(105),
}
