package com.android2ee.formation.jassimile.tp.depart.mmxv.view;

import android.os.Bundle;
import androidx.appcompat.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.android2ee.formation.jassimile.tp.depart.mmxv.R;
import com.android2ee.formation.jassimile.tp.depart.mmxv.com.ForecastServiceUpdater;
import com.android2ee.formation.jassimile.tp.depart.mmxv.com.intf.ForecastCallBack;
import com.android2ee.formation.jassimile.tp.depart.mmxv.transverse.model.Forecast;

import java.util.List;


public class MainActivity extends ActionBarActivity implements ForecastCallBack{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new ForecastServiceUpdater().updateForecastFromServer(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Use this method to be callbacked with the forecasts
     *
     * @param forecasts
     */
    @Override
    public void forecastLoaded(List<Forecast> forecasts) {

    }
}
