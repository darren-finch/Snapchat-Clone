package com.example.snapchat

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.net.URI
import java.util.*

class CreateSnapActivity : AppCompatActivity()
{
    var imageView: ImageView? = null
    var messageInput: EditText? = null
    var imageName = UUID.randomUUID().toString() + ".jpg";
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)

        imageView = findViewById(R.id.imageView)
        messageInput = findViewById(R.id.messageInput)
    }

    fun chooseImage(view: View)
    {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 1 && data != null)
        {
            try
            {
                var uri = data.data
                var bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                imageView?.setImageBitmap(bitmap)
            }
            catch (e: Exception)
            {
                e.printStackTrace()
            }
        }
    }

    fun sendMessage(view: View)
    {
        // Get the data from an ImageView as bytes
        if(imageView?.drawable != null)
        {
            imageView?.isDrawingCacheEnabled = true
            imageView?.buildDrawingCache()
            val bitmap = (imageView?.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            var ref = FirebaseStorage.getInstance().reference.child("images").child(imageName)
            var uploadTask = ref.putBytes(data)

            val urlTask = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result

                    var intent = Intent(this, ChooseUserActivity::class.java)
                    intent.putExtra("imageURL", downloadUri.toString())
                    intent.putExtra("imageName", imageName)
                    intent.putExtra("message", messageInput?.text.toString())
                    startActivity(intent)
                } else {
                    Toast.makeText(baseContext, "Failed to upload image.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
