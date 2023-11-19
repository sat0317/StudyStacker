package com.khumakers.studystacker.ui.home

import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.khumakers.studystacker.ListviewAdapter
import com.khumakers.studystacker.R
import com.khumakers.studystacker.databinding.FragmentHomeBinding
import com.khumakers.studystacker.toTimestamp


class HomeFragment : Fragment(){
    lateinit var adapter: ListviewAdapter
    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
                ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var listview: RecyclerView = binding.root.findViewById(R.id.subjectTable)
        var timeallview: TextView = binding.root.findViewById(R.id.time_all)
        var aimtimeview: TextView = binding.root.findViewById(R.id.aim_time_head)

        adapter = ListviewAdapter()
        listview.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        listview.adapter = adapter
        val dividerDecoration: DividerItemDecoration =
            DividerItemDecoration(listview.getContext(), LinearLayoutManager(activity).orientation)
        listview.addItemDecoration(dividerDecoration)

        timeallview.text = adapter.getSumTime().toTimestamp();
        aimtimeview.text = "일일 목표: "+(adapter.getAimTime()/60).toString()+"시간 "+(adapter.getAimTime()%60).toString()+"분";
        listview.setHasFixedSize(false)

        return root
    }

    inner class VerticalSpaceItemDecoration(private val verticalSpaceHeight: Int) :
        RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.bottom = verticalSpaceHeight
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun refreshAdapter() {

    }

}