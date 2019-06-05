package com.android2ee.formation.jassimile.tp.depart.mmxv.com.intf;

import android.graphics.Bitmap;

import com.android2ee.formation.jassimile.tp.depart.mmxv.transverse.model.Forecast;

/**
 * Created by Mathias Seguy - Android2EE on 05/05/2015.
 */
public interface ForecastPictureCallBack {

    public void onPictureLoaded(Bitmap picture, Forecast forecast);
}
