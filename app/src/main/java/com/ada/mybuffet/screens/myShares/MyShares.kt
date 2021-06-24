package com.ada.mybuffet.screens.myShares

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ada.mybuffet.R
import com.ada.mybuffet.databinding.FragmentMysharesBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MyShares.newInstance] factory method to
 * create an instance of this fragment.
 */
class MyShares : Fragment() {
    // set the _binding variable initially to null and
    // also when the view is destroyed again it has to be set to null
    private var _binding: FragmentMysharesBinding? = null

    // extract the non null value of the _binding with kotlin backing
    private val binding get() = _binding!!  // only valid between onCreateView & onDestroyView

    private val viewModel: MySharesViewModel by lazy {
        ViewModelProvider(this).get(MySharesViewModel::class.java)
    }

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMysharesBinding.inflate(inflater, container, false)

        val stocksListDummy = arrayListOf<ShareItem>()
        stocksListDummy.add(ShareItem("APPL", "Apple Inc."))
        stocksListDummy.add(ShareItem("TSLA", "Tesla Inc."))

        val stocksRecyclerView = binding.mySharesRecyclerViewStocks
        stocksRecyclerView.adapter = SharesListAdapter(arrayListOf<ShareItem>())    // init empty
        stocksRecyclerView.layoutManager = LinearLayoutManager(activity)

        viewModel.shareItems.observe(viewLifecycleOwner, Observer { shareItems ->
            val adapter = SharesListAdapter(shareItems)
            stocksRecyclerView.adapter = adapter
        })



        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //var btn_detail = view.findViewById<Button>(R.id.myShares_bT_detail)
        var btn_newShare = view.findViewById<FloatingActionButton>(R.id.myShares_fab_add)


        //btn_detail.setOnClickListener {
        //    findNavController().navigate(R.id.action_myShares_to_shareDetail2)
        //}

        btn_newShare.setOnClickListener {
            findNavController().navigate(R.id.action_myShares_to_newShare)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ShareDetail.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MyShares().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}