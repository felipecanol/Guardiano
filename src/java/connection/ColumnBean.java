/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package connection;

/**
 *
 * @author jhonf
 */
public class ColumnBean {

    private String name;
    private String type;
    private int character_maximum_length;

    public ColumnBean() {
        this.character_maximum_length = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCharacter_maximum_length() {
        return character_maximum_length;
    }

    public void setCharacter_maximum_length(int character_maximum_length) {
        this.character_maximum_length = character_maximum_length;
    }

}
