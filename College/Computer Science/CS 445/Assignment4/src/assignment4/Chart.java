/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package assignment4;

/**
 *
 * @author Brian Cullinan
 */
public class Chart {
    double values[];
    int cluster = 0;

    public Chart(double[] values)
    {
        this.values = values;
    }

    public Chart(String[] values)
    {
        this.values = new double[values.length];
        for(int i = 0; i < values.length; i++)
            this.values[i] = Double.parseDouble(values[i]);
    }

    public Chart(String[] values, boolean cluster)
    {
        this.values = new double[values.length-2];
        for(int i = 0; i < values.length-2; i++)
            this.values[i] = Double.parseDouble(values[i]);
        int start = values[values.length-1].indexOf("_")+1;
        int end = values[values.length-1].length();
        this.cluster = Integer.parseInt(values[values.length-1].substring(start, end));
    }

    public double max()
    {
        double result = 0;
        for(int i = 0; i < this.values.length; i++)
        {
            if(this.values[i] > result)
                result = this.values[i];
        }
        return result;
    }

    public double min(double max)
    {
        double result = max;
        for(int i = 0; i < this.values.length; i++)
        {
            if(this.values[i] < result)
                result = this.values[i];
        }
        return result;
    }

    public double normal(int index)
    {
        double max = this.max();
        double min = this.min(max);
        return (this.values[index] - min) / (max - min);
    }

    public double dist(Chart other)
    {
        double sum = 0;
        for(int i = 0; i < Math.min(this.values.length, other.values.length); i++)
        {
            sum += Math.pow(this.normal(i) - other.normal(i), 2);
        }
       
        return Math.sqrt(sum);
    }
}
