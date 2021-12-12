# RH_Embedded_Bluetooth
mobile application: bluetooth communication with electronic card, allows LED to be turned on and off, display resistance value, and other functionality

Bluetooth Classic VS Bluetooth Low Energy (BLE)
Bluetooth is a form of technology that eliminates the need for cables and wires. Two devices in short distance of each other and equipped with Bluetooth can transfer voice and data information between each other wirelessly. In recent years, Bluetooth technology has seen a rapid expansion of Bluetooth-capable devices.



Bluetooth Classic vs. Bluetooth Low Energy (BLE) – which to choose for your Android project? what is the difference between Bluetooth Classic and BLE?

Bluetooth  Classic

The Bluetooth Classic radio, also referred to as Bluetooth Basic Rate/Enhanced Data Rate (BR/EDR), is a low power radio that streams data over 79 channels in the 2.4GHz unlicensed industrial, scientific, and medical (ISM) frequency band. Supporting point-to-point device communication, Bluetooth Classic is mainly used to enable wireless audio streaming and has become the standard radio protocol behind wireless speakers, headphones, and in-car entertainment systems. The Bluetooth Classic radio also enables data transfer applications, including mobile printing.

Bluetooth  Low Energy (LE)

The Bluetooth Low Energy (LE) radio is designed for very low power operation. Transmitting data over 40 channels in the 2.4GHz unlicensed ISM frequency band, the Bluetooth LE radio provides developers a tremendous amount of flexibility to build products that meet the unique connectivity requirements of their market. Bluetooth LE supports multiple communication topologies, expanding from point-to-point to broadcast and, most recently, mesh, enabling Bluetooth technology to support the creation of reliable, large-scale device networks. While initially known for its device communications capabilities, Bluetooth LE is now also widely used as a device positioning technology to address the increasing demand for high accuracy indoor location services. Initially supporting simple proximity capabilities, Bluetooth LE now supports Bluetooth  Direction Finding and soon, high-accuracy distance measurement












Bluetooth Classic vs Bluetooth Low Energy (BLE) on Android

Bluetooth Classic vs Bluetooth Low Energy (BLE) – which of these is better? It depends on our requirements. We need to compare the parameters and features of both standards.

What’s more, we should keep in mind that usually, we need to create an application that allows us to handle the communication between our smartphone and the device. So, it’s also important to know what is offered by the operating system (Android – in this case) in terms of using each solution.

Bluetooth Classic – highly effective for short distances
Bluetooth Classic is designed for continuous two-way data transfer with high Application throughput (up to 2.1 Mbps); highly effective, but only for short distances. So, it’s a perfect solution in the case of streaming audio and video, or mice and other devices that need a continuous, broadband link.

Bluetooth Low Energy – as much as 100x lower power consumption
Bluetooth Low Energy provides only 0.3 Mbps of Application throughput. The data is sent in small (20 bytes) packages, but the range can be even more than 100 meters (330 feet) and the minimum latency between unconnected state to data transfer can be counted in a handful of milliseconds, while in BT Classic it’s about 100 ms.

Yet, the main advantage of using BLE is its very low power consumption. Although the peak current of BLE (up to 15 mA) is half of the BT Classic current (up to 30 mA). But the power consumption can be even 100x lower for BLE!

So, for the 1W reference value for BT Classic, BLE offers 0.01W – 0.5 W power consumption. This means that simple BLE devices, like beacons, may function for 1–2 years with a 1,000 mAh coin cell battery.

How to use Bluetooth Classic and Bluetooth Low Energy on Android – basic steps


But if there is no need to use Bluetooth Classic, it’s better to use BLE, because of its low power consumption.

The implementation of BLE is provided by Android version 4.3 (API 18) and above. So, if we have already decided which solution will be suitable for our needs, let’s start the implementation process. Here are the basic steps.

1. Requesting permissions and features
This is the first step before using Bluetooth Classic or BLE features. We need to make sure all required permissions and features are applied and enabled. What do we need?

Permissions:
android.permission.BLUETOOTH – basic BC and BLE features
android.permission.BLUETOOTH_ADMIN – advanced BC and BLE operations like enabling/disabling Bluetooth module, device discovery, creating sockets
android.permission.ACCESS_COARSE_LOCATION – required for BLE scanning on Android 5.0 (API 21) or higher. Note: This is dangerous permission from API 23.
So, to use all the features, our manifest file should look like this:

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.bt">

    <uses-permission android:name="android.permission.BLUETOOTH" />
    <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

    <application ... />

</manifest>
Features:
We need also to make sure that the smartphone or tablet has a built-in Bluetooth adapter. If we just want to make our app unavailable for devices without Bluetooth, we can just add a proper manifest tag:
<uses-feature android:name="android.hardware.bluetooth"/>
But, according to the documentation on developer.android.com, it’s not required if we declare the Bluetooth permission and set the target SDK version to 5 or above.

We also need to keep in mind that using Bluetooth requires the Bluetooth adapter to be enabled. We can check its current state by BluetoothAdapter.getDefaultAdapter().isEnabled. If it is disabled, we can enable it in many different ways:

inform the user to enable it manually (in a drop-down menu from the status bar)
use an intent to show a system-provided dialog for the user:
enable it programmatically by BluetoothAdapter.getDefaultAdapter().enable()
This method requires the android.permission.BLUETOOTH_ADMIN permission and it’s an asynchronous call: it will return immediately. So, in this case, we still need to wait until the Bluetooth adapter enables, so we can use a broadcast receiver to catch the BluetoothAdapter.ACTION_STATE_CHANGED intent:

