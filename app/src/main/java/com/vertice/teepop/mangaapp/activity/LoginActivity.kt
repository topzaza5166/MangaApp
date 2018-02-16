package com.vertice.teepop.mangaapp.activity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.vertice.teepop.mangaapp.fragment.LoginFragment
import com.vertice.teepop.mangaapp.fragment.RegisterFragment
import com.vertice.teepop.mangaapp.R
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                    .add(R.id.loginContentContainer, LoginFragment.newInstance())
                    .commit()
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
        FirebaseAuth.getInstance().signOut()
    }

    @Subscribe
    fun onLoginSuccess(event: LoginFragment.LoginSuccessEvent) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    @Subscribe
    fun onClickRegisterButton(event: LoginFragment.ClickRegisterButtonEvent) {
        supportFragmentManager.beginTransaction()
                .setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                .replace(R.id.loginContentContainer, RegisterFragment.newInstance())
                .addToBackStack(null)
                .commit()
    }
}
