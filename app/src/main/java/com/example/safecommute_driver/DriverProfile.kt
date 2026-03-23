package com.example.safecommute_driver

/**
 * Driver record from Realtime Database `drivers/{uid}` (created by admin website).
 */
data class DriverProfile(
    val uid: String? = null,
    val id: String? = null,
    val email: String? = null,
    val username: String? = null,
    val driverId: String? = null,
    val fullName: String? = null,
    val firstName: String? = null,
    val middleName: String? = null,
    val lastName: String? = null,
    val name: String? = null,
    val phoneNumber: String? = null,
    val contactNumber: String? = null,
    val recoveryEmail: String? = null,
    val role: String? = null,
    val status: String? = null,
    val mustChangePassword: Boolean = false,
    val createdAt: Long? = null
) {
    fun displayName(): String =
        fullName?.takeIf { it.isNotBlank() }
            ?: name?.takeIf { it.isNotBlank() }
            ?: firstName?.takeIf { it.isNotBlank() }?.let { fn ->
                listOfNotNull(fn, middleName, lastName).joinToString(" ").trim()
            }?.takeIf { it.isNotBlank() }
            ?: username?.takeIf { it.isNotBlank() }
            ?: driverId?.takeIf { it.isNotBlank() }
            ?: "Driver"

    companion object {
        private fun bool(v: Any?): Boolean = when (v) {
            is Boolean -> v
            is Long -> v != 0L
            is Int -> v != 0
            is String -> v.equals("true", true)
            else -> false
        }

        private fun long(v: Any?): Long? = when (v) {
            is Long -> v
            is Int -> v.toLong()
            is Double -> v.toLong()
            else -> null
        }

        fun fromMap(map: Map<String, Any?>): DriverProfile {
            return DriverProfile(
                uid = map["uid"] as? String,
                id = map["id"] as? String,
                email = map["email"] as? String,
                username = map["username"] as? String,
                driverId = map["driverId"] as? String,
                fullName = map["fullName"] as? String,
                firstName = map["firstName"] as? String,
                middleName = map["middleName"] as? String,
                lastName = map["lastName"] as? String,
                name = map["name"] as? String,
                phoneNumber = map["phoneNumber"] as? String,
                contactNumber = map["contactNumber"] as? String,
                recoveryEmail = map["recoveryEmail"] as? String,
                role = map["role"] as? String,
                status = map["status"] as? String,
                mustChangePassword = bool(map["mustChangePassword"]),
                createdAt = long(map["createdAt"])
            )
        }
    }
}
