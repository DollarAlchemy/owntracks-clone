package org.owntracks.android.preferences

import android.content.SharedPreferences
import org.owntracks.android.preferences.types.ReverseGeocodeProvider
import kotlin.reflect.KProperty
import kotlin.reflect.typeOf

/***
 * Allows a preferences class to read and write values from some sort of store
 */
interface PreferencesStore {
    fun getSharedPreferencesName(): String

    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, default: Boolean): Boolean

    fun putInt(key: String, value: Int)
    fun getInt(key: String, default: Int): Int

    fun putFloat(key: String, value: Float)
    fun getFloat(key: String, default: Float): Float

    fun putString(key: String, value: String)
    fun getString(key: String, default: String): String?

    fun putStringSet(key: String, values: Set<String>)
    fun getStringSet(key: String, defaultValues: Set<String>): Set<String>

    fun remove(key: String)

    fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    )

    fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener
    )

    // For getting, we have to maybe assume that the type that we're passed of the property will
    // be the same as what was previously written to the store, and then just throw caution to
    // the wind and cast it to that thing.
    @Suppress("UNCHECKED_CAST")
    operator fun <T> getValue(preferences: Preferences, property: KProperty<*>): T {
        return when (property.returnType) {
            typeOf<Boolean>() -> getBoolean(property.name, false) as T
            typeOf<String>() -> getString(property.name, "") as T
            typeOf<Int>() -> getInt(property.name, 0) as T
            typeOf<Float>() -> getFloat(property.name, 0f) as T
            typeOf<Set<String>>() -> getStringSet(property.name, emptySet()) as T
            typeOf<ReverseGeocodeProvider>() -> ReverseGeocodeProvider.getByValue(
                getString(
                    property.name,
                    ""
                ) ?: ""
            ) as T
            else -> throw Exception("BAD BAD BAD BAD")
        }
    }

    // For setting, we just switch on the type of the value
    @Suppress("UNCHECKED_CAST")
    operator fun <T> setValue(preferences: Preferences, property: KProperty<*>, value: T) {
        when (value) {
            is Boolean -> putBoolean(property.name, value)
            is String -> putString(property.name, value)
            is Int -> putInt(property.name, value)
            is Float -> putFloat(property.name, value)
            is Set<*> -> putStringSet(property.name, value as Set<String>)
            is ReverseGeocodeProvider -> putString(property.name, value.value)
            else -> throw Exception("Nopety nope.")
        }
    }
}
