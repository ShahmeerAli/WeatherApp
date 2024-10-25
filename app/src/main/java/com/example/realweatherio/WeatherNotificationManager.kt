package com.example.realweatherio

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Calendar
import java.util.stream.DoubleStream.builder


private val channel_Id = "122"
private val NotificationId = 1020
private lateinit var channel: NotificationChannel

class WeatherNotificationManager : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {




        val intent= Intent(context,MainActivity::class.java)
        intent.flags=Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val temp=intent.getStringExtra("report")

        val pending_intent=PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_IMMUTABLE)

        val notifcationBuilder= context?.let {
            NotificationCompat.Builder(it, channel_Id)
                .setContentIntent(pending_intent)
                .setSmallIcon(R.drawable.mainlogo)
                .setContentTitle("Daily Weather")
                .setContentText("Don't wanna see how things will go on today?")
                .setAutoCancel(true)


        }

        val manager= context?.let { NotificationManagerCompat.from(it) }
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        if (notifcationBuilder != null) {
            manager?.notify(NotificationId,notifcationBuilder.build())
        }

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            channel=NotificationChannel(
                channel_Id,
                "Daily_Notification",
                NotificationManager.IMPORTANCE_HIGH
            )

            val manager= context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

        }












    }
}