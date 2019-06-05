/**<ul>
 * <li>ForecastRestYahooSax</li>
 * <li>com.android2ee.formation.restservice.sax.forecastyahoo</li>
 * <li>28 mai 2014</li>
 *
 * <li>======================================================</li>
 *
 * <li>Projet : Mathias Seguy Project</li>
 * <li>Produit par MSE.</li>
 *
 /**
 * <ul>
 * Android Tutorial, An <strong>Android2EE</strong>'s project.</br> 
 * Produced by <strong>Dr. Mathias SEGUY</strong>.</br>
 * Delivered by <strong>http://android2ee.com/</strong></br>
 *  Belongs to <strong>Mathias Seguy</strong></br>
 ****************************************************************************************************************</br>
 * This code is free for any usage but can't be distribute.</br>
 * The distribution is reserved to the site <strong>http://android2ee.com</strong>.</br>
 * The intelectual property belongs to <strong>Mathias Seguy</strong>.</br>
 * <em>http://mathias-seguy.developpez.com/</em></br> </br>
 *
 * *****************************************************************************************************************</br>
 *  Ce code est libre de toute utilisation mais n'est pas distribuable.</br>
 *  Sa distribution est reservée au site <strong>http://android2ee.com</strong>.</br> 
 *  Sa propriété intellectuelle appartient à <strong>Mathias Seguy</strong>.</br>
 *  <em>http://mathias-seguy.developpez.com/</em></br> </br>
 * *****************************************************************************************************************</br>
 */

package com.android2ee.formation.jassimile.tp.depart.mmxv.transverse.model;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;


import java.util.Date;

/**
 * Created by Mathias Seguy - Android2EE on 04/05/2015.
 */
public class Forecast implements Parcelable {
    public static final String NOT_SET = "notSet";
    String temp;
    String tempMin;
    String tempMax;
    String tendence;
    Drawable drawable;
    String imageCode;
    Date date;
    public Boolean isToday=true;
    public Forecast(String tendence) {
        this.temp = "32";
        this.tempMin = "11";
        this.tempMax = "36";
        this.tendence = tendence;
        this.imageCode = "1";
        this.date = new Date();

        isToday=true;
    }
    public Forecast(String temp, String tempMin, String tempMax, String tendence, String imageCode, Date date) {
        this.temp = temp;
        this.tempMin = tempMin;
        this.tempMax = tempMax;
        this.tendence = tendence;
        this.imageCode = imageCode;
        this.date = date;
    }

    /**
     * Used to build object when downloaded
     * @param tendence
     * @param code
     * @param temp
     */
    public Forecast(String tendence, String code, String temp) {
        this.tendence=tendence;
        this.imageCode=code;
        this.temp=temp;
        this.date=new Date();
        isToday=true;
        tempMax=tempMin=NOT_SET;
    }

    /**
     *
     * Used to build object when downloaded
     * @param tendence
     * @param code
     * @param templ
     * @param temph
     * @param dayCount
     */
    public Forecast(String tendence, String code, String templ, String temph, int dayCount) {
        this.tendence=tendence;
        this.imageCode=code;
        this.tempMin=templ;
        this.tempMax=temph;
        this.date=new Date();

        isToday=false;
        date.setTime(date.getTime()+dayCount*24*3600*1000);
        temp= NOT_SET;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getTempMin() {
        return tempMin;
    }

    public void setTempMin(String tempMin) {
        this.tempMin = tempMin;
    }

    public String getTempMax() {
        return tempMax;
    }

    public void setTempMax(String tempMax) {
        this.tempMax = tempMax;
    }

    public String getTendence() {
        return tendence;
    }

    public void setTendence(String tendence) {
        this.tendence = tendence;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public String getImageCode() {
        return imageCode;
    }

    public void setImageCode(String imageCode) {
        this.imageCode = imageCode;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "Forecast{" +
                "temp='" + temp + '\'' +
                ", tempMin='" + tempMin + '\'' +
                ", tempMax='" + tempMax + '\'' +
                ", tendence='" + tendence + '\'' +
                ", drawable=" + drawable +
                ", imageCode='" + imageCode + '\'' +
                ", date=" + date +
                '}';
    }

    protected Forecast(Parcel in) {
        temp = in.readString();
        tempMin = in.readString();
        tempMax = in.readString();
        tendence = in.readString();
        imageCode = in.readString();
        long tmpDate = in.readLong();
        date = tmpDate != -1 ? new Date(tmpDate) : null;
        byte isTodayVal = in.readByte();
        isToday = isTodayVal == 0x02 ? null : isTodayVal != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(temp);
        dest.writeString(tempMin);
        dest.writeString(tempMax);
        dest.writeString(tendence);
        dest.writeString(imageCode);
        dest.writeLong(date != null ? date.getTime() : -1L);
        if (isToday == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (isToday ? 0x01 : 0x00));
        }
    }

    @SuppressWarnings("unused")
    public static final Creator<Forecast> CREATOR = new Creator<Forecast>() {
        @Override
        public Forecast createFromParcel(Parcel in) {
            return new Forecast(in);
        }

        @Override
        public Forecast[] newArray(int size) {
            return new Forecast[size];
        }
    };


}
