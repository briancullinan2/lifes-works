/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package assignment4;

import java.util.*;
import java.io.*;
import java.awt.*;
import javax.imageio.*;
import java.awt.image.*;

/**
 *
 * @author Brian Cullinan
 */
public class Main {

    public static ArrayList<Chart> read_charts(String filename)
    {
        ArrayList<Chart> result = new ArrayList<Chart>();
        // load in data
        try {
            InputStream instream = assignment4.Main.class.getResourceAsStream(filename);
            Scanner in = new Scanner(instream);
            while (in.hasNext()) {
                String[] chart_data = in.nextLine().split("\\s");
                if(chart_data.length > 0)
                    result.add(new Chart(chart_data));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<Chart> read_known_charts(String filename)
    {
        ArrayList<Chart> result = new ArrayList<Chart>();
        // load in data
        try {
            InputStream instream = assignment4.Main.class.getResourceAsStream(filename);
            Scanner in = new Scanner(instream);
            in.nextLine();
            while (in.hasNext()) {
                String[] chart_data = in.nextLine().split("\\s");
                if(chart_data.length > 2)
                    result.add(new Chart(chart_data, true));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // load every chart
        ArrayList<Chart> raw = read_charts("data/synthetic_control_data.txt");
        Chart[] centroids = get_random_centroids(6, raw);

        for(int j = 0; j < 4; j++)
        {
            // reassign each chart to the closest cluster
            k_means_assign(centroids, raw);

            // update means
            for(int i = 0; i < centroids.length; i++)
            {
                centroids[i] = k_means_update(i, raw);
            }
        }

        // load rapid miner results
        ArrayList<Chart> rapid_miner = read_known_charts("data/assignment4.csv");
      
        // create output of sample data
        save_results(centroids, raw, "output");
        save_results(centroids, rapid_miner, "rapid_miner_output");

   }

    public static void save_results(Chart[] centroids, ArrayList<Chart> raw, String name)
    {
        // create image
        int largest_count = 0;
        for(int j = 0; j < centroids.length; j++)
        {
            int count = 0;
            for(int i = 0; i < raw.size(); i++)
            {
                if(raw.get(i).cluster == j)
                    count++;
            }
            if(count > largest_count)
                largest_count = count;
        }

        BufferedImage image = new BufferedImage(centroids.length * 200, largest_count*30, BufferedImage.TYPE_INT_ARGB);
        Graphics2D draw = image.createGraphics();
        String output1 = "";
        for(int j = 0; j < centroids.length; j++)
        {
            int count = 0;
            for(int i = 0; i < raw.size(); i++)
            {
                if(raw.get(i).cluster == j)
                {
                    draw_chart(raw.get(i), draw, j*200, count*30, 200, 30);
                    count++;
                }
                output1 += i + "," + raw.get(i).cluster + "\n";
            }
        }

        try{
            ImageIO.write(image, "PNG", new File(name + ".png"));
            // Create file
            FileWriter fstream = new FileWriter(name + ".txt");
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(output1);
            //Close the output stream
            out.close();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void draw_chart(Chart chart, Graphics2D draw, int left, int top, double width, double height)
    {
        draw.setPaint(Color.WHITE);
        draw.fillRect(left, top, (int)width, (int)height);
        switch(chart.cluster)
        {
            case 0:
                draw.setPaint(Color.GREEN);
                break;
            case 1:
                draw.setPaint(Color.BLUE);
                break;
            case 2:
                draw.setPaint(Color.RED);
                break;
            case 3:
                draw.setPaint(Color.YELLOW);
                break;
            case 4:
                draw.setPaint(Color.MAGENTA);
                break;
            case 5:
                draw.setPaint(Color.BLACK);
                break;
        }
        double max = chart.max();
        double min = chart.min(max);
        for(int i = 0; i < chart.values.length-1; i++)
        {
            int x1 = (int)(width / chart.values.length * i) + left;
            int y1 = (int)((chart.values[i] - min) / (max - min) * height) + top;
            int x2 = (int)(width / chart.values.length * (i+1)) + left;
            int y2 = (int)((chart.values[i+1] - min) / (max - min) * height) + top;
            draw.drawLine(x1, y1, x2, y2);
       }
    }

    public static void k_means_assign(Chart[] centroids, ArrayList<Chart> list)
    {
        // put everything in the closest cluster
        for(int j = 0; j < list.size(); j++)
        {
            for(int i = 0; i < centroids.length; i++)
            {
                if(list.get(j).dist(centroids[i]) < list.get(j).dist(centroids[list.get(j).cluster]))
                {
                    Chart item = list.get(j);
                    item.cluster = i;
                    list.set(j, item);
                }
            }
        }
    }

    public static Chart k_means_update(int cluster, ArrayList<Chart> list)
    {
        double[] values = new double[60];
        for(int i = 0; i < 60; i++)
        {
            values[i] = 0;
            for(int j = 0; j < list.size(); j++)
            {
                if(list.get(j).cluster == cluster)
                    values[i] += list.get(j).values[i];
            }
            values[i] = values[i] / 60;
        }
        return new Chart(values);
    }

    public static Chart get_random_centroid(ArrayList<Chart> list)
    {
        // select random values between min and max
        return list.get((int)(list.size() * Math.random()));
    }

    public static Chart[] get_random_centroids(int k, ArrayList<Chart> list)
    {
        Chart[] result = new Chart[k];

        for(int i = 0; i < k; i++)
        {
            result[i] = get_random_centroid(list);
        }

        return result;
    }
}
