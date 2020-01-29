package com.example.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class MainActivity : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth
    var snapsListView: ListView? = null
    var emails: ArrayList<String> = ArrayList()
    var snaps: ArrayList<DataSnapshot> = ArrayList()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        var inflater = menuInflater
        inflater.inflate(R.menu.snap_menu, menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when(item.itemId)
        {
            R.id.createSnap -> createSnap()
            R.id.logout -> logout()
        }
        return super.onOptionsItemSelected(item)
    }
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        snapsListView = findViewById(R.id.snapsListView)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, emails)
        snapsListView?.adapter = adapter

        FirebaseDatabase.getInstance().reference.child("users").child(auth.currentUser!!.uid).child("snaps").addChildEventListener(object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?)
            {
                emails.add(p0?.child("from")?.value as String)
                snaps.add(p0!!)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(p0: DataSnapshot)
            {
                for((index, snap: DataSnapshot) in snaps.withIndex())
                {
                    if(snap.key == p0.key)
                    {
                        snaps.removeAt(index)
                        emails.removeAt(index)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        })

        snapsListView?.onItemClickListener = object : AdapterView.OnItemClickListener {
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long)
            {
                loadSnap(position)
            }
        }
    }

    private fun loadSnap(position: Int)
    {
        var snapshot = snaps.get(position)

        var intent = Intent(baseContext, SnapActivity::class.java)
        intent.putExtra("message", snapshot.child("message").value as String)
        intent.putExtra("imageURL", snapshot.child("imageURL").value as String)
        intent.putExtra("imageName", snapshot.child("imageName").value as String)
        intent.putExtra("key", snapshot.key)
        intent.putExtra("position", position)
        startActivity(intent)
    }

    private fun createSnap()
    {
        var intent = Intent(this, CreateSnapActivity::class.java)
        startActivity(intent)
    }

    private fun logout()
    {
        finish()
        auth.signOut()
    }

    override fun onBackPressed()
    {
        super.onBackPressed()
        auth.signOut()
    }
}
