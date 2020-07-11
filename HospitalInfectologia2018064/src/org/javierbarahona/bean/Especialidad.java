/* 
    Programador: 
        Javier Alejandro Barahona Pasan
    Creación: 
        04/06/2019
*/
package org.javierbarahona.bean;


public class Especialidad {
    private int codigoEspecialidad;
    private String nombreEspecialidad;

    public Especialidad() {
    }

    public Especialidad(int codigoEspecialidad, String nombreEspecialidad) {
        this.codigoEspecialidad = codigoEspecialidad;
        this.nombreEspecialidad = nombreEspecialidad;
    }

    public int getCodigoEspecialidad() {
        return codigoEspecialidad;
    }

    public void setCodigoEspecialidad(int codigoEspecialidad) {
        this.codigoEspecialidad = codigoEspecialidad;
    }

    public String getNombreEspecialidad() {
        return nombreEspecialidad;
    }

    public void setNombreEspecialidad(String nombreEspecialidad) {
        this.nombreEspecialidad = nombreEspecialidad;
    }
    
    public String toString(){
        return getCodigoEspecialidad() + "| " + getNombreEspecialidad();
    }
    
}

