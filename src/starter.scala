package com.alexknvl.wifiriffic

import android.content.{Intent, Context, BroadcastReceiver}

class ServiceStarter extends BroadcastReceiver {
  override def onReceive(ctx: Context, intent: Intent): Unit = {
    intent.getAction match {
      case Intent.ACTION_BOOT_COMPLETED
           | Intent.ACTION_USER_PRESENT =>
        ctx.startService(new Intent(ctx, classOf[WifiDataCollectionService]))
    }
  }
}
