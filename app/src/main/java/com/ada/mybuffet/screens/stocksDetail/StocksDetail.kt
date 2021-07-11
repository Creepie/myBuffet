package com.ada.mybuffet.screens.stocksDetail

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.ada.mybuffet.R
import com.ada.mybuffet.databinding.FragmentStocksDetailBinding
import com.ada.mybuffet.repo.Dividend
import com.ada.mybuffet.repo.StockCandle
import com.ada.mybuffet.repo.StockShare
import com.ada.mybuffet.utils.NumberFormatUtils
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * @author Mario Eberth
 * This class is the VIEW of the Stocks Detail
 */
class StocksDetail : BottomSheetDialogFragment() {

    private val viewModel: StocksDetailViewModel by lazy {
        ViewModelProvider(this).get(StocksDetailViewModel::class.java)
    }

    //get the args from the StocksOverview (stockShare)
    private val args: StocksDetailArgs by navArgs()

    private var _binding: FragmentStocksDetailBinding? = null
    val binding: FragmentStocksDetailBinding get() = _binding!!

    private lateinit var dividendHistoryChart: LineChart
    private lateinit var chartValueSelectedListener: DividendsChartSelectionListener
    private var chartValueDisplayLimit = 7


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val stockShare: StockShare = args.stockShare

        val list = if (stockShare.dividends?.data?.size ?: 0 > 7){
            stockShare.dividends?.data?.takeLast(chartValueDisplayLimit)
        } else {
            stockShare.dividends?.data
        }

        binding.stocksDetailTVShareName.text = stockShare.name
        binding.stocksDetailTVSymbol.text = stockShare.symbol
        binding.stocksDetailTvPercent.text = "( ${stockShare.getPercentDividend()} %)"


        dividendHistoryChart = binding.stocksDetailChartDividend
        chartValueSelectedListener = DividendsChartSelectionListener(binding, list as MutableList<Dividend>,stockShare)
        chartValueSelectedListener.onNothingSelected()
        dividendHistoryChart.setOnChartValueSelectedListener(chartValueSelectedListener)

        setupDividendChartSettings()
        setPortfolioTotalChartData(list)

        viewModel.loadCandleData(stockShare.symbol)

        val observer = Observer<StockCandle>{
                candle ->
            val test = candle
            //the api don't return the right values > maybe because sandbox > stop here
        }

        viewModel.candles.observe(viewLifecycleOwner,observer)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStocksDetailBinding.inflate(inflater,container,false)
        // Inflate the layout for this fragment
        return binding.root
    }

    private fun setupDividendChartSettings() {
        dividendHistoryChart = binding.stocksDetailChartDividend

        // change no data placeholder
        dividendHistoryChart.setNoDataText(getString(R.string.myShares_chart_empty_message))
        dividendHistoryChart.setNoDataTextColor(Color.WHITE)

        // remove description text
        dividendHistoryChart.description.isEnabled = false

        // enable touch gestures
        dividendHistoryChart.setTouchEnabled(true)
        dividendHistoryChart.isHighlightPerTapEnabled = true

        // disable scaling and dragging
        dividendHistoryChart.isDragEnabled = false
        dividendHistoryChart.setScaleEnabled(false)
        dividendHistoryChart.setPinchZoom(false)


        // remove grid
        dividendHistoryChart.setDrawGridBackground(false)

        // remove axis descriptions and legend
        dividendHistoryChart.xAxis.isEnabled = false
        dividendHistoryChart.axisLeft.isEnabled = false
        dividendHistoryChart.axisRight.isEnabled = false
        dividendHistoryChart.legend.isEnabled = false

        // set animation with duration
        dividendHistoryChart.animateXY(100, 100)

        // refresh the drawing
        dividendHistoryChart.invalidate()
    }

    private fun setPortfolioTotalChartData(portfolioValueList: MutableList<Dividend>) {
        // convert to ArrayList of MPAndroidChart's Entry type
        val values = ArrayList<Entry>()
        portfolioValueList.forEachIndexed { index, element ->
            values.add(Entry(index.toFloat(), element.amount.toFloat()))
        }

        var lineDataSet: LineDataSet

        if (dividendHistoryChart.data != null &&
            dividendHistoryChart.data.dataSetCount > 0
        ) {
            lineDataSet = dividendHistoryChart.data.getDataSetByIndex(0) as LineDataSet
            lineDataSet.values = values
            lineDataSet.notifyDataSetChanged()
            dividendHistoryChart.data.notifyDataChanged()
            dividendHistoryChart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            lineDataSet = LineDataSet(values, "LineDataSet")
            lineDataSet.setDrawIcons(false)

            // set line mode to cubic to round the edges
            lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
            lineDataSet.cubicIntensity = 0.2f

            // black lines and points
            lineDataSet.color = Color.WHITE
            lineDataSet.setCircleColor(Color.WHITE)

            // line thickness and point size
            lineDataSet.lineWidth = 1f
            lineDataSet.circleRadius = 3f

            // draw points as solid circles
            lineDataSet.setDrawCircleHole(false)

            // disable highlight lines
            lineDataSet.setDrawHighlightIndicators(false)

            // disable value drawing
            lineDataSet.setDrawValues(false)

            val dataSets: ArrayList<ILineDataSet> = ArrayList()
            dataSets.add(lineDataSet) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            dividendHistoryChart.data = data
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


class  DividendsChartSelectionListener(private val binding: FragmentStocksDetailBinding,
                                       private var list: MutableList<Dividend>, private var stockShare: StockShare) : OnChartValueSelectedListener {

    override fun onValueSelected(e: Entry, h: Highlight?) {
        binding.stocksDetailTvAmount.text = NumberFormatUtils.toCurrencyString(e.y.toString())
        binding.stocksDetailTvExDate.text = list[e.x.toInt()].exDate
        binding.stocksDetailTvPercent.text = "( ${stockShare.getPercentDividendOfIndex(list[e.x.toInt()].exDate)} %)"
    }

    override fun onNothingSelected() {
        if (list.isEmpty()){
            return
        } else {
            binding.stocksDetailTvAmount.text = NumberFormatUtils.toCurrencyString(list.last().amount.toString())
            binding.stocksDetailTvExDate.text = list.last().exDate
        }
    }

}