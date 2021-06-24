package net.simplifiedcoding.sseproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Globals g = Globals.getInstance();
    BluetoothDevice mDevice;
    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    static final int REQUEST_ENABLE_BT = 1;  // The request code
    Button btn_on,btn_off,btn_run,btn_search;
    ListView liste;
    TextView txtliste,msg;
    private Set<BluetoothDevice> devices;
    //BroadcastReceiver bluetoothReceiver;
    ArrayList<String> mylist = new ArrayList<String>();
    ArrayAdapter<String> adapter;
    private static final UUID EMBEDDED_BOARD_SPP = UUID
            .fromString("00001101-0000-1000-8000-00805F9B34FB");
    boolean stopWorker = false;
    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        } else {
            startApp();
        }
    }
    private void startApp(){
        txtliste = (TextView) findViewById(R.id.titreliste);
        msg = (TextView) findViewById(R.id.msg);
        liste = (ListView) findViewById(R.id.liste);
        btn_on = (Button) findViewById(R.id.bt_on);
        btn_off = (Button) findViewById(R.id.bt_off);
        btn_run = (Button) findViewById(R.id.btn_run);
        btn_search = (Button) findViewById(R.id.btn_search);

        txtliste.setVisibility(View.GONE);
        liste.setVisibility(View.GONE);
        btn_run.setVisibility(View.GONE);
        btn_search.setVisibility(View.GONE);
        msg.setText("");

        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_list_item_1, android.R.id.text1);
        liste.setAdapter(adapter);

        liste.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(!mylist.get(position).equals("APPAREILS JUMELÉS :")
                        && !mylist.get(position).equals("APPAREILS DISPONIBLES :")){
                    BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mylist.get(position));
                    if(device.getBondState() == BluetoothDevice.BOND_BONDED){
                        try {
                            mDevice = device;
                            g.setSocket(device.createRfcommSocketToServiceRecord(EMBEDDED_BOARD_SPP));
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(),"Not connected",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                        try {
                            g.getSocket().connect();
                            g.setMinputStream(g.getSocket().getInputStream());
                            g.setMoutputStream(g.getSocket().getOutputStream());
                            stopWorker = true;
                            Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
                            btn_run.setVisibility(View.VISIBLE);
                            btn_run.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                    startActivity(intent);
                                }
                            });
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(),"Not connected",Toast.LENGTH_SHORT).show();
                            e.printStackTrace();
                        }
                    } else {
                        mDevice = device;
                        device.createBond();
                    }
                }
            }
        });
        BtAdapter();
    }
    private void BtAdapter(){
        //Obtenez l'adaptateur Bluetooth
        if (bluetoothAdapter == null) {
            // L'appareil ne supporte pas Bluetooth
            msg.setText("L'appareil ne supporte pas Bluetooth");
            msg.setTextColor(Color.BLACK);
        } else {
            //Activer Bluetooth
            btn_off.setOnClickListener(new View.OnClickListener() {


                @Override
                public void onClick(View v) {
                   desactiver();
                    bluetoothAdapter.disable();
                    msg.setText("Bluetooth n'est pas activé");
                    msg.setTextColor(Color.RED);
                    btn_on.setEnabled(true);
                    btn_off.setEnabled(false);
                    txtliste.setVisibility(View.GONE);
                    liste.setVisibility(View.GONE);
                    btn_run.setVisibility(View.GONE);
                    btn_search.setVisibility(View.GONE);
                }
            });
            btn_on.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    activer();
                    bluetoothAdapter.enable();
                    msg.setText("Bluetooth est activé");
                    msg.setTextColor(Color.GREEN);
                    btn_on.setEnabled(false);
                    btn_off.setEnabled(true);
                    txtliste.setVisibility(View.GONE);
                    liste.setVisibility(View.GONE);
                    btn_run.setVisibility(View.GONE);
                    btn_search.setVisibility(View.VISIBLE);

                }
            });
            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    get_Devices();
                }
            });

            if (!bluetoothAdapter.isEnabled()) {

                msg.setText("Bluetooth n'est pas activé");
                msg.setTextColor(Color.RED);
                btn_on.setEnabled(true);
                btn_off.setEnabled(false);
                txtliste.setVisibility(View.GONE);
                liste.setVisibility(View.GONE);
                btn_run.setVisibility(View.GONE);
                btn_search.setVisibility(View.GONE);
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                //bluetoothAdapter.enable();
            } else {
                mediaPlayer= MediaPlayer.create(this,R.raw.bactiver);
                mediaPlayer.start();
                msg.setText("Bluetooth est activé");
                msg.setTextColor(Color.GREEN);
                btn_on.setEnabled(false);
                btn_off.setEnabled(true);
                txtliste.setVisibility(View.GONE);
                liste.setVisibility(View.GONE);
                btn_run.setVisibility(View.GONE);
                btn_search.setVisibility(View.VISIBLE);
            }
        }

    }
    private final BroadcastReceiver bluetoothReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);

                if (state == BluetoothAdapter.STATE_ON) {

                    Toast.makeText(getApplicationContext(),"ACTION_STATE_CHANGED: STATE_ON",Toast.LENGTH_SHORT).show();

                }

                if (state == BluetoothAdapter.STATE_OFF) {

                    Toast.makeText(getApplicationContext(),"ACTION_STATE_CHANGED: STATE_OFF",Toast.LENGTH_SHORT).show();
                }
            }

            else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //adapter.clear();
                Toast.makeText(getApplicationContext(),"ACTION_DISCOVERY_STARTED",Toast.LENGTH_SHORT).show();
                //mProgressDlg.show();
            }

            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action) && bluetoothAdapter.isEnabled()) {
                //mProgressDlg.dismiss();
                Toast.makeText(getApplicationContext(),"ACTION_DISCOVERY_FINISHED",Toast.LENGTH_SHORT).show();

                    /*Intent newIntent = new Intent(MainActivity.this, DeviceListActivity.class);

                    newIntent.putParcelableArrayListExtra("device.list", mDeviceList);

                    startActivity(newIntent);*/

            }

            else if (BluetoothDevice.ACTION_FOUND.equals(action)) {// When discovery finds a device
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                boolean trouve = false;
                /*for (int i=0;i<mylist.size();i++){
                    if(mylist.get(i).equals(device.getAddress())){
                        trouve = true;
                    }
                }*/
                //if(!trouve){
                mylist.add(device.getAddress());
                adapter.add(device.getName() + "\n" + device.getAddress());
                adapter.notifyDataSetChanged();
                //}
                Toast.makeText(getApplicationContext(),"Device found = " + device.getName(),Toast.LENGTH_SHORT).show();
            }

            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {// When discovery finds a device
                // Get the BluetoothDevice object from the Intent
                final int state        = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState    = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    try {
                        g.setSocket(mDevice.createRfcommSocketToServiceRecord(EMBEDDED_BOARD_SPP));
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(),"Not connected",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    try {
                        g.getSocket().connect();
                        g.setMinputStream(g.getSocket().getInputStream());
                        g.setMoutputStream(g.getSocket().getOutputStream());
                        stopWorker = true;
                        Toast.makeText(getApplicationContext(),"Connected",Toast.LENGTH_SHORT).show();
                        btn_run.setVisibility(View.VISIBLE);
                        btn_run.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                                startActivity(intent);
                            }
                        });
                    } catch (IOException e) {
                        Toast.makeText(getApplicationContext(),"Not connected",Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(),"Paired",Toast.LENGTH_SHORT).show();
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
                    Toast.makeText(getApplicationContext(),"UnPaired",Toast.LENGTH_SHORT).show();
                }
            }
            else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                Toast.makeText(getApplicationContext(),"Device is now connected",Toast.LENGTH_SHORT).show();
                //Device is now connected
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                Toast.makeText(getApplicationContext(),"Device is about to disconnect",Toast.LENGTH_SHORT).show();
                //Device is about to disconnect
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                Toast.makeText(getApplicationContext(),"Device has disconnected",Toast.LENGTH_SHORT).show();
                //Device has disconnected
            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                msg.setText("Bluetooth est activé");
                msg.setTextColor(Color.GREEN);
                btn_on.setEnabled(false);
                btn_off.setEnabled(true);
                txtliste.setVisibility(View.GONE);
                liste.setVisibility(View.GONE);
                btn_run.setVisibility(View.GONE);
                btn_search.setVisibility(View.VISIBLE);
            } else {
                msg.setText("Bluetooth n'est pas activé");
                msg.setTextColor(Color.RED);
                btn_on.setEnabled(true);
                btn_off.setEnabled(false);
                txtliste.setVisibility(View.GONE);
                liste.setVisibility(View.GONE);
                btn_run.setVisibility(View.GONE);
                btn_search.setVisibility(View.GONE);
            }
        }
    }


    private void get_Devices(){

        /*Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 30000);
        startActivity(discoverableIntent);*/

        bluetoothAdapter.startDiscovery();

        adapter.clear();
        mylist.clear();

        devices = bluetoothAdapter.getBondedDevices();

        mylist.add("APPAREILS JUMELÉS :");
        adapter.add("APPAREILS JUMELÉS :");

        for (BluetoothDevice device : devices){
            boolean trouve = false;
            for (int i=0;i<mylist.size();i++){
                if(mylist.get(i).equals(device.getAddress())){
                    trouve = true;
                }
            }
            if(!trouve){
                mylist.add(device.getAddress());
                adapter.add(device.getName() + "\n" + device.getAddress());
            }
        }

        mylist.add("APPAREILS DISPONIBLES :");
        adapter.add("APPAREILS DISPONIBLES :");

        txtliste.setVisibility(View.VISIBLE);
        liste.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        registerReceiver(bluetoothReceiver, filter);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    startApp();
                    Toast.makeText(MainActivity.this, "Permission accordé!!!", Toast.LENGTH_SHORT).show();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission non accordé!!!", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    void closeBT() throws IOException
    {
        if (stopWorker) {
            g.getMoutputStream().close();
            g.getMinputStream().close();
            g.getSocket().close();
        }
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if (bluetoothAdapter != null)
        {
            bluetoothAdapter.cancelDiscovery();
            unregisterReceiver(bluetoothReceiver);
            //unregisterReceiver(bluetoothReceiver);
        }
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public   void  activer(){
        mediaPlayer= MediaPlayer.create(this,R.raw.bactiver);
        mediaPlayer.start();
    }
    public   void  desactiver(){
        mediaPlayer= MediaPlayer.create(this,R.raw.bldesc);
        mediaPlayer.start();
    }
}
