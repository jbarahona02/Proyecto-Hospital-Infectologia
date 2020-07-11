/* 
    Programador: 
        Javier Alejandro Barahona Pasan
    Creación: 
        01/06/2019
    Modificaciones:
        Validación solo letras en nombres y apellidos 15/07/2019
*/
package org.javierbarahona.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.javierbarahona.bean.Especialidad;
import org.javierbarahona.db.Conexion;
import org.javierbarahona.sistema.Principal;

public class EspecialidadController implements Initializable {
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Especialidad> listaEspecialidad;
    @FXML private TextField txtNombreEspecialidad;
    @FXML private TableView tblEspecialidades;
    @FXML private TableColumn colCodigoEspecialidad;
    @FXML private TableColumn colNombreEspecialidad;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnCancelar;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblEspecialidades.setItems(getEspecialidades());
        colCodigoEspecialidad.setCellValueFactory(new PropertyValueFactory<Especialidad, Integer>("codigoEspecialidad"));
        colNombreEspecialidad.setCellValueFactory(new PropertyValueFactory<Especialidad, String>("nombreEspecialidad"));
    }
    
    public ObservableList<Especialidad> getEspecialidades(){
        ArrayList<Especialidad> lista = new ArrayList<Especialidad>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarEspecialidades()}");
            ResultSet resultado = procedimiento.executeQuery();
            while (resultado.next()){
                lista.add(new Especialidad (resultado.getInt("codigoEspecialidad"),
                                            resultado.getString("nombreEspecialidad")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaEspecialidad = FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if (tblEspecialidades.getSelectionModel().getSelectedItem() != null && tipoDeOperacion != operaciones.GUARDAR){
            txtNombreEspecialidad.setText(((Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem()).getNombreEspecialidad());
        }
    }
    
    public Especialidad  buscarEspecialidad(int codigoEspecialidad){
        Especialidad resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarEspecialidad(?)}");
            procedimiento.setInt(1, codigoEspecialidad);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Especialidad (registro.getInt("codigoCargo"),
                                              registro.getString("nombreCargo"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                desactivarControl();
                limpiarControl();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                tblEspecialidades.getSelectionModel().clearSelection();
            break;
            default:
                if(tblEspecialidades.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el elemento?","Eliminar Especialidad",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarEspecialidad(?)}");
                            procedimiento.setInt(1, ((Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem()).getCodigoEspecialidad()); 
                            procedimiento.execute();
                            listaEspecialidad.remove(tblEspecialidades.getSelectionModel().getSelectedIndex());
                            limpiarControl();
                            tblEspecialidades.getSelectionModel().clearSelection();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        tblEspecialidades.getSelectionModel().clearSelection();
                        limpiarControl();
                    }
                }else
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
            break;
        
        }
    }
    
    public boolean restriccionLetrasOEspacio(String cadena){
        boolean restriccion = false;
        for(int i = 0;i<cadena.length();i++){
            char caracter = cadena.charAt(i);
            if(Character.isLetter(caracter) || Character.isSpaceChar(caracter)){
                restriccion = true;
            }else{
                restriccion = false;
                i = cadena.length();
            }    
        }
        return restriccion;
    }
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControl();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                limpiarControl();
                tblEspecialidades.getSelectionModel().clearSelection();
            break;
            case GUARDAR:
                if(!txtNombreEspecialidad.getText().equals("")){
                    if(txtNombreEspecialidad.getLength() <= 45){
                        if(restriccionLetrasOEspacio(txtNombreEspecialidad.getText()) == true){
                            guardar();
                            desactivarControl();
                            limpiarControl();
                            btnNuevo.setText("Nuevo");
                            btnEliminar.setText("Eliminar");
                            btnEditar.setDisable(false);
                            tipoDeOperacion = operaciones.NINGUNO;
                            cargarDatos();
                        }else
                            JOptionPane.showMessageDialog(null,"Intento colocar números o caracteres especiales en el nombre de especialidad, revíselo e inténtelo de nuevo");
                    }else
                        JOptionPane.showMessageDialog(null,"El nombre de especialidad que ingreso supera el tamaño que puede ingresar");
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes intentelo de nuevo");  
            break;
        }
    }
    
    public void guardar(){
            Especialidad registro = new Especialidad();
            registro.setNombreEspecialidad(txtNombreEspecialidad.getText());
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarEspecialidad(?)}");
                procedimiento.setString(1, registro.getNombreEspecialidad());
                procedimiento.execute();
                listaEspecialidad.add(registro);
            }catch(Exception e){
                e.printStackTrace();
        }     
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblEspecialidades.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnCancelar.setDisable(false);
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControl();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else 
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
            break;
            case ACTUALIZAR:
                 if(!txtNombreEspecialidad.getText().equals("")){
                    if(txtNombreEspecialidad.getLength() <= 45){
                        if(restriccionLetrasOEspacio(txtNombreEspecialidad.getText()) == true){    
                            actualizar();
                            btnEditar.setText("Editar");
                            btnCancelar.setDisable(true);
                            tipoDeOperacion = operaciones.NINGUNO;
                            btnNuevo.setDisable(false);
                            btnEliminar.setDisable(false);
                            cargarDatos();
                            limpiarControl();
                            desactivarControl();
                            tblEspecialidades.getSelectionModel().clearSelection();
                        }else
                            JOptionPane.showMessageDialog(null,"Intento colocar números o caracteres especiales en el nombre de especialidad, revíselo e inténtelo de nuevo");
                    }else
                        JOptionPane.showMessageDialog(null,"El nombre de especialidad que ingreso supera el tamaño que puede ingresar");
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes intentelo de nuevo"); 
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarEspecialidad(?,?)}");
            Especialidad registro = (Especialidad)tblEspecialidades.getSelectionModel().getSelectedItem();
            registro.setNombreEspecialidad(txtNombreEspecialidad.getText());
            procedimiento.setInt(1, registro.getCodigoEspecialidad());
            procedimiento.setString(2, registro.getNombreEspecialidad());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void cancelar(){
         switch(tipoDeOperacion){
            case ACTUALIZAR:
                desactivarControl();
                limpiarControl();
                btnEditar.setText("Editar");
                btnCancelar.setDisable(true);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                tblEspecialidades.getSelectionModel().clearSelection();
            break;
        }
    }
    
    public void desactivarControl(){
        txtNombreEspecialidad.setEditable(false);
    }

    public void activarControl(){
        txtNombreEspecialidad.setEditable(true);
    }
    
    public void limpiarControl(){
        txtNombreEspecialidad.setText("");
    }
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void menuPrincipal(){
        escenarioPrincipal.menuPrincipal();
    }
    
    
    
    
}
