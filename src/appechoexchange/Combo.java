/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package appechoexchange;

/**
 *
 * @author oleg
 */
public class Combo {

private int id;
private String name;

public Combo(int id, String name) {
    super();
    this.id = id;
    this.name = name;
}

public int getId() {
    return id;
}

public void setId(int id) {
    this.id = id;
}

public String getName() {
    return name;
}

public void setName(String name) {
    this.name = name;
}
@Override
public String toString() {
    return this.getName();
}
}