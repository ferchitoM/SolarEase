package com.example.solarease;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DecimalFormat;
import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {

    private EditText    latitud, longitud, area;
    private SeekBar     inclinacion;
    private TextView    inclinacionTextView, resultado;
    private Button      calcular;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        latitud             = findViewById(R.id.latitud);
        longitud            = findViewById(R.id.longitud);
        area                = findViewById(R.id.area);
        inclinacion         = findViewById(R.id.inclinacion);
        inclinacionTextView = findViewById(R.id.inclinacion_textview);
        resultado           = findViewById(R.id.resultado);
        calcular            = findViewById(R.id.calcular);

        inclinacion.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                inclinacionTextView.setText("Inclinación paneles: " + progress + "º");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        calcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean error       = false; //No hay ningún error

                double _latitud     = 0;
                double _longitud    = 0;
                int _area           = 0;
                int _inclinacion    = inclinacion.getProgress();

                switch(_inclinacion){
                    case 0:
                        Toast.makeText(getApplicationContext(), "Los paneles tienen muy poca inclinación", Toast.LENGTH_LONG).show();
                        break;
                    case 90:
                        Toast.makeText(getApplicationContext(), "Los paneles tienen demasiada inclinación", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "Todo OK", Toast.LENGTH_LONG).show();
                        break;
                }

                if(validarCampo(latitud.getText().toString(), "Latitud"))
                    _latitud = Double.parseDouble(latitud.getText().toString());
                else error = true; //elCampoEstaVacio = true;

                if(validarCampo(longitud.getText().toString(), "Longitud"))
                    _longitud = Double.parseDouble(longitud.getText().toString());
                else error = true;

                if(validarCampo(area.getText().toString(), "Área", 10.0, 100.0))
                    _area = Integer.parseInt(area.getText().toString());
                else error = true;

                //Si no hay ningún error o campo vacío
                if(!error) { // if(!elCampoEstaVacio){
                    double produccionEnergia = calcularProduccionEnergia(_latitud, _longitud, _area, _inclinacion);

                    // DecimalFormat decimalFormat = new DecimalFormat("0.00");
                    // resultado.setText("Producción de energía: " + decimalFormat.format(produccionEnergia) + " Watios");
                    resultado.setText("Producción de energía: " + produccionEnergia + " kWh");
                }
                else resultado.setText("");

            }
        });
    }

    private boolean validarCampo(String dato, String nombreCampo){
        if(dato.isEmpty()){
            Toast.makeText(getApplicationContext(), nombreCampo + " debe tener un valor", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean validarCampo(String dato, String nombreCampo, double rangoInferior, double rangoSuperior){
        if(dato.isEmpty()){
            Toast.makeText(getApplicationContext(), nombreCampo + " debe tener un valor", Toast.LENGTH_LONG).show();
            return false;
        }
        //Menor que 10 o mayor que 100
        else if(Double.parseDouble(dato) < rangoInferior || Double.parseDouble(dato) > rangoSuperior){
            Toast.makeText(getApplicationContext(),
                    nombreCampo + " debe estar entre " + rangoInferior + " y " + rangoSuperior, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private double calcularProduccionEnergia(double latitud, double longitud, double area, int inclinacion){

        Log.e("msg", "Primera función calcularProduccionEnergia");

        double latitudRad       = Math.toRadians(latitud);
        double longitudRad      = Math.toRadians(longitud);
        double inclinacionRad   = Math.toRadians(inclinacion);

        int diaDelAnio          = LocalDate.now().getDayOfYear();

        double anguloIncidencia =
                Math.acos(
                        Math.sin(latitudRad) * Math.sin(inclinacionRad) +
                        Math.cos(latitudRad) * Math.cos(inclinacionRad) * Math.cos(longitudRad)
                );

        double constanteSolar   = 0.1367;
        double radiacion = constanteSolar *
                Math.cos(anguloIncidencia) *
                (1 + 0.033 * Math.cos(Math.toRadians(360 * diaDelAnio / 365.0)));

        double areaPanel        = area/10000;
        double eficienciaPanel  = 0.16;
        double factorPerdidas   = 0.9;
        double prodEnergia      = areaPanel * radiacion * eficienciaPanel * factorPerdidas;

        return prodEnergia;
    }

    private double calcularProduccionEnergia(double latitud, double longitud, int areaInt, int inclinacion){
        Log.e("msg", "Segunda función calcularProduccionEnergia");

        return calcularProduccionEnergia(latitud, longitud, (double) areaInt, inclinacion);
    }
}