package com.example.projet_mobile1;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class ListeArticlesFragment extends Fragment {
    Spinner spnFiltre;
    RadioGroup radGrpTriage;
    private boolean filtrerParNom = true;
    private boolean trierAscendant = true;
    ArrayList<Article> articles = new ArrayList<>();
    ListView lstArticles;
    ArticleAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_liste_articles, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        spnFiltre = view.findViewById(R.id.spnFiltre);
        radGrpTriage = view.findViewById(R.id.radGrpTriage);
        lstArticles = view.findViewById(R.id.lstArticles);

        loadData();

        if (savedInstanceState != null){
            filtrerParNom = savedInstanceState.getBoolean("filtrerParNom", true);
            trierAscendant = savedInstanceState.getBoolean("trierAscendant", true);
            ArrayList<Article> savedArticles = (ArrayList<Article>) savedInstanceState.getSerializable("articles");
            trier();
            if (savedArticles != null){
                articles.clear();
                articles.addAll(savedArticles);
            }
        }



//        Handle filter condition
        spnFiltre.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view instanceof TextView){
                    TextView textView = (TextView) view;
                    textView.setTextColor(getResources().getColor(R.color.white));
                    textView.setTypeface(null, Typeface.BOLD);
                }
                filtrerParNom = position == 0;
                trier();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


//        Handle sort order
        radGrpTriage.setOnCheckedChangeListener((group, checkedId) ->{
            trierAscendant = checkedId == R.id.radAscendant;
            trier();
        });


//        Dyanmic update and save to sharedpreferences
        getParentFragmentManager().setFragmentResultListener("article_result", this, (key, bundle) ->{
            Article article = (Article) bundle.getSerializable("article");
            if (article != null){
                int position = bundle.getInt("position", -1);
                if (position != -1){
                    articles.set(position, article);
                } else {
                    articles.add(article);
                }
                adapter.notifyDataSetChanged();
                saveData();
            }
        });



//        modification dun article
        lstArticles.setOnItemClickListener((parent, articleView, position, id) -> {
            Article clickedArticle = articles.get(position);
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.edit_title)
                    .setMessage(R.string.edit_msg)
                    .setPositiveButton(R.string.oui, (dialog, which) ->{
                        Bundle result = new Bundle();
                        result.putSerializable("article", clickedArticle);
                        result.putInt("position", position);
                        getParentFragmentManager().setFragmentResult("article_edit", result);
                        AddEditFragment fragment = new AddEditFragment();
                        fragment.setArguments(result);
                        getParentFragmentManager().beginTransaction()
                                .replace(R.id.fragmentContainer, fragment)
                                .addToBackStack(null)
                                .commit();
                    })
                    .setNegativeButton(R.string.non, null)
                    .show();
        });



//        suppression dun article
        lstArticles.setOnItemLongClickListener((parent, delArticleView, position, id) -> {
            Article article = articles.get(position);
            new AlertDialog.Builder(requireContext())
                    .setTitle(R.string.del_title)
                    .setMessage(R.string.del_msg)
                    .setPositiveButton(R.string.oui, (dialog, which) -> {
                        supprimerArticle(article, position);
                    })
                    .setNegativeButton(R.string.non, null)
                    .show();
            return true;
        });

    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putBoolean("filtrerParNom", filtrerParNom);
        outState.putBoolean("trierAscendant", trierAscendant);
        outState.putSerializable("articles", articles);
    }


    @Override
    public void onResume(){
        super.onResume();
        Panier.getInstance().loadPanier(requireContext());
    }


    private void supprimerArticle(Article article, int position){
        articles.remove(position);
        adapter.notifyDataSetChanged();
        saveData();
        Panier.getInstance().supprimerArticle(article);
        Panier.getInstance().savePanier(requireContext());
    }


    private void trier(){
        Comparator<Article> comparator = filtrerParNom ? Comparator.comparing(Article::getNom) : Comparator.comparingDouble(Article::getPrix);
        if (!trierAscendant){
            comparator = comparator.reversed();
        }
        Collections.sort(articles, comparator);
        adapter.notifyDataSetChanged();
    }


    public void saveData(){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("article_count", articles.size());

        for (int i = 0; i < articles.size(); i++){
            Article article = articles.get(i);
            editor.putString("article_" + i + "_nom", article.getNom());
            editor.putString("article_" + i + "_description", article.getDescription());
            editor.putString("article_" + i + "_prix", String.valueOf(article.getPrix()));
            editor.putString("article_" + i + "_categorie", article.getCategorie().name());
            editor.putLong("article_" + i + "_date", article.getDate().getTime());
            editor.putString("article_" + i + "_type", article.getType().name());
            editor.putBoolean("article_" + i + "_taxable", article.getTaxable());
            editor.putString("article_" + i + "_taux", String.valueOf(article.getTaux()));
            editor.putInt("article_" + i + "_quantite", article.getQuantite());
        }
        editor.apply();
    }

    private void loadData(){
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("shared preferences", Context.MODE_PRIVATE);
        int articleCount = sharedPreferences.getInt("article_count", 0);
        articles = new ArrayList<>();

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
            articles.add(article);
        }
        adapter = new ArticleAdapter(requireContext(), articles);
        lstArticles.setAdapter(adapter);
    }
}