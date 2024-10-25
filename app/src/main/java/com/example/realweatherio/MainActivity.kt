package com.example.realweatherio


import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.realweatherio.databinding.ActivityMainBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import java.util.Locale


/*
   API KEY-5 DAY/3 HOUR PER DAY
  d6d8351b4f48d0b57ce3386f6e5f777d
 */



class MainActivity : AppCompatActivity() {

    private lateinit var pendingIntent: PendingIntent
    private val channel_Id = "122"

    private val NotificationId = 1020

    private val MIN_TIME: Long = 10
    private val MIN_DISTANCE: Float = 1000f

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }


    val Location_PERMISSION_CODE = 105
    val Notification_Permission_CODE = 100
    private var Cityname: String = ""

    private lateinit var sheetBinding:BottomSheetDialog
    private lateinit var dialog: BottomSheetDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        //GETTING ACCESS FOR LOCATION
        getLocation()

        //Notification permission method
        getNotificationPermission()

        createNotifcationChannel()

        sendNotificationsDaily()

        //Creating pending intent and alarm manager








        // ------------------------------------------------------------------------------------


        // QUERY FOR SEARCHING THE LOCATIONS

        val searchview = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchview)

        searchview.setOnQueryTextListener(
            object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    if (query != null) {
                        fetchData(query)
                    }
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            }

        )


    }



    private fun getForecast(cityname:String) {
        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/").build()
            .create(WeatherData::class.java)

        val response = retrofit.getForecastWeather(cityname, "d6d8351b4f48d0b57ce3386f6e5f777d", "metric")
        val city = findViewById<TextView>(R.id.textViewCityname)
        city.text = cityname

    }


    private fun createNotifcationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channel_Id,
                "Daily_Notification",

                NotificationManager.IMPORTANCE_HIGH
            )
            val manger = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manger.createNotificationChannel(channel)
        }


    }


    private fun sendNotificationsDaily() {
        val intent = Intent(this, WeatherNotificationManager::class.java)
        //creating the pending intent

        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 8)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1)
            }

        }


        //creating the alarm manager
       val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )


    }


    @SuppressLint("SuspiciousIndentation")
    private fun getCity(latitude: Double, longitude: Double): String {

        var CityName: String = "Not Found"
        var geocode = Geocoder(this, Locale.getDefault())

        try {

            val address: MutableList<android.location.Address>? =
                geocode.getFromLocation(latitude, longitude, 10)
            if (address != null) {
                for (add in address) {
                    if (add != null) {
                        val city: String = add.locality
                        if (city != null && !city.equals("")) {
                            CityName = city
                        } else {
                            Toast.makeText(this, "City Not Found", Toast.LENGTH_SHORT).show()
                        }

                    }
                }
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }



        return CityName
    }


    public fun fetchData(cityname: String) {
        //using retrofit to male the api data readable

        val retrofit = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/").build()
            .create(WeatherData::class.java)

        val response =
            retrofit.getWeatherData(cityname, "d6d8351b4f48d0b57ce3386f6e5f777d", "metric")
        val city = findViewById<TextView>(R.id.textViewCityname)
        city.text = cityname


        response.enqueue(object : Callback<WeatherApp> {
            override fun onResponse(p0: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()


                if (response.isSuccessful) {
                    //SETTING VALUES
                    val temperature = responseBody?.main?.temp
                    val conditions = responseBody?.weather?.firstOrNull()?.main ?: "unknown"
                    val windSpeed = responseBody?.wind?.speed
                    val humid = responseBody?.main?.humidity
                    val sea = responseBody?.main?.sea_level
                    val feelsLike = responseBody?.main?.feels_like

                    //FINDING IDS
                    val temptext = findViewById<TextView>(R.id.temperaturetxt)
                    val condition = findViewById<TextView>(R.id.condition)
                    val humidity = findViewById<TextView>(R.id.humiditylevel)
                    val sealevel = findViewById<TextView>(R.id.sealevel)
                    val wind = findViewById<TextView>(R.id.windspeed)
                    val feels = findViewById<TextView>(R.id.FeelsLiketextView)
                    val message=binding.temperaturetxt.text
                    val intentTemp = Intent(this@MainActivity, WeatherNotificationManager::class.java)
                    intentTemp.putExtra("report", message)
                    //GIVING VALUES TO THE UI
                    temptext.text = "${temperature}°"
                    condition.text = "${conditions}"
                    humidity.text = "${humid}%"
                    sealevel.text = "${sea} mah"
                    wind.text = "${windSpeed}m/s"
                    feels.text = "${feelsLike}°"

                    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                    changeAnimation(conditions, hour)


                }

            }


            override fun onFailure(p0: Call<WeatherApp>, p1: Throwable) {
                TODO("Not yet implemented")
            }


        })

    }


    private fun changeDayNight() {

        binding.FeelsLiketextView.setTextColor(Color.parseColor("#FFFFFFFF"))
        binding.condition.setTextColor(Color.parseColor("#FFFFFFFF"))
        binding.textView2.setTextColor(Color.parseColor("#FFFFFFFF"))
        binding.humidity.setTextColor(Color.parseColor("#FFFFFFFF"))
        binding.humiditylevel.setTextColor(Color.parseColor("#FFFFFFFF"))
        binding.sealevel.setTextColor(Color.parseColor("#FFFFFFFF"))
        binding.sealeveltxt.setTextColor(Color.parseColor("#FFFFFFFF"))
        binding.temperaturetxt.setTextColor(Color.parseColor("#FFFFFFFF"))
        binding.windspeed.setTextColor(Color.parseColor("#FFFFFFFF"))
        binding.windtxt.setTextColor(Color.parseColor("#FFFFFFFF"))
        binding.textViewCityname.setTextColor(Color.parseColor("#FFFFFFFF"))
    }


    private fun changeAnimation(conditions: String, hour: Int) {
        when (conditions) {

            "Clear", "Sunny", "Hot", "Haze" -> {
                if (hour > 5 && hour < 18) {
                    binding.lottieAnim.setAnimation(R.raw.sunnyanim)
                    binding.root.setBackgroundResource(R.drawable.sunnybackground)
                } else {
                    binding.lottieAnim.setAnimation(R.raw.moonanim)
                    binding.root.setBackgroundResource(R.drawable.nightbg)
                    changeDayNight()
                }

            }

            "Rain", "Drizzle", "Showers", "Moderate Rain" -> {

                binding.lottieAnim.setAnimation(R.raw.rainycloudsanim)


            }

            "Partly Clouds", "Foggy", "Smoke", "Clouds", "Windy", "Mist" -> {

                binding.lottieAnim.setAnimation(R.raw.cloudyanim)

            }

            "Heavy Rain", "Thunder Storm" -> {

                binding.lottieAnim.setAnimation(R.raw.thunder)

            }

            "Snow", "Light Snow", "Heavy Snow", "Blizzard" -> {

                binding.lottieAnim.setAnimation(R.raw.snowanim)

            }

            else -> {
                // Log unexpected condition for debugging
                Log.d("WeatherCondition", "Unexpected condition: $conditions")
            }


        }
        binding.lottieAnim.playAnimation()
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            Location_PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    getLocation()
                } else {
                    // Permission denied
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    ) {

                        Toast.makeText(
                            this,
                            "Please enable location permission in settings.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }

            Notification_Permission_CODE -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.POST_NOTIFICATIONS
                        ) != PackageManager.PERMISSION_GRANTED
                    )
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                            Notification_Permission_CODE
                        )
                }


            }
        }
    }


    fun getLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (location != null) {
                Cityname = getCity(location.latitude, location.longitude)
                fetchData(Cityname)
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                Location_PERMISSION_CODE
            )
        }
    }


    /*
      IMPLEMENTING NOTIFICATIONS
      AND
      ALARM MANAGER

      First implementing Notifications
     */


    private fun getNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(

                    this,
                    Manifest.permission.POST_NOTIFICATIONS

                ) != PackageManager.PERMISSION_GRANTED
            )

            //requesting the permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    Notification_Permission_CODE
                )


        }

    }


}



