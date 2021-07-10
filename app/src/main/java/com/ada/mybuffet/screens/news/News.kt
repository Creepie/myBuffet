package com.ada.mybuffet.screens.news

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
import com.ada.mybuffet.databinding.FragmentNewsBinding
import com.ada.mybuffet.features.NewsRecyclerAdapter
import com.ada.mybuffet.repo.StockShare
import com.ada.mybuffet.repo.SymbolPressResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [News.newInstance] factory method to
 * create an instance of this fragment.
 */
class News : Fragment() {
    var _binding: FragmentNewsBinding? = null
    val binding: FragmentNewsBinding get() = _binding!!

    private val viewModel: NewsViewModel by lazy {
        ViewModelProvider(this).get(NewsViewModel::class.java)
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

        // refreshbutton
        binding.newsBtnRefresh.setOnClickListener {
            viewModel.loadData()
        }

       // binding.newsRecyclerViewStocks.layoutManager = LinearLayoutManager(activity)


        val newsListAdapter = NewsListAdapter()

        binding.apply {
            //recycler

            newsRecyclerViewStocks.apply {
                adapter = newsListAdapter
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
            newsSPChooseStock.apply {
                val list = ArrayList<String>()
                list.addAll(viewModel.model.map.keys.toList())
                list.add("All")
                adapter = ArrayAdapter(context,android.R.layout.simple_spinner_item,list)
            }

        }


        binding.newsSPChooseStock.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                if (viewModel.model.indexList.size == position){
                    CoroutineScope(Dispatchers.IO).launch {
                        viewModel.loadData()
                    }
                } else {
                    viewModel.chanceStockData(position)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }


        val observer = Observer<MutableList<SymbolPressResponse>> {
            newNews -> println(newNews)
            newNews.sortBy { it.symbol }
            newsListAdapter.submitList(newNews)


            //  val adapter = NewsRecyclerAdapter(newNews)
            // binding.newsRecycler.adapter = adapter
          //  binding.newsRecyclerViewStocks.adapter = adapter

        }


        viewModel.news.observe(viewLifecycleOwner,observer)



        /**
         * -----------------------------------------------------------------------------------
         */


        /*
       // binding.newsBtnRefresh.
        binding.newsBTLoadNews

        binding.newsBTLoadNews.setOnClickListener {
            viewModel.loadData()
        }

        binding.newsRecycler.layoutManager = LinearLayoutManager(activity)



        val observer = Observer<MutableList<SymbolPressResponse>> {
            newNews ->
            val adapter = NewsRecyclerAdapter(newNews)
            binding.newsRecycler.adapter = adapter
        }

        viewModel.news.observe(viewLifecycleOwner,observer)

         */

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNewsBinding.inflate(inflater,container,false)
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
         * @return A new instance of fragment News.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            News().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}