package com.vertice.teepop.mangaapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.vertice.teepop.mangaapp.R
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(savedInstanceState)

        if (savedInstanceState != null)
            onRestoreInstanceState(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
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

        createButton.setOnClickListener(createButtonClickListener)
    }

    private fun toggleError(email: String, password: String) {
        if (email.isEmpty()) {
            emailInputLayout.isErrorEnabled = true
            emailInputLayout.error = "Filed can't be empty"
        } else {
            emailInputLayout.isErrorEnabled = false
        }

        if (password.isEmpty()) {
            passwordInputLayout.isErrorEnabled = true
            passwordInputLayout.error = "Filed can't be empty"
        } else {
            passwordInputLayout.isErrorEnabled = false
        }
    }

    override fun onStart() {
        super.onStart()
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        FirebaseAuth.getInstance().removeAuthStateListener(firebaseAuthListener)
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

    private val firebaseAuthListener: (FirebaseAuth) -> Unit = { firebaseAuth ->
        val user: FirebaseUser? = firebaseAuth.currentUser
        user?.let {
            Toast.makeText(context, "Login", Toast.LENGTH_SHORT).show()
        }
    }

    private val createButtonClickListener: (View) -> Unit = {
        val email = editEmail.text?.toString() ?: ""
        val password = editPassword.text?.toString() ?: ""

        if (email.isNotEmpty() && password.isNotEmpty()) {
            FirebaseAuth.getInstance()
                    .createUserWithEmailAndPassword(editEmail.text.toString(), editPassword.text.toString())
                    .addOnFailureListener { Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show() }
                    .addOnCompleteListener {
                        if (it.isSuccessful)
                            Toast.makeText(context, "Create User Complete", Toast.LENGTH_SHORT).show()
                        else
                            Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                    }
        } else {
            toggleError(email, password)
        }
    }

    companion object {

        fun newInstance(): RegisterFragment {
            val fragment = RegisterFragment()
            val args = Bundle()
            fragment.arguments = args
            return fragment
        }
    }

}
