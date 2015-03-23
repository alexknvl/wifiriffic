package com.alexknvl.wifiriffic

import java.io._
import java.util.zip.GZIPOutputStream

import android.app.{ Activity, Service }
import android.content.{IntentFilter, Intent, BroadcastReceiver, Context}
import android.location.{Location, LocationListener, LocationManager}
import android.net.wifi.{ScanResult, WifiManager}
import android.os.{Build, Environment, Bundle, IBinder}
import android.telephony.TelephonyManager
import android.util.Log
import scala.collection.JavaConverters._
import scala.util.control.NonFatal

class WifiStatusReceiver(wifi: WifiManager, cb: Iterable[ScanResult] => Unit) extends BroadcastReceiver {
  override def onReceive(ctx: Context, intent: Intent): Unit =
    cb(wifi.getScanResults.asScala)
}

class WifiDataCollectionService extends Service {
  def gzipper(file: File) =
    new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(file, true)))

  lazy val wifiManager = this.getSystemService(Context.WIFI_SERVICE).asInstanceOf[WifiManager]
  lazy val telephonyManager = this.getSystemService(Context.TELEPHONY_SERVICE).asInstanceOf[TelephonyManager]
  lazy val locationManager = this.getSystemService(Context.LOCATION_SERVICE).asInstanceOf[LocationManager]

  lazy val receiver = new WifiStatusReceiver(wifiManager, { results =>
    try {
      val outputFile = new File(Environment.getExternalStoragePublicDirectory(
        Environment.DIRECTORY_DOWNLOADS), "wifi.dat")
      val fileStream = new PrintWriter(gzipper(outputFile))

      val timestamp = System.currentTimeMillis()
      for (r <- results) {
        fileStream.print(s"${timestamp}\t")
        fileStream.print(s"${r.frequency}\t")
        fileStream.print(s"${r.level}\t")
        fileStream.print(s"${r.capabilities}\t")
        fileStream.print(s"${r.BSSID}\t")
        fileStream.print(s"${r.SSID}\n")
      }

      fileStream.close()
    } catch {
      case NonFatal(e) =>
        Log.e("wifiriffic", "Could not write to wifi log file.")
    }
  })

  lazy val locationReceiver = new LocationListener {
    override def onProviderEnabled(provider: String): Unit = ()
    override def onStatusChanged(provider: String, status: Int, extras: Bundle): Unit = ()
    override def onLocationChanged(location: Location): Unit = ()
    override def onProviderDisabled(provider: String): Unit = ()
  }

  override def onBind(intent: Intent): IBinder = null

  override def onCreate(): Unit = {
    super.onCreate()
    this.registerReceiver(receiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationReceiver)
  }

  override def onDestroy(): Unit = {
    this.unregisterReceiver(receiver)
    super.onDestroy()
  }
}
