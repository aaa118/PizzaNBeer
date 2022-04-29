package com.demo.pizzanbeer.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.demo.pizzanbeer.MyApplication.Companion.appComponent
import com.demo.pizzanbeer.databinding.FragmentItemListBinding
import com.demo.pizzanbeer.model.Businesses
import com.demo.pizzanbeer.repo.PizzaNBeerRepository
import com.demo.pizzanbeer.viewmodel.ListOfBusinessesViewModel
import com.demo.pizzanbeer.viewmodel.ViewModelFactory
import javax.inject.Inject

class ListFragment : Fragment() {
    @Inject
    lateinit var pizzaNBeerRepository: PizzaNBeerRepository

    private lateinit var listOfBusinessesViewModel: ListOfBusinessesViewModel
    lateinit var fragmentItemListBinding: FragmentItemListBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appComponent.inject(this)
        listOfBusinessesViewModel = ViewModelProvider(
            this,
            ViewModelFactory(pizzaNBeerRepository)
        )[ListOfBusinessesViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentItemListBinding = FragmentItemListBinding.inflate(layoutInflater, container, false)
        return fragmentItemListBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listOfBusinessesViewModel.listOfBusinessesMutableLiveData.observe(
            viewLifecycleOwner
        ) {
            if (it.isNotEmpty()) {
                Log.i(TAG, "onViewCreated: ${it.size} ")
                startRecyclerView(it)
            } else {
                Log.i(TAG, "onViewCreated: List is null")
                listOfBusinessesViewModel.loadApi()
            }
        }

    }

    private fun startRecyclerView(listOfBusinesses: List<Businesses>) {
        val recyclerView = fragmentItemListBinding.list
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = MyItemRecyclerViewAdapter(listOfBusinesses)
        recyclerView.adapter = adapter
    }

    companion object {
        private const val TAG = "ListFragment"
    }
}