package com.example.projet_mobile1;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class PanierAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<Article> articles;
    private LayoutInflater inflater;
    private OnQuantiteChangeListener listener;

    public PanierAdapter(Context context, ArrayList<Article> articles, OnQuantiteChangeListener listener){
        this.context = context;
        this.articles = articles;
        this.inflater = LayoutInflater.from(context);
        this.listener = listener;
    }

    public interface OnQuantiteChangeListener{
        void onQuantiteChange();
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.cart_item_layout, parent, false);
            holder = new ViewHolder();

            holder.txtNomArticle = convertView.findViewById(R.id.nomArticle);
            holder.txtPrixArticle = convertView.findViewById(R.id.prixArticle);
            holder.txtTaxableArticle = convertView.findViewById(R.id.taxableArticle);
            holder.edtQuantite = convertView.findViewById(R.id.quantite);
            holder.augmenter = convertView.findViewById(R.id.increase);
            holder.baisser = convertView.findViewById(R.id.decrease);

            convertView.setTag(holder);
            Article article = articles.get(position);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Article article = articles.get(position);

        holder.txtNomArticle.setText(article.getNom());
        holder.txtPrixArticle.setText(String.format("$%.2f", article.getPrix() * article.getQuantite()));
        holder.txtTaxableArticle.setText(article.getTaxable()? R.string.rad_taxable : R.string.rad_non_taxable);
        holder.edtQuantite.setText(String.valueOf(article.getQuantite()));

        if (holder.edtQuantite.getTag() instanceof TextWatcher){
            holder.edtQuantite.removeTextChangedListener((TextWatcher) holder.edtQuantite.getTag());
        }

        TextWatcher watcher = new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                String input = s.toString().trim();
                if (input.isEmpty()) return;

                int newQty = Integer.parseInt(s.toString());
                if (newQty < 1) newQty = 1;

                article.setQuantite(newQty);
                Panier.getInstance().modifierArticle(article);
                Panier.getInstance().savePanier(context);
                if (listener != null){
                    listener.onQuantiteChange();
                }
            }
        };
        holder.edtQuantite.addTextChangedListener(watcher);
        holder.edtQuantite.setTag(watcher);

        holder.augmenter.setOnClickListener(v -> {
                int qty = article.getQuantite() + 1;
                article.setQuantite(qty);
                holder.edtQuantite.setText(String.valueOf(qty));
                holder.txtPrixArticle.setText(String.format("$%.2f", article.getPrix() * qty));

                if (listener != null){
                    listener.onQuantiteChange();
                }
            });

        holder.baisser.setOnClickListener(v -> {

            int qty = article.getQuantite() - 1;
            if (qty > 0) {
                article.setQuantite(qty);
                holder.edtQuantite.setText(String.valueOf(qty));
                holder.txtPrixArticle.setText(String.format("$%.2f", article.getPrix() * qty));
            } else {
                articles.remove(position);
                Panier.getInstance().supprimerArticle(article);
                notifyDataSetChanged();
            }
            if (listener != null){
                listener.onQuantiteChange();
            }
            Panier.getInstance().savePanier(context);
        });

        convertView.setOnClickListener(v ->{
            new androidx.appcompat.app.AlertDialog.Builder(context)
                    .setTitle(R.string.del_title)
                    .setMessage(R.string.del_msg)
                    .setPositiveButton(R.string.oui, (dialog, which) ->{
                        Panier.getInstance().supprimerArticle(article);
                        articles.remove(position);
                        notifyDataSetChanged();
                        if (listener != null){
                            listener.onQuantiteChange();
                        }
                        Panier.getInstance().savePanier(context);
                    })
                    .setNegativeButton(R.string.non, null)
                    .show();
        });
        return convertView;
    }


    static class ViewHolder{
        TextView txtNomArticle;
        TextView txtPrixArticle;
        TextView txtTaxableArticle;
        EditText edtQuantite;
        ImageButton augmenter;
        ImageButton baisser;
        EditText qtyEdtText;
    }
}
