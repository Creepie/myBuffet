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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ada.mybuffet.databinding.FragmentStocksOverviewBinding
import com.ada.mybuffet.repo.StockShare
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [StocksOverview.newInstance] factory method to
 * create an instance of this fragment.
 */
class StocksOverview : Fragment() {
    var _binding: FragmentStocksOverviewBinding? = null
    val binding: FragmentStocksOverviewBinding get() = _binding!!

    private val viewModel: StocksOverviewViewModel by lazy {
        ViewModelProvider(this).get(StocksOverviewViewModel::class.java)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stocksListAdapter = StocksListAdapter()

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
                var list = ArrayList<String>()
                list.addAll(viewModel.model.map.keys.toList())
                list.add("All")
                adapter = ArrayAdapter(context,android.R.layout.simple_spinner_item,list)

            }
        }

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

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        }



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
        _binding = FragmentStocksOverviewBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
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
         * @return A new instance of fragment stocksOverview.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StocksOverview().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}