package com.example.realweatherio

import android.content.Context
import android.media.RouteListingPreference.Item
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import org.w3c.dom.Text

class WeatherAdapter (private var list: MutableList<ForecastData>,private var context:Context) :RecyclerView.Adapter<WeatherAdapter.MyViewHolder>(){



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflator=LayoutInflater.from(parent.context).inflate(R.layout.weatherholder,parent,false)

        return MyViewHolder(inflator)


    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data=list[position]
        holder.temp.text="${data.main.temp}"
        holder.date.text=data.dt_txt

        //setting the image after fetching it from the open weather
        val iconUrl = "https://openweathermap.org/img/wn/${data.weather[0].icon}.png"
        Picasso.get().load(iconUrl).into(holder.image)





    }



    class MyViewHolder(item: View):RecyclerView.ViewHolder(item) {
        var image:ImageView=item.findViewById(R.id.imgholder)
        var date:TextView=item.findViewById(R.id.dateholder)
        var time:TextView=item.findViewById(R.id.timeholder)
        var temp:TextView=item.findViewById(R.id.tempholder)

    }


}