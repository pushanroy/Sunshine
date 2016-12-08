package app.com.sunshine.android.sunshine2;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ForecastFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ForecastFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private ArrayAdapter<String> mForecastAdapter;

    public ForecastFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.action_refresh){
            FetchWeatherTask weatherTask = new FetchWeatherTask();
            weatherTask.execute("94043");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        String[] data = { "Mon 6/23 - Sunny - 31/17",
                "Tue 6/24 - Foggy - 21/8",
                "Wed 6/25 - Cloudy - 22/17",
                "Thurs 6/26 - Rainy - 18/11",
                "Fri 6/27 - Foggy - 21/10",
                "Sat 6/28 - TRAPPED IN WEATHERSTATION - 23/18",
                "Sun 6/29 - Sunny - 20/7"};

        List<String> weekForecast = new ArrayList<String>(Arrays.asList(data));
        mForecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                weekForecast
        );
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ListView listView = (ListView)rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(mForecastAdapter);


        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, Void>{

        private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

        private String getReadableDateString(int time){
            SimpleDateFormat sdf = new SimpleDateFormat("EE MM DD");
            return sdf.format(time);
        }

        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays) throws JSONException{

            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DEXCRIPTION = "main";

            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            Calendar calendar = new GregorianCalendar();

            String[] resultStrs = new String[numDays];
            for(int i=0; i < weatherArray.length(); i++){
                String day;
                String description;
                String highAndLow;

                calendar.add(Calendar.DATE, i);
                day = getReadableDateString(calendar.get(Calendar.DATE));
            }

            return resultStrs;

        }

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String forecastJsonStr = null;
            String format = "json";
            String units = "metric";
            String numDays = "7";

            try{
                final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String QUERY_PARAM = "q";
                final String FORMAT_PARAM = "mode";
                final String UNITS_PARAM = "units";
                final String DAYS_PARAM = "cnt";
                final String APPID_PARAM = "APPID";
                /*String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
                String apiKey = "&appid=" + "afb2e5338cb3fd3724b48844326b8537";*/

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(QUERY_PARAM, params[0])
                        .appendQueryParameter(FORMAT_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, units)
                        .appendQueryParameter(DAYS_PARAM,numDays)
                        .appendQueryParameter(APPID_PARAM, "afb2e5338cb3fd3724b48844326b8537")
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URL: "+builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                if(inputStream == null){
                    return null;
                }

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while((line  = reader.readLine()) != null){
                    buffer.append(line+"\n");
                }

                if(buffer == null){
                    return null;
                }

                forecastJsonStr = buffer.toString();
                //forecastJsonStr = new String("{\"city\":{\"id\":1257325,\"name\":\"Santoshpur\",\"coord\":{\"lon\":88.264999,\"lat\":22.52528},\"country\":\"IN\",\"population\":0},\"cod\":\"200\",\"message\":0.0205,\"cnt\":7,\"list\":[{\"dt\":1481176800,\"temp\":{\"day\":30.47,\"min\":14.15,\"max\":30.47,\"night\":14.15,\"eve\":23.69,\"morn\":26},\"pressure\":1026.43,\"humidity\":70,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":3.86,\"deg\":358,\"clouds\":0},{\"dt\":1481263200,\"temp\":{\"day\":25.36,\"min\":12.84,\"max\":26.1,\"night\":13.7,\"eve\":20.84,\"morn\":12.84},\"pressure\":1024.04,\"humidity\":69,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":4.37,\"deg\":3,\"clouds\":0},{\"dt\":1481349600,\"temp\":{\"day\":24.68,\"min\":13.66,\"max\":25.54,\"night\":13.72,\"eve\":20.52,\"morn\":13.66},\"pressure\":1024.13,\"humidity\":67,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":4.8,\"deg\":4,\"clouds\":0},{\"dt\":1481436000,\"temp\":{\"day\":25.38,\"min\":13.86,\"max\":25.38,\"night\":19.13,\"eve\":23,\"morn\":13.86},\"pressure\":1022.92,\"humidity\":0,\"weather\":[{\"id\":802,\"main\":\"Clouds\",\"description\":\"scattered clouds\",\"icon\":\"03d\"}],\"speed\":2.14,\"deg\":354,\"clouds\":34},{\"dt\":1481522400,\"temp\":{\"day\":25.34,\"min\":18.77,\"max\":25.34,\"night\":19.98,\"eve\":24.16,\"morn\":18.77},\"pressure\":1022.22,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":2.08,\"deg\":19,\"clouds\":35},{\"dt\":1481608800,\"temp\":{\"day\":26.25,\"min\":18.13,\"max\":26.25,\"night\":19.07,\"eve\":24.05,\"morn\":18.13},\"pressure\":1021.5,\"humidity\":0,\"weather\":[{\"id\":500,\"main\":\"Rain\",\"description\":\"light rain\",\"icon\":\"10d\"}],\"speed\":1.17,\"deg\":67,\"clouds\":21,\"rain\":0.63},{\"dt\":1481695200,\"temp\":{\"day\":26.59,\"min\":16.63,\"max\":26.59,\"night\":17.55,\"eve\":23.33,\"morn\":16.63},\"pressure\":1023.42,\"humidity\":0,\"weather\":[{\"id\":800,\"main\":\"Clear\",\"description\":\"clear sky\",\"icon\":\"01d\"}],\"speed\":2.48,\"deg\":23,\"clouds\":1}]}");
                Log.v(LOG_TAG, "Date Received: "+forecastJsonStr);

            }catch (IOException e){
                Log.e("MainFragment", "Error", e);
                return null;
            }finally {
                if(urlConnection != null){
                    urlConnection.disconnect();
                }

                if(reader != null){
                    try{
                        reader.close();
                    }catch (IOException e){
                        Log.e("PlaceholderFragment", "Error closing stream", e);
                    }
                }

            }

            return  null;
        }
    }
}
