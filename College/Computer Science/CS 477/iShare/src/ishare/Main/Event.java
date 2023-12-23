/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ishare.Main;

import ishare.Main.Person;
import java.awt.*;
import javax.swing.*;
import ishare.People.*;
/**
 *
 * @author Brian Cullinan
 */
public class Event {
    public int year;
    public int month;
    public int day;

    public int minute;
    public int hour;
    public int second;

    public boolean repeating;
    
    public Person person;
    public Chore chore;
    
    public ImageIcon icon;

    public String title;
    public String notes;

    public Event (int year, int month, int day, Person person, Chore chore)
    {
        this.year = year;
        this.month = month;
        this.day = day;
        this.person = person;
        this.chore = chore;
    }
}
