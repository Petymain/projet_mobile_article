package com.example.projet_mobile1;

import android.app.Activity;
import android.app.LocaleManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.content.res.Configuration;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.os.LocaleListCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private boolean isEdit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        MaterialToolbar toolbar = findViewById(R.id.menuToolbar);
        setSupportActionBar(toolbar);
        Drawable overflowIcon = toolbar.getOverflowIcon();
        overflowIcon.setTint(ContextCompat.getColor(this, R.color.white));

        if (savedInstanceState == null) {
            String fragmentName = getIntent().getStringExtra("fragment");
            if (fragmentName != null) {
                Fragment fragmentToLoad = null;
                switch (fragmentName) {
                    case "PanierFragment":
                        fragmentToLoad = new PanierFragment();
                        break;
                    case "AddEditFragment":
                        fragmentToLoad = new AddEditFragment();
                        break;
                    case "ListeArticlesFragment":
                    default:
                        fragmentToLoad = new ListeArticlesFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, fragmentToLoad)
                        .commit();
            } else {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new ListeArticlesFragment())
                        .commit();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem mnuLang = menu.findItem(R.id.mnuLang);
        if (mnuLang != null){
            SpannableString s = new SpannableString(mnuLang.getTitle());
            s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
            s.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, s.length(), 0);
            s.setSpan(new android.text.style.AbsoluteSizeSpan(20, true), 0, s.length(), 0);
            mnuLang.setTitle(s);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        int itemId = item.getItemId();

        if (itemId == R.id.mnuLang){
            String currentLang = Locale.getDefault().getLanguage();
            String nextLang = "en";
            if (currentLang.equals("en")){
                nextLang = "fr";
            }
            switchLang(this, nextLang);
        }

        if (isEdit){
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle(R.string.edit_title)
                    .setMessage(R.string.edit_inprogress_msg)
                    .setPositiveButton(R.string.oui, (dialog, which) -> {
                        isEdit = false;
                        fragmentMenu(itemId);
                    })
                    .setNegativeButton(R.string.non, null)
                    .show();
            return true;
        } else {
            return fragmentMenu(item.getItemId());
        }
    }

    private boolean fragmentMenu(int itemId){
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if (itemId == R.id.mnuList){
            if (!(currentFragment instanceof ListeArticlesFragment)){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new ListeArticlesFragment())
                        .commit();
            }
            return true;
        } else if (itemId == R.id.mnuCart) {
            if (!(currentFragment instanceof PanierFragment)){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new PanierFragment())
                        .addToBackStack(null)
                        .commit();
            }
            return true;
        } else if (itemId == R.id.mnuAjout){
            if (!(currentFragment instanceof AddEditFragment)){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, new AddEditFragment())
                        .addToBackStack(null)
                        .commit();
            }
            return true;
        }
        return false;
    }


    public void setEditMode(boolean edit){
        this.isEdit = edit;
    }

    private void switchLang(Context context, String langCode){
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        String currentTag = currentFragment != null ? currentFragment.getClass().getSimpleName() : null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            context.getSystemService(LocaleManager.class)
                    .setApplicationLocales(new LocaleList(Locale.forLanguageTag(langCode)));
        } else {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(langCode));
        }

        Intent refresh = new Intent(context, MainActivity.class);
        refresh.putExtra("fragment", currentTag);
        refresh.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(refresh);

        if (context instanceof Activity){
            ((Activity) context).finish();
        }
    }
}