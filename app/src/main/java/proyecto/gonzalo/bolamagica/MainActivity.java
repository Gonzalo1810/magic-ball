   package proyecto.gonzalo.bolamagica;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static java.net.HttpURLConnection.HTTP_OK;

   public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_JSON = "json"; //Variable para el archivo json

    //Variables para el sensor de agitado
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeListener mShakeListener;

       /**
        * Sobrecarga para eliminar el registro del Listener de agitado cuando está en pausa
        */
       @Override
       protected void onPause() {
           mSensorManager.unregisterListener(mShakeListener);
           super.onPause();
       }

       /**
        * Sobrecarga para registrar el Listener de agitado
        */
       @Override
       protected void onResume() {
           mSensorManager.registerListener(mShakeListener, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
           super.onResume();
       }

       /**
        * Creacion del activity
        * @param savedInstanceState
        */
       @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //boton
        Button button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startResultActivity();
            }
        });
        //Instancia de las variables de agitado del telefono
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeListener = new ShakeListener();
        mShakeListener.setOnShakeListener(new ShakeListener.OnShakeListener() {
            /**
             * Sobrecarga del metodo onShake que llama al metodo startResultActivity
             * @param count
             */
            @Override
            public void onShake(int count) {
                startResultActivity();
            }
        });
    }

       /**
        * Cuando todos los campos esten completos inicia AsyncServer, en caso contrario muestra un mensaje de error.
        */
    private void startResultActivity(){
        //Referencia de los Edit Text en variables
        String user = ( (EditText)findViewById(R.id.user)).getText().toString();
        String question = ( (EditText)findViewById(R.id.question)).getText().toString();
        //Comprobaion de errores
        if(user.length() ==0 || question.length() == 0){
            TextView error = findViewById(R.id.error);
            error.setVisibility(View.VISIBLE);
        }else{
            TextView error = findViewById(R.id.error);
            error.setVisibility(View.GONE);
            new AsyncServer().execute(user,question);
        }

    }

       /**
        * Si el objecto JSON ue recibe es nulo muestra un mensaje de error, en caso contrario inicia ResultActivity
        * @param json
        */
    private void receive(JSONObject json){

        if(json==null){
            TextView error = findViewById(R.id.error);
            error.setVisibility(View.VISIBLE);
            error.setText(R.string.no_connection);
        }
        else{
            //Creacion del nuevo intent y paso de parametros
            Intent intent = new Intent(this, ResultActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(EXTRA_JSON, json.toString());
            startActivity(intent);
        }
    }

       /**
        * Actividad asincrona que se ejecuta en segundo plano en el activity
        */
    private class AsyncServer extends AsyncTask<String,Void,JSONObject>{

        //Direccion URL del servidor
        private static final String SERVER_URL="http://10.20.2.93:8080/bola8/";


           /**
            * Sobrecarga del metodo onPostExecute que llama al metodo receive() pasandole por parametro el JSONObject recibido
            * @param jsonObject
            */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            receive(jsonObject);
            super.onPostExecute(jsonObject);
        }

           /**
            * Metodo que se ejecuta en segundo plano encargado de la conexion con el servidor.
            * @param strings Array de Strings referentes al JSON
            * @return
            */
        @Override
        protected JSONObject doInBackground(String... strings) {
            JSONObject request = JSONParser.generateMessage(strings[0],strings[1]);
            HttpURLConnection connection;

            try {
                //Inicializacion de la conexion
                connection = (HttpURLConnection) new URL(SERVER_URL).openConnection();
                connection.setRequestMethod("POST"); //Metodo de peticion
                connection.setConnectTimeout(3000); //Segundos x 1000 maximos de conexion
                connection.setReadTimeout(3000); //Segundos x 1000 maximos de lectura
                connection.setDoInput(true); //Posibilidad de recibir
                connection.setDoOutput(true); //Posibilidad de envio
                connection.setRequestProperty("Content-type", "application/json"); //Propiedad de peticion


                DataOutputStream writer = new DataOutputStream(connection.getOutputStream());
                writer.writeBytes(request.toString());

                int responseCode = connection.getResponseCode(); //Codigo de respuesta.
                Log.d(MainActivity.class.getCanonicalName(),"response-code= "+responseCode);
                Log.e(MainActivity.class.getCanonicalName(),"response-code= "+responseCode);

                //Comprueba que el codigo de respuesta es válido (200)
                if(responseCode!=HTTP_OK){
                    Log.e("responseCode: ", " !=HTTP_OK");
                    return null;
                }

                InputStream stream = connection.getInputStream();
                if(stream!=null){
                    //Lector de datos recibidos
                    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
                    StringBuilder builder = new StringBuilder();
                    String result;
                    while((result=reader.readLine())!=null){
                        builder.append(result);
                    }
                    stream.close(); //Cerrado del stream
                    connection.disconnect(); //Desconexion
                    return new JSONObject(builder.toString()); //Retorno del objeto JSON
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
