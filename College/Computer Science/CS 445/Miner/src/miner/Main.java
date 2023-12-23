/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package miner;

import java.util.*;
import java.io.*;


/**
 *
 * @author Brian Cullinan
 */
public class Main {

    public static final String[] localization = {
            "cell wall",
            "cytoplasm",
            "cytoskeleton",
            "endosome",
            "ER",
            "extracellular",
            "golgi",
            "integral membrane",
            "lipid particles",
            "mitochondria",
            "nucleus",
            "peroxisome",
            "plasma membrane",
            "transport vesicles",
            "vacuole"
    };

    public static ArrayList<Gene> read_genes(String filename)
    {
        // load in data
        ArrayList<Gene> new_data = new ArrayList<Gene>();
        try {
            InputStream instream = miner.Main.class.getResourceAsStream(filename);
            Scanner in = new Scanner(instream);
            String str;
            while (in.hasNext()) {
                String[] gene_data_buffer = in.nextLine().split(",");
                String[] gene_data = new String[gene_data_buffer.length];
                // fix commas in string values
                int count = 0;
                boolean in_str = false;
                for(int i = 0; i < gene_data_buffer.length; i++)
                {
                    if(gene_data_buffer[i].startsWith("\""))
                        in_str = true;
                    else
                        if(gene_data_buffer[i].endsWith("\""))
                            in_str = false;

                    if(gene_data_buffer[i].endsWith("."))
                        gene_data_buffer[i] = gene_data_buffer[i].substring(0, gene_data_buffer[i].length() - 1);

                    if(gene_data_buffer[i].equals("?"))
                        gene_data_buffer[i] = "";

                    if(gene_data[count] != null)
                        gene_data[count] += gene_data_buffer[i];
                    else
                        gene_data[count] = gene_data_buffer[i];

                    if(in_str == false)
                        count++;
                }

                Gene new_gene = new Gene(
                        gene_data[0],
                        gene_data[1],
                        gene_data[2],
                        gene_data[3],
                        gene_data[4],
                        gene_data[5],
                        gene_data[6].equals("") ? 0 : Integer.valueOf(gene_data[6]).intValue(),
                        gene_data[7],
                        gene_data[8]
                        );

                new_data.add(new_gene);

            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new_data;
    }

    public static ArrayList<Gene> read_keys(String filename)
    {
        // load in data
        ArrayList<Gene> new_data = new ArrayList<Gene>();
        try {
            InputStream instream = miner.Main.class.getResourceAsStream(filename);
            Scanner in = new Scanner(instream);
            String str;
            while (in.hasNext()) {
                String[] gene_data_buffer = in.nextLine().split(",");
                String[] gene_data = new String[gene_data_buffer.length];
                // fix commas in string values
                int count = 0;
                boolean in_str = false;
                for(int i = 0; i < gene_data_buffer.length; i++)
                {
                    if(gene_data_buffer[i].startsWith("\""))
                        in_str = true;
                    else
                        if(gene_data_buffer[i].endsWith("\""))
                            in_str = false;

                    if(gene_data_buffer[i].endsWith("."))
                        gene_data_buffer[i] = gene_data_buffer[i].substring(0, gene_data_buffer[i].length() - 1);

                    if(gene_data_buffer[i].equals("?"))
                        gene_data_buffer[i] = "";

                    if(gene_data[count] != null)
                        gene_data[count] += gene_data_buffer[i];
                    else
                        gene_data[count] = gene_data_buffer[i];

                    if(in_str == false)
                        count++;
                }

                Gene new_gene = new Gene(
                        gene_data[0],
                        "",
                        "",
                        "",
                        "",
                        "",
                        0,
                        "",
                        gene_data[1]
                        );

                new_data.add(new_gene);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new_data;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        ArrayList<Gene> training = read_genes("data/Genes_relation.data");

        ArrayList<Gene> testing = read_genes("data/Genes_relation.test");

        for(int j = 0; j < testing.size(); j++)
        {
            // classify the localization for all input data

            // loop through each possible localization
            String new_localization = "";
            double max_localization = 0;
            for(int i = 0; i < localization.length; i++)
            {
                double p_localization = 1.0 * p_localization(localization[i], training);
                double max = p_all(localization[i], training, testing.get(j)) * (p_localization / training.size());
                if(max > max_localization)
                {
                    max_localization = max;
                    new_localization = localization[i];
                }
            }
            testing.get(j).localization = new_localization;

            long percent = Math.round(1.0 * j / testing.size() * 1000);
            if(percent % 10 == 0)
                System.out.println(percent / 10.0);
        }

        // figure out how correct is was
        ArrayList<Gene> keys = read_keys("data/keys.txt");
        int correct = 0;
        String output = "";
        for(int i = 0; i < keys.size(); i++)
        {
            for(int j = 0; j < testing.size(); j++)
            {
                if(testing.get(j).label.equals(keys.get(i).label) &&
                        testing.get(j).localization.equals(keys.get(i).localization))
                {
                    correct++;
                    output += testing.get(j).label + "," + testing.get(j).localization + "\n";
                }
            }
        }

        // save output
        try{
            // Create file
            FileWriter fstream = new FileWriter("output.txt");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(output);
            //Close the output stream
            out.close();
        }catch (Exception e){
            //Catch exception if any
            e.printStackTrace();
        }

        System.out.println("Correct: " + correct + " out of " + testing.size());
    }

    public static int p_localization(String value, ArrayList<Gene> data)
    {
        int count = 0;
        for(int i = 0; i < data.size(); i++)
        {
            if(data.get(i).localization.equals(value))
            {
               count++;
            }
        }
        return (count == 0) ? 1 : count;
    }

    public static double p_all(String value, ArrayList<Gene> data, Gene gene)
    {
        double localization_count = 1.0 * p_localization(value, data);


        // find total essential values
        int count_essential = 0;
        int count_class = 0;
        int count_complex = 0;
        int count_phenotype = 0;
        int count_motif = 0;
        int count_chromosome = 0;

        for(int i = 0; i < data.size(); i++)
        {
            if(data.get(i).localization.equals(value) &&
                    (data.get(i).essential.equals(gene.essential) || data.get(i).essential.equals("")))
                count_essential++;

            if(data.get(i).localization.equals(value) &&
                    (data.get(i).gene_class.equals(gene.gene_class) || data.get(i).gene_class.equals("")))
                count_class++;

            if(data.get(i).localization.equals(value) &&
                    (data.get(i).complex.equals(gene.complex) || data.get(i).complex.equals("")))
                count_complex++;

            if(data.get(i).localization.equals(value) &&
                    (data.get(i).phenotype.equals(gene.phenotype) || data.get(i).phenotype.equals("")))
                count_phenotype++;

            if(data.get(i).localization.equals(value) &&
                    (data.get(i).motif.equals(gene.motif) || data.get(i).motif.equals("")))
                count_motif++;

            if(data.get(i).localization.equals(value) &&
                    (data.get(i).chromosome == gene.chromosome || data.get(i).chromosome == 0))
                count_chromosome++;

        }
        if(count_essential == 0)
            count_essential = 1;

        if(count_class == 0)
            count_class = 1;

        if(count_complex == 0)
            count_complex = 1;

        if(count_phenotype == 0)
            count_phenotype = 1;

        if(count_motif == 0)
            count_motif = 1;

        if(count_chromosome == 0)
            count_chromosome = 1;

        return (count_essential / localization_count) * (count_class / localization_count) *
                (count_complex / localization_count) * (count_phenotype / localization_count) *
                (count_motif / localization_count) * (count_chromosome / localization_count);
    }
}
