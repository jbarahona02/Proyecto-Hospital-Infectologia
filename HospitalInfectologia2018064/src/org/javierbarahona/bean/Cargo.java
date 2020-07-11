/* 
    Programador: 
        Javier Alejandro Barahona Pasan
    Creación: 
        04/06/2019
*/
package org.javierbarahona.bean;


public class Cargo {
   private int codigoCargo;
   private String nombreCargo;

    public Cargo() {
    }

    public Cargo(int codigoCargo, String nombreCargo) {
        this.codigoCargo = codigoCargo;
        this.nombreCargo = nombreCargo;
    }

    public int getCodigoCargo() {
        return codigoCargo;
    }

    public void setCodigoCargo(int codigoCargo) {
        this.codigoCargo = codigoCargo;
    }

    public String getNombreCargo() {
        return nombreCargo;
    }

    public void setNombreCargo(String nombreCargo) {
        this.nombreCargo = nombreCargo;
    }
   
    public String toString(){
        return getCodigoCargo() + "| " + getNombreCargo();
    }
   
}
