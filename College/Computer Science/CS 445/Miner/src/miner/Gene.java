/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package miner;

/**
 *
 * @author Brian Cullinan
 */

public class Gene {
    protected String label, essential, gene_class, complex, phenotype,
            motif, function, localization;
    protected int chromosome;

    public Gene(String label, String essential, String gene_class, String complex, String phenotype,
            String motif, int chromosome, String function, String localization)
    {
        this.label = label;
        this.essential = essential;
        this.gene_class = gene_class;
        this.complex = complex;
        this.phenotype = phenotype;
        this.motif = motif;
        this.chromosome = chromosome;
        this.function = function;
        this.localization = localization;

    }
}
