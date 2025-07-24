package com.example.projet_mobile1;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Article implements Serializable {
    private String nom;
    private String description;
    private double prix;
    private Categorie categorie;
    private Date date;
    private Type type;
    private boolean taxable;
    private double taux;
    private int quantite;

    public Article(String nom, String description, double prix, Categorie categorie, Date date, Type type, boolean taxable, double taux){
        this.nom = nom;
        this.description = description;
        this.prix = prix;
        this.categorie = categorie;
        this.date = date;
        this.type = type;
        this.taxable = taxable;
        this.taux = taux;
        this.quantite = 1;
    }


    public String getNom(){
        return nom;
    }


    public String getDescription(){
        return description;
    }


    public double getPrix(){
        return prix;
    }


    public Categorie getCategorie(){
        return categorie;
    }


    public Date getDate(){
        return date;
    }


    public Type getType(){
        return type;
    }


    public boolean getTaxable(){
        return taxable;
    }

    public double getTaux(){
        return taux;
    }

    public int getQuantite(){
        return quantite;
    }

    public void setQuantite(int quantite){
        this.quantite = quantite;
    }


    public enum Categorie{
        BIJOUX(R.string.cat_bijoux),
        TEXTILE(R.string.cat_textile),
        BOIS(R.string.cat_bois),
        CERAMIQUE(R.string.cat_ceramique);

        private final int resId;

        Categorie(int resId){
            this.resId = resId;
        }

        public int getResId(){
            return resId;
        }
    }




    public enum Type{
        ORIGINAL(R.string.rad_original),
        REPRODUCTION(R.string.rad_reproduction);

        private final int resId;

        Type(int resId){
            this.resId = resId;
        }

        public int getResId(){
            return resId;
        }
    }



    public static List<Double> tauxPossible = Arrays.asList(
            0.05,
            0.09975,
            0.14975
    );
}
