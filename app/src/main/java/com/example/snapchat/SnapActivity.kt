package com.example.snapchat

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.net.HttpURLConnection
import java.net.URL

class SnapActivity : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth
    var messageTextView: TextView? = null
    var imageView: ImageView? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap)
        auth = FirebaseAuth.getInstance()

        messageTextView = findViewById(R.id.messageTextView)
        imageView = findViewById(R.id.imageView)

        messageTextView?.text = intent.getStringExtra("message")

        var downloader = ImageDownloader()

        try
        {
            var myImage = downloader.execute(intent.getStringExtra("imageURL")).get()
            imageView?.setImageBitmap(myImage)
        }
        catch (e: java.lang.Exception)
        {
            e.printStackTrace()
        }
    }

    class ImageDownloader : AsyncTask<String?, Void?, Bitmap?>()
    {
        override fun doInBackground(vararg params: String?): Bitmap?
        {
            return try
            {
                val url = URL(params[0])
                val connection =
                    url.openConnection() as HttpURLConnection
                connection.connect()
                val `in` = connection.inputStream
                BitmapFactory.decodeStream(`in`)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
                null
            }
        }
    }

    override fun onBackPressed()
    {
        FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid).child("snaps").child(intent.getStringExtra("key")!!).removeValue()
        FirebaseStorage.getInstance().reference.child("images").child(intent.getStringExtra("imageName")!!).delete()
        super.onBackPressed()
    }
}
