package com.example.projet_mobile1;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddEditFragment extends Fragment {
    EditText edtNom, edtDescription, edtPrix, edtDate;
    Spinner spnCategorie, spnTaux;
    RadioGroup radGrpType, radGrpTaxable;
    Button btnEnregistrer, btnRecommencer;
    Calendar calendar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edtNom = view.findViewById(R.id.edtNom);
        edtDescription = view.findViewById(R.id.edtDescription);
        edtPrix = view.findViewById(R.id.edtPrix);
        spnCategorie = view.findViewById(R.id.spnCategorie);
        edtDate = view.findViewById(R.id.edtDate);
        radGrpType = view.findViewById(R.id.radGrpType);
        radGrpTaxable = view.findViewById(R.id.radGrpTaxable);
        radGrpTaxable.setVisibility(View.INVISIBLE);
        spnTaux = view.findViewById(R.id.spnTaux);
        spnTaux.setVisibility(View.INVISIBLE);
        btnEnregistrer = view.findViewById(R.id.btnEnregistrer);
        btnRecommencer = view.findViewById(R.id.btnRecommencer);
        calendar = Calendar.getInstance();


//        datepicker
        edtDate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        String selectedDate = String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, (month + 1), year);
                        edtDate.setText(selectedDate);
                    }
                }, year, month, day);

                Calendar today = Calendar.getInstance();
                datePickerDialog.getDatePicker().setMaxDate(today.getTimeInMillis());
                datePickerDialog.show();
            }
        });


//        categorie spinner
        List<String> categorieLbls = new ArrayList<>();
        categorieLbls.add(getString(R.string.cat_default));
        for (Article.Categorie c : Article.Categorie.values()){
            categorieLbls.add(getString(c.getResId()));
        }
        ArrayAdapter<String> categorieAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categorieLbls);
        categorieAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategorie.setAdapter(categorieAdapter);


//        taux spinner
        List<String> tauxLbls = new ArrayList<>();
        tauxLbls.add(getString(R.string.spn_tax_default));
        for (double taux : Article.tauxPossible){
            String format = String.format(Locale.getDefault(), "%.2f %%", taux);
            tauxLbls.add(format);
        }
        ArrayAdapter<String> tauxAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, tauxLbls);
        tauxAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTaux.setAdapter(tauxAdapter);


//        rad group type
        radGrpType.setOnCheckedChangeListener((group, checkedId) ->{
            if (radGrpType.getCheckedRadioButtonId() == R.id.radOriginal){
                radGrpTaxable.setVisibility(View.VISIBLE);
            } else {
                radGrpTaxable.clearCheck();
                radGrpTaxable.setVisibility(View.INVISIBLE);
                spnTaux.setVisibility(View.INVISIBLE);
                spnTaux.setSelection(0);
            }
        });


//        rad group taxable
        radGrpTaxable.setOnCheckedChangeListener((group, checkedId) ->{
            if (radGrpTaxable.getCheckedRadioButtonId() == R.id.radTaxable){
                spnTaux.setVisibility(View.VISIBLE);
            } else {
                spnTaux.setVisibility(View.INVISIBLE);
                spnTaux.setSelection(0);
            }
        });



//        receive edit article
        getParentFragmentManager().setFragmentResultListener("article_edit", this, (key, bundle) ->{
            Article article = (Article) bundle.getSerializable("article");
            if (article != null){
                edtNom.setText(article.getNom());
                edtDescription.setText(article.getDescription());
                edtPrix.setText(String.valueOf(article.getPrix()));
                spnCategorie.setSelection(article.getCategorie().ordinal());
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String formattedDate = format.format(article.getDate());
                edtDate.setText(formattedDate);
                radGrpType.check(article.getType() == Article.Type.ORIGINAL ? R.id.radOriginal : R.id.radReproduction);
                radGrpTaxable.check(article.getTaxable() ? R.id.radTaxable : R.id.radNonTaxable);
                spnTaux.setSelection(Article.tauxPossible.indexOf(article.getTaux()) + 1);
            }
        });



