package com.example.listeat;

/**
 * La class qui permet de stocker une liste d'ingredients
 * sous forme d'objet appelé MyList
 *
 * Cette classe contient seulement les accesseurs et les mutateurs des attributs
 */
public class MyList {

    private String nomList;
    private String mesIngredients;

    public MyList(String nomList, String mesIngredients) {
        this.nomList = nomList;
        this.mesIngredients = mesIngredients;
    }

    public String getNomList() {
        return nomList;
    }

    public void setNomList(String nomList) {
        this.nomList = nomList;
    }

    public String getMesIngredients() {
        return mesIngredients;
    }

    public void setMesIngredients(String mesIngredients) {
        this.mesIngredients = mesIngredients;
    }


}
