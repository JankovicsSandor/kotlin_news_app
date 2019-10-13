package com.shopping.news_app

import android.os.Bundle
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragment
import androidx.preference.PreferenceManager

class NewsPreferenceFragment: PreferenceFragment(), Preference.OnPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings_main)
        val minDepth = findPreference(getString(R.string.setting_category_key))
        bindPreferenceSummaryToValue(minDepth)

        val orderBy = findPreference(getString(R.string.setting_orberby_key))
        bindPreferenceSummaryToValue(orderBy)
    }


    override fun onPreferenceChange(preference: Preference, value: Any?): Boolean {
        val stringValue = value.toString()
        if (preference is ListPreference) {
            val prefIndex = preference.findIndexOfValue(stringValue)
            if (prefIndex >= 0) {
                val labels = preference.entries
                preference.setSummary(labels[prefIndex])
            }
        } else {
            preference.summary = stringValue
        }
        return true
    }

    // Writing out the actual preferences
    private fun bindPreferenceSummaryToValue(preference: Preference) {
        preference.onPreferenceChangeListener = this
        val preferences = PreferenceManager.getDefaultSharedPreferences(preference.context)
        val preferenceString = preferences.getString(preference.key, "")
        onPreferenceChange(preference, preferenceString!!)
    }
}