import network
import socket
from machine import Pin
import utime

ssid = 'INSERT SSID HERE'
password = 'INSERT PASSWORD HERE'

def connect():
    #Connect to WLAN
    #red_led = Pin(3, Pin.OUT)
    wlan = network.WLAN(network.STA_IF)
    wlan.active(True)
    wlan.connect(ssid, password)
    while wlan.isconnected() == False:
        print('Waiting for connection...')
        #red_led.on()
        utime.sleep(1)
        #red_led.off()

def motionDetector():
    seq_num = 0
    pir = Pin(2, Pin.IN, Pin.PULL_UP)
    utime.sleep(3)
    UDP_IP = "INSERT IP OF ANDROID DEVICE HERE"
    UDP_PORT = INSERT PORT OF ANDROID DEVICE HERE
    sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    
    while True:
        if pir.value() == 1:
            seq_num += 1
            MESSAGE = b"Motion Detected Seq Num: " + str(seq_num).encode("utf-8")
            sendUDP(sock, UDP_IP, UDP_PORT, MESSAGE)
        
def sendUDP(sock, UDP_IP, UDP_PORT, MESSAGE):
    sock.sendto(MESSAGE, (UDP_IP, UDP_PORT))
    
try:
    #green_led = Pin(4, Pin.OUT)
    #blue_led = Pin(5, Pin.OUT)
    connect()
    #green_led.on()
    #utime.sleep(3)
    #print("Connected")
    #print("Standby...")
    #blue_led.toggle()
    #utime.sleep(3)
    #print("Detection Enabled")
    motionDetector()
except KeyboardInterrupt:
    machine.reset()
