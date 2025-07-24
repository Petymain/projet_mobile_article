package com.example.projet_mobile1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ArticleAdapter extends BaseAdapter {
    private Context context;
    private List<Article> articles;

    public ArticleAdapter(Context context, List<Article> articles){
        this.context = context;
        this.articles = articles;
    }

    @Override
    public int getCount() {
        return articles.size();
    }

    @Override
    public Object getItem(int position) {
        return articles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_layout, parent, false);
        }

        Article article = articles.get(position);

//        adding to cart
        Button btnAjouter = convertView.findViewById(R.id.btnAjouter);
        btnAjouter.setOnClickListener(v ->{
           Panier.getInstance().ajouterArticle(article);
           Panier.getInstance().savePanier(context);
           Toast.makeText(context, article.getNom() + context.getString(R.string.ajoute), Toast.LENGTH_SHORT).show();
        });


//        populate list
        TextView txtNom = convertView.findViewById(R.id.txtNom);
        TextView txtDate = convertView.findViewById(R.id.txtDate);
        TextView txtPrix = convertView.findViewById(R.id.txtPrix);
        TextView txtFiscal = convertView.findViewById(R.id.txtFiscal);



        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateStr = sdf.format(article.getDate());

        int taxableResId = article.getTaxable() ? R.string.rad_taxable : R.string.rad_non_taxable;
        String fiscal = context.getString(taxableResId);

        txtNom.setText(article.getNom());
        txtDate.setText(context.getString(R.string.fabrication, dateStr));
        txtPrix.setText(String.valueOf(article.getPrix()));
        txtFiscal.setText(fiscal);

        return convertView;
    }
}
