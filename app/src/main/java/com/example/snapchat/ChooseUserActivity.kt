package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChooseUserActivity : AppCompatActivity()
{
    var listView: ListView? = null
    val emailList: ArrayList<String?> = ArrayList()
    val keys: ArrayList<String?> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_user)
        listView = findViewById(R.id.listView)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emailList)
        listView?.adapter = adapter

        FirebaseDatabase.getInstance().reference.child("users").addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError)
            {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?)
            {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?)
            {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?)
            {
                var email: String = p0.child("email").value as String
                emailList.add(email)
                keys.add(p0.key)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(p0: DataSnapshot)
            {
            }
        })

        listView?.onItemClickListener = object : AdapterView.OnItemClickListener
        {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
            {
                var curUser = FirebaseAuth.getInstance().currentUser
                var snapMap: Map<String, String> = mapOf("from" to curUser!!.email!!, "imageURL" to intent.getStringExtra("imageURL"), "imageName" to intent.getStringExtra("imageName"), "message" to intent.getStringExtra("message"))

                FirebaseDatabase.getInstance().reference.child("users").child(keys[position]!!).child("snaps").push().setValue(snapMap)

                var intent = Intent(baseContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
            }
        }
    }
}
