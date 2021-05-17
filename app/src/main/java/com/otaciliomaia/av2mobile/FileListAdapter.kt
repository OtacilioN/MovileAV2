package com.otaciliomaia.av2mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FileListAdapter(
    private val fileList: List<String>,
    private val listener: OnDeleteClickListener
) : RecyclerView.Adapter<FileListAdapter.FileViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.file_item, parent, false)
        return FileViewHolder(itemView)
    }

    override fun getItemCount() = fileList.size

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val currentItem = fileList[position]
        holder.textViewTitle.text = currentItem
    }

    inner class FileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val imageView: ImageView = itemView.findViewById(R.id.deleteFileImg)
        val textViewTitle: TextView = itemView.findViewById(R.id.fileName)

        init {
            imageView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onDeleteClick(position)
            }
        }
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(position: Int)
    }
}