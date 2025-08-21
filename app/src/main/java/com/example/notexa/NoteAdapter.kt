package com.example.notexa

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class NoteAdapter(
    private var notes: MutableList<Pair<Note, String>>, // Pair of Note and docId
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(), Filterable {

    private var notesListFull = ArrayList(notes)
    private var selectedPosition = -1 // Track long-pressed note

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val content: TextView = itemView.findViewById(R.id.tvContent)
        val card: CardView = itemView.findViewById(R.id.cardNote)
        val imgDelete: ImageView = itemView.findViewById(R.id.imgDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun getItemCount(): Int = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val (note, docId) = notes[position]
        holder.title.text = note.title
        holder.content.text = note.content
        holder.imgDelete.visibility = if (position == selectedPosition) View.VISIBLE else View.GONE
        holder.card.setOnLongClickListener {
            selectedPosition = position
            notifyDataSetChanged()
            true
        }
        holder.imgDelete.setOnClickListener {
            onDelete(docId)
            selectedPosition = -1
            notifyDataSetChanged()
        }

        // Clicking elsewhere deselects
        holder.card.setOnClickListener {
            if (selectedPosition != -1) {
                selectedPosition = -1
                notifyDataSetChanged()
            }
        }
    }

    fun updateList(newNotes: List<Pair<Note, String>>) {
        notes.clear()
        notes.addAll(newNotes)
        notesListFull = ArrayList(newNotes)
        selectedPosition = -1
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<Pair<Note, String>>()
                if (constraint.isNullOrEmpty()) {
                    filteredList.addAll(notesListFull)
                } else {
                    val filterPattern = constraint.toString().lowercase().trim()
                    for ((note, docId) in notesListFull) {
                        if (note.title.lowercase().contains(filterPattern) ||
                            note.content.lowercase().contains(filterPattern)
                        ) {
                            filteredList.add(Pair(note, docId))
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notes.clear()
                notes.addAll(results?.values as List<Pair<Note, String>>)
                selectedPosition = -1
                notifyDataSetChanged()
            }
        }
    }
}
