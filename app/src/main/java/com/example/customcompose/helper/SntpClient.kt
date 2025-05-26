package com.example.customcompose.helper

import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

object SntpClient {
    fun getUtcTime(): Long? {
        return try {
            val ntpHost = "time.google.com"
            val address = InetAddress.getByName(ntpHost)
            val buffer = ByteArray(48)
            buffer[0] = 0b00100011 // NTP request header

            val request = DatagramPacket(buffer, buffer.size, address, 123)
            val socket = DatagramSocket()
            socket.soTimeout = 3000
            socket.send(request)
            socket.receive(request)
            socket.close()

            val transmitTimeSeconds = ((buffer[40].toLong() and 0xFFL) shl 24) or
                    ((buffer[41].toLong() and 0xFFL) shl 16) or
                    ((buffer[42].toLong() and 0xFFL) shl 8) or
                    (buffer[43].toLong() and 0xFFL)

            val unixTime = (transmitTimeSeconds - 2208988800L) * 1000L
            unixTime
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
