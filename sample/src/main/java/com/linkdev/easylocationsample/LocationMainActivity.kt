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
package com.linkdev.easylocationsample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment

class LocationMainActivity : AppCompatActivity(),
    LocationMainFragment.ILocationMainFragmentListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        if (savedInstanceState == null)
            replaceFragment(
                LocationMainFragment.newInstance(),
                LocationMainFragment.TAG,
                false
            )
    }

    private fun replaceFragment(fragment: Fragment, tag: String, addToBackStack: Boolean = true) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragmentContainer, fragment, tag)
            if (addToBackStack)
                addToBackStack(null)
            commit()
        }
    }

    override fun onSampleClicked(fragment: Fragment, tag: String) {
        replaceFragment(fragment, tag)
    }
}
