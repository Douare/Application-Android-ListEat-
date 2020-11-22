package com.example.listeat;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

/**
 * La classe qui gere le stockage interne
 * DataBase
 */
public class MyDBHandler extends SQLiteOpenHelper{

    /**
     * declaration des constants necessaires pour la creation d'une base de données
     */
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "listStorage.db";
    public static final String TABLE_INGREDIENTS = "ingredients";
    public static final String COLUMN_NAME_LIST = "nameList";
    public static final String COLUMN_INGREDIENTS = "nameIngredient";


    //We need to pass database information along to superclass

    /**
     * Creation ed la base de données
     * @param context
     * @param name
     * @param factory
     * @param version
     */
    public MyDBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    /**
     * Creation de la table
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_INGREDIENTS + "(" +
                COLUMN_NAME_LIST + " TEXT, " +
                COLUMN_INGREDIENTS + " TEXT " +
                ");";
        db.execSQL(query);
    }

    /**
     * MAJ de la table en general
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INGREDIENTS);
        onCreate(db);
    }

    //Add a new row to the database

    /**
     * Ajout d'une nouvelle liste dans la base de données
     * @param myList contients le nom et le contenue de la liste 'ingredients
     */
    public void addNewIngredientsList(MyList myList){
        String mesIngredients = myList.getMesIngredients();
        String nomList = myList.getNomList();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME_LIST, nomList);
        values.put(COLUMN_INGREDIENTS,mesIngredients);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_INGREDIENTS, null, values);
        db.close();
    }

    /**
     * recuperation & conversion des listes d'ingredient depuis la base de donnees
     * en une liste pour l'affichage dans la page d'accueil
     * @return
     */
    public ArrayList<String> databaseToString(){

        ArrayList<String> list = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_INGREDIENTS;

        Cursor recordSet = db.rawQuery(query, null);

        recordSet.moveToFirst();

        while (!recordSet.isAfterLast()) {
            if (recordSet.getString(recordSet.getColumnIndex(COLUMN_NAME_LIST)) != null) {
                list.add(recordSet.getString(recordSet.getColumnIndex(COLUMN_NAME_LIST)));
            }
            recordSet.moveToNext();
        }
        db.close();
        return list;
    }

    /**
     * verification si le nom d'une liste existe deja dans la base de donnee
     * cette methode est appele a la fois lost d'une modification a la fois
     * lost de la creation d'une liste
     * @param trim
     * @return
     */
    public boolean nameExistAlready(String trim) {
        boolean exist = false;

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_INGREDIENTS;

        Cursor recordSet = db.rawQuery(query, null);
        recordSet.moveToFirst();

        while (!recordSet.isAfterLast()) {
            if (recordSet.getString(recordSet.getColumnIndex(COLUMN_NAME_LIST)) != null) {
                exist = (recordSet.getString(recordSet.getColumnIndex(COLUMN_NAME_LIST)).equals(trim));
            }
            recordSet.moveToNext();
        }
        db.close();

        return exist;
    }

    /**
     * modifie en meme temps le contenue et le nom de la liste
     * @param myList la liste a modifier
     * @param nomList nom de la liste a modifier
     */
    public void editIngredientsList(MyList myList, String nomList) {
        String newNom = myList.getNomList();
        String newIngredients = myList.getMesIngredients();

        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("UPDATE " + TABLE_INGREDIENTS + " SET "+ COLUMN_NAME_LIST + " = ?," + COLUMN_INGREDIENTS + " = ?" + " WHERE " + COLUMN_NAME_LIST + " = ? " ,
                new String[]{newNom,newIngredients,nomList});
        db.close();
    }

    /**
     * recuperer le contenue d'une liste de puis la base de donnee
     * @param nomListe nom de la lsite a recuperer
     * @return retourne la valeur sous forme d'une liste
     */
    public ArrayList<String> getListFromDB(String nomListe) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT "+ COLUMN_INGREDIENTS +" FROM " + TABLE_INGREDIENTS + " WHERE " + COLUMN_NAME_LIST + " = ?";
        ArrayList<String> displayedList = new ArrayList<String>();

        Cursor recordSet = db.rawQuery(query, new String[] { nomListe });
        recordSet.moveToFirst();

        while (!recordSet.isAfterLast()) {
            //On a un seul élément
            String strList = recordSet.getString(recordSet.getColumnIndex(COLUMN_INGREDIENTS));
            String[] tabList = strList.split(",");
            for (String st:tabList) {
                displayedList.add(st);
            }
            db.close();
            recordSet.moveToNext();
        }

        db.close();


        return displayedList;
    }

    /**
     * supprime liste de la base de donnes
     * @param nomList nom de la liste a supprimer
     */
    public void deleteList(String nomList) {
        SQLiteDatabase db = getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_INGREDIENTS + " WHERE " + COLUMN_NAME_LIST + " = ? " ,
                new String[]{nomList});
        db.close();
    }
}