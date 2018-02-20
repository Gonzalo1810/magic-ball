package proyecto.gonzalo.bolamagica;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by gonza on 28/12/2017.
 */

/**
 * Clase estatica para la comunicacion mediante archivos JSON
 */
public class JSONParser {

    //Variables necesarias para los nodos del archivo JSON
    private static final String TYPE_LABEL="type";
    private static final String TYPE_VALUE_REQUEST="request";
    private static final String QUESTION_LABEL="question";
    private static final String MESSAGE_LABEL="message";
    private static final String USER_NAME_LABEL="user_name";
    private static final String TYPE_VALUE_ERROR="response_error";
    private static final String ERROR_LABEL="error_text";
    private static final String TIME="time";
    private static final String ANSWER_LABEL="answer";

    /**
     * Genera el mensaje a enviar all servidor
     * @param user Usuario que esta realizando la peticion
     * @param question Pregunta del usuario
     * @return devuelve un objeto JSON
     */
    static JSONObject generateMessage(String user, String question){
        JSONObject root = new JSONObject(); //Objeto principal
        JSONObject message = new JSONObject(); //Subobjecto
        try {
            //Dentro de "message" incluimos el tipo de meticion, el usuario, la pregunta
            message.put(TYPE_LABEL,TYPE_VALUE_REQUEST);
            message.put(USER_NAME_LABEL, user);
            message.put(QUESTION_LABEL, question);
            //Incluimos al final el mensaje dentro del objeto principal
            root.put(MESSAGE_LABEL, message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return root;
    }
    /**
     * Comprueba si el objeto JSON recibido contiene un error
     * @param json
     * @return devuelve true en caso de error
     */
    static boolean isErrorResponse(JSONObject json){
        try {
            JSONObject message = json.getJSONObject(MESSAGE_LABEL);
            String type = message.getString(TYPE_LABEL);
            if(type.equals(TYPE_VALUE_ERROR)){
                return true;
            }else
                return false;
        } catch (JSONException e) {
            e.printStackTrace();
            return true;
        }

    }

    /**
     * Obtiene la respuesta del servidor que se encuentra dentro del objeto JSON recibido
     * @param json
     * @return
     */
    static String getAnswer(JSONObject json){
        String response = "";
        try {
            JSONObject message = json.getJSONObject(MESSAGE_LABEL);
            response =  message.getString(ANSWER_LABEL);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Obtiene el tiempo que se encuentra dentro del objeto JSON recibido
     * @param json
     * @return
     */
    static String getTime(JSONObject json){
        String time = "";
        try {
            JSONObject message = json.getJSONObject(MESSAGE_LABEL);
            time = message.getString(TIME);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return time;
    }

    /**
     * Obtiene el usuario que se encuentra dentro del objeto JSON recibido
     * @param json
     * @return
     */
    static String getUser(JSONObject json){
        String user = "";
        try {
            JSONObject message = json.getJSONObject(MESSAGE_LABEL);
            user = message.getString(USER_NAME_LABEL);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return user;
    }

    /**
     * Obtiene el mensaje de error dentro del objeto JSON recibido
     * @param json
     * @return
     */
    static String getErrorMessage(JSONObject json){
        try {
            JSONObject message = json.getJSONObject(MESSAGE_LABEL);
            String error= message.getString(ERROR_LABEL);
            return error;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

    }
}
