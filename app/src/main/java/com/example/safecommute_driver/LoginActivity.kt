package com.example.safecommute_driver

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.safecommute_driver.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    private val database by lazy { FirebaseDatabase.getInstance().reference }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogIn.setOnClickListener { attemptLogin() }
    }

    /**
     * Website should create BOTH:
     * 1) Firebase Authentication user (email + password)
     * 2) Realtime Database `drivers/{uid}` profile
     *
     * User can enter Driver ID (DRV-0003) or full email.
     */
    private fun attemptLogin() {
        val rawLogin = binding.etEmail.text?.toString()?.trim().orEmpty()
        // Trim password — fixes copy/paste spaces; rare passwords with leading/trailing spaces would need no trim
        val password = binding.etPassword.text?.toString()?.trim().orEmpty()

        if (rawLogin.isBlank()) {
            Toast.makeText(this, getString(R.string.login_enter_driver_id_or_email), Toast.LENGTH_SHORT).show()
            return
        }
        if (password.isBlank()) {
            Toast.makeText(this, getString(R.string.login_enter_password), Toast.LENGTH_SHORT).show()
            return
        }

        val candidates = candidateEmails(rawLogin)
        binding.btnLogIn.isEnabled = false
        signInWithEmailCandidates(candidates, password, 0)
    }

    /** Try several email strings — website may store different casing than the user types. */
    private fun candidateEmails(raw: String): List<String> {
        val unique = linkedSetOf<String>()
        unique.add(resolveLoginToEmail(raw))
        if (raw.contains("@")) {
            val parts = raw.trim().split("@", limit = 2)
            if (parts.size == 2) {
                val local = parts[0].trim()
                val domain = parts[1].trim().lowercase()
                if (domain.isNotEmpty() && local.isNotEmpty()) {
                    unique.add("${local.lowercase()}@$domain")
                    unique.add("${local.uppercase()}@$domain")
                    unique.add("$local@$domain")
                }
            }
        } else {
            val id = raw.trim()
            unique.add("${id.uppercase()}@$DRIVER_EMAIL_DOMAIN")
            unique.add("${id.lowercase()}@$DRIVER_EMAIL_DOMAIN")
        }
        return unique.toList()
    }

    private fun signInWithEmailCandidates(emails: List<String>, password: String, index: Int) {
        if (index >= emails.size) {
            binding.btnLogIn.isEnabled = true
            Toast.makeText(
                this,
                getString(R.string.login_error_wrong_email_or_password),
                Toast.LENGTH_LONG
            ).show()
            Toast.makeText(this, getString(R.string.login_error_check_project), Toast.LENGTH_LONG).show()
            return
        }
        val email = emails[index]
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: run {
                    binding.btnLogIn.isEnabled = true
                    Toast.makeText(this, getString(R.string.login_failed_generic), Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }
                fetchDriverProfile(uid)
            }
            .addOnFailureListener { e ->
                val canRetry = index + 1 < emails.size && isRetryableAuthError(e)
                if (canRetry) {
                    signInWithEmailCandidates(emails, password, index + 1)
                } else {
                    binding.btnLogIn.isEnabled = true
                    when (e) {
                        is FirebaseAuthInvalidUserException -> {
                            Toast.makeText(this, getString(R.string.login_error_user_not_found), Toast.LENGTH_LONG).show()
                            Toast.makeText(this, getString(R.string.login_error_check_project), Toast.LENGTH_LONG).show()
                        }
                        is FirebaseAuthInvalidCredentialsException -> {
                            Toast.makeText(this, getString(R.string.login_error_wrong_email_or_password), Toast.LENGTH_LONG).show()
                            Toast.makeText(this, getString(R.string.login_error_check_project), Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            val msg = e.message.orEmpty()
                            Toast.makeText(
                                this,
                                getString(R.string.login_failed, msg.ifBlank { getString(R.string.login_failed_generic) }),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
    }

    private fun isRetryableAuthError(e: Exception): Boolean {
        if (e is FirebaseAuthInvalidCredentialsException || e is FirebaseAuthInvalidUserException) return true
        val msg = e.message.orEmpty()
        return msg.contains("malformed", true) ||
            msg.contains("incorrect", true) ||
            msg.contains("invalid", true) ||
            msg.contains("credential", true)
    }

    private fun resolveLoginToEmail(raw: String): String {
        return if (raw.contains("@")) {
            val parts = raw.trim().split("@", limit = 2)
            "${parts[0].trim().lowercase()}@${parts[1].trim().lowercase()}"
        } else {
            val localPart = raw.trim().uppercase()
            "$localPart@$DRIVER_EMAIL_DOMAIN"
        }
    }

    private fun fetchDriverProfile(uid: String) {
        database.child("drivers").child(uid)
            .get()
            .addOnSuccessListener { snapshot ->
                binding.btnLogIn.isEnabled = true
                if (!snapshot.exists()) {
                    DriverSession.clear()
                    Toast.makeText(
                        this,
                        getString(R.string.login_profile_not_found),
                        Toast.LENGTH_LONG
                    ).show()
                    proceedToMain()
                    return@addOnSuccessListener
                }
                val profile = parseDriverSnapshot(snapshot)
                DriverSession.setProfile(profile)
                val welcome = getString(R.string.login_welcome, profile.displayName())
                Toast.makeText(this, welcome, Toast.LENGTH_SHORT).show()
                if (profile.mustChangePassword) {
                    Toast.makeText(
                        this,
                        getString(R.string.login_must_change_password_hint),
                        Toast.LENGTH_LONG
                    ).show()
                }
                proceedToMain()
            }
            .addOnFailureListener { e ->
                binding.btnLogIn.isEnabled = true
                DriverSession.clear()
                Toast.makeText(
                    this,
                    getString(R.string.login_profile_fetch_failed, e.message ?: ""),
                    Toast.LENGTH_LONG
                ).show()
                proceedToMain()
            }
    }

    private fun parseDriverSnapshot(snapshot: DataSnapshot): DriverProfile {
        val map = mutableMapOf<String, Any?>()
        for (child in snapshot.children) {
            map[child.key ?: continue] = child.value
        }
        return DriverProfile.fromMap(map)
    }

    private fun proceedToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    companion object {
        /** Must match the email domain used when the website creates driver accounts. */
        const val DRIVER_EMAIL_DOMAIN = "safecommute.com"
    }
}
