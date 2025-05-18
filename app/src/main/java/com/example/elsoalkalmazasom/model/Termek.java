package com.example.elsoalkalmazasom.model;

import java.io.Serializable;

public class Termek implements java.io.Serializable {
    private String id;
    private String nev;
    private String leiras;
    private int ar;

    public Termek() {
        // Üres konstruktor Firestore-hoz
    }

    public Termek(String id, String nev, String leiras, int ar) {
        this.id = id;
        this.nev = nev;
        this.leiras = leiras;
        this.ar = ar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNev() {
        return nev;
    }

    public void setNev(String nev) {
        this.nev = nev;
    }

    public String getLeiras() {
        return leiras;
    }

    public void setLeiras(String leiras) {
        this.leiras = leiras;
    }

    public int getAr() {
        return ar;
    }

    public void setAr(int ar) {
        this.ar = ar;
    }
}