If we want to use BLE scanning, location services also need to be enabled, which can be done by the user in a drop-down menu from the status bar (we need to register a broadcast receiver for LocationManager.MODE_CHANGED_ACTION or LocationManager.PROVIDERS_CHANGED_ACTION). Location services can be also enabled using a system-provided dialog, using GooglePlayService

Note: Add a dependency to your app/build.gradle file

dependencies {
  implementation 'com.google.android.gms:play-services-location:17.0.0'
}
2. Getting BluetoothDevice object
To do some Bluetooth Classic or Bluetooth Low Energy actions on a particular Bluetooth device, we need to obtain a BluetoothDevice object. If we already know the MAC address of our Bluetooth device and we want to hardcode it in our  app , we can just us


Otherwise, we need to start the discovery process. This starts scanning to find nearby devices and uses an SDP protocol to obtain advertisement data from the devices. It’s used to acknowledge the smartphone and what kind of services are provided by a Bluetooth device, such as an audio headset, keyboard, etc.
Basic discovery (Bluetooth Classic and Bluetooth Low Energy):
In this case, we need to start the discovery and register a BroadcastReceiver, which will catch BluetoothDevice.ACTION_FOUND and BluetoothDevice.ACTION_DISCOVERY_FINISHED intents. The discovery process usually involves an inquiry scan of about 12 seconds.

3. Connecting to Bluetooth device
If we need a high-speed connection (up to 2.1 Mbps) to stream big amounts of data between the smartphone and Bluetooth device, we can use one of the BT Classic features – the RFCOMM socket. If the large bandwidth is not needed in our case and we only need to exchange small packets of data, we should just use a GATT profile.

RFCOMM:
RFCOMM is a Bluetooth protocol emulating RS-232 serial ports. It can be used to create an InputStream and OutputStream to a Bluetooth device. First, we need to obtain a Bluetooth socket. It can be secure, as the data will be encrypted then

Note: The device.createInsecureRfcommSocketToServiceRecord() was officially introduced in Android 2.3.3 (Gingerbread – API 10). We can call it using reflection, but it’s not guaranteed to work.

RFCOMM – pairing:
In the case of using RFCOMM sockets, the device we want to transfer data with needs to firstly be paired with our smartphone. To check if a device is already paired, we can use

Devices can be paired manually by the user in the Android Bluetooth Settings. We can also request pairing programmatically. The pairing request can be done by bluetoothDevice.createBond() but the user still needs to accept the pairing by tapping on a system-provided dialog.

Tip: If we want to make a hands-free solution, we can try a workaround, but it’s not recommended and not guaranteed to work. It relies on catching system intent (action BluetoothDevice.ACTION_PAIRING_REQUES), applying pairing by bluetoothDevice.setPairingConfirmation(true) and aborting the broadcast. If we set a high priority for the IntentFilter, the intent would never reach the system classes, which are handling the pairing dialog, so it will never be shown. This can throw SecurityException on Android 7.1+.

RFCOMM – data transfer
After creating a socket, we can connect to it and start reading and writing data using the input and output stream. Please keep in mind that it needs to be handled in background threads and it’s recommended to cancel the discovery process before connecting to a sockt


GATT:
The Generic Attributes (GATT) define a hierarchical data structure that is exposed to connected BLE devices. GATT allows us to read or write values on available characteristics. We can also get asynchronous notifications and indications when a characteristic value has changed.

GATT Characteristics
A GATT Characteristic is a data value transferred between client and server – for example, the current battery level. Each of them is identified by a UUID (unique identifier). There are some standard characteristics like:

val deviceNameUUID = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb")
val appearanceUUID = UUID.fromString("00002a01-0000-1000-8000-00805f9b34fb")
val batteryLevelUUID = UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb")
val modelNumberUUID = UUID.fromString("00002A24-0000-1000-8000-00805f9b34fb")
val serialNumberUUID = UUID.fromString("00002A25-0000-1000-8000-00805f9b34fb")
val firmwareRevisionUUID = UUID.fromString("00002A26-0000-1000-8000-00805f9b34fb")
val hardwareRevisionUUID = UUID.fromString("00002A27-0000-1000-8000-00805f9b34fb")
val softwareRevisionUUID = UUID.fromString("00002A28-0000-1000-8000-00805f9b34fb")
val manufacturerNameUUID = UUID.fromString("00002A29-0000-1000-8000-00805f9b34fb")
GATT allows us to read or write values on available characteristics. We can also get asynchronous notifications and indications when a characteristic value has changed. We can use a maximum of 20 Byte data packets that we want to write on a characteristic. If we want to send bigger amounts of data, we need to split them. Each characteristic can offer a few basic operations:

read – read the current value of characteristic features
write – update the current value (it needs to be queued on our side) operations like enabling/disabling Bluetooth module, device discovery, creating sockets
notification / indication – asynchronous call – notifies about the changed value of characteristics




GATT handling is provided by native Android classes, but I recommend using dedicated libraries, like this one

attached you will find my bluettoth communication mobile application (HR Bluetooth) https://github.com/rayssi/RH_Bluetooth.git.
  https://github.com/rayssi/RH_Embedded_Bluetooth.git


