package com.example.testquestion

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader

@Suppress("UNUSED_PARAMETER")


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val actionbar = supportActionBar
        actionbar!!.title = "List Post"

        listview.onItemClickListener = object : OnItemClickListener {

            override fun onItemClick(parent: AdapterView<*>, view: View,
                                     position: Int, id: Long) {


                val intent = Intent(this@MainActivity,DetailActivity::class.java)
                intent.putExtra("name",listViewModelArrayList[position].title)
                intent.putExtra("body",listViewModelArrayList[position].content)
                startActivity(intent)

            }
        }


        Log.d("LOG", verifyAvailableNetwork(this@MainActivity).toString())

        swipeRefresh.isRefreshing = true



        requestPsot()


        swipeRefresh.setOnRefreshListener {

            requestPsot()

        }


    }



    fun requestPsot(){

        if (verifyAvailableNetwork(this@MainActivity) == true){


            listview.isEnabled = true

            GetJsonWithOkHttpClient(listview).execute()


        }else{

            swipeRefresh.isRefreshing = false


            var array = arrayOf("Не удается установить соединение с сетью\n" +
                    "Проверьте ваше соединение\n" +
                    "Для перезагрузки списка постов потяните экран вниз")
            var adapter = ArrayAdapter(this@MainActivity, R.layout.listview_item, array)

            listview.setAdapter(adapter)

            listview.isEnabled = false


        }

    }


    fun verifyAvailableNetwork(activity:AppCompatActivity):Boolean{
        val connectivityManager=activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo
        return  networkInfo!=null && networkInfo.isConnected
    }



    var listViewModelArrayList = ArrayList<ListViewModel>()


    class  ListViewModel {
        var id: String? = null
        var title: String? = null
        var content: String? = null

        constructor(id: String, title: String, content: String) {
            this.id = id
            this.title = title
            this.content = content
        }
    }


    inner class GetJsonWithOkHttpClient(listView: ListView) : AsyncTask<Unit, Unit, String>() {



        override fun doInBackground(vararg params: Unit?): String? {
            val networkClient = NetworkClient()
            val stream = BufferedInputStream(
                networkClient.get("https://jsonplaceholder.typicode.com/posts/"))
            return readStream(stream)
        }

        override fun onPostExecute(result: String?) {
            super.onPostExecute(result)

            val arrayList = ArrayList<String>()


            swipeRefresh.isRefreshing = false


            var jsonArray = JSONArray(result)
            for (jsonIndex in 0..(jsonArray.length() - 1)) {
                Log.d("JSON", jsonArray.getJSONObject(jsonIndex).getString("title"))

                listViewModelArrayList.add(ListViewModel(jsonArray.getJSONObject(jsonIndex).getString("id"), jsonArray.getJSONObject(jsonIndex).getString("title"), jsonArray.getJSONObject(jsonIndex).getString("body")))

               arrayList.add("ID: " + jsonArray.getJSONObject(jsonIndex).getString("id") +"\n"+ "Title: "+ jsonArray.getJSONObject(jsonIndex).getString("title")  )
            }


            var adapter = ArrayAdapter(this@MainActivity, R.layout.listview_item,arrayList)

            listview.setAdapter(adapter)
        }



        fun readStream(inputStream: BufferedInputStream): String {
            val bufferedReader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            bufferedReader.forEachLine { stringBuilder.append(it) }
            return stringBuilder.toString()
        }
    }







    class NetworkClient {

        fun get(url: String): InputStream {
            val request = Request.Builder().url(url).build()
            val response = OkHttpClient().newCall(request).execute()
            val body = response.body()
            return body!!.byteStream()
        }
    }



}
