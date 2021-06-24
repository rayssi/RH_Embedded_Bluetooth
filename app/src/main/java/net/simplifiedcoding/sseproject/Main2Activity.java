package net.simplifiedcoding.sseproject;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main2Activity extends AppCompatActivity {


    private Globals g = Globals.getInstance();
    Button btnOn,btnOFF,compte;
    TextView txtled,voltage;
    ImageView led;

    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    int counter;
    volatile boolean stopWorker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btnOn = (Button) findViewById(R.id.btnON);
       compte = (Button) findViewById(R.id.compte);
        btnOFF = (Button) findViewById(R.id.btnOFF);
        txtled = (TextView) findViewById(R.id.txtled);
        voltage = (TextView) findViewById(R.id.voltage);
        led = (ImageView) findViewById(R.id.led);
compte.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View view) {
        compte();
    }
});
        btnOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] msgBuffer = "1".getBytes();
                try {
                    g.getMoutputStream().write(msgBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnOFF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] msgBuffer = "0".getBytes();
                try {
                    g.getMoutputStream().write(msgBuffer);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        beginListenForData();
    }

    void bginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = g.getMinputStream().available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            g.getMinputStream().read(packetBytes);
                            final String val = new String(packetBytes, "UTF-8"); // for UTF-8 encoding
                            handler.post(new Runnable()
                            {
                                public void run()
                                {
                                    if(val.equals("1")){
                                        led.setImageResource(R.drawable.ampouleallume);
                                        txtled.setText("allumé");
                                    } else if(val.equals("0")){
                                        led.setImageResource(R.drawable.pouleett);
                                        txtled.setText("éteint");
                                    } else if(Float.parseFloat(val) >= 0 && Float.parseFloat(val) < 10) {
                                        voltage.setText(val.concat(" VOLT"));
                                    }
                                }
                            });
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }


    void beginListenForData()
    {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character
        final byte line = 13; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = g.getMinputStream().available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            g.getMinputStream().read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, StandardCharsets.US_ASCII);
                                    readBufferPosition = 0;
                                    //final String str = data.substring(1);
                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            if(data.substring(0,1).equals("P")){
                                                voltage.setText(data.substring(1).concat(" VOLT"));
                                            }
                                            else if(data.substring(0,1).equals("L")) {
                                                if(data.substring(0,2).equals("L1")){
                                                    led.setImageResource(R.drawable.ampouleallume);
                                                    txtled.setText("allumé");
                                                } else if(data.substring(0,2).equals("L0")){
                                                    led.setImageResource(R.drawable.pouleett);
                                                    txtled.setText("éteint");
                                                }
                                            }
                                        }
                                    });
                                }
                                else if(b == line){
                                    readBufferPosition++;
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });

        workerThread.start();
    }

    void closeBT() throws IOException
    {
        stopWorker = true;
        g.getMoutputStream().close();
        g.getMinputStream().close();
        g.getSocket().close();
    }

    @Override
    protected void onDestroy() {
        try {
            closeBT();
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
    public void compte(){
        Intent intent=new Intent(Main2Activity.this,Main3Activity.class);
        startActivity(intent);
    }
}


