package com.sys.velazquez.casanova.workshopasistance;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.sys.velazquez.casanova.workshopasistance.utils.Utils;

import static com.sys.velazquez.casanova.workshopasistance.utils.Const.CAT_MECANICO;
import static com.sys.velazquez.casanova.workshopasistance.utils.Const.CAT_OTRO;
import static com.sys.velazquez.casanova.workshopasistance.utils.Const.CAT_ELECTRICO;
import static com.sys.velazquez.casanova.workshopasistance.utils.Const.ID_CAT;

/**
 * Created by EverNote on 08/07/16.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    //private static Logger LOG = MyLogger.getLogger(MainActivity.class);

    Bundle bundle = new Bundle();
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //LOG.info("Inicia la actividad principal MainActivity");
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.app_name));

        //setSupportActionBar(toolbar);
    }

    public void searchCinema(View view) {
        bundle.putInt(ID_CAT, CAT_MECANICO);
        sendIntentPlace(bundle);
    }

    public void searchTheater(View view) {
        bundle.putInt(ID_CAT, CAT_ELECTRICO);
        sendIntentPlace(bundle);
    }

    public void searchRestaurant(View view) {
        bundle.putInt(ID_CAT, CAT_OTRO);
        sendIntentPlace(bundle);
    }

    public void sendIntentPlace(Bundle bundle) {
        //LOG.info(String.format("Se ha seleccionado busqueda por tipo: %d", bundle.toString()));
        try {
            if (Utils.isOnline(this)) {
                Utils.saveCat(bundle, getApplicationContext());
                Log.d(TAG, "sendIntentPlace: " + bundle.get(ID_CAT));
                Intent intent = new Intent(MainActivity.this, PlaceActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        } catch (Exception ex) {
            Log.e(TAG, ex.getMessage());
        }
    }


}
