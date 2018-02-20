package proyecto.gonzalo.bolamagica;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultActivity extends Activity {

    /**
     * Muestra la nueva actividad con los datos del JSON
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        String json = getIntent().getStringExtra(MainActivity.EXTRA_JSON); //Recibe el nombre del JSON

        try {
            //Comprobacion de error en el archivo y visualizacion del error
            if(JSONParser.isErrorResponse(new JSONObject(json))){
                TextView textView = findViewById(R.id.error_result);
                textView.setText(JSONParser.getErrorMessage(new JSONObject(json)));
                textView.setVisibility(View.VISIBLE);
            }else{
                //Mostrado de la respuesta por pantalla asi como el tiempo y el usuario
                TextView user = findViewById(R.id.usuario);
                user.setText(JSONParser.getUser(new JSONObject(json)));
                TextView time = findViewById(R.id.tiempo);
                time.setText(JSONParser.getTime(new JSONObject(json)));
                TextView answer = findViewById(R.id.respuesta);
                answer.setText(JSONParser.getAnswer(new JSONObject(json)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
