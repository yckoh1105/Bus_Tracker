package com.example.dellpc.bus_tracker;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> ArraySpinner = new ArrayList<>();
    private ArrayList<Route> RouteList = new ArrayList<>();
    private Spinner route_spinner;
    private ListView l;
    private MqttAndroidClient client;
    private String trackingTopicIndicator = "";
    private ArrayList<RealTimeBus> busOnRouteList = new ArrayList<>();
    private int routeID;
    private ProgressDialog progressBar;
    private ArrayList<String> aaa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        route_spinner = (Spinner) findViewById(R.id.route_spinner);
        //loadSpinner();
        l = (ListView) findViewById(R.id.busList);
        firstConnectMqtt();
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                reconnectMqtt();
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if (topic.contains(trackingTopicIndicator)) {
                    String strPayload = new String(message.getPayload());
                    Log.d("Message Arrived", strPayload);
                    if (strPayload.contains(Constant.MQTT_START_CMD)) {
                        String[] arrPayload = strPayload.split("\\*" + "\\|" + "\\+");
                        double lat = Double.parseDouble(arrPayload[1]);
                        double lon = Double.parseDouble(arrPayload[2]);
                        String status = arrPayload[3];
                        int routeID = Integer.parseInt(arrPayload[4]);
                        String busPlateNum = arrPayload[5];
                        String traf_dateTime = arrPayload[6];
                        int orderNum = Integer.parseInt(arrPayload[7]);
                        double speed = Double.parseDouble(arrPayload[8]);

                        if(busPlateNum.length()==0){
                            return;
                        }

                        RealTimeBus rtTraffic = new RealTimeBus(routeID, busPlateNum, lat, lon, orderNum, traf_dateTime, status, speed);

                        if (busOnRouteList.size() == 0) {
                            busOnRouteList.add(rtTraffic);
                        } else {
                            boolean haveBus = false;
                            for (int i = 0; i < busOnRouteList.size(); i++) {
                                if (busOnRouteList.get(i).getBusPlateNum().equals(rtTraffic.getBusPlateNum())) {
                                    busOnRouteList.set(i, rtTraffic);
                                    haveBus = true;
                                }
                            }

                            if (!haveBus) {
                                busOnRouteList.add(rtTraffic);
                            }
                        }
                    } else if (strPayload.contains(Constant.MQTT_STOP_CMD)) {
                        String[] arrPayload = strPayload.split("\\*" + "\\|" + "\\+");
                        String busplate = arrPayload[1];

                        for (int i = 0; i < busOnRouteList.size(); i++) {
                            if (busOnRouteList.get(i).getBusPlateNum().equals(busplate)) {
                                busOnRouteList.remove(i);
                            }
                        }
                    }
                    listBus();
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.unregisterResources();
        client.close();
    }

    @Override
    protected void onStart() {
        super.onStart();
        progressBar = new ProgressDialog(this);
        progressBar.setCancelable(false);
        if(isPortOpen()) {
            progressBar.setMessage("Loading Route...");
            loadSpinner();
        }else{
            progressBar.setMessage("Waiting Connection...");
        }
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        if(!progressBar.isShowing()){
            progressBar.show();
        }
        Thread mthread = new Thread(){
            @Override
            public void run() {
                while(!isPortOpen()){
                    if(isPortOpen()){
                        loadSpinner();
                        if(progressBar.isShowing()){
                            progressBar.dismiss();
                        }
                    }
                }
                while(isPortOpen()){
                    if(progressBar.isShowing()){
                        progressBar.dismiss();
                    }
                }
            }
        };
        mthread.start();
    }

    public void loadSpinner(){
        String phpUrl = Constant.UrlGetRoute;
        ArraySpinner.clear();
        com.android.volley.RequestQueue queue = Volley.newRequestQueue(this);
        try {
            StringRequest postRequest = new StringRequest(
                    Request.Method.POST,
                    phpUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray locations = jsonObject.getJSONArray("route");

                                for (int i = 0; i < locations.length(); i++) {
                                    JSONObject c = locations.getJSONObject(i);
                                    int routeId = c.getInt("route_id");
                                    String route_name = c.getString("name");


                                    if (!route_name.equals("Outstation")) {
                                        Route r = new Route(routeId,route_name);
                                        RouteList.add(r);
                                        ArraySpinner.add(route_name);
                                    }
                                }
                                initializeSpinner();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }) {
            };
            int socketTimeout = 10000;//30 seconds - change to what you want
            RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
            postRequest.setRetryPolicy(policy);
            queue.add(postRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeSpinner(){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ArraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        route_spinner.setAdapter(adapter);
        route_spinner.setSelection(0);
        routeID = RouteList.get(0).getRouteId();

        route_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    unsubscribeTrackingTopic(Integer.toString(routeID));
                    routeID = RouteList.get(i).getRouteId();
                    subscribeTrackingTopic(Integer.toString(routeID));
                    listBus();
                }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void firstConnectMqtt() {
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(this, Constant.MQTT_HOST, clientId);
        reconnectMqtt();
    }

    private void reconnectMqtt() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(Constant.MQTT_USERNAME);
        options.setPassword(Constant.MQTT_PASSWORD.toCharArray());
        options.setKeepAliveInterval(5000);

        try {
            IMqttToken token = client.connect(options);
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Log.d("MQTT connection", "Connect Successful");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    reconnectMqtt();
                    Log.d("MQTT connection", "Connect Failed");
                }

            });
        } catch (MqttException e) {
            e.printStackTrace();
            Log.println(Log.ERROR,"err","sss");
        }
    }

    private void subscribeTrackingTopic(String rtID) {
        final String topicStr = Constant.MQTT_TRACKING_TOPIC_PREFIX + rtID + "/#";
        try {
            client.subscribe(topicStr, Constant.MQTT_TRACKING_TOPIC_ORDER);
            trackingTopicIndicator = topicStr.substring(0, topicStr.length() - 1);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void unsubscribeTrackingTopic(String rtID) {
        final String topicStr = Constant.MQTT_TRACKING_TOPIC_PREFIX + rtID + "/#";
        try {
            client.unsubscribe(topicStr);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void listBus(){
        //BusAdapter listAdapter = new BusAdapter(this,R.layout.item_bus_list,busOnRouteList);
        BusAdapter listAdapter = new BusAdapter();
        l.setAdapter(listAdapter);
    }

    class BusAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return busOnRouteList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(busOnRouteList.size()!=0) {
                convertView = getLayoutInflater().inflate(R.layout.item_bus_list, null);
                TextView txtSpeed = (TextView) convertView.findViewById(R.id.tvSpeed);
                TextView txtPlateNumber = (TextView) convertView.findViewById(R.id.tvPlateNumber);
                txtPlateNumber.setText(busOnRouteList.get(position).getBusPlateNum());
                txtSpeed.setText(String.format("%.2f",busOnRouteList.get(position).getSpeed()*3.6));
            }
            return convertView;
        }
    }

    public static boolean isPortOpen() {
        //Check Server Status, url or ip address with port number can't be "ping" using ping command
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            URI WebSocketUri = new URI(Constant.URL);
            InetAddress serverAddress = InetAddress.getByName(WebSocketUri.getHost());
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(serverAddress, Constant.ServerPort),10*1000);
            socket.close();
            return true;
        }catch(ConnectException ce){
            ce.printStackTrace();
            return false;
        }catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
}
