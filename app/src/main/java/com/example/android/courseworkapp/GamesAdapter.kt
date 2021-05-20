package com.example.android.courseworkapp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.courseworkapp.model.HostedGame

private const val TAG = "GamesAdapterActivity"

class GamesAdapter(val context: Context , val hostedGames: List<HostedGame>, val onClickListener: OnClickListener) : RecyclerView.Adapter<GamesAdapter.ViewHolder>() {
    interface OnClickListener {
        fun onItemClick(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val hostedGame = hostedGames[position]
        holder.itemView.setOnClickListener{
            Log.i(TAG, "Tapped on $position")
            onClickListener.onItemClick(position)
        }
        val textViewTitle = holder.itemView.findViewById<TextView>(android.R.id.text1)
        textViewTitle.text = hostedGame.title

    }

    override fun getItemCount() = hostedGames.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
