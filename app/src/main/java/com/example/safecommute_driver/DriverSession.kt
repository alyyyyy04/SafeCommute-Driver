package com.example.safecommute_driver

/**
 * Holds the logged-in driver's profile after successful login + RTDB fetch.
 */
object DriverSession {
    @Volatile
    var profile: DriverProfile? = null
        private set

    fun setProfile(p: DriverProfile?) {
        profile = p
    }

    fun clear() {
        profile = null
    }
}
