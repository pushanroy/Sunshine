package app.com.sunshine.android.sunshine2;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements ForecastFragment.OnFragmentInteractionListener {

    private final String FORECASTFRAGMENT_TAG = "FFTAG";
    private final String LOG_TAG = MainActivity.class.getSimpleName();

    private String mLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLocation = Utility.getPreferredLocation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment(), FORECASTFRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        String location = Utility.getPreferredLocation(this);

        if(location != null && !location.equals(mLocation)){
            ForecastFragment ff = (ForecastFragment)getSupportFragmentManager().findFragmentByTag(FORECASTFRAGMENT_TAG);
            if(ff != null){
                ff.onLocationChanged();
            }
            mLocation = location;
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
        //TODO
    }
}
