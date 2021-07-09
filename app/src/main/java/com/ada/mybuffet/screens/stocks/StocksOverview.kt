package com.ada.mybuffet.screens.stocks

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.opengl.Visibility
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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ada.mybuffet.databinding.FragmentStocksOverviewBinding
import com.ada.mybuffet.repo.StockShare
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

    private var currentSpinnerPos = 0

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

            stocksBtnRefresh.setOnClickListener {
                loadStocks(currentSpinnerPos)
                stocksBtnRefresh.visibility = View.INVISIBLE
                stocksPBLoadingBar.visibility = View.VISIBLE
            }

            //set the spinner on Item Selected Listener
            stocksSPChooseStock.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    currentSpinnerPos = position
                    loadStocks(position)
                    stocksBtnRefresh.visibility = View.INVISIBLE
                    stocksPBLoadingBar.visibility = View.VISIBLE
                }
                override fun onNothingSelected(parent: AdapterView<*>?) {}
            }
        }


        //observe the data from the viewModel > if new data comes update the recycler
        val observer = Observer<MutableList<StockShare>> {
                stockList -> println(stockList)
            stockList.sortByDescending { it.getPercentDividend()}
            stocksListAdapter.submitList(stockList)
            binding.stocksPBLoadingBar.visibility = View.INVISIBLE
            binding.stocksBtnRefresh.visibility = View.VISIBLE
        }

        viewModel.stocks.observe(viewLifecycleOwner,observer)
    }

    private fun loadStocks(position: Int){
        if (viewModel.model.indexList.size == position){
            viewModel.loadAllStocks()
        } else {
            viewModel.chanceStockData(position)
        }
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

    override fun onCardClicked(stockShare: StockShare) {
        //go to detail screen of the stock share
        val action = StocksOverviewDirections.actionStocksToStocksDetail(stockShare)
        findNavController().navigate(action)
    }
}