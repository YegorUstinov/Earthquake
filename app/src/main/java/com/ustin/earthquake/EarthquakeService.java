package com.ustin.earthquake;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

// класс получает и записывает данные в бд
public class EarthquakeService extends IntentService {
    public static String TAG = "EARTHQUAKE_UPDATE_SERVICE";

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private Notification.Builder earthquakeNotificationBuilder;
    public static final int NOTIFICATION_ID = 1;

    @Override
    public void onCreate() {
        super.onCreate();
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        String ALARM_ACTION;
        ALARM_ACTION = EarthquakeAlarmReceiver.ACTION_REFRESH_EARTHQUAKE_ALARM;
        Intent intentToFire = new Intent(ALARM_ACTION);
        alarmIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
        earthquakeNotificationBuilder = new Notification.Builder(this);
        earthquakeNotificationBuilder.
                setAutoCancel(true).
                setTicker("Earthquake detected").
                setSmallIcon(R.drawable.ic_launcher_foreground);
    }

    public EarthquakeService() {
        super("EarthquakeUpdateService");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public static String QUAKES_REFRESHED = "com.ustin.earthquake.QUAKES_REFRESHED";

    @Override
    protected void onHandleIntent(Intent intent) {
        Context context = getApplicationContext();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int updateFreq = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));
        boolean autoUpdateChecked = prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);
        if (autoUpdateChecked) {
            int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
            long timeToRefresh = SystemClock.elapsedRealtime() + updateFreq * 60 * 1000;
            alarmManager.setInexactRepeating(alarmType, timeToRefresh, updateFreq * 60 * 1000, alarmIntent);
        } else {
            alarmManager.cancel(alarmIntent);
        }
        refreshEarthquakes();
        sendBroadcast(new Intent(QUAKES_REFRESHED));
    }

    // этот метод парсит данные о землетрясениях из usgs.gov путем сервисного запроса к сайту и получения xml документа
    public void refreshEarthquakes() {
        URL url;
        final String DATE_FORMAT = "yyyy-MM-dd";
        SimpleDateFormat formater = new SimpleDateFormat(DATE_FORMAT);
        formater.setTimeZone(TimeZone.getTimeZone("GMT-12"));
        Calendar currentTime = Calendar.getInstance();
        String timeStr = formater.format(currentTime.getTime());
        Log.w(TAG, "Date is: " + timeStr);

        try {
            url = new URL("https://earthquake.usgs.gov/fdsnws/event/1/query?format=xml&starttime=" + timeStr + "&minmagnitude=3");
            URLConnection connection;
            connection = url.openConnection();

            HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
            int responseCode = httpsConnection.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                InputStream in = httpsConnection.getInputStream();

                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();

                Document dom = db.parse(in);
                Element docEle = dom.getDocumentElement();

                NodeList n1 = docEle.getElementsByTagName("event");
                NodeList nMag = docEle.getElementsByTagName("mag");
                NodeList nTime = docEle.getElementsByTagName("time");
                NodeList longitude = docEle.getElementsByTagName("longitude");
                NodeList latitude = docEle.getElementsByTagName("latitude");
                if (n1 != null && n1.getLength() > 0) {
                    for (int i = 0; i < n1.getLength(); i++) {
                        Element entry = (Element) n1.item(i);
                        Element nM = (Element) nMag.item(i);
                        Element nT = (Element) nTime.item(i);
                        Element longiTude = (Element) longitude.item(i);
                        Element latiTude = (Element) latitude.item(i);

                        Element longi = (Element) longiTude.getElementsByTagName("value").item(0);
                        String longiStr = longi.getFirstChild().getNodeValue();
                        Element lati = (Element) latiTude.getElementsByTagName("value").item(0);
                        String latiStr = lati.getFirstChild().getNodeValue();
                        String location = latiStr + " " + longiStr;

                        Element title = (Element) entry.getElementsByTagName("text").item(0);
                        String details = title.getFirstChild().getNodeValue();

                        Element magnitude = (Element) nM.getElementsByTagName("value").item(0);
                        String mag = magnitude.getFirstChild().getNodeValue();
                        int end = mag.length();
                        double _m = Double.parseDouble(mag.substring(0, end));

                        Element when = (Element) nT.getElementsByTagName("value").item(0);
                        String dt = when.getFirstChild().getNodeValue();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                        Date qdate = new GregorianCalendar(0, 0, 0).getTime();
                        try {
                            qdate = sdf.parse(dt);
                        } catch (ParseException e) {
                            Log.d(TAG, "Date parsing exception.", e);
                        }

                        Quake quake = new Quake(qdate, details, _m, location);

                        addNewQuake(quake);
                    }
                }
            }
        } catch (MalformedURLException e) {
            Log.d(TAG, "Malformed URL Exception");
        } catch (IOException e) {
            Log.d(TAG, "IO Exception");
        } catch (ParserConfigurationException e) {
            Log.d(TAG, "Parser Configuration Exception");
        } catch (SAXException e) {
            Log.d(TAG, "SAX Exception");
        } finally {
        }
    }

    // этот метод добавляет запись в БД
    private void addNewQuake(Quake _quake) {
        ContentResolver cr = getContentResolver();
        String w = EarthquakeProvider.KEY_DATE + " = " + _quake.getDate().getTime();
        Cursor query = cr.query(EarthquakeProvider.CONTENT_URI, null, w, null, null);
        if (query.getCount() == 0) {
            ContentValues values = new ContentValues();

            values.put(EarthquakeProvider.KEY_DATE, _quake.getDate().getTime());
            values.put(EarthquakeProvider.KEY_DETAILS, _quake.getDetails());
            values.put(EarthquakeProvider.KEY_SUMMARY, _quake.toString());

            values.put(EarthquakeProvider.KEY_LOCATION, _quake.getLocation());
            values.put(EarthquakeProvider.KEY_MAGNITUDE, _quake.getMagnitude());

            broadcastNotification(_quake);

            cr.insert(EarthquakeProvider.CONTENT_URI, values);
        }
        query.close();
        Log.w(TAG, "addNewQuake " + "\n" + _quake.toString());
    }

    private void broadcastNotification(Quake quake) {
        Intent startActivityIntent = new Intent(this, MainActivity.class);
        PendingIntent launchIntent = PendingIntent.getActivity(this, 0, startActivityIntent, 0);
        earthquakeNotificationBuilder.setContentIntent(launchIntent)
                .setWhen(quake.getDate().getTime())
                .setContentTitle("M:" + quake.getMagnitude())
                .setContentText(quake.getDetails());
        try {
            if (quake.getMagnitude() > 6) {
                Uri ringURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                earthquakeNotificationBuilder.setSound(ringURI);
            }
        } catch (Exception e) {
            System.out.println("NOTIFICATION");
        }
        try {
            double vibrateLength = 100 * Math.exp(0.53 * quake.getMagnitude());
            long[] vibrate = new long[]{100, 100, (long) vibrateLength};
            earthquakeNotificationBuilder.setVibrate(vibrate);
        } catch (Exception e) {
            System.out.println("VIBRATION");
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, earthquakeNotificationBuilder.getNotification());
    }
}
