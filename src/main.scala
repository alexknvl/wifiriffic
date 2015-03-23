package com.alexknvl.wifiriffic

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.{TextView, Button, LinearLayout}

import macroid._
import macroid.FullDsl._
import macroid.contrib.TextTweaks

object OurTweaks {
  def greeting(greeting: String)(implicit appCtx: AppContext) =
    TextTweaks.large +
      text(greeting) +
      hide
}

class MainActivity extends Activity with Contexts[Activity] {
  override def onCreate(bundle: Bundle): Unit = {
    super.onCreate(bundle)

    var greeting = slot[TextView]

    setContentView {
      getUi {
        l[LinearLayout](
          w[Button] <~ text("Click me") <~
          On.click {
            greeting <~ show
          },
          w[TextView] <~ wire(greeting) <~ OurTweaks.greeting("Hello!")
        ) <~ vertical
      }
    }

    this.startService(new Intent(this, classOf[WifiDataCollectionService]))
  }
}
