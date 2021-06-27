package com.ada.mybuffet.screens.myShares

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RectShape
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.ada.mybuffet.R
import com.ada.mybuffet.databinding.FragmentMysharesBinding
import com.ada.mybuffet.screens.myShares.model.ShareItem
import com.ada.mybuffet.screens.myShares.repo.MySharesRepository
import com.ada.mybuffet.screens.myShares.repo.MySharesDataProvider
import com.ada.mybuffet.screens.myShares.viewModel.MySharesViewModel
import com.ada.mybuffet.screens.myShares.viewModel.MySharesViewModelFactory
import com.ada.mybuffet.utils.NumberFormatUtils
import com.ada.mybuffet.utils.Resource
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.math.BigDecimal

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
        ViewModelProvider(this,
            MySharesViewModelFactory(
                MySharesDataProvider(
                    MySharesRepository()
                )
            )
        ).get(
            MySharesViewModel::class.java
        )
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

        // recycler view setup
        val stocksRecyclerView = binding.mySharesRecyclerViewStocks
        stocksRecyclerView.adapter = SharesListAdapter(arrayListOf<ShareItem>())    // init empty
        stocksRecyclerView.layoutManager = LinearLayoutManager(activity)

        // add vertical inter-item spacing of 8dp to recyclerview items
        stocksRecyclerView.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL).also { deco ->
            with(ShapeDrawable(RectShape())) {
                intrinsicHeight = (resources.displayMetrics.density * 8).toInt()
                alpha = 0
                deco.setDrawable(this)
            }
        })

        viewModel.fetchShareItemList.observe(viewLifecycleOwner, Observer { observedResource ->

            when (observedResource) {
                is Resource.Loading -> {
                    // could use this to indicate data loading in the UI
                    // for this you need to use emit(Resource.Loading()) before the try catch
                    // in the view model
                    // showProgress()
                }

                is Resource.Success -> {
                    // hideProgress()

                    val sharesItemsList: MutableList<ShareItem> = observedResource.data as MutableList<ShareItem>

                    if (sharesItemsList.size == 0) {
                        // show empty recycler view placeholder message
                        binding.mySharesTvRecyclerViewEmptyMessage.visibility = View.VISIBLE

                        // hide recycler view
                        stocksRecyclerView.visibility = View.GONE
                    } else {
                        // display recycler view
                        stocksRecyclerView.visibility = View.VISIBLE

                        // hide placeholder message
                        binding.mySharesTvRecyclerViewEmptyMessage.visibility = View.GONE

                        // connect recyclerview to viewModel data
                        val adapter = SharesListAdapter(sharesItemsList)
                        stocksRecyclerView.adapter = adapter
                    }


                }

                is Resource.Failure -> {
                    // hideProgress()
                    Toast.makeText(context, "Data could not be loaded", Toast.LENGTH_SHORT).show()
                }
            }
        })



        viewModel.fetchProfitLossOverviewData.observe(viewLifecycleOwner, Observer { observedResource ->
            when (observedResource) {
                is Resource.Loading -> {
                    // could use this to indicate data loading in the UI
                    // for this you need to use emit(Resource.Loading()) before the try catch
                    // in the view model
                    // showProgress()
                }

                is Resource.Success -> {
                    // hideProgress()

                    val profitLossOverviewData: HashMap<String, BigDecimal> = observedResource.data as HashMap<String, BigDecimal>
                    val totalProfit = profitLossOverviewData["totalProfit"]
                    binding.mySharesTvProfitCardTotalSum.text = totalProfit?.let {
                        NumberFormatUtils.toCurrencyString(it)
                    }

                    val exchangeProfitLoss = profitLossOverviewData["exchangeProfitLoss"]
                    if (exchangeProfitLoss != null) {
                        binding.mySharesTvExchangeProfitLossValue.text = NumberFormatUtils.toCurrencyString(exchangeProfitLoss)

                        if (exchangeProfitLoss >= BigDecimal.ZERO) {
                            val textColor = ContextCompat.getColor(binding.root.context, R.color.sharePrice_profit)
                            binding.mySharesTvExchangeProfitLossValue.setTextColor(textColor)
                            binding.mySharesImgExchangeProfitLossTrending.setImageResource(R.drawable.ic_trending_up)
                        } else {
                            val textColor = ContextCompat.getColor(binding.root.context, R.color.sharePrice_loss)
                            binding.mySharesTvExchangeProfitLossValue.setTextColor(textColor)
                            binding.mySharesImgExchangeProfitLossTrending.setImageResource(R.drawable.ic_trending_down)
                        }
                    }


                    val dividendProfit = profitLossOverviewData["dividendProfit"]
                    binding.mySharesTvDividendProfitValue.text = dividendProfit?.let {
                        NumberFormatUtils.toCurrencyString(it)
                    }

                    val totalFees = profitLossOverviewData["fees"]
                    binding.mySharesTvFeesValue.text = totalFees?.let {
                        NumberFormatUtils.toCurrencyString(it)
                    }

                    val profitPercentage = profitLossOverviewData["profitPercentage"]

                    if (profitPercentage != null) {
                        binding.mySharesTvProfitCardTotalPercentage.text = NumberFormatUtils.toPercentString(profitPercentage)

                        if (profitPercentage >= BigDecimal.ZERO) {
                            val textColor = ContextCompat.getColor(binding.root.context, R.color.sharePrice_profit)
                            binding.mySharesTvProfitCardTotalSum.setTextColor(textColor)
                            binding.mySharesTvProfitCardTotalPercentage.setTextColor(textColor)
                            binding.mySharesImgProfitCardTotalTrending.setImageResource(R.drawable.ic_trending_up)
                        } else {
                            val textColor = ContextCompat.getColor(binding.root.context, R.color.sharePrice_loss)
                            binding.mySharesTvProfitCardTotalSum.setTextColor(textColor)
                            binding.mySharesTvProfitCardTotalPercentage.setTextColor(textColor)
                            binding.mySharesImgProfitCardTotalTrending.setImageResource(R.drawable.ic_trending_down)
                        }

                    }
                }

                is Resource.Failure -> {
                    // hideProgress()
                    Toast.makeText(context, "Data could not be loaded", Toast.LENGTH_SHORT).show()
                }
            }
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //var btn_detail = view.findViewById<Button>(R.id.myShares_bT_detail)
        var btn_newShare = view.findViewById<FloatingActionButton>(R.id.myShares_fab_add)


        //btn_detail.setOnClickListener {
        //    findNavController().navigate(R.id.action_myShares_to_shareDetailScreen)
        //}

        btn_newShare.setOnClickListener {
            findNavController().navigate(R.id.action_myShares_to_newShare)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    /*
    fun showProgress(){
        progressBar.visibility = View.VISIBLE
    }

    fun hideProgress(){
        progressBar.visibility = View.GONE
    }
     */

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