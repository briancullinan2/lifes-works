/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package project2;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.*;
import java.awt.event.*;
/**
 *
 * @author Brian Cullinan
 */
public class HighlightButton extends JButton {

    public boolean highlighted = false;
    public Image background_image;
    
    public HighlightButton(String title)
    {
        super(title);
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if(background_image != null)
        {
            g.drawImage(background_image, 10, 10, 60, 60, null);
        }
        if(highlighted)
        {
            float HSBvalues[] = new float[3];
            Color.RGBtoHSB(this.getBackground().getRed(), this.getBackground().getGreen(), this.getBackground().getBlue(), HSBvalues);
            
            HSBvalues[0] += .5;
            if(HSBvalues[0] > 1.0)
                HSBvalues[0] -= 1.0;

            Color tmpcolor;
            if(HSBvalues[1] == 0)
            {
                tmpcolor = Color.YELLOW;
            }
            else
            {
                tmpcolor = Color.getHSBColor(HSBvalues[0], HSBvalues[1], HSBvalues[2]);
            }
            double tmpred = (this.getBackground().getRed() - tmpcolor.getRed()) * .05;
            double tmpgreen = (this.getBackground().getGreen() - tmpcolor.getGreen()) * .05;
            double tmpblue = (this.getBackground().getBlue() - tmpcolor.getBlue()) * .05;
            
            //if(tmpcolor.getGreen() > this.getBackground().getGreen()) tmpgreen = tmpcolor.getGreen() - this.getBackground().getGreen();
                        
            int red = tmpcolor.getRed();
            int green = tmpcolor.getGreen();
            int blue = tmpcolor.getBlue();
              
            for(int i = 0; i < 20; i++)
            {
                red += tmpred;
                green += tmpgreen;
                blue += tmpblue;
                
                if(red > 255) red = 255;
                if(red < 0) red = 0;
                if(green > 255) green = 255;
                if(green < 0) green = 0;
                if(blue > 255) blue = 255;
                if(blue < 0) blue = 0;
                
                g.setColor(new Color(red, green, blue));
                g.drawRect(i, i, 80 - i*2, 80 - i*2);
            }
        }
    }
    
    
}
