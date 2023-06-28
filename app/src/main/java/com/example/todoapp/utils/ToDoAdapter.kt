package com.example.todoapp.utils

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.todoapp.databinding.EachTod0ItemBinding

class ToDoAdapter(private val list:MutableList<ToDoData>):
    RecyclerView.Adapter<ToDoAdapter.ToDoViewHolder>() {
    inner class ToDoViewHolder(val binding: EachTod0ItemBinding): RecyclerView.ViewHolder(binding.root)
    private lateinit var binding : EachTod0ItemBinding
    private var listener :ToDoAdapterClickInterface?=null
    fun setListener(listner:ToDoAdapterClickInterface){
        this.listener = listener
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoViewHolder {
        binding = EachTod0ItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ToDoViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ToDoViewHolder, position: Int) {
        with(holder){
            with(list[position]){
                binding.todoTask.text = this.task
                binding.deleteTask.setOnClickListener {
                    listener?.onDeleteTaskBtnClick(this)
                }
                binding.editTask.setOnClickListener {
                    listener?.onEditTaskBtnClick(this)
                }
            }
        }
    }
    interface ToDoAdapterClickInterface{
        fun onDeleteTaskBtnClick(toDoData: ToDoData)
        fun onEditTaskBtnClick(toDoData: ToDoData)
    }
}