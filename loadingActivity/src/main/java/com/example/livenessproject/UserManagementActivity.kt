package com.example.livenessproject

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.StrictMode
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import com.baoyz.swipemenulistview.SwipeMenuCreator
import com.baoyz.swipemenulistview.SwipeMenuItem
import com.baoyz.swipemenulistview.SwipeMenuListView
import com.example.livenessproject.util.HttpHelper
import com.megvii.livenessproject.R
import java.util.ArrayList
import kotlinx.android.synthetic.main.activity_user_management.*
import kotlinx.android.synthetic.main.activity_user_management_list_item.view.*
import org.json.JSONArray
import org.json.JSONObject

class UserManagementActivity : AppCompatActivity() {

    private val mArrayList = ArrayList<ArrayList<String>>()
    private var mListDataAdapter: ListDataAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_management)

        // Allow Network Connection to be made on main thread
        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        initActionBar()
        initSwipeListView()
    }

    private fun initActionBar() {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        actionBar?.title = getString(R.string.user_management)
    }

    private fun initSwipeListView() {

        swipe_list_view.setSwipeDirection(SwipeMenuListView.DIRECTION_LEFT)

        val result = HttpHelper.listAllUsers()
        val jsonArray = JSONArray(result)
        for (i in 0..(jsonArray.length() - 1)) {
            val user = jsonArray.getJSONObject(i)
            val delFlag = user.get("delFlag") as Boolean
            val displayName = user.get("displayName") as String
            val id = user.get("_id") as String

            if(!delFlag) {
                val innerList: ArrayList<String> = arrayListOf(displayName, id)
                mArrayList.add(innerList)
            }
        }

        mListDataAdapter = ListDataAdapter()
        swipe_list_view.adapter = mListDataAdapter

        val creator = SwipeMenuCreator { menu ->
//            editItem.background = ColorDrawable(Color.rgb(0x30, 0xB1,0xF5)) // This is part of edit button that is not used anymore

            // add "delete" button to swipe menu
            val deleteItem = SwipeMenuItem(applicationContext)
            deleteItem.background = ColorDrawable(Color.rgb(0xF9,0x3F, 0x25))
            deleteItem.width = dpTopx(90)
            deleteItem.setIcon(R.drawable.ic_rubbish_bin)
            menu.addMenuItem(deleteItem)
        }

        swipe_list_view.setMenuCreator(creator)

        swipe_list_view.setOnMenuItemClickListener { position, menu, index ->
            when (index) {
                0 -> {
                    mArrayList.removeAt(position)
                    mListDataAdapter!!.notifyDataSetChanged()
                    toast("Item deleted")
                }
            }
            true
        }

        swipe_list_view.setOnMenuStateChangeListener(object : SwipeMenuListView.OnMenuStateChangeListener {
            override fun onMenuOpen(position: Int) {}
            override fun onMenuClose(position: Int) {}
        })

        swipe_list_view.setOnSwipeListener(object : SwipeMenuListView.OnSwipeListener {
            override fun onSwipeStart(position: Int) {}
            override fun onSwipeEnd(position: Int) {}
        })

        swipe_list_view.setOnItemClickListener { parent, view, position, id ->
            toast(view.user_management_id.text.toString())
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.user_management_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId

        if (id == R.id.action_add) {
            //TODO add item to list from here
            //mArrayList.add("List item --> " + mArrayList.size)
            //mListDataAdapter!!.notifyDataSetChanged()
        }

        return super.onOptionsItemSelected(item)
    }

    internal inner class ListDataAdapter : BaseAdapter() {

        private var holder: ViewHolder? = null

        override fun getCount(): Int {
            return mArrayList.size
        }

        override fun getItem(i: Int): Any? {
            return null
        }

        override fun getItemId(i: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                holder = ViewHolder()
                convertView = layoutInflater.inflate(R.layout.activity_user_management_list_item, null)
                holder!!.mTextView = convertView!!.findViewById(R.id.user_management_text_view) as TextView
                holder!!.mId = convertView!!.findViewById(R.id.user_management_id) as TextView
                convertView.tag = holder
            } else {
                holder = convertView.tag as ViewHolder
            }
            holder!!.mTextView!!.text = mArrayList[position][0]
            holder!!.mId!!.text = mArrayList[position][1]
            return convertView
        }

        internal inner class ViewHolder {
            var mTextView: TextView? = null
            var mId : TextView? = null
        }
    }

    private fun dpTopx(dp: Int): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp.toFloat(), resources.displayMetrics).toInt()
    }

    // Extension function to show toast message easily
    private fun Context.toast(message:String){
        Toast.makeText(applicationContext,message, Toast.LENGTH_SHORT).show()
    }
}
