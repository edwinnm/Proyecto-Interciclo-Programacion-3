package proyecto.ejemplo.mundo.hola.edwin.myapplication;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


/*
to handle the creation, rotation, and updating of registration tokens. This is required for sending to specific devices or for creating device groups.
*/
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {
    public MyFirebaseInstanceIDService() {
    }

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String token = FirebaseInstanceId.getInstance().getToken();

        ReportesFragment.registrarToken(token);
    }

}