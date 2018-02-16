package com.vertice.teepop.mangaapp.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.vertice.teepop.mangaapp.R
import kotlinx.android.synthetic.main.fragment_login.*
import org.greenrobot.eventbus.EventBus


class LoginFragment : Fragment() {

    private val TAG = this::class.java.simpleName

    private val RC_SIGN_IN = 1001

    private lateinit var mGoogleSignInClient: GoogleSignInClient

    private lateinit var mAuth: FirebaseAuth

    private lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)

        dialog = ProgressDialog(context).apply {
            setMessage("Please Wait")
            setCancelable(false)
            isIndeterminate = true
        }

        mAuth = FirebaseAuth.getInstance()

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1069943470222-r85ehmuc5oakqqfb68u8ro01m5d3m3po.apps.googleusercontent.com")
                .requestEmail()
                .build()

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initInstances(savedInstanceState)
    }

    private fun init(savedInstanceState: Bundle?) {
        // Init Fragment level's variable(s) here
    }

    private fun initInstances(savedInstanceState: Bundle?) {
        // Init 'View' instance(s) with rootView.findViewById here

        signInButton.setOnClickListener {
            dialog.show()
            signInWithGoogle()
        }

        loginButton.setOnClickListener(loginButtonClickListener)

        registerButton.setOnClickListener {
            EventBus.getDefault().post(ClickRegisterButtonEvent())
        }
    }

    override fun onStart() {
        super.onStart()

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        val currentUser = mAuth.currentUser
        currentUser?.let {
            //            Toast.makeText(context, "Login", Toast.LENGTH_SHORT).show()
            loginSuccess()
            Log.i(TAG, "User: ${it.displayName} Email: ${it.email}")
        }
    }

    override fun onStop() {
        super.onStop()
    }

    /*
     * Save Instance State Here
     */
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save Instance State here
    }

    /*
     * Restore Instance State Here
     */
    private fun onRestoreInstanceState(savedInstanceState: Bundle) {
        // Restore Instance State here
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                dialog.dismiss()
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.id!!)

        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(activity, { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        dialog.dismiss()
                        Log.d(TAG, "signInWithCredential:success")
//                        Toast.makeText(context, "Login", Toast.LENGTH_SHORT).show()
                        val user = mAuth.currentUser
                        Log.i(TAG, "User: ${user?.displayName} Email: ${user?.email}")
                        loginSuccess()
                    } else {
                        // If sign in fails, display a message to the user.
                        dialog.dismiss()
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()
//                        updateUI(null)
                    }
                })
    }

    private fun loginSuccess() {
        EventBus.getDefault().post(LoginSuccessEvent())
    }

    private fun signInWithGoogle() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun toggleError(email: String, password: String) {
        if (email.isEmpty()) {
            usernameInputLayout.isErrorEnabled = true
            usernameInputLayout.error = "Filed can't be empty"
        } else {
            usernameInputLayout.isErrorEnabled = false
        }

        if (password.isEmpty()) {
            passwordInputLayout.isErrorEnabled = true
            passwordInputLayout.error = "Filed can't be empty"
        } else {
            passwordInputLayout.isErrorEnabled = false
        }
    }

    private val loginButtonClickListener: (View) -> Unit = {
        val userName = editUsername.text.toString()
        val password = editPassword.text.toString()

        if (userName.isNotEmpty() && password.isNotEmpty()) {
            dialog.show()
            mAuth.signInWithEmailAndPassword(userName, password)
                    .addOnFailureListener {
                        dialog.dismiss()
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnCompleteListener {
                        dialog.dismiss()
                        if (it.isSuccessful)
//                            Toast.makeText(context, "Login", Toast.LENGTH_SHORT).show()
                            loginSuccess()
                        else
                            Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    }
        } else {
            toggleError(userName, password)
        }

    }

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment().apply {
                arguments = Bundle().apply {

                }
            }
        }
    }

    class ClickRegisterButtonEvent

    class LoginSuccessEvent
}
