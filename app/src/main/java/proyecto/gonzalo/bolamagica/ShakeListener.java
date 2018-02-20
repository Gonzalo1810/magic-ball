package proyecto.gonzalo.bolamagica;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by gonza on 08/01/2018.
 */

/**
 * Clase que permite la captacion de agitacion del dispositivo
 */
public class ShakeListener implements SensorEventListener {

    private static final float SHAKE_THRESHOLD_GRAVITY = 2.7F; //Variable que representa la gravedad terrestre (1, pero funciona mejor con rangos mas altos)
    private static final int SHAKE_SLOP_TIME_MS = 500; //Tiempo de agitacion
    private static final int SHAKE_COUNT_RESET_TIME_MS = 3000; //Tiempo de reseteo

    private OnShakeListener mListener; //Referencia al Listener
    private long mShakeTimestamp; //Tiempo
    private int mShakeCount; //Cantidad

    /**
     * Setter del Listener
     * @param listener
     */
    public void setOnShakeListener(OnShakeListener listener) {
        this.mListener = listener;
    }

    /**
     * Interfaz de la clase
     */
    public interface OnShakeListener {
        void onShake(int count);
    }

    /**
     * Se ejecuta cuando detecta un cambio en la posicion del sensor del dispositivo
     * @param event
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (mListener != null) {
            //Movimiento de los tres ejes del espacio
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            //Division entre la gravedad
            float gX = x / SensorManager.GRAVITY_EARTH;
            float gY = y / SensorManager.GRAVITY_EARTH;
            float gZ = z / SensorManager.GRAVITY_EARTH;

            // Será 1 cuando no haya movimiento
            float gForce = (float) Math.sqrt(gX * gX + gY * gY + gZ * gZ);

            //Tiene que ser mayor a la referenciada
            if (gForce > SHAKE_THRESHOLD_GRAVITY) {
                final long now = System.currentTimeMillis();
                // ignora los eventos muy proximos
                if (mShakeTimestamp + SHAKE_SLOP_TIME_MS > now) {
                    return;
                }

                // resetea el agitado
                if (mShakeTimestamp + SHAKE_COUNT_RESET_TIME_MS < now) {
                    mShakeCount = 0;
                }

                mShakeTimestamp = now;
                mShakeCount++;

                mListener.onShake(mShakeCount);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //ignorar
    }
}
