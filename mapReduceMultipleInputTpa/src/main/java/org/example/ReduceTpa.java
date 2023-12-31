package org.example;

import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

public class ReduceTpa extends   Reducer<Text, Text, NullWritable, Text> {
    private static final DecimalFormat df = new DecimalFormat("0.00");
    static private boolean node_was_initialized = false;
    String moyCo2;
    // La fonction REDUCE elle-meme. Les arguments: la clef key (d'un type generique K), un Iterable de toutes les valeurs
    // qui sont associees a la clef en question, et le contexte Hadoop (un handle qui nous permet de renvoyer le resultat a Hadoop).
    protected void reduce(Text key, Iterable<Text> values, Context context) throws IOException,InterruptedException {

        NullWritable nullWritable = NullWritable.get();
        ArrayList<String> list = new ArrayList<>();
        String valeurCO2 = "";


        /*
         * nous savons bien que la clé AAAA sera traiter en premier,
         * donc en stock sa valeur dans sle variable de class moyenTotal pour s'on servir plus tard
         */
        if (key.toString().equals("AAAA")) {
            for (Text val : values) {
                if (val != null || !val.equals("null") || val.getLength() > 0){
                    this.moyCo2 = val.toString();
                }
                else {
                    this.moyCo2 = "val = null";
                }
            }
        } else {
            /*
             * Initialiser la valeur de CO2Columns avec la moyenne, car on ne sait pas si elle sera overrided ou pas.
             * cela depend si la marque dans catalogue exist dans la table CO2
             */
            valeurCO2 = this.moyCo2;

            //boucler sur les vals
            for (Text val : values) {

                String valueString = val.toString();

                //recuperer la valeur du tag pour destinguer les deux table catalogue et co2
                String tag = valueString.split(":")[0];

                /*
                 * ovveride la valeur moyenne de toute la table co2 avec la valeur moyenne de la marque seul
                 * si il n'exist pas d'une valeur avec un tag co2 cela veut dire que la marque n'exist pas dans  la table co2,
                 * et donc elle garde la valeur moyenne de toute la table ( regarder en haut )
                 */
                if (tag.equals("CO2")) {
                    valeurCO2 = valueString.split(":")[1];
                }
                if (tag.equals("CATALOGUE")) {
                    list.add(valueString.split(":")[1]);
                }
                }
            }
            //cette boucle nous permette de concatener le moyen d'une marque dans CO2
            // avec tout les ligne de cette marque dans catalogue, si la marque n'exist pas dans table CO2 elle prends la valeur moyenne
            for (String myVal : list) {
                if (valeurCO2 == null || valeurCO2.length() == 0 || valeurCO2.equals("null")){
                    valeurCO2 = this.moyCo2;
                    context.write(nullWritable, new Text(key + "," + myVal + "," + "je suiss nuuuull" + this.moyCo2));
                }
                else {
                    context.write(nullWritable, new Text(key + "," + myVal + "," + valeurCO2));
                }

            }
        }


    }


