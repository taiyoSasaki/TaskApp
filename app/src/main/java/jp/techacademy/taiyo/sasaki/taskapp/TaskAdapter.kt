package jp.techacademy.taiyo.sasaki.taskapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class TaskAdapter(context: Context): BaseAdapter() {
    private val mLayInflater: LayoutInflater
    var mTaskList = mutableListOf<Task>()

    init {
        this.mLayInflater = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return mTaskList.size
    }

    override fun getItem(position: Int): Any {
        return mTaskList[position]
    }

    override fun getItemId(position: Int): Long {
        return mTaskList[position].id.toLong()
    }

    fun getItemCategry(position: Int): String{
        return  mTaskList[position].category
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = convertView ?: mLayInflater.inflate(android.R.layout.simple_list_item_2, null)

        val textView1 = view.findViewById<TextView>(android.R.id.text1)
        val textView2 = view.findViewById<TextView>(android.R.id.text2)

        //後でTaskクラスから情報を取得するように変更する
        textView1.text = mTaskList[position].title

        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.JAPANESE)
        val date = mTaskList[position].date
        textView2.text = simpleDateFormat.format(date)

        return view
    }
}