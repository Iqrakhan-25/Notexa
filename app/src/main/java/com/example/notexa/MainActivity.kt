package com.example.notexa

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private val noteList = mutableListOf<Pair<Note, String>>() // Note + docId
    private lateinit var adapter: NoteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerViewNotes = findViewById<RecyclerView>(R.id.recyclerViewNotes)
        val fabAddNote = findViewById<FloatingActionButton>(R.id.fabAddNote)
        val searchBar = findViewById<EditText>(R.id.searchBar)

        adapter = NoteAdapter(noteList) { docId ->
            // Delete note from Firestore using docId
            db.collection("notes").document(docId).delete()
        }

        recyclerViewNotes.layoutManager = LinearLayoutManager(this)
        recyclerViewNotes.adapter = adapter

        fabAddNote.setOnClickListener {
            startActivity(Intent(this, AddNoteActivity::class.java))
        }

        searchBar.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                adapter.filter.filter(s.toString())
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        loadNotes()
    }

    private fun loadNotes() {
        db.collection("notes")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val tempList = mutableListOf<Pair<Note, String>>()
                for (doc in snapshot) {
                    val note = doc.toObject(Note::class.java)
                    tempList.add(Pair(note, doc.id))
                }
                adapter.updateList(tempList)
            }
    }
}
