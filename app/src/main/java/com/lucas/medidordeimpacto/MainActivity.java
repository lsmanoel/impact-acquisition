package com.lucas.medidordeimpacto;

import com.example.lucassearamanoel.atividade_cadastrodecarros.SQLite_handler;

import android.Manifest;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //------------------------------------------------------------------------------------------
    //Variáveis do Acelerômetro - SensorMonager
    private SensorManager sensorManager;
    private boolean color = false;
    private TextView view;
    private long lastTime;

    private int size_buffer = 1024;

    //------------------------------------------------------------------------------------------
    //Acelerometro
    float instant_x_accel;
    float instant_y_accel;
    float instant_z_accel;

    float last_x_accel;
    float last_y_accel;
    float last_z_accel;

    float delta_x_accel;
    float delta_y_accel;
    float delta_z_accel;

    float acumulador_accel;  

    float media_accel;

    float n_accel;


    float samplePeriod_accel = 200//milisegundos
    float mediaMovel_size_accel = 50; //10 segundos: 200 ms ... 5 amostras a cada 1 segundo
    
    long lastTime_accel;
    //------------------------------------------------------------------------------------------
    //GPS
    string latitude_gps;
    string longitude_gps;

    int samplerModeOff = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //------------------------------------------------------------------------------------------
        //Instanciando SQLite
        SQLite_handler db = new SQLite_handler(this);// Extensao da classe SQLiteOpenHelper

        //db.deleteAllBook();

        //------------------------------------------------------------------------------------------
        //Inicialização do Acelerômetro
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        lastTime_accel = System.currentTimeMillis();

        //------------------------------------------------------------------------------------------
        //Inicialização do GPS

        this.checkPermissions();

        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //String provider  = LocationManager.NETWORK_PROVIDER;
        String provider  = LocationManager.GPS_PROVIDER;

        // Getting Current Location
        Location location = locationManager.getLastKnownLocation(provider);

        if(location!=null){
            onLocationChanged(location);
        }

        locationManager.requestLocationUpdates(provider, 1000, 0, this);
    }

    //----------------------------------------------------------------------------------------------
    //Métodos do GPS

    public void getLocation(Location location)
    {
        // Getting latitude of the current location
        double latitude = location.getLatitude();
        latitude_gps = Double.toString(latitude);

        // Getting longitude of the current location
        // Getting latitude of the current location
        double longitude = location.getLongitude();
        longitude_gps = Double.toString(longitude);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    public void checkPermissions(){
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP){
            int permissionCheck = this.checkSelfPermission("Manifest.permission.ACCESS_FINE_LOCATION");
            permissionCheck += this.checkSelfPermission("Manifest.permission.ACCESS_COARSE_LOCATION");
            if (permissionCheck != 0) {
                this.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1001); //Any number
            }
        }else{
            Log.d(getString(R.string.app_name), "checkBTPermissions: No need to check permissions. SDK version < LOLLIPOP.");

        }
    }

    //----------------------------------------------------------------------------------------------
    //Métodos do Acelerômetro
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {  //seleciona tipo de sensor a ser utilizado
            getAccelerometer(event);
        }

    }

    private void getAccelerometer(SensorEvent event) {
        //------------------------------------------------------------------------------------------
        //==========================================================================================
        long actualTime = System.currentTimeMillis();
        long deltaTime = actualTime - lastTime_accel;

        if (deltaTime < samplePeriod_accel) {
            return;
        }

        lastTime_accel = actualTime;
        //==========================================================================================
        //------------------------------------------------------------------------------------------

        float[] values = event.values;//Joga os valores no array
        // Movement
        instant_x_accel = values[0];
        instant_y_accel = values[1];
        instant_z_accel = values[2];

        // Saída diferencial: Sem nível DC
        delta_x_accel = instant_x_accel - last_x_accel;
        delta_y_accel = instant_x_accel - last_y_accel;
        delta_z_accel = instant_x_accel - last_z_accel;

        last_x_accel = instant_x_accel;
        last_y_accel = instant_x_accel;
        last_z_accel = instant_x_accel;


        float accelationSquareRoot = (delta_x_accel * delta_x_accel + delta_y_accel * delta_y_accel + delta_z_accel * delta_z_accel); 
        accelationSquareRoot = accelationSquareRoot/(SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);

        acumulador_accel = acumulador_accel +  accelationSquareRoot;

        n_accel = n_accel + 1;

        if(n_accel > mediaMovel_size){
            media_accel = acumulador_accel/mediaMovel_size_accel;

            getLocation();//Define latitude_gps e latitude_gps

            SQLite_handler db = new SQLite_handler(this);
            db.addBook(new Book("lat:" + latitude_gps + "lon:" + longitude_gps, media_accel));

            n_accel=0;               
        }
        
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        // registrando o listener
        sensorManager.registerListener((SensorEventListener) this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    //------------------------------------------------------------------------------------------
    //Métodos do Banco de Dados
    public void ExibirBancoDados_button(View view) {
        // Instância do elemento de opção.
        // Cria o intent indicando qual a opção foi usada para salvar os dados.
        Intent output_intent = new Intent(this, dataPrinter_Activity.class);

        // get all books
        SQLite_handler db = new SQLite_handler(this);
        List<Book> caminhoMedido = db.getAllBooks();
        Log.d("size_List_1", "==========================" + Integer.toString(caminhoMedido.size()));
        Bundle output_bundle = new Bundle();

        int i;
        for(i=0; i<caminhoMedido.size(); i++){
            output_bundle.putString("Km" + Integer.toString(i), caminhoMedido.get(i).get_modeloCarro());
            output_bundle.putString("aceleracao:"  + Integer.toString(i), caminhoMedido.get(i).get_valorCarro());
        }

        output_bundle.putInt("size_List", i);
        Log.d("size_List_2", "==========================" + Integer.toString(i));
        output_intent.putExtras(output_bundle);

        //------------------------------------------------------------------------------------------
        // Inicia a nova tela.
        startActivity(output_intent);
    }
}
