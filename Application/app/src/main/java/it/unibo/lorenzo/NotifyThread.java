package it.unibo.lorenzo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotifyThread{

    private static final String CHANNEL_ID = "0";
    private static final String group = "gruppo1Pantry";
    List<Product> prod;
    String date;
    NotificationManager nm;
    Context context;

    public NotifyThread(Context context) {
        this.prod = new ArrayList<>();
        if(prod != null){
            this.prod.addAll(prod);
        }
        Calendar tmp = Calendar.getInstance();
        int year = tmp.get(Calendar.YEAR);
        int month = tmp.get(Calendar.MONTH);
        int day = tmp.get(Calendar.DAY_OF_MONTH);
        date = day + "/" + month + "/" + year;
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "notifiche dispensa";
            String description = "canale per notifiche dispensa";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            nm = context.getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        } else {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
    }

    public void run(List<Product> prod){
        if (prod != null) {
            for (int i = 0; i < prod.size(); i++) {
                if (prod.get(i).getDate() != null) {
                    try {
                        Calendar tmpCalendar = Calendar.getInstance();
                        int year = tmpCalendar.get(Calendar.YEAR);
                        int month = tmpCalendar.get(Calendar.MONTH);
                        int day = tmpCalendar.get(Calendar.DAY_OF_MONTH);
                        String now = day + "/" + month + "/" + year;
                        Date prodDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(prod.get(i).getDate());
                        Date nowDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).parse(now);
                        if (prodDate.before(nowDate) || prodDate.equals(nowDate)) {
                            NotificationCompat.Builder mBuilder;
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                mBuilder = new NotificationCompat.Builder(this.context, CHANNEL_ID);
                            } else {
                                mBuilder = new NotificationCompat.Builder(this.context);
                            }

                            String tmp[] = prod.get(i).getDate().split("/", prod.get(i).getDate().length());
                            year = Integer.parseInt(tmp[2]);
                            month = Integer.parseInt(tmp[1]) + 1;
                            day = Integer.parseInt(tmp[0]);
                            String stringProdDate = day + "/" + month + "/" + year;
                            String textToSend = "il prodotto " + prod.get(i).getName() + " è scaduto in data: " + stringProdDate;
                            mBuilder.setContentTitle("Prodotto scaduto").setContentText(textToSend);
                            mBuilder.setSmallIcon(R.drawable.ic_calendar);
                            mBuilder.setGroup(group);
                            mBuilder.setGroupSummary(true);
                            mBuilder.setPriority(NotificationCompat.PRIORITY_DEFAULT);
                            nm.notify(i, mBuilder.build());
                        }
                    } catch (ParseException e) {
                        Log.e("ciao", e.toString());
                    }
                }
            }
        }
    }

}

