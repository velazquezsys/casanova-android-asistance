package com.sys.velazquez.casanova.workshopasistance;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.sys.velazquez.casanova.workshopasistance.model.Place;
import com.sys.velazquez.casanova.workshopasistance.utils.Utils;
import com.sys.velazquez.casanova.workshopasistance.utils.Singleton;

/**
 * Created by edergomez on 10/11/16.
 */

public class PlaceViewer extends Activity{

    SubsamplingScaleImageView imageView;
    private Place place;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_viewer);

        Singleton sin=Singleton.getInstance();

        int ID = getIntent().getIntExtra("workshop_id", 0);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(Utils.getIconByCategory(getApplicationContext()));
        toolbar.setTitle(getIntent().getStringExtra("workshop_name"));

        imageView = (SubsamplingScaleImageView)findViewById(R.id.iv_image_workshop);
        imageView.setImage(ImageSource.bitmap(sin.getWorkshop_image()));
    }

}
