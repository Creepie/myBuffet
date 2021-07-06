package com.ada.mybuffet.screens.stocks

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ada.mybuffet.databinding.FragmentStocksOverviewBinding
import com.ada.mybuffet.repo.StockShare
import com.ada.mybuffet.screens.myShares.model.ShareItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * This class is the VIEW for the Stocks
 */
class StocksOverview : Fragment(), StocksRecyclerViewClickListener {

    //global binding variables
    private var _binding: FragmentStocksOverviewBinding? = null
    val binding: FragmentStocksOverviewBinding get() = _binding!!

    //init the viewModel
    private val viewModel: StocksOverviewViewModel by lazy {
        ViewModelProvider(this).get(StocksOverviewViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //add recycler adapter
        val stocksListAdapter = StocksListAdapter(this)

        binding.apply {
            //recycler
            stocksRecyclerViewStocks.apply {
                adapter = stocksListAdapter
                layoutManager = LinearLayoutManager(requireContext())
                addItemDecoration(
                    DividerItemDecoration(
                        context,
                        DividerItemDecoration.VERTICAL
                    ).also { deco ->
                        with(ShapeDrawable(RectShape())) {
                            intrinsicHeight = (resources.displayMetrics.density * 8).toInt()
                            alpha = 0
                            deco.setDrawable(this)
                        }
                    })
            }
            //spinner
            stocksSPChooseStock.apply {
                val list = ArrayList<String>()
                list.addAll(viewModel.model.map.keys.toList())
                list.add("All")
                adapter = ArrayAdapter(context,android.R.layout.simple_spinner_item,list)

            }
        }

        //set the spinner on Item Selected Listener
        binding.stocksSPChooseStock.onItemSelectedListener = object :
        AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (viewModel.model.indexList.size == position){
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.loadAllStocks()
                    }
                } else {
                    viewModel.chanceStockData(position)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        //observe the data from the viewModel > if new data comes update the recycler
        val observer = Observer<MutableList<StockShare>> {
                stockList -> println(stockList)
            stockList.sortByDescending { it.getPercentDividend()}
            stocksListAdapter.submitList(stockList)
        }

        viewModel.stocks.observe(viewLifecycleOwner,observer)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //init the binding
        _binding = FragmentStocksOverviewBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCardClicked(shareItem: StockShare) {
        Toast.makeText(binding.root.context,"${shareItem.symbol} clicked",Toast.LENGTH_SHORT).show()
    }
}