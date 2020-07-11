/*
    Programador 
        Javier Alejandro Barahona Pasan
    Creación:
        23/07/2019
*/

package org.javierbarahona.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.javierbarahona.bean.ControlCita;
import org.javierbarahona.bean.Receta;
import org.javierbarahona.db.Conexion;
import org.javierbarahona.sistema.Principal;

public class RecetaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones {NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,NINGUNO,CANCELAR};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Receta> listaReceta;
    private ObservableList<ControlCita> listaControlCita;
    @FXML private TextField txtDescripcionReceta;
    @FXML private ComboBox cmbCodigoControlCita;
    @FXML private TableView tblRecetas;
    @FXML private TableColumn colCodigoReceta;
    @FXML private TableColumn colDescripcion;
    @FXML private TableColumn colCodigoControlCita;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnCancelar;
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoControlCita.setItems(getControlCitas());
    }
    
    public void cargarDatos(){
        tblRecetas.setItems(getRecetas());
        colCodigoReceta.setCellValueFactory(new PropertyValueFactory<Receta, Integer>("codigoReceta"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<Receta, String>("descripcionReceta"));
        colCodigoControlCita.setCellValueFactory(new PropertyValueFactory<Receta, Integer>("codigoControlCita"));
    }
    
    public ObservableList<Receta> getRecetas(){
        ArrayList<Receta> lista = new ArrayList<Receta>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarRecetas()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Receta(resultado.getInt("codigoReceta"),
                                     resultado.getString("descripcionReceta"),
                                     resultado.getInt("codigoControlCita")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaReceta = FXCollections.observableList(lista);
    }
    
     public ObservableList<ControlCita> getControlCitas(){
        ArrayList<ControlCita> lista = new ArrayList<ControlCita>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarControlCitas()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new ControlCita(resultado.getInt("codigoControlCita"),
                                          resultado.getDate("fecha"),
                                          resultado.getString("horaInicio"),
                                          resultado.getString("horaFin"),
                                          resultado.getInt("codigoMedico"),
                                          resultado.getInt("codigoPaciente")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaControlCita = FXCollections.observableList(lista);
    }
    
    public void seleccionarElementos(){
        if(tblRecetas.getSelectionModel().getSelectedItem() != null && tipoDeOperacion != operaciones.GUARDAR){
            txtDescripcionReceta.setText(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getDescripcionReceta());
            cmbCodigoControlCita.getSelectionModel().select(buscarControlCita(((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoControlCita()));
        }
    }
    
    public Receta buscarReceta(int codigoReceta){
        Receta resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarReceta(?)}");
            procedimiento.setInt(1, codigoReceta);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Receta(registro.getInt("codigoReceta"),
                                       registro.getString("descripcionReceta"),
                                       registro.getInt("codigoControlCita"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
     public ControlCita buscarControlCita(int codigoControlCita){
        ControlCita resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarControlCita(?)}");
            procedimiento.setInt(1, codigoControlCita);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new ControlCita(registro.getInt("codigoControlCita"),
                                          registro.getDate("fecha"),
                                          registro.getString("horaInicio"),
                                          registro.getString("horaFin"),
                                          registro.getInt("codigoMedico"),
                                          registro.getInt("codigoPaciente"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
     
    public void eliminar(){
        switch(tipoDeOperacion){
            case GUARDAR:
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                tblRecetas.getSelectionModel().clearSelection();
            break;
            default:
                if(tblRecetas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el registro?","Eliminar Receta",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarReceta(?)}");
                            procedimiento.setInt(1, ((Receta)tblRecetas.getSelectionModel().getSelectedItem()).getCodigoReceta());
                            procedimiento.execute();
                            listaReceta.remove(tblRecetas.getSelectionModel().getSelectedIndex());
                            tblRecetas.getSelectionModel().clearSelection();
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else
                        tblRecetas.getSelectionModel().clearSelection();
                        limpiarControles();
                } else
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
            break;
        }
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                tblRecetas.getSelectionModel().clearSelection();
                limpiarControles();
            break;
            case GUARDAR:
                if(!txtDescripcionReceta.getText().equals("") && cmbCodigoControlCita.getSelectionModel().getSelectedItem() != null){
                    if(txtDescripcionReceta.getLength() <= 45){
                        guardar();
                        btnNuevo.setText("Nuevo");
                        btnEliminar.setText("Eliminar");
                        btnEditar.setDisable(false);
                        tipoDeOperacion = operaciones.NINGUNO;
                        tblRecetas.getSelectionModel().clearSelection();
                        limpiarControles();
                        desactivarControles();
                        cargarDatos();
                    }else
                        JOptionPane.showMessageDialog(null,"El dato que ingreso como descripción de receta excede de la cantidad caracteres posibles");
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso lo datos suficientes, inténtelo de nuevo");
            break;
        }
    }
    
    public void guardar(){
        Receta registro = new Receta();
        registro.setDescripcionReceta(txtDescripcionReceta.getText());
        registro.setCodigoControlCita(((ControlCita)cmbCodigoControlCita.getSelectionModel().getSelectedItem()).getCodigoControlCita());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarReceta(?,?)}");
            procedimiento.setString(1, registro.getDescripcionReceta());
            procedimiento.setInt(2, registro.getCodigoControlCita());
            procedimiento.execute();
            listaReceta.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
        
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblRecetas.getSelectionModel().getSelectedItem() != null){
                   activarControles();
                   cmbCodigoControlCita.setDisable(true);
                   btnNuevo.setDisable(true);
                   btnEliminar.setDisable(true);
                   tipoDeOperacion = operaciones.ACTUALIZAR;
                   btnEditar.setText("Actualizar");
                   btnCancelar.setDisable(false);
                }else
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
            break;
            case ACTUALIZAR:
                if(!txtDescripcionReceta.getText().equals("") && cmbCodigoControlCita.getSelectionModel().getSelectedItem() != null){
                    if(txtDescripcionReceta.getLength() <= 45){
                        actualizar();
                        limpiarControles();
                        desactivarControles();
                        btnEditar.setText("Editar");
                        btnCancelar.setDisable(true);
                        btnNuevo.setDisable(false);
                        btnEliminar.setDisable(false);
                        tipoDeOperacion = operaciones.NINGUNO;
                        tblRecetas.getSelectionModel().clearSelection();
                        cargarDatos();
                    }else
                        JOptionPane.showMessageDialog(null,"El dato que ingreso como descripción de receta excede de la cantidad caracteres posibles");
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso lo datos suficientes, inténtelo de nuevo");
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarReceta(?,?)}");
            Receta registro = ((Receta)tblRecetas.getSelectionModel().getSelectedItem());
            registro.setDescripcionReceta(txtDescripcionReceta.getText());
            procedimiento.setInt(1, registro.getCodigoReceta());
            procedimiento.setString(2, registro.getDescripcionReceta());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void cancelar(){
        switch(tipoDeOperacion){
            case ACTUALIZAR:
                limpiarControles();
                desactivarControles();
                btnEditar.setText("Editar");
                btnCancelar.setDisable(true);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                tblRecetas.getSelectionModel().clearSelection();
            break;
        }
    }
    
    
    public void desactivarControles(){
        txtDescripcionReceta.setEditable(false);
        cmbCodigoControlCita.setDisable(true);
    }
    
    public void activarControles(){
        txtDescripcionReceta.setEditable(true);
        cmbCodigoControlCita.setDisable(false);
    } 
    
    public void limpiarControles(){
        txtDescripcionReceta.setText("");
        cmbCodigoControlCita.getSelectionModel().select(null);
    }
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaControlCitas(){
        escenarioPrincipal.ventanaControlCitas();
    }
    
}
