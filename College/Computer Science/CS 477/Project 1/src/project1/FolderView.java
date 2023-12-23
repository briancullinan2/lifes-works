/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package project1;

import javax.swing.*;
import java.awt.*;
import javax.xml.transform.stream.*;
import javax.xml.transform.sax.*;
import javax.xml.transform.*;
import javax.xml.*;
import org.xml.sax.*;
import java.io.*;
import java.util.*;
import org.xml.sax.helpers.*;
import javax.swing.table.*;

/**
 *
 * @author Brian Cullinan
 */
public class FolderView extends JScrollPane implements ContentHandler
{
    
    JTable files;
    Address address;
    DefaultTableModel model;
    boolean column_empty[];
    TableColumn table_columns[];
    
    public FolderView(Address address)
    {
        this.address = address;
        // this is the main folder view
        
        files = new JTable();
        model = new DefaultTableModel();
        
        try {
            // get the columns from the server
            XMLReader xr = XMLReaderFactory.createXMLReader();
            xr.setContentHandler(this);
            xr.parse("http://dev.bjcullinan.com/plugins/display.php?template=extjs");
        } catch (Exception ex) {
            System.out.println(ex);
        }
            
        // get the files and folders
        refresh(address.address.getText());
        files.setModel(model);
        
        
        setViewportView(files);
        
        // hide blank columns
        TableColumnModel cm = files.getColumnModel();
        for(int i = 0; i < column_empty.length; i++)
        {
            if(table_columns[i] == null)
            {
                table_columns[i] = cm.getColumn(i);
            }
            
            if(column_empty[i])
            {
                cm.removeColumn(table_columns[i]);
            }
            else
            {
                boolean has_column = false;
                Enumeration columns = cm.getColumns();
                while(columns.hasMoreElements())
                {
                    if(columns.equals(table_columns[i]))
                    {
                        has_column = true;
                        break;
                    }
                    columns.nextElement();
                }
                if(has_column == false)
                {
                    cm.addColumn(table_columns[i]);
                    cm.moveColumn(cm.getColumnCount(), i);
                }
            }
        }
        
    }
    
    public void refresh(String address)
    {
        try {
            for(int i = 0; i < column_empty.length; i++)
                column_empty[i] = true;
            // get the columns from the server
            XMLReader xr = XMLReaderFactory.createXMLReader();
            xr.setContentHandler(this);
            xr.parse("http://dev.bjcullinan.com/plugins/select.php?template=extjs&dir=" + address + "&start=0&limit=300");
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
    
    public void setDocumentLocator (Locator locator)
    {   }
    
    public void startDocument ()
	throws SAXException
    {   }
    public void endDocument()
	throws SAXException
    {   }
    public void startPrefixMapping (String prefix, String uri)
	throws SAXException
    {   }
    public void endPrefixMapping (String prefix)
	throws SAXException
    {   }
    
    String current_el;
    String columns = "";
    Vector<String> column_arr;
    Vector<String> row;
    boolean in_file = false;
    
    public void startElement (String uri, String localName,
			      String qName, Attributes atts)
	throws SAXException
    {
        current_el = qName;
        if(current_el.equals("file"))
        {
            in_file = true;
            row = new Vector<String>(column_arr.size(), column_arr.size());
            for(int i = 0; i < column_arr.size(); i++)
                row.add("");
        }
    }
    public void endElement (String uri, String localName,
			    String qName)
	throws SAXException
    {

        if(qName.equals("columns"))
        {
            column_arr = new Vector<String>();
            String column_tmp[] = columns.split(",");
            column_empty = new boolean[column_tmp.length];
            table_columns = new TableColumn[column_tmp.length];
            int counter = 0;
            for(String column_str : column_tmp)
            {
                column_arr.add(column_str);
                model.addColumn(counter);
                counter++;
            }
        }
        if(qName.equals("file"))
        {
            in_file = false;
            model.addRow(row);
        }
        
        current_el = "";
    }
    public void characters (char ch[], int start, int length)
	throws SAXException
    {
        if(current_el.equals("columns"))
        {
            for(int i = start; i < start+length; i++)
            {
                columns += ch[i];
            }
        }
        if(in_file && current_el.length() > 5 && current_el.substring(0, 5).equals("info-"))
        {
            int index = column_arr.indexOf(current_el.substring(5, current_el.length()));
            if(index != -1)
            {
                String value = "";
                for(int i = start; i < start+length; i++)
                {
                    value += ch[i];
                }
                value = value.trim();
                if(!value.equals(""))
                    column_empty[index] = false;
                row.set(index, row.get(index) + value);
            }
        }
    }
    public void ignorableWhitespace (char ch[], int start, int length)
	throws SAXException
    {   }
    public void processingInstruction (String target, String data)
	throws SAXException
    {   }
    public void skippedEntity (String name)
	throws SAXException
    {   }

}
