package com.ada.mybuffet.screens.stocksDetail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.ada.mybuffet.R
import com.ada.mybuffet.databinding.FragmentStocksDetailBinding
import com.ada.mybuffet.repo.StockShare

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * This class is the VIEW of the Stocks Detail
 */
class StocksDetail : Fragment() {

    private val args: StocksDetailArgs by navArgs()

    private var _binding: FragmentStocksDetailBinding? = null
    val binding: FragmentStocksDetailBinding get() = _binding!!


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stockShare: StockShare = args.stockShare

        binding.textView5.text = stockShare.name
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStocksDetailBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }


}