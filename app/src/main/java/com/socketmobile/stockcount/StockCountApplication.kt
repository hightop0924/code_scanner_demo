/**  Copyright © 2018 Socket Mobile, Inc. */

package com.socketmobile.stockcount

import android.app.Application
import android.os.Environment
import com.socketmobile.capture.android.Capture
import io.realm.Realm
import java.io.File
import java.io.IOException


class StockCountApplication: Application() {
    override fun onCreate() {
        super.onCreate()
    }
}