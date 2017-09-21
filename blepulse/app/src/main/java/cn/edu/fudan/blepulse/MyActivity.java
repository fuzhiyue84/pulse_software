package cn.edu.fudan.blepulse;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.achartengine.GraphicalView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import cn.edu.fudan.blepulse.chartview.MyChartView;
import cn.edu.fudan.blepulse.ormlite.DataHelper;
import cn.edu.fudan.blepulse.ormlite.ReceiverData;
import cn.edu.fudan.blepulse.view.IMainView;

public class MyActivity extends AppCompatActivity implements IMainView {

    private Button connectButton;
    private Button stopButton;
    private Button queryButton;
    private BluetoothAdapter mBluetoothAdapter;
    final private String myUUID = "00001101-0000-1000-8000-00805F9B34FB";
    BluetoothSocket socket;
    ConnectedThread thread;;
    private MyChartView chartView;
    int count = 0;
    byte[] dataStore = new byte[300];
    private DataHelper helper;
    private Date startDate;
    private boolean startRecord = false;
    private Handler stopHandler = new Handler();
    private volatile boolean flag = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        helper = DataHelper.getHelper(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool);
        toolbar.setTitle("脉诊检测");
        setSupportActionBar(toolbar);

        connectButton = (Button) findViewById(R.id.connect_but);
        stopButton = (Button) findViewById(R.id.stop_but);
        queryButton = (Button) findViewById(R.id.query_but);

        FrameLayout layout = (FrameLayout) findViewById(R.id.chart_view);
        chartView = new MyChartView(getApplicationContext());
        GraphicalView view = chartView.getChartView();
        layout.addView(view, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, 1);
        }

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
                if (pairedDevices.size() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
                    final String[] devicesName = new String[pairedDevices.size()];
                    final String[] devicesAddress = new String[pairedDevices.size()];
                    int i = 0;
                    for (BluetoothDevice device: pairedDevices) {
                        devicesName[i] = device.getName() + "\n" + device.getAddress();
                        devicesAddress[i] = device.getAddress();
                        i++;
                    }
                    builder.setItems(devicesName, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(devicesAddress[i]);
                            try {
                                socket = device.createRfcommSocketToServiceRecord(UUID.fromString(myUUID));
                                socket.connect();
                                Toast.makeText(MyActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            thread = new ConnectedThread(socket);
                            flag = true;
                            thread.start();
                        }
                    });
                    builder.show();
                }
            }
        });

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                startRecord = false;
                chartView.changeSeriesColor(1);
            }
        };

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDate = new Date();
                startRecord = true;
                chartView.changeSeriesColor(0);
                stopHandler.postDelayed(runnable, 1000*30);
            }
        });

        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (thread != null) {
                    flag = false;
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    thread = null;
                    startRecord = false;
                }
            }
        });
    }

    private void storageData(){
        List<ReceiverData> list = helper.queryOneDateData(startDate);
        if (list != null) {
            StringBuffer buffer = new StringBuffer();
            for (int i=0; i<list.size(); i++) {
                String str = list.get(i).getContent();
                buffer.append(str);
                buffer.append(" ");
            }
            String SDPATH = Environment.getExternalStorageDirectory().getPath();
            SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
            String dateStr = sdf.format(startDate);
            File file = new File(SDPATH + "//" +dateStr + ".txt");
            if (!file.exists()) try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                FileOutputStream fos = new FileOutputStream(file);
                byte[] message = buffer.toString().getBytes();
                fos.write(message);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public boolean onCreateOptionsMenu(Menu menu)  {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_settings:
                break;

            case R.id.storage_settings:
                storageData();
                break;

            default:
                return super.onOptionsItemSelected(item);

        }
        return true;
    }

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            byte[] data = (byte[]) msg.obj;
            count = count % 300;
            dataStore[count] = data[0];
            int i = data[0] & 0xff;
            chartView.updateView(i);

            if (startRecord) {
                count++;
                if (count == 300) {
                    String s = byte2HexStr(dataStore);
                    ReceiverData receiverdata = new ReceiverData(startDate, s);
                    try {
                        helper.getReceiverDao().create(receiverdata);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    @Override
    public void update(int i) {

    }

    @Override
    public void changeColor() {

    }

    @Override
    public void connectDevice() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
            final String[] devicesName = new String[pairedDevices.size()];
            final String[] devicesAddress = new String[pairedDevices.size()];
            int i = 0;
            for (BluetoothDevice device : pairedDevices) {
                devicesName[i] = device.getName() + "\n" + device.getAddress();
                devicesAddress[i] = device.getAddress();
                i++;
            }
            builder.setItems(devicesName, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(devicesAddress[i]);
                    try {
                        socket = device.createRfcommSocketToServiceRecord(UUID.fromString(myUUID));
                        socket.connect();
                        Toast.makeText(MyActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            builder.show();
        }
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

    public String byte2HexStr(byte[] b)
    {
        String stmp="";
        StringBuilder sb = new StringBuilder("");
        for (int n=0;n<b.length;n++)
        {
            stmp = Integer.toHexString(b[n] & 0xFF);
            sb.append((stmp.length()==1)? "0"+stmp : stmp);
            sb.append(" ");
        }
        return sb.toString().toUpperCase().trim();
    }
}
