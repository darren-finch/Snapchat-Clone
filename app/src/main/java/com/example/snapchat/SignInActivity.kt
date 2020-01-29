package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SignInActivity : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText

    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        emailInput = findViewById(R.id.emailInput)
        passwordInput = findViewById(R.id.passwordInput)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference
    }

    fun signInOrRegister(view: View)
    {
        var email = emailInput.text.toString();
        var password = passwordInput.text.toString();

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this)
            { task ->
                if (task.isSuccessful)
                {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(SIGN_IN_TAG, "signInWithEmail:success")
                    val user = auth.currentUser
                    signIn(user)
                }
                else
                {
                    // If sign in fails, display a message to the user.
                    Log.w(SIGN_IN_TAG, "signInWithEmail:failure", task.exception)
//                    Toast.makeText(baseContext, "Authentication failed." + task.exception,
//                        Toast.LENGTH_SHORT).show()

                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(this)
                        { task2 ->
                            if (task2.isSuccessful)
                            {
                                // Sign in success, update UI with the signed-in user's information
                                val user = auth.currentUser
                                createUser(auth.currentUser?.uid.toString(), email, password)
                                Log.d(SIGN_IN_TAG, "createUserWithEmail:success")
                                signIn(user)
                            }
                            else
                            {
                                // If sign in fails, display a message to the user.
                                Log.w(SIGN_IN_TAG, "createUserWithEmail:failure", task2.exception)
                                Toast.makeText(baseContext, "Authentication failed. " + task2.exception!!.message,
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                }
            }
    }

    fun signIn(user: FirebaseUser?)
    {
        var intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun createUser(userID: String, email: String, password: String)
    {
        //Create new user and put him in the database
        database.child("users").child(userID).child("email").setValue(email)
    }

    companion object {
        const val SIGN_IN_TAG = "Sign-in Info"
    }
}
