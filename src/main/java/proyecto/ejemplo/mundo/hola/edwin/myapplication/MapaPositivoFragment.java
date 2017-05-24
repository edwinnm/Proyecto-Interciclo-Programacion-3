package proyecto.ejemplo.mundo.hola.edwin.myapplication;


import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MapaPositivoFragment extends Fragment implements OnMapReadyCallback{
    GoogleMap mGoogleMap;
    MapView mMapView;
    View mView;
    double latitudes[];
    double longitudes[];
    String eventos[];
    Marker marcador;
    Circle circle;
    String[] opcionesPositivas = {"Juegos Pirotecnicos", "Concierto", "Obra teatral", "Festival de comida", "Danzas"};


    public MapaPositivoFragment() {
        // Required empty public constructor

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.mapa_positivo_fragment, container, false);
        return  mView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mMapView = (MapView) mView.findViewById(R.id.mapPos);
        if(mMapView != null){
            mMapView.onCreate(null);
            mMapView.onResume();
            mMapView.getMapAsync(this);

            final Handler handler = new Handler();
            Timer timer = new Timer();
            ///////////////////////////////////////////////

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            public void run() {
                                try {
                                    //Ejecuta tu AsyncTask!
                                    new ConsultarDatos().execute("https://programacion3proyecto.000webhostapp.com/obtenertodo.php");

                                } catch (Exception e) {

                                }
                            }
                        });
                    }
                };
                timer.schedule(task, 0, 10000);

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MapsInitializer.initialize(getContext());
        mGoogleMap= googleMap;
        UiSettings settings = mGoogleMap.getUiSettings();
        settings.setRotateGesturesEnabled(false);
        settings.setScrollGesturesEnabled(true);
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        LatLng cuenca = new LatLng(-2.896,-79.0067);
        CameraPosition cameraPosition = CameraPosition.builder().target(cuenca).zoom(13).bearing(0).tilt(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    public class ConsultarDatos extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            String cadena = urls[0];
            URL url;
            String devuelve="";
            try {
                url = new URL(cadena);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                StringBuilder result = new StringBuilder();


                if (response == HttpURLConnection.HTTP_OK) {
                    InputStream is = new BufferedInputStream(conn.getInputStream());

                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }

                    JSONObject respuestaJSON = new JSONObject(result.toString());

                    String resultJSON = respuestaJSON.getString("estado");


                    if (resultJSON.equals("1")) {
                        JSONArray coordenadasJSON = respuestaJSON.getJSONArray("datos");
                        latitudes = new double[coordenadasJSON.length()];
                        longitudes = new double[coordenadasJSON.length()];
                        eventos = new String[coordenadasJSON.length()];

                        for (int i = 0; i < coordenadasJSON.length(); i++) {

                            /*devuelve = devuelve + coordenadasJSON.getJSONObject(i).getString("lat")+ " "+
                                    coordenadasJSON.getJSONObject(i).getString("lng")+" "+
                                    coordenadasJSON.getJSONObject(i).getString("motivo")+" ";*/

                            latitudes[i] = coordenadasJSON.getJSONObject(i).getDouble("lat");
                            longitudes[i] = coordenadasJSON.getJSONObject(i).getDouble("lng");
                            eventos[i] = coordenadasJSON.getJSONObject(i).getString("motivo");
                            devuelve = "Datos Cargados";

                        }
                    } else if (resultJSON.equals("2")) {
                        return "No hay datos";
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return devuelve;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute (String result){
            //Toast.makeText(getContext(), result , Toast.LENGTH_LONG).show();
            if (!result.equals("No hay datos")){
                for (int i = 0; i < latitudes.length; i++){
                    if(consta(eventos[i], opcionesPositivas))
                        anadirMarcador(latitudes[i], longitudes[i],eventos[i]);

                }
            }

        }
    }

    private boolean consta(String cadena, String [] arreglo){

        for (int i=0 ; i<arreglo.length; i++){
            if(cadena.equals(arreglo[i])){
                return true;
            }
        }
        return false;
    }

    private void anadirMarcador(double lat, double lng, String evento){
        LatLng coordenadas = new LatLng(lat, lng);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(coordenadas);
        markerOptions.title(evento);

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(coordenadas);
        circleOptions.radius(200);

        switch (evento){
            case "Juegos Pirotecnicos":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.fuegosartificiales));
                circleOptions.strokeColor(Color.parseColor("#FE000D"));
                circleOptions.fillColor(Color.argb(20, 254, 0, 13));
                break;
            case "Concierto":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.concierto));
                circleOptions.strokeColor(Color.parseColor("#63E0EB"));
                circleOptions.fillColor(Color.argb(20, 99, 224, 235));
                break;
            case "Obra teatral":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.obrateatral));
                circleOptions.strokeColor(Color.parseColor("#FE0098"));
                circleOptions.fillColor(Color.argb(20, 254, 0, 152));
                break;
            case "Festival de comida":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.feriadecomida));
                circleOptions.strokeColor(Color.parseColor("#FEED00"));
                circleOptions.fillColor(Color.argb(20, 254, 237, 0));
                break;
            case "Danzas":
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.danzas));
                circleOptions.strokeColor(Color.parseColor("#FCD112"));
                circleOptions.fillColor(Color.argb(20, 252, 209, 18));
                break;
        }
        circleOptions.strokeWidth(4);

        circle = mGoogleMap.addCircle(circleOptions);
        marcador = mGoogleMap.addMarker(markerOptions);
    }
}



