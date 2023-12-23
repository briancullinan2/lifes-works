package ishare.Main;

import javax.swing.*;

/**
 * @author Jarid Bredemeier
 * @version 2009.11.04
 */

public class Chore {

    // Constructor(s)
    public Chore() {
        title = "undefined";
        description = "undefined";
        icon = null;
    }

    public Chore(String title, String description, ImageIcon icon) {
        this.title = title;
        this.icon = icon;
        this.description = description;
    }

    // Getter Setter Method(s)
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setIcon(ImageIcon icon) {
        this.icon = icon;
    }

    public ImageIcon getIcon() {
        return icon;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    // Class Varable(s)
    private String title;
    private ImageIcon icon;
    private String description;

}