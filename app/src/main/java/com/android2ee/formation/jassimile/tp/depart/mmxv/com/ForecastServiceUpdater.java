/**
 * <ul>
 * <li>ForecastRestYahooSax</li>
 * <li>com.android2ee.formation.restservice.sax.forecastyahoo.service</li>
 * <li>22 nov. 2013</li>
 * <p/>
 * <li>======================================================</li>
 * <p/>
 * <li>Projet : Mathias Seguy Project</li>
 * <li>Produit par MSE.</li>
 * <p/>
 * /**
 * <ul>
 * Android Tutorial, An <strong>Android2EE</strong>'s project.</br>
 * Produced by <strong>Dr. Mathias SEGUY</strong>.</br>
 * Delivered by <strong>http://android2ee.com/</strong></br>
 * Belongs to <strong>Mathias Seguy</strong></br>
 * ***************************************************************************************************************</br>
 * This code is free for any usage but can't be distribute.</br>
 * The distribution is reserved to the site <strong>http://android2ee.com</strong>.</br>
 * The intelectual property belongs to <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * <p/>
 * *****************************************************************************************************************</br>
 * Ce code est libre de toute utilisation mais n'est pas distribuable.</br>
 * Sa distribution est reservée au site <strong>http://android2ee.com</strong>.</br>
 * Sa propriété intellectuelle appartient à <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * *****************************************************************************************************************</br>
 */
package com.android2ee.formation.jassimile.tp.depart.mmxv.com;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.android2ee.formation.jassimile.tp.depart.mmxv.MyApplication;
import com.android2ee.formation.jassimile.tp.depart.mmxv.com.intf.ForecastCallBack;
import com.android2ee.formation.jassimile.tp.depart.mmxv.com.intf.ForecastPictureCallBack;
import com.android2ee.formation.jassimile.tp.depart.mmxv.transverse.model.Forecast;
import com.android2ee.formation.jassimile.tp.depart.mmxv.transverse.parser.ForcastSaxHandler;

import org.apache.http.client.HttpClient;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * @author Mathias Seguy (Android2EE)
 * @goals This class aims to retrieve YahooForecast from the internet and then save them in DB
 */
public class ForecastServiceUpdater {

    /******************************************************************************************/
    /** Attributes **************************************************************************/
    /******************************************************************************************/

    /**
     * The logCat's tag
     */
    private final String tag = "ForecastServiceUpdater";
    /**
     * The url to use
     */
    private String url;
    /**
     * The object used to communicate with http
     */
    private HttpClient client;
    /**
     * The raw xml answer
     */
    private String responseBody;
    /**
     * The forecasts to display
     */
    private List<Forecast> forecasts;
    /**
     * The callBack to update activity
     * Use WeakReference to avoid memory leaks
     */
    private WeakReference<ForecastCallBack> weakCallBack = null;


    /******************************************************************************************/
    /** Public method **************************************************************************/
    /******************************************************************************************/
    /**
     * The runnable to execute when requesting update from the server
     */
    private RestCallRunnable restCallRunnable = new RestCallRunnable();
    /**
     * The handler awoke when the Runnable has finished it's execution
     */
    private Handler restCallHandler = new Handler() {
        /* (non-Javadoc)
         * @see android.os.Handler#handleMessage(android.os.Message)
         */
        @Override
        public void handleMessage(Message msg) {
            returnForecast();
        }

    };
    private List<ForecastPictureCallBack> pictureCallBack = null;
    /******************************************************************************************/
    /** Loading Forecast **************************************************************************/
    /******************************************************************************************/

    /**
     * Return the forecast
     *
     * @param callback The callback to use to deliver the data when data updated
     */
    public void updateForecastFromServer(ForecastCallBack callback) {
        Log.e(tag, "updateForecastFromServer called");
        weakCallBack = new WeakReference<ForecastCallBack>(callback);

        // retrieve the url
        url = "http://weather.yahooapis.com/forecastrss?w=628886&u=c";
        //then link the Handler with the handler of the runnable
        if (restCallRunnable.restCallHandler == null) {
            restCallRunnable.restCallHandler = restCallHandler;
        }
        new Thread(restCallRunnable).start();
    }

    public void downloadPicture(Forecast forecast, ForecastPictureCallBack pictureCallBack, String pictureCode) {
        //do a new AsyncTask, do not reuse because it can be called n times at the same time
        new DownloadPictureAsync(forecast, pictureCallBack).execute(pictureCode);
    }

    /**
     * Called when the forecast are built
     * Return that list to the calling Activity using the ForecastCallBack
     */
    private void returnForecast() {

        if (weakCallBack.get() != null) {
            weakCallBack.get().forecastLoaded(forecasts);
        }
    }

    private class DownloadPictureAsync extends AsyncTask<String, Void, Bitmap> {
        /**
         * The callBack to update activity
         * Use WeakReference to avoid memory leaks
         */
        private WeakReference<ForecastPictureCallBack> weakPictureCallBack;
        private Forecast forecast;
        /**
         * The url to use
         */
        private String pictureUrl;

        private DownloadPictureAsync(Forecast forecast, ForecastPictureCallBack pictureCallBack) {
            weakPictureCallBack = new WeakReference<ForecastPictureCallBack>(pictureCallBack);
            this.forecast = forecast;
        }

        @Override
        protected Bitmap doInBackground(String... params) {
            pictureUrl = "http://l.yimg.com/a/i/us/we/52/" + params[0] + ".gif";
            HttpURLConnection conn = null;
            // retrieve the URL
            URL myFileUrl = null;
            Bitmap bmImg = null;

            try {
                Log.e("ForecastServiceUpdater", "I sleep");
                Thread.sleep(1000);
                Log.e("ForecastServiceUpdater", "I have slept");
                myFileUrl = new URL(pictureUrl);
                //Define the HttpConnection and open it
                conn = (HttpURLConnection) myFileUrl.openConnection();
                //Define that connection is an input
                conn.setDoInput(true);
                //connect
                conn.connect();
                //retrieve the input stream returned by the connection
                InputStream is = conn.getInputStream();
                //Use this input stream to build your bitmpa
                bmImg = BitmapFactory.decodeStream(is);
                forecast.setDrawable(new BitmapDrawable(MyApplication.getInstance().getResources(), bmImg));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                //disconnect
                if (conn != null) {
                    conn.disconnect();
                }
            }
            Log.e("ForecastServiceUpdater", "I return the drawable for the forecast " + forecast.getImageCode());
            return bmImg;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            Log.e("ForecastServiceUpdater", "onPostExecute " + bitmap);

            if (weakPictureCallBack.get() != null) {
                weakPictureCallBack.get().onPictureLoaded(bitmap, forecast);
            } else {
                Log.e("ForecastServiceUpdater", "weakPictureCallBack.get() =" + weakPictureCallBack.get());
            }
        }
    }

    /**
     * @author Mathias Seguy (Android2EE)
     * @goals This class aims to implements a Runnable with an Handler
     */
    private class RestCallRunnable implements Runnable {
        /**
         * The handler to use to communicate outside the runnable
         */
        public Handler restCallHandler = null;

        @Override
        public void run() {
            // Do the rest http call
            // Parse the element
            buildForecasts(getForecast());
            restCallHandler.sendMessage(restCallHandler.obtainMessage());
        }

        /**
         * Retrieve the forecast
         */
        private String getForecast() {
            Log.e(tag, "getForecast called");
            //Define your connection
            HttpURLConnection connection = null;
            //YourURL
            URL realURL = null;
            //And the response
            StringBuilder response = null;
            try {
                //create your url
                realURL = new URL(url);
                //open the connection
                connection = (HttpURLConnection) realURL.openConnection();
                //test if the connection is ok or not
                connection.setDoInput(true);
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                int statusCode = connection.getResponseCode();
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    // do the job
                    response = new StringBuilder();
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                } else {
                    //bad connection status, something failed
                    //ExceptionManager.manage(new ExceptionManaged(this.getClass(), R.string.exc_http_get_error, e));
                    return null;
                }
                //cmlose your injput stream
                in.close();
            } catch (MalformedURLException e) {
                Log.e(tag, "IOException", e);
                //ExceptionManager.manage(new ExceptionManaged(this.getClass(), R.string.exc_http_get_error, e));
            } catch (IOException e) {
                Log.e(tag, "IOException", e);
                //ExceptionManager.manage(new ExceptionManaged(this.getClass(), R.string.exc_http_get_error, e));
            } finally {
                //ensure to close your connection
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return response == null ? null : response.toString();
        }

        /**
         * Build the Forecasts list by parsing the xml response using SAX
         *
         * @param raw the xml response of the web server
         */
        private void buildForecasts(String raw) {

            Log.e(tag, "buildForecasts called");
            try {
                // Create a new instance of the SAX parser
                SAXParserFactory saxPF = SAXParserFactory.newInstance();
                SAXParser saxP = saxPF.newSAXParser();
                // The xml reader
                XMLReader xmlR = saxP.getXMLReader();
                // Create the Handler to handle each of the XML tags.
                ForcastSaxHandler forecastHandler = new ForcastSaxHandler();
                xmlR.setContentHandler(forecastHandler);
                // then parse
                xmlR.parse(new InputSource(new StringReader(raw)));
                // and retrieve the parsed forecasts
                forecasts = forecastHandler.getForecasts();
                Log.e(tag, "buildForecasts finished");
            } catch (ParserConfigurationException e) {
                Log.e(tag, "ParserConfigurationException", e);
//				ExceptionManager.manage(new ExceptionManaged(this.getClass(), R.string.exc_parsing, e));
            } catch (SAXException e) {
                Log.e(tag, "SAXException", e);
//				ExceptionManager.manage(new ExceptionManaged(this.getClass(), R.string.exc_parsing, e));
            } catch (IOException e) {
                Log.e(tag, "IOException", e);
//				ExceptionManager.manage(new ExceptionManaged(this.getClass(), R.string.exc_parsing, e));
            }
        }
    }
}
