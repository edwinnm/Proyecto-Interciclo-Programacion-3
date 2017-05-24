package proyecto.ejemplo.mundo.hola.edwin.myapplication;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


import java.util.Map;
/*
* Clase encargada de recibir los mensajes tipo notificacion y mostrarlos en la seccion notificaciones.
*
*
*
*
*/
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String result = "From : " + remoteMessage.getFrom() + "\nMessageId = " + remoteMessage.getMessageId() + "\nMessageType =  " + remoteMessage.getMessageType()
                + "\nCollapeseKey = " + remoteMessage.getCollapseKey() + "\nTo: " + remoteMessage.getTo() + "\nTtl = " + remoteMessage.getTtl()
                + "\nSent Time = " + remoteMessage.getSentTime();
        Map<String, String> map = remoteMessage.getData();
        for (String key : map.keySet())
            result += "\n(" + key + "," + map.get(key) + ")";

        Intent intent = new Intent(this, ReportesFragment.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("result", result);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Defino el comportamiento de mi notificacion

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Nuevo Reporte")
                .setSmallIcon(R.drawable.notification)
                .setContentIntent(pi)
                .setVibrate(new long[] { 1000, 1000})
                .setOnlyAlertOnce(true);


        //Coloacre informacion sobre el nuevo reporte
        builder.setContentText("Se ja reportado un nuevo suceso");

        //Sonido para la notificacion
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }
}