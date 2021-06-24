package net.simplifiedcoding.sseproject;

import android.bluetooth.BluetoothSocket;

import java.io.InputStream;
import java.io.OutputStream;

public class Globals {
    private static Globals instance;

    private Globals(){}

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }

    public InputStream minputStream;
    public OutputStream moutputStream;
    BluetoothSocket socket = null;

    public BluetoothSocket getSocket() {
        return socket;
    }

    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
    }

    public InputStream getMinputStream() {
        return minputStream;
    }

    public void setMinputStream(InputStream minputStream) {
        this.minputStream = minputStream;
    }

    public OutputStream getMoutputStream() {
        return moutputStream;
    }

    public void setMoutputStream(OutputStream moutputStream) {
        this.moutputStream = moutputStream;
    }
}
