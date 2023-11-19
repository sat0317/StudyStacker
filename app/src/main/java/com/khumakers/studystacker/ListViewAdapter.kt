package com.khumakers.studystacker

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView


class ListviewAdapter() : RecyclerView.Adapter<ListviewAdapter.ViewHolder>() {
    companion object{
        @JvmStatic var listViewItemList = mutableListOf<ListViewItem>();
    }
    private var aimTime = 0
    private var sumTime = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_main,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = listViewItemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listViewItemList[position])
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView!!) {
        private val context = itemView.context
        private val item_text: TextView = itemView.findViewById(R.id.item_text)
        private val item_time: TextView = itemView.findViewById(R.id.item_time)

        fun bind(item: ListViewItem) {
            item_text.text = item.subjectName
            item_time.text = item.timeClock?.toTimestamp()

            itemView.setOnClickListener {
                val intent = Intent(context, TimerActivity::class.java)
                intent.putExtra("subjectName", item.subjectName)
                intent.putExtra("timeClock", item.timeClock)
                intent.putExtra("innerCounter", item.innerCounter)
                intent.run { context.startActivity(this) }

            }
        }
    }
    fun addItem(a: String, b: Int) {
        var item = ListViewItem()

        item.subjectName = a;
        item.timeClock = b;
        item.innerCounter = listViewItemList.size

        listViewItemList.add(item)
    }
    fun changeItem(index: Int, data: Int){
        listViewItemList[index].timeClock=data;
    }
    fun getSumTime(): Int{
        sumTime = 0
        for(i: Int in 0..<listViewItemList.size) {
            sumTime += listViewItemList[i].timeClock!!
        }
        return sumTime;
    }

    fun getAimTime(): Int{
        return aimTime;
    }

    fun setAimTime(x: Int){
        aimTime = x
    }

    fun getListSize(): Int{
        return listViewItemList.size;
    }

    fun getListItem(x: Int): ListViewItem{
        return listViewItemList[x]
    }

    fun clearList(){
        listViewItemList = ArrayList<ListViewItem>()
    }
}
