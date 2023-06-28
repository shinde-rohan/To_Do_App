package com.example.todoapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todoapp.databinding.FragmentHomeBinding
import com.example.todoapp.utils.ToDoAdapter
import com.example.todoapp.utils.ToDoData
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class HomeFragment : Fragment(), PopUpFragment.DialogNextBtnClickListener,
    ToDoAdapter.ToDoAdapterClickInterface {
    private lateinit var auth: FirebaseAuth
    private lateinit var navController: NavController
    private lateinit var databaseReference: DatabaseReference
    private lateinit var binding: FragmentHomeBinding
    private  var popUpFragment : PopUpFragment?=null
    private lateinit var adapter: ToDoAdapter
    private lateinit var mList :MutableList<ToDoData>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater,container,false)
       return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init(view)
        getDataFromFireBase()
        registerEvents()
    }
    private fun init(view:View){
        auth = FirebaseAuth.getInstance()
        navController = Navigation.findNavController(view)
        databaseReference = FirebaseDatabase.getInstance().reference
            .child("Tasks").child(auth.currentUser?.uid.toString())

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        mList = mutableListOf()
        adapter = ToDoAdapter(mList)
        adapter.setListener(this)
        binding.recyclerView.adapter = adapter

    }
    private fun registerEvents(){
        binding.addBtn.setOnClickListener {
            if(popUpFragment!=null){
                childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
            }
            popUpFragment  = PopUpFragment()
            popUpFragment!!.setListener(this)
            popUpFragment!!.show(childFragmentManager,PopUpFragment.TAG)

        }
    }
    private fun getDataFromFireBase(){
        databaseReference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                mList.clear()
                for(taskSnapShot in snapshot.children){
                    val todoTask = taskSnapShot.key?.let {
                        ToDoData(it,taskSnapShot.value.toString())
                    }
                    if(todoTask != null){
                        mList.add(todoTask)
                    }
                }
                adapter.notifyDataSetChanged()
            }


            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context,error.message,Toast.LENGTH_SHORT).show()
            }

        })
    }
    override fun onSaveTask(todo: String, todoEt: TextInputEditText) {
        databaseReference.push().setValue(todo).addOnCompleteListener {
            if(it.isSuccessful){
                Toast.makeText(context,"To do save successfully",Toast.LENGTH_SHORT).show()
                todoEt.text = null
            }else{
                Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
            }
            popUpFragment!!.dismiss()
        }
    }

    override fun onUpdateTask(toDoData: ToDoData, todoEt: TextInputEditText) {
       val  map = HashMap<String,Any>()
        map[toDoData.taskId] = toDoData.task
        databaseReference.updateChildren(map).addOnCompleteListener {
            if (it.isSuccessful){
                Toast.makeText(context,"Updated Successfully",Toast.LENGTH_SHORT).show()

            }else{
                Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
            }
            todoEt.text= null
            popUpFragment!!.dismiss()
        }
    }

    override fun onDeleteTaskBtnClick(toDoData: ToDoData) {
       databaseReference.child(toDoData.taskId).removeValue().addOnCompleteListener {
           if (it.isSuccessful){
               Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_SHORT).show()
           }else{
               Toast.makeText(context,it.exception?.message,Toast.LENGTH_SHORT).show()
           }
       }
    }

    override fun onEditTaskBtnClick(toDoData: ToDoData) {
        if(popUpFragment!=null){
            childFragmentManager.beginTransaction().remove(popUpFragment!!).commit()
        }
        popUpFragment = PopUpFragment.newInstance(toDoData.taskId,toDoData.task)
        popUpFragment!!.setListener(this)
        popUpFragment!!.show(childFragmentManager,PopUpFragment.TAG)
    }
}