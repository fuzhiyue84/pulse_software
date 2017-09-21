package cn.edu.fudan.blepulse.presenter;

import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.io.InputStream;

import cn.edu.fudan.blepulse.ormlite.DataHelper;
import cn.edu.fudan.blepulse.view.IMainView;

/**
 * Created by dell on 2017-09-10.
 */

public class MainPresenter implements IMainPresenter {

    private volatile boolean flag = false;
    private DataHelper helper;
    private Handler mHandler = new Handler(Looper.getMainLooper()){

    };
    private IMainView view;

    public MainPresenter(DataHelper helper, IMainView view){
        this.helper = helper;
        this.view = view;
    }

    @Override
    public void saveToFile() {
    }

    @Override
    public void connectDevice() {

    }

    @Override
    public void recordData() {

    }

    @Override
    public void disconnect() {

    }

    class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tempIn = null;

            try {
                tempIn = mmSocket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            mmInStream = tempIn;
        }

        public void run() {
            byte[] buffer = new byte[1];
            int bytes = 0;
            while (flag) {
                try {
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(1, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {

                mmInStream.close();
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
