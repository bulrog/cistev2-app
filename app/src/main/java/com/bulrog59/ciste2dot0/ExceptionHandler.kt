package com.bulrog59.ciste2dot0

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Process
import com.bulrog59.ciste2dot0.CrashActivity.Companion.ERROR
import java.io.PrintWriter
import java.io.StringWriter


class ExceptionHandler(private val myContext: Activity) : Thread.UncaughtExceptionHandler {

    val LINE_SEPARATOR='\n'

    override fun uncaughtException(t: Thread, exception: Throwable) {
        val stackTrace = StringWriter()
        exception.printStackTrace(PrintWriter(stackTrace))
        val errorReport = StringBuilder()

        errorReport.append("\n************ DEVICE INFORMATION ***********\n")
        errorReport.append("Brand: ")
        errorReport.append(Build.BRAND)
        errorReport.append(LINE_SEPARATOR)
        errorReport.append("Device: ")
        errorReport.append(Build.DEVICE)
        errorReport.append(LINE_SEPARATOR)
        errorReport.append("Model: ")
        errorReport.append(Build.MODEL)
        errorReport.append(LINE_SEPARATOR)
        errorReport.append("Id: ")
        errorReport.append(Build.ID)
        errorReport.append(LINE_SEPARATOR)
        errorReport.append("Product: ")
        errorReport.append(Build.PRODUCT)
        errorReport.append(LINE_SEPARATOR)
        errorReport.append("\n************ FIRMWARE ************\n")
        errorReport.append("SDK: ")
        errorReport.append(Build.VERSION.SDK_INT)
        errorReport.append(LINE_SEPARATOR)
        errorReport.append("Release: ")
        errorReport.append(Build.VERSION.RELEASE)
        errorReport.append(LINE_SEPARATOR)
        errorReport.append("Incremental: ")
        errorReport.append(Build.VERSION.INCREMENTAL)
        errorReport.append(LINE_SEPARATOR)
        errorReport.append("************ CAUSE OF ERROR ************\n\n")
        errorReport.append(stackTrace.toString())


        val intent = Intent(myContext, CrashActivity::class.java)
        intent.putExtra(ERROR, errorReport.toString())
        myContext.startActivity(intent)

        Process.killProcess(Process.myPid())
        System.exit(10)
    }
}