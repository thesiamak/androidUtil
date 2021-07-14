package ir.drax

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import ir.drax.extensions.model.Channel

class NotificationUtil(private val context: Context){
    private val channels = arrayOf(
        Channel("public", "Public"),
    )
    private val generalChannel = channels[0]

    fun popNotification(notif: Notification, tag: Int) {

        //we give each notification the ID of the event it's describing,
        //to ensure they all show up and there are no duplicates
        NotificationManagerCompat.from(context).notify(tag, notif)
        println("###tag = $tag")
    }

    fun pullNotification(tag: Int) {
        println("### clear tag = $tag")
        NotificationManagerCompat.from(context).cancel(tag)
    }

    fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            context.getSystemService(Context.NOTIFICATION_SERVICE).let {
                it as NotificationManager

                channels.forEach { ch ->
                    it.createNotificationChannel(
                        NotificationChannel(ch.id, ch.name, NotificationManager.IMPORTANCE_HIGH).apply {
                            description = ch.name
                            if(ch.soundUri > 0)
                                setSound(
                                    Uri.parse("android.resource://" + context.packageName + "/" + ch.soundUri), AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
                                )
                        })
                }
            }
        }
    }

    fun showNotification(notificationTitle: String, notificationText: String,icon:Int,target:Class<AppCompatActivity>) {
        val pendingIntent = PendingIntent.getActivity(context, 0, Intent(context,target::class.java), PendingIntent.FLAG_ONE_SHOT)

        val notificationBuilder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, generalChannel.id)
                .setSmallIcon(icon)
                .setContentTitle(notificationTitle)
                .setContentText(notificationText)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
        val notificationManager =
            NotificationManagerCompat.from(context)
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    companion object{
        const val ITEM = "notif_service_object"
    }
}