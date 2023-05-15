/**  Copyright © 2018 Socket Mobile, Inc. */

package com.socketmobile.stockcount.helper

import android.content.Context
import android.preference.PreferenceManager


const val SHOW_INSTRUCTION_KEY = "HaveToShowInstruction"
const val AUTO_ADD_QUANTITY_KEY = "AutoAddQuantity"
const val D600_SUPPORT_KEY = "D600Support"
const val DELINEATOR_COMMA_SET_KEY = "DelineatorCommaSet"
const val DEFAULT_QUANTITY_KEY = "DefaultQuantity"
const val NEW_LINE_KEY = "NewLineForNewScan"
const val VIBRATION_KEY = "VibrationOnScan"
const val SCAN_DATE_KEY = "ScanDate"
const val SCAN_COUNT_KEY = "ScanCount"
const val CONSOLIDATING_COUNTS = "ConsolidatingCounts"

const val DEFAULT_AUTO_ADD_QUANTITY = true
const val DEFAULT_D600_SUPPORT = false
const val DEFAULT_DELINEATOR_COMMA_SET = true
const val DEFAULT_QUANTITY = 1
const val DEFAULT_NEW_LINE = true
const val DEFAULT_VIBRATION_ON_SCAN = false
const val DEFAULT_SCAN_DATE = ""
const val DEFAULT_SCAN_COUNT = 0
const val DEFAULT_CONSOLIDATING_COUNTS = true

fun autoAddQuantity(c: Context): Boolean {
    val sp = PreferenceManager.getDefaultSharedPreferences(c)
    return sp.getBoolean(AUTO_ADD_QUANTITY_KEY, DEFAULT_AUTO_ADD_QUANTITY)
}

fun isDelineatorComma(c: Context): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(DELINEATOR_COMMA_SET_KEY, DEFAULT_DELINEATOR_COMMA_SET)
}

fun getDefaultQuantity(c: Context): Int {
    return PreferenceManager.getDefaultSharedPreferences(c).getInt(DEFAULT_QUANTITY_KEY, DEFAULT_QUANTITY)
}

fun isAddNewLine(c: Context): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(NEW_LINE_KEY, DEFAULT_NEW_LINE)
}

fun isVibrationOnScan(c: Context): Boolean {
    return PreferenceManager.getDefaultSharedPreferences(c).getBoolean(VIBRATION_KEY, DEFAULT_VIBRATION_ON_SCAN)
}

fun getLineForBarcode(c: Context, barcode: String? = null): String {
    var retValue = if (barcode.isNullOrEmpty()) "Invalid barcode" else barcode
    return retValue
}