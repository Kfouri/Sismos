package com.kfouri.sismos;

/**
 * Created by MSI on 29/11/2015.
 */
public class Sismo {

    private String mag;
    private String fecha;
    private String ciudad;
    private String lat;
    private String lon;

    public Sismo(String mag,String fecha,String ciudad,String lat,String lon)
    {
        super();
        this.mag = mag;
        this.fecha = fecha;
        this.ciudad = ciudad;
        this.lat = lat;
        this.lon = lon;
    }

    public String getMag() {
        return mag;
    }

    public void setMag(String mag) {
        this.mag = mag;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
        this.lon = lon;
    }
}
