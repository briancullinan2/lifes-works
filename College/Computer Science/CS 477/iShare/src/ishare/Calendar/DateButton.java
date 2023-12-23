/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ishare.Calendar;

import ishare.Main.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;
/**
 *
 * @author Brian Cullinan
 */
public class DateButton extends JButton {
    public enum Type {GRAYED, TODAY, NORMAL}
    int year;
    int month;
    int day;
    
    Type type;
    
    TestFrame containedBy = null;
    
    public DateButton(JFrame containedBy, int year, int month, int day, Type type)
    {
        super();
        this.containedBy = (TestFrame)containedBy;
        
        this.year = year;
        this.month = month;
        this.day = day;
        
        this.type = type;
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        switch(this.type)
        {
            case GRAYED:
                g.setColor(new Color(128, 128, 128, 64));
                break;
            case TODAY:
                g.setColor(new Color(255, 255, 0, 64));
                break;
            case NORMAL:
                g.setColor(new Color(0, 128, 255, 64));
                break;
        }
        
        g.fillRoundRect(2, 2, this.getWidth()-4, this.getHeight()-4, 5, 5);
        
        //ImageIcon icon = containedBy.iconHashMap.get( "blueGlass.png" );
        //g.drawImage(icon.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
        
        g.setColor(new Color(0, 0, 128, 192));
        g.fillRoundRect(this.getWidth()-44, 4, 40, 50, 5, 5);
        
        g.setColor(Color.WHITE);
        g.setFont(new Font(g.getFont().getName(), g.getFont().getStyle(), 24));
        if((""+this.day).length() == 2)
            g.drawString(""+this.day, this.getWidth()-40, 34);
        else
            g.drawString(""+this.day, this.getWidth()-32, 34);
        
        int current_x = 10;
        int current_y = 10;
        
        // draw chores icons
        ArrayList<ishare.Main.Event> events = this.containedBy.getEvents(year, month, day);
        for(int i = 0; i < events.size(); i++)
        {
            Chore chore = events.get(i).chore;
            if(chore != null)
            {
                g.drawImage(chore.getIcon().getImage(), current_x, current_y, 32, 32, null);
                if(current_x + 32 > this.getWidth()-44)
                {
                    current_x = 10;
                    current_y += 32;
                }
                else
                {
                    current_x += 32;
                }
            }
        }
    }
}
