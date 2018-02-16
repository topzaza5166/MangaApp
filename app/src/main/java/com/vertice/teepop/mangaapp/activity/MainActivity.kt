package com.vertice.teepop.mangaapp.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.vertice.teepop.mangaapp.R
import com.vertice.teepop.mangaapp.fragment.AddMangaFragment

class MainActivity : AppCompatActivity() {

    private val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        user?.let {
            Toast.makeText(this, "Welcome ${user.displayName}", Toast.LENGTH_SHORT).show()
        }

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                    .add(R.id.mainContentContainer, AddMangaFragment.newInstance())
                    .commit()
    }
}
