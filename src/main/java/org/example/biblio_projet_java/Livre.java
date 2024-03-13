package org.example.biblio_projet_java;

public class Livre {
    private String titre;
    private Auteur auteur;
    private String presentation;
    private int parution;
    private int colonne;
    private int rangee;

    public Livre(String titre, Auteur auteur, String presentation, int parution, int colonne, int rangee) {
        this.titre = titre;
        this.auteur = auteur;
        this.presentation = presentation;
        this.parution = parution;
        this.colonne = colonne;
        this.rangee = rangee;
    }

    public Livre() {
    }

    public String getTitre() {
        return titre;
    }

    public Auteur getAuteur() {
        return auteur;
    }

    public String getPresentation() {
        return presentation;
    }

    public int getParution() {
        return parution;
    }

    public int getColonne() {
        return colonne;
    }

    public int getRangee() {
        return rangee;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setAuteur(Auteur auteur) {
        this.auteur = auteur;
    }

    public void setPresentation(String presentation) {
        this.presentation = presentation;
    }

    public void setParution(int parution) {
        this.parution = parution;
    }

    public void setColonne(int colonne) {
        this.colonne = colonne;
    }

    public void setRangee(int rangee) {
        this.rangee = rangee;
    }

    @Override
    public String toString() {
        return "Livre{" +
                "titre='" + titre + '\'' +
                ", auteur=" + auteur +
                ", presentation='" + presentation + '\'' +
                ", parution=" + parution +
                ", colonne=" + colonne +
                ", rangee=" + rangee +
                '}';
    }

    public static void main(String[] args) {
        Auteur auteur = new Auteur("Hugo", "Victor");
        Livre livre = new Livre("Les Misérables", auteur, "Un roman historique", 1862, 1, 1);
        System.out.println(livre);
    }

}
