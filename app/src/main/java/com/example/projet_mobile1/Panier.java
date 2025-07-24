package com.example.projet_mobile1;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Panier {
    Context context;
    private static Panier instance;
    private Map<String, Article> articles;
    private static final String PREFS_NAME = "PanierPrefs";
    private static final String PREF_KEY_COUNT = "article_count";


    private Panier(){
        articles = new HashMap<>();
    }

    public static Panier getInstance(){
        if (instance == null){
            instance = new Panier();
        }
        return instance;
    }

    public void ajouterArticle(Article article){
        if (articles.containsKey(article.getNom())){
            Article existing = articles.get(article.getNom());
            existing.setQuantite(existing.getQuantite() + 1);
        } else {
            articles.put(article.getNom(), article);
        }
    }

    public void supprimerArticle(Article article){
        articles.remove(article.getNom());
    }

    public void modifierArticle(Article article){
        if (articles.containsKey(article.getNom())){
            articles.put(article.getNom(), article);
        }
    }

    public ArrayList<Article> getArticles(){
        return new ArrayList<>(articles.values());
    }

    public void savePanier(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(PREF_KEY_COUNT, articles.size());

        int i = 0;
        for (Article article : articles.values()){
            editor.putString("article_" + i + "_nom", article.getNom());
            editor.putString("article_" + i + "_description", article.getDescription());
            editor.putString("article_" + i + "_prix", String.valueOf(article.getPrix()));
            editor.putString("article_" + i + "_categorie", article.getCategorie().name());
            editor.putLong("article_" + i + "_date", article.getDate().getTime());
            editor.putString("article_" + i + "_type", article.getType().name());
            editor.putBoolean("article_" + i + "_taxable", article.getTaxable());
            editor.putString("article_" + i + "_taux", String.valueOf(article.getTaux()));
            editor.putInt("article_" + i + "_quantite", article.getQuantite());
            i++;
        }
        editor.apply();
    }

    public void loadPanier(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        int articleCount = sharedPreferences.getInt(PREF_KEY_COUNT, 0);

        articles.clear();
        for (int i = 0; i < articleCount; i++){
            String nom = sharedPreferences.getString("article_" + i + "_nom", "");
            String description = sharedPreferences.getString("article_" + i + "_description", "");
            Double prix = Double.parseDouble(sharedPreferences.getString("article_" + i + "_prix", "0"));
            Article.Categorie categorie = Article.Categorie.valueOf(sharedPreferences.getString("article_" + i + "_categorie", ""));
            Date date = new Date(sharedPreferences.getLong("article_" + i + "_date", 0));
            Article.Type type = Article.Type.valueOf(sharedPreferences.getString("article_" + i + "_type", ""));
            boolean taxable = sharedPreferences.getBoolean("article_" + i + "_taxable", false);
            Double taux = Double.parseDouble(sharedPreferences.getString("article_" + i + "_taux", ""));
            int quantite = sharedPreferences.getInt("article_" + i + "_quantite", 1);

            Article article = new Article(nom, description, prix, categorie, date, type, taxable, taux);
            article.setQuantite(quantite);
            articles.put(nom, article);
        }
    }
}