//        Save button
        int editPosition = getArguments() != null ? getArguments().getInt("position", -1) : -1;
        if (editPosition != -1){
            ((MainActivity) requireActivity()).setEditMode(true);
        }
        btnEnregistrer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ArticleInput input = validateInputs();
                if (input == null) return;

                boolean isEdit = (editPosition != -1);
                int dialogTitle = isEdit? R.string.edit_title : R.string.add_title;
                int dialogMessage = isEdit? R.string.confirm_edit_msg : R.string.add_msg;

                new AlertDialog.Builder(requireContext())
                        .setTitle(dialogTitle)
                        .setMessage(dialogMessage)
                        .setPositiveButton(R.string.oui, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Article article = new Article(input.nom, input.description, input.prix, input.categorie, input.date, input.type, input.taxable, input.taux);
                                Bundle result = new Bundle();
                                result.putSerializable("article", article);
                                result.putInt("position", editPosition);
                                getParentFragmentManager().setFragmentResult("article_result", result);
                                Toast.makeText(requireContext(), R.string.t_success, Toast.LENGTH_SHORT).show();
                                clearFields();
                                ((MainActivity) requireActivity()).setEditMode(false);
                                requireActivity().getSupportFragmentManager().popBackStack();
                            }
                        })
                       .setNegativeButton(R.string.non, null)
                       .show();
            }
        });


//        Cancel button
        btnRecommencer.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(requireContext())
                        .setTitle(R.string.btn_recommencer_title)
                        .setMessage(R.string.btn_recommencer_msg)
                        .setPositiveButton(R.string.oui, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                clearFields();
                            }
                        })
                        .setNegativeButton(R.string.non, new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .show();

            }
        });
    }





//    HELPERS
    private class ArticleInput {
        String nom;
        String description;
        double prix;
        Article.Categorie categorie;
        Date date;
        Article.Type type;
        boolean taxable;
        double taux;

        public ArticleInput(String nom, String description, double prix, Article.Categorie categorie, Date date, Article.Type type, boolean taxable, double taux) {
            this.nom = nom;
            this.description = description;
            this.prix = prix;
            this.categorie = categorie;
            this.date = date;
            this.type = type;
            this.taxable = taxable;
            this.taux = taux;
        }
    }



    private ArticleInput validateInputs(){
        String nom = edtNom.getText().toString().trim();
        if (nom.isEmpty()){
            Toast.makeText(requireContext(), R.string.t_nom, Toast.LENGTH_SHORT).show();
            return null;
        }

        String description = edtDescription.getText().toString().trim();

        String prixStr = edtPrix.getText().toString();
        if (prixStr.isEmpty()){
            Toast.makeText(requireContext(), R.string.t_prix_empty, Toast.LENGTH_SHORT).show();
            return null;
        }
        if (prixStr.contains(".")){
            String decimalPart = prixStr.substring(prixStr.indexOf(".") + 1);
            if (decimalPart.length() > 2){
                Toast.makeText(requireContext(), R.string.t_prix_decimal, Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        double prix = Double.parseDouble(prixStr);
        if (prix < 0){
            Toast.makeText(requireContext(), R.string.t_prix_positif, Toast.LENGTH_SHORT).show();
            return null;
        }

        int selectedCategoriePos = spnCategorie.getSelectedItemPosition();
        if (selectedCategoriePos == 0){
            Toast.makeText(requireContext(), R.string.t_categorie, Toast.LENGTH_SHORT).show();
            return null;
        }
        Article.Categorie categorie = Article.Categorie.values()[selectedCategoriePos - 1];

        Date date = calendar.getTime();
        if (edtDate.getText().toString().isEmpty()){
            Toast.makeText(requireContext(), R.string.t_date, Toast.LENGTH_SHORT).show();
            return null;
        }

        int selectedTypeId = radGrpType.getCheckedRadioButtonId();
        if (selectedTypeId == -1){
            Toast.makeText(requireContext(), R.string.t_type, Toast.LENGTH_SHORT).show();
            return null;
        }
        RadioButton selectedType = radGrpType.findViewById(selectedTypeId);
        Article.Type type = Article.Type.valueOf(selectedType.getTag().toString());

        boolean taxable = false;
        double taux = -1;
        if (type == Article.Type.ORIGINAL){
            int selectedTaxableId = radGrpTaxable.getCheckedRadioButtonId();
            if (selectedTaxableId == -1){
                Toast.makeText(requireContext(), R.string.t_taxable, Toast.LENGTH_SHORT).show();
                return null;
            }
            taxable = (selectedTaxableId == R.id.radTaxable);

            if (taxable){
                int selectedTauxPos = spnTaux.getSelectedItemPosition();
                if (selectedTauxPos == 0){
                    Toast.makeText(requireContext(), R.string.t_taux, Toast.LENGTH_SHORT).show();
                    return null;
                }
                taux = Article.tauxPossible.get(selectedTauxPos - 1);
            }
        }

        return new ArticleInput(nom, description, prix, categorie, date, type, taxable, taux);
    }


    private void clearFields(){
        edtNom.setText("");
        edtDescription.setText("");
        edtPrix.setText("");
        spnCategorie.setSelection(0);
        edtDate.setText("");
        radGrpType.clearCheck();
        radGrpTaxable.clearCheck();
        spnTaux.setSelection(0);
    }
}
