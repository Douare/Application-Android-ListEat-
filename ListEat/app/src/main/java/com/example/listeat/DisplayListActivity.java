package com.example.listeat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DisplayListActivity extends AppCompatActivity {

    /**
     * Creation et instanciation des attributs et des composants
     */
    TextView textViewDisplayedListTitle;
    TextView textViewCaddieLabel;
    ListView listViewContentList;
    ListView listViewCaddie;
    MyDBHandler dbHandler;
    ArrayList<String> listFromDB;
    ArrayList<String> caddieContent = new ArrayList<>();
    Button buttonShowCaddie;
    Button buttonShowList;
    Button buttonSaveList;
    Button buttonEditList;

    /**
     *
     * @param savedInstanceState
     * onCreate initialise l'activité et match les composants
     * ainsi, permet de gerer les listes des deux caddies d'ingrediends achetes / non achetes
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_list);

        listViewContentList = findViewById(R.id.listViewContentList);
        listViewCaddie = findViewById(R.id.listViewContentCaddie);
        buttonShowCaddie = findViewById(R.id.buttonShowCaddie);
        buttonShowList = findViewById(R.id.buttonShowList);
        buttonSaveList = findViewById(R.id.buttonSaveList);
        buttonEditList = findViewById(R.id.buttonEditList);
        textViewDisplayedListTitle = findViewById(R.id.textViewDisplayedListTitle);
        listViewContentList = findViewById(R.id.listViewContentList);

        dbHandler = new MyDBHandler(this, null, null, 1);
        String nomListe = getIntent().getStringExtra("nomListe");
        textViewDisplayedListTitle.setText(nomListe);

        listFromDB = dbHandler.getListFromDB(nomListe);
        //Récupération de la liste correspondant au nom "nomListe"
        final ArrayAdapter<String> displayedList = new ArrayAdapter<String>(DisplayListActivity.this, android.R.layout.simple_list_item_1, listFromDB );
        final ArrayAdapter<String> caddieContentAdapter = new ArrayAdapter<String>(DisplayListActivity.this, android.R.layout.simple_list_item_1, caddieContent );

        //Initialisation de textViewCaddieLabel
        setCaddieLabel();
        listViewContentList.setAdapter(displayedList);
        listViewContentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                caddieContent.add(listFromDB.get(position));
                caddieContentAdapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), listFromDB.get(position) + " added to your caddie", Toast.LENGTH_LONG).show();
                listFromDB.remove(listFromDB.get(position));
                displayedList.notifyDataSetChanged();
                setCaddieLabel();
            }
        });
        listViewCaddie.setAdapter(caddieContentAdapter);
        listViewCaddie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listFromDB.add(caddieContent.get(position));
                displayedList.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), listFromDB.get(position) + " removed from your caddie", Toast.LENGTH_LONG).show();
                caddieContent.remove(caddieContent.get(position));
                caddieContentAdapter.notifyDataSetChanged();
                setCaddieLabel();
            }
        });

    }

    /**
     * Affichage du message du caddie en cours
     */
    public void setCaddieLabel(){
        textViewCaddieLabel = findViewById(R.id.textViewCaddieLabel);
        if (caddieContent.size()>0){
            textViewCaddieLabel.setText("Here are the items you put in your caddie.");
        }else{
            textViewCaddieLabel.setText("Your caddie is empty.");
        }
    }

    /**
     * Recuperation des données envoyes depuis une autres activités
     * @param view
     */
    public void addOrEditListFromDisplayList(View view) {
        Intent intent = new Intent(this,AddOrEditListActivity.class);
        intent.putExtra("nomList", textViewDisplayedListTitle.getText().toString());
        intent.putExtra("mesIngredients", listFromDB);
        startActivity(intent);
    }

    /**
     * permet de switcher entres les éléments achetés / non achetés
     * @param view
     */
    public void showCaddie(View view) {
        if (listViewContentList.getVisibility() == View.VISIBLE) {
            listViewContentList.setVisibility(View.INVISIBLE);
            buttonShowCaddie.setVisibility(View.INVISIBLE);
            buttonEditList.setVisibility(View.INVISIBLE);
            buttonSaveList.setVisibility(View.INVISIBLE);
            listViewCaddie.setVisibility(View.VISIBLE);
            buttonShowList.setVisibility(View.VISIBLE);
            textViewCaddieLabel.setVisibility(View.VISIBLE);

        }
    }

    /**
     * permet de switcher entres les éléments achetés / non achetés
     * @param view
     */
    public void showList(View view) {
        if (listViewCaddie.getVisibility() == View.VISIBLE) {
            listViewCaddie.setVisibility(View.INVISIBLE);
            buttonShowList.setVisibility(View.INVISIBLE);
            textViewCaddieLabel.setVisibility(View.INVISIBLE);
            listViewContentList.setVisibility(View.VISIBLE);
            buttonShowCaddie.setVisibility(View.VISIBLE);
            buttonEditList.setVisibility(View.VISIBLE);
            buttonSaveList.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Permet d'engregistrer les modification faites les les caddies
     * @param view
     */
    public void saveChanges(View view) {
        String mesIngredients = "";
        for (String str : listFromDB) {
            mesIngredients += str + ",";
        }
        MyList myList = new MyList(textViewDisplayedListTitle.getText().toString(),mesIngredients);
        dbHandler.editIngredientsList(myList,textViewDisplayedListTitle.getText().toString());

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    /**
     * Annule l'action si le boutton annuler a ete clické
     * @param view
     */
    public void cancelTheAction(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}