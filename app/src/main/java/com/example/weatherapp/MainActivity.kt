package com.example.weatherapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    val url = "https://api.openweathermap.org/data/2.5/"
    val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        fetchData("Attock")
        SearchView()

    }
fun SearchView(){
    val searchView = binding.searchView
    searchView.setOnQueryTextListener(object : android.widget.SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(query: String?): Boolean {
            fetchData(query!!)
            return true
        }

        override fun onQueryTextChange(newText: String?): Boolean {
            return true
        }

    })
}
    private fun fetchData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(url)
            .build()
            .create(ApiInterface::class.java)
        val data = retrofit.getWeatherData(cityName,"your api key","metric")
        data.enqueue(object : Callback<WeatherData>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(p0: Call<WeatherData>, response: Response<WeatherData>) {
                val weatherData = response.body()
                if (response.isSuccessful && weatherData != null) {
                    val temp = weatherData.main.temp
                    val humidity = weatherData.main.humidity
                    val minTemp = weatherData.main.temp_min
                    val maxTemp = weatherData.main.temp_max
                    val windSpeed = weatherData.wind.speed
                    val condition = weatherData.weather.firstOrNull()?.main ?: "unknown"
                    val sunsetTime = weatherData.sys.sunset.toLong()
                    val sunriseTime = weatherData.sys.sunrise.toLong()
                    binding.temp.text = temp.toString()
                    binding.humidityRate.text = "$humidity%"
                    binding.minTemp.text = "Min : $minTemp°C"
                    binding.maxTemp.text = "Max : $maxTemp°C"
                    binding.windSpeed.text = "$windSpeed m/s"
                    binding.weatherCondition.text = condition
                    binding.day.text = day(System.currentTimeMillis())
                    binding.date.text = date(System.currentTimeMillis())
                    binding.condition.text = condition
                    binding.sunset.text = time(sunsetTime)
                    binding.sunrise.text = time(sunriseTime)
                    binding.sea.text = weatherData.main.pressure.toString()
                    binding.cityName.text = cityName
                    changeBackgroundImage(condition)
                }else{
                    Toast.makeText(this@MainActivity, "Enter Valid City Name", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onFailure(p0: Call<WeatherData>, p1: Throwable) {
                Toast.makeText(this@MainActivity, "Something Went Wrong", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun changeBackgroundImage(condition: String) {
        when(condition){
            "Clear Sky","Sunny","Clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Fog", "Smoke" ,  "Partly Clouds","Clouds","Overcast","Mist"-> {
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Rain", "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)

            }
        }
        binding.lottieAnimationView.playAnimation()
    }

    private fun day(timestamp: Long) :String {
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return  sdf.format(Date())
    }
    private fun date(timestamp: Long) :String {
        val sdf = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        return  sdf.format(Date())
    }
    private fun time(timestamp: Long):String{
        val sdf = SimpleDateFormat("HH:mm",Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }

}