package com.shift4.threed

import android.util.Log
import com.nsoftware.ipworks3ds.sdk.ClientEventListener

internal class DebugEventListener : ClientEventListener {
    override fun fireLog(logLevel: Int, message: String, logType: String) {
        Log.i("ClientLog", "$logType - $message")
    }

    override fun fireDataPacketIn(dataPacket: ByteArray?) {
        Log.i("ClientDataPacketIn", String(dataPacket!!))
    }

    override fun fireDataPacketOut(dataPacket: ByteArray?) {
        Log.i("ClientDataPacketOut", String(dataPacket!!))
    }

    override fun fireSSLStatus(message: String?) {
        Log.i("ClientSSLStatus", message!!)
    }

    override fun fireSSLServerAuthentication(
        certEncoded: ByteArray?,
        certSubject: String?,
        certIssuer: String?,
        status: String?,
        accept: BooleanArray?
    ) {}
}