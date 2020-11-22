package com.example.listeat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ExampleDialog.ExampleDialogListener {
    MyDBHandler dbHandler;
    ListView listsListView;
    TextView textViewLabel;
    ArrayList<String> maListe = new ArrayList<>();

    int pos;

    /**
     * Cree l'activité recupere les composants et attributs
     * gere l'affichage de toutes les listes des ingredients
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listsListView = findViewById(R.id.listViewLists);
        textViewLabel = findViewById(R.id.textViewLabel);
        dbHandler = new MyDBHandler(this, null, null, 1);
        maListe = dbHandler.databaseToString();

        if (maListe.size()>0) {

            textViewLabel.setText("Click on a list to go shopping !");
            final ArrayAdapter<String> listViewAdapterMaListe = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, maListe);
            listsListView.setAdapter(listViewAdapterMaListe);

            listsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    displayContent(maListe.get(position));
                }
            });

            listsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    String nomList = maListe.get(position);
                    pos=position;
                    openDialog(nomList);
                    return true;
                }
            });
        }else{
            textViewLabel.setText("Create a list clicking the Add button !");
        }
    }

    /**
     * permet de passer a l'activité AddOrEditListActivity
     * @param view
     */
    public void addOrEditList(View view) {
        Intent intent = new Intent(this,AddOrEditListActivity.class);
        intent.putExtra("nomList","");
        startActivity(intent);
    }

    /**
     * permet de passer a l'activité DisplayListActivity
     * @param nomListe
     */
    public void displayContent(String nomListe) {
        Intent intent = new Intent(this,DisplayListActivity.class);
        intent.putExtra("nomListe", nomListe );
        startActivity(intent);
    }

    /**
     * Supprimer la liste de database
     * @param nomList
     */
    @Override
    public void deleteList(String nomList) {
        dbHandler.deleteList(nomList);

        final ArrayAdapter<String> listViewAdapterMaListe = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, maListe);
        listsListView.setAdapter(listViewAdapterMaListe);
        maListe.remove(pos);
        listViewAdapterMaListe.notifyDataSetChanged();
        Toast.makeText(getApplicationContext(), "You deleted " + nomList + " with success.", Toast.LENGTH_LONG).show();

    }

    /**
     * ouvre le Pop-Up pour supprimer une liste
     * @param nomList
     */
    private void openDialog(String nomList) {
        ExampleDialog exampleDialog = new ExampleDialog();
        Bundle bundle = new Bundle();
        bundle.putString("nomList", nomList);
        exampleDialog.setArguments(bundle);
        exampleDialog.show(getSupportFragmentManager(), "Example dialog");

    }

    /**
     * quitte activité
     */
    @Override
    public void finish() {
        super.finish();
        /*Intent intent = this.getIntent();
        startActivity(intent);*/
    }
}
