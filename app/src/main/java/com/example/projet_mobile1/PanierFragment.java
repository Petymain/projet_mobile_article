package com.example.projet_mobile1;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class PanierFragment extends Fragment implements PanierAdapter.OnQuantiteChangeListener {
    ListView panierLst;
    PanierAdapter adapter;
    ArrayList<Article> articles;
    TextView txtTotalPrix, txtTotalTaxe, txtTotalTTC;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_panier, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Panier.getInstance().loadPanier(requireContext());

        panierLst = view.findViewById(R.id.panierLst);
        txtTotalPrix = view.findViewById(R.id.txtTotalPrix);
        txtTotalTaxe = view.findViewById(R.id.txtTotalTaxe);
        txtTotalTTC = view.findViewById(R.id.txtTotalTTC);

        articles = Panier.getInstance().getArticles();
        adapter = new PanierAdapter(requireContext(), articles, this);
        panierLst.setAdapter(adapter);

        calculerSommes();
    }


    public void onQuantiteChange(){
        calculerSommes();
    }

    public void calculerSommes(){
        double totalPrix = 0.0;
        double totalTaxe = 0.0;
        double totalTTC;

        for (Article article : articles){
            double prixArticle = article.getPrix() * article.getQuantite();
            totalPrix += prixArticle;

            if (article.getTaxable()){
                totalTaxe += prixArticle * 0.15;
            }
        }

        totalTTC = totalPrix + totalTaxe;
        txtTotalPrix.setText(String.format(getString(R.string.total), totalPrix));
        txtTotalTaxe.setText(String.format(getString(R.string.taxe), totalTaxe));
        txtTotalTTC.setText(String.format(getString(R.string.ttc), totalTTC));
    }
}