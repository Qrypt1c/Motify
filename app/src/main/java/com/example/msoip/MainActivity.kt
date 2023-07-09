package com.example.msoip

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.msoip.ui.theme.MSOIPTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.Inet4Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var ipaddr = ""
        var data = ""
        setContent {
            MSOIPTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    ConnectionLayout(ipaddr, data)
                }
            }
        }
        GlobalScope.launch(Dispatchers.IO){
            ipaddr = getIPaddr()
            while(true){
                data = receiveUDP(ipaddr) + "\n" + data
                withContext(Dispatchers.Main){
                    setContent {
                        MSOIPTheme {
                            // A surface container using the 'background' color from the theme
                            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                                ConnectionLayout(ipaddr, data)
                            }
                        }
                    }
                }
            }
        }

    }
}
public final class LocalDate

fun getIPaddr(): String {
    try {
        val en = NetworkInterface.getNetworkInterfaces()
        while (en.hasMoreElements()) {
            val networkInterface = en.nextElement()
            val enu = networkInterface.inetAddresses
            while (enu.hasMoreElements()) {
                val inetAddress = enu.nextElement()
                if (!inetAddress.isLoopbackAddress && inetAddress is Inet4Address) {
                    return inetAddress.getHostAddress()
                }
            }
        }
    } catch (ex: Exception) {
        ex.printStackTrace()
    }

    return null.toString()
}
suspend fun receiveUDP(deviceIP: String): String{
    val buffer = ByteArray(64)
    var socket: DatagramSocket? = null
    try {
        socket = DatagramSocket(6666, InetAddress.getByName(deviceIP))
        socket.broadcast = true
        var packet = DatagramPacket(buffer, buffer.size)
        socket.receive(packet)

        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")
        val current = LocalDateTime.now().format(formatter).toString()

        return current + "\t\t" + String(packet.data)

    } catch (e: Exception){
        println("Catch exception: " + e.toString())
        e.printStackTrace()
    } finally {
        socket?.close()
    }

    return ""
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectionLayout(ipaddr: String, dataFeed: String, modifier: Modifier = Modifier){

    // Device Info Column
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ){
        Text(
            text = "Device IP Address",
            color = Color.Cyan,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = ipaddr,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal
        )

    }

    // Motion Detection
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp, bottom = 100.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ){
        Text(
            text = dataFeed,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Normal
        )
    }

    // Classification Prompt
    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
    ){
        Text(
            text = "CONFIDENTIAL//RSEN/ORCON",
            color = Color.Red,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = false)
@Composable
fun ConnectionPreview(){
    MSOIPTheme {
        ConnectionLayout("192.168.1.7", "Pleb")
    }
}
