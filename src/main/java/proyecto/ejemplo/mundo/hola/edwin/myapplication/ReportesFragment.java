package proyecto.ejemplo.mundo.hola.edwin.myapplication;



import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ReportesFragment extends Fragment implements View.OnClickListener {
    Spinner spinnerOpciones;
    String[] opcionesPositivas = {"Juegos Pirotecnicos", "Concierto", "Obra teatral", "Festival de comida", "Danzas"};
    String[] opcionesNegativas = {"Asalto/Robo", "Accidente automovilistico", "Incendio", "Alteracion del orden publico"};
    RadioGroup radioGroup;
    EditText textoUbicacion;
    Button botonGps, botonIngresar;
    String motivo= null, localizacion=null;
    String estadoGps="Activado";
    double lat = 0.0, lng = 0.0;

    String token;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Utilizo un View porque es un fragment.
        View v = inflater.inflate(R.layout.fragment_reportes, container, false);

        //Inicializo los elementos de la interfaz

        textoUbicacion = (EditText) v.findViewById(R.id.etLocalizacion);
        botonGps = (Button) v.findViewById(R.id.buttonGps);
        botonIngresar = (Button) v.findViewById(R.id.buttonManual);
        radioGroup = (RadioGroup) v.findViewById(R.id.rGPosNeg);
        radioGroup.clearCheck();
        spinnerOpciones = (Spinner) v.findViewById(R.id.Spinner1);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.opcPos:
                        // De la misma manera utilizo getActivity() porque es un fragment.
                        ArrayAdapter<String> adaptador = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, opcionesPositivas);
                        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerOpciones.setAdapter(adaptador);

                        break;
                    case R.id.opcNeg:
                        // De la misma manera utilizo getActivity() porque es un fragment.
                        ArrayAdapter<String> adaptador2 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, opcionesNegativas);
                        adaptador2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerOpciones.setAdapter(adaptador2);
                        break;
                }
            }
        });
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        token = FirebaseInstanceId.getInstance().getToken();
        Log.d("token", token);

        botonGps.setOnClickListener(this);
        botonIngresar.setOnClickListener(this);



        return v;
    }


    @Override
    public void onClick(View v) {
    switch(v.getId()){
        case R.id.buttonManual:
            localizacion = textoUbicacion.getText().toString().replace(' ','+');
            if (!localizacion.equals("")) {
                motivo = spinnerOpciones.getSelectedItem().toString().replace(' ','+');
                if(motivo!=null){
                    registrarToken(token);
                    new CargarDatos().execute("https://programacion3proyecto.000webhostapp.com/convertidor.php?localizacion=" + localizacion + "&motivo=" + motivo);
                }else{
                    Toast.makeText(getContext(), "Suceso no seleccionado", Toast.LENGTH_LONG).show();
                }

            } else
                Toast.makeText(getContext(), "Falta llenar el campo de localización manual", Toast.LENGTH_LONG).show();
            break;

        case R.id.buttonGps:
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

            if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            }else{

                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                ObtenerUbicacion(location);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0,locationListener);
            }

             break;
    }
    }

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE");
                    break;
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            estadoGps="Activado";
            Toast.makeText(getContext(), "Gps Activado", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onProviderDisabled(String provider) {
            estadoGps="Desactivado";
            Toast.makeText(getContext(), "Gps Desactivado", Toast.LENGTH_SHORT).show();

        }
    };
    private void ObtenerUbicacion(Location location) {
        motivo = spinnerOpciones.getSelectedItem().toString().replace(' ', '+');
        if(motivo != null){
            if (location != null) {
                lat = location.getLatitude();
                lng = location.getLongitude();

            }else{
                Toast.makeText(getContext(), "No se puede obtener la ubicación actual, Asegurese de que el GPS está activado", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(getContext(), "Suceso no seleccionado.", Toast.LENGTH_LONG).show();
        }
        registrarToken(token);
        new CargarDatos().execute("https://programacion3proyecto.000webhostapp.com/registro.php?localizacion=Gps&lat=" + lat + "&lng=" + lng + "&motivo=" +motivo);
    }


    private class CargarDatos extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if (estadoGps.equals("Activado")){
                Toast.makeText(getContext(), "Se almacenaron los datos correctamente", Toast.LENGTH_LONG).show();
            }

        }
    }

    private String downloadUrl(String myurl) throws IOException {
        Log.i("URL",""+myurl);
        myurl.replace(" ", "%20");
        InputStream is = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d("respuesta", "The response is: " + response);
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public String readIt(InputStream stream, int len) throws IOException {
        Reader reader;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }
    public static void registrarToken(String token) {
        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("token", token)
                .build();

        Request request = new Request.Builder()
                .url("https://programacion3proyecto.000webhostapp.com/registrartokens.php")
                .post(body)
                .build();

        Request request1 = new Request.Builder()
                .url("https://programacion3proyecto.000webhostapp.com/enviarpush.php")
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("OkHttp", call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.d("OkHttp", responseStr);
                }
            }
        });
        client.newCall(request1).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("OkHttp", call.toString());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.d("OkHttp", responseStr);
                }
            }
        });

    }
}

