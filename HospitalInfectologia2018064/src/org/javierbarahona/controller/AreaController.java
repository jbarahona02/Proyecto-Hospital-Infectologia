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
import org.javierbarahona.bean.Area;
import org.javierbarahona.db.Conexion;
import org.javierbarahona.sistema.Principal;


public class AreaController implements Initializable{
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Area> listaArea;
    @FXML private TextField txtNombreArea;
    @FXML private TableView tblAreas;
    @FXML private TableColumn colCodigoArea;
    @FXML private TableColumn colNombreArea;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnCancelar;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblAreas.setItems(getAreas());
        colCodigoArea.setCellValueFactory(new PropertyValueFactory<Area, Integer>("codigoArea"));
        colNombreArea.setCellValueFactory(new PropertyValueFactory<Area, String>("nombreArea"));
        
    }
    
    public ObservableList<Area> getAreas(){
        ArrayList<Area> lista = new ArrayList<Area>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarAreas()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Area(resultado.getInt("codigoArea"),
                                   resultado.getString("nombreArea")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaArea = FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblAreas.getSelectionModel().getSelectedItem() != null && tipoDeOperacion != operaciones.GUARDAR){
            txtNombreArea.setText(((Area)tblAreas.getSelectionModel().getSelectedItem()).getNombreArea());
        }
    }
    
    public Area buscarArea(int codigoArea){
        Area resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarArea(?)}");
            procedimiento.setInt(1, codigoArea);            
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Area (registro.getInt("codigoArea"),
                                      registro.getString("nombreArea"));
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
                tblAreas.getSelectionModel().clearSelection();
            break;
            default:
                if(tblAreas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el registro?","Eliminar Área",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarArea(?)}");
                            procedimiento.setInt(1, ((Area)tblAreas.getSelectionModel().getSelectedItem()).getCodigoArea());
                            procedimiento.execute();
                            listaArea.remove(tblAreas.getSelectionModel().getSelectedIndex());
                            limpiarControl();
                            tblAreas.getSelectionModel().clearSelection();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        tblAreas.getSelectionModel().clearSelection();
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
                tblAreas.getSelectionModel().clearSelection();
            break;
            case GUARDAR:
                if(!txtNombreArea.getText().equals("")){
                    if(txtNombreArea.getLength() <= 45){
                        if(restriccionLetrasOEspacio(txtNombreArea.getText()) == true){
                            guardar();
                            desactivarControl();
                            limpiarControl();
                            btnNuevo.setText("Nuevo");
                            btnEliminar.setText("Eliminar");
                            btnEditar.setDisable(false);
                            tipoDeOperacion = operaciones.NINGUNO;
                            cargarDatos();
                        }else
                            JOptionPane.showMessageDialog(null,"Intento colocar números o caracteres especiales en el nombre de área, revíselo e inténtelo de nuevo");
                    }else
                        JOptionPane.showMessageDialog(null,"El nombre de área que ingreso supera el tamaño que puede ingresar");
                 }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo");
            break;  
        }
    }
    
    public void guardar(){
            Area registro = new Area();
            registro.setNombreArea(txtNombreArea.getText());
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call  sp_AgregarArea(?)}");
                procedimiento.setString(1, registro.getNombreArea());
                procedimiento.execute();
                listaArea.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }
    }
     
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblAreas.getSelectionModel().getSelectedItem() != null){
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
                if(!txtNombreArea.getText().equals("")){
                    if(txtNombreArea.getLength() <= 45){
                        if(restriccionLetrasOEspacio(txtNombreArea.getText()) == true){
                            actualizar();
                            btnEditar.setText("Editar");
                            btnCancelar.setDisable(true);
                            tipoDeOperacion = operaciones.NINGUNO;
                            btnNuevo.setDisable(false);
                            btnEliminar.setDisable(false);
                            cargarDatos();
                            limpiarControl();
                            desactivarControl();
                            tblAreas.getSelectionModel().clearSelection();
                        }else
                            JOptionPane.showMessageDialog(null,"Intento colocar números o caracteres especiales en el nombre de área, revíselo e inténtelo de nuevo");
                    }else
                        JOptionPane.showMessageDialog(null,"El nombre de área que ingreso supera el tamaño que puede ingresar");
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo");
            break;
        }       
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarArea(?,?)}");
            Area registro = (Area)tblAreas.getSelectionModel().getSelectedItem();
            registro.setNombreArea(txtNombreArea.getText());
            procedimiento.setInt(1, registro.getCodigoArea());
            procedimiento.setString(2, registro.getNombreArea());
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
                tblAreas.getSelectionModel().clearSelection();
            break;
        }
    }
    
    public void desactivarControl(){
        txtNombreArea.setEditable(false);
    }
    
    public void activarControl(){
        txtNombreArea.setEditable(true);
    }
    
    public void limpiarControl(){
        txtNombreArea.setText("");
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
