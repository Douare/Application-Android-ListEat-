package com.example.listeat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class AddOrEditListActivity extends AppCompatActivity {

    //Instanciation de la classe de stockage
    MyDBHandler dbHandler;

    //Instanciation des composants
    ListView resultResearchListView;
    ListView contentListListView;
    EditText titleInputEditText;
    EditText inputResearchEditText;

    //Instanciation des attributs
    String nomList;
    ArrayList<String> mesIngredients;
    final ArrayList<String> ingredientApi = new ArrayList<>();
    final ArrayList<String> maListeB = new ArrayList<>();

    /**
     *
     * @param savedInstanceState
     * Permet de créer l'activité,
     * initialise la DataBase
     * Matching des composants
     * Recuperation des données envoyés depuis une autre activité
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_list);
        dbHandler = new MyDBHandler(this, null, null, 1);

        resultResearchListView =  findViewById(R.id.resultsReseach);
        contentListListView =  findViewById(R.id.contentList);
        titleInputEditText = findViewById(R.id.editTextTitleInput);
        inputResearchEditText = findViewById(R.id.editTextInputResearch);

        nomList = getIntent().getStringExtra("nomList");
        if (!"".equals(nomList)){
            putValuesInLists();
        }
    }

    /**
     *
     * @param view
     * Permet de questionner l'API en Background en appelant le service requestDatabaseApi()
     */
    public void dotheResearch(View view) {
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                requestDatabaseApi();
                return null;
            }
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                postExecuteRequest();
            }
        }.execute();
    }

    /**
     * Utilise un service de Reseaux afin de questionner une API
     * Lobjet de type JSON est recupere, traite et converti en liste
     */
    public void requestDatabaseApi(){
        if (inputResearchEditText.getText().toString().trim().length() >0){
            String research = inputResearchEditText.getText().toString().trim();
            ingredientApi.clear();
            ingredientApi.add(research);
            final String urlComplet = "https://api.spoonacular.com/food/ingredients/autocomplete?query="+research+"&apiKey=0438425f5d01479baf68e7739b0fa263";
            String data = "";
            String ligne = "";
            try {
                URL url = new URL(urlComplet);
                HttpURLConnection cnx = (HttpURLConnection) url.openConnection();
                InputStream inputStream = cnx.getInputStream();
                InputStreamReader lecture = new InputStreamReader(inputStream);
                BufferedReader buff = new BufferedReader(lecture);
                while ( (ligne=buff.readLine()) != null ){
                    data += ligne;
                }
                buff.close();
                lecture.close();
                JSONArray json = new JSONArray(data);
                //ingredientApi.add(research);
                for(int i=0; i<json.length();i++){
                    ingredientApi.add(json.getJSONObject(i).getString("name"));
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Apres la creation d'une liste, cette methode permet de faire
     * l'ajout ou la suppression d'un ingredient d'une liste a une autre
     * autrement dit ; permet de switcher un ingredient entre la liste de tous les ingredients
     * et la liste des ingredients souhaites
     */
    public void postExecuteRequest(){
        final ArrayAdapter<String> listViewAdapterH = new ArrayAdapter<String>(AddOrEditListActivity.this, android.R.layout.simple_list_item_1, ingredientApi);
        resultResearchListView.setAdapter(listViewAdapterH);
        final ArrayAdapter<String> listViewAdapterB = new ArrayAdapter<String>(AddOrEditListActivity.this, android.R.layout.simple_list_item_1, maListeB);
        contentListListView.setAdapter(listViewAdapterB);

        resultResearchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (!maListeB.contains(ingredientApi.get(position))){
                    maListeB.add(ingredientApi.get(position));
                    listViewAdapterB.notifyDataSetChanged();
                }

            }
        });

        contentListListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                maListeB.remove(maListeB.get(position));
                listViewAdapterB.notifyDataSetChanged();
            }
        });
    }

    /**
     * Si cette activité a ete ouvert afin de modifier une liste
     * alors cette methode est appele pour remplir la liste
     * qu'on souhaite mofidier
     */
    private void putValuesInLists() {
        mesIngredients = getIntent().getStringArrayListExtra("mesIngredients");
        maListeB.addAll(mesIngredients);

        final ArrayAdapter<String> listViewAdapterB = new ArrayAdapter<String>(AddOrEditListActivity.this, android.R.layout.simple_list_item_1, maListeB);
        contentListListView.setAdapter(listViewAdapterB);

        contentListListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                maListeB.remove(maListeB.get(position));
                listViewAdapterB.notifyDataSetChanged();
            }
        });

        titleInputEditText.setText(nomList);

    }

    /**
     * permet soit d'jouter une nouvelle liste
     * soit de modifier une liste existant quand le bouton buttonSaveList a ete sollicité
     * @param view
     */
    public void addNewListOrEditList(View view){
        if (nomList.equals("")){
            addNewList(view);
        } else {
            editList(view);
        }
    }

    /**
     * La methode s'assure l'exactitude des informations saisies
     * et modifie les informations souhaites dans la database
     * @param view
     */
    private void editList(View view) {
        if ((titleInputEditText.getText().toString().trim().length() != 0) && (maListeB.size() != 0) ){
            if (titleInputEditText.getText().toString().trim().equals(nomList) || !dbHandler.nameExistAlready(titleInputEditText.getText().toString().trim())){
                String mesIngredients = "";
                for (String str : maListeB) {
                    mesIngredients += str + ",";
                }
                MyList myList = new MyList(titleInputEditText.getText().toString(),mesIngredients);
                dbHandler.editIngredientsList(myList, nomList);

                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Your list name already exists.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Your list name or your list is empty.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * La methode s'assure l'exactitude des informations saisies
     * et ajoute une nouvelle liste dans la database
     * @param view
     */
    public void addNewList(View view) {
        if ((titleInputEditText.getText().toString().trim().length() != 0) && (maListeB.size() != 0) ){
            if (!dbHandler.nameExistAlready(titleInputEditText.getText().toString().trim())){
                String mesIngredients = "";
                for (String str : maListeB) {
                    mesIngredients += str + ",";
                }
                MyList myList = new MyList(titleInputEditText.getText().toString(),mesIngredients);
                dbHandler.addNewIngredientsList(myList);

                Intent intent = new Intent(this,MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(getApplicationContext(), "Your list name already exists.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Your list name or your list is empty.", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Annule l'action si le bouton annuler est sollicite
     * @param view
     */
    public void cancelTheAction(View view) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }

    /**
     * quitte l'activité
     */
    @Override
    public void finish() {
        super.finish();
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
    }
}


