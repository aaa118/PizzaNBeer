package com.demo.pizzanbeer.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.demo.pizzanbeer.MyApplication.Companion.appComponent
import com.demo.pizzanbeer.databinding.FragmentItemListBinding
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
        val adapter = MyItemRecyclerViewAdapter()
        fragmentItemListBinding.list.adapter = adapter
        subscribeToUi(adapter)
        return fragmentItemListBinding.root
    }

    private fun subscribeToUi(adapter: MyItemRecyclerViewAdapter) {
        listOfBusinessesViewModel.listOfBusinessesMutableLiveData.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                Log.i(TAG, "subscribeToUi: ${it.size} ")
                adapter.submitList(it)
            } else {
                Log.i(TAG, "subscribeToUi: List is null")
                listOfBusinessesViewModel.loadApi()
            }
        }
    }

    companion object {
        private const val TAG = "ListFragment"
    }
}