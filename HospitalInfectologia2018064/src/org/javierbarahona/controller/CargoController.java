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
import org.javierbarahona.bean.Cargo;
import org.javierbarahona.db.Conexion;
import org.javierbarahona.sistema.Principal;

public class CargoController implements Initializable {
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Cargo> listaCargo;
    @FXML private TextField txtNombreCargo;
    @FXML private TableView tblCargos;
    @FXML private TableColumn colCodigoCargo;
    @FXML private TableColumn colNombreCargo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnCancelar;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblCargos.setItems(getCargos());
        colCodigoCargo.setCellValueFactory(new PropertyValueFactory<Cargo, Integer>("codigoCargo"));
        colNombreCargo.setCellValueFactory(new PropertyValueFactory<Cargo, String>("nombreCargo"));
    }
    
    public ObservableList<Cargo> getCargos(){
        ArrayList<Cargo> lista = new ArrayList<Cargo>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarCargos()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Cargo(resultado.getInt("codigoCargo"),
                                    resultado.getString("nombreCargo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaCargo = FXCollections.observableList(lista);
    }
    
    public void seleccionarElemento(){
        if(tblCargos.getSelectionModel().getSelectedItem() != null && tipoDeOperacion != operaciones.GUARDAR){
            txtNombreCargo.setText(((Cargo)tblCargos.getSelectionModel().getSelectedItem()).getNombreCargo());
        }
    }
    
    public Cargo buscarCargo(int codigoCargo){
        Cargo resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarCargo(?)}");
            procedimiento.setInt(1, codigoCargo);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Cargo( registro.getInt("codigoCargo"),
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
                tblCargos.getSelectionModel().clearSelection();
            break;
            default:
                if(tblCargos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el elemento?","Eliminar Cargo",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarCargo(?)}");
                            procedimiento.setInt(1, ((Cargo)tblCargos.getSelectionModel().getSelectedItem()).getCodigoCargo());
                            procedimiento.execute();
                            listaCargo.remove(tblCargos.getSelectionModel().getSelectedIndex());
                            limpiarControl();
                            tblCargos.getSelectionModel().clearSelection();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        tblCargos.getSelectionModel().clearSelection();
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
                tblCargos.getSelectionModel().clearSelection();
            break;
            case GUARDAR:
                if(!txtNombreCargo.getText().equals("")){
                    if(txtNombreCargo.getLength() <= 45){
                        if(restriccionLetrasOEspacio(txtNombreCargo.getText()) == true){
                            guardar();
                            desactivarControl();
                            limpiarControl();
                            btnNuevo.setText("Nuevo");
                            btnEliminar.setText("Eliminar");
                            btnEditar.setDisable(false);
                            tipoDeOperacion = operaciones.NINGUNO;
                            cargarDatos();
                        }else
                            JOptionPane.showMessageDialog(null,"Intento colocar números o caracteres especiales en el nombre de cargo, revíselo e inténtelo de nuevo");
                    }else
                        JOptionPane.showMessageDialog(null,"El nombre de cargo que ingreso supera el tamaño que puede ingresar");
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo");
            break;    
        }
    }
    
    public void guardar(){
            Cargo registro = new Cargo();
            registro.setNombreCargo(txtNombreCargo.getText());
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarCargo(?)}");
                procedimiento.setString(1, registro.getNombreCargo());
                procedimiento.execute();
                listaCargo.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblCargos.getSelectionModel().getSelectedItem() != null){
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
                if(!txtNombreCargo.getText().equals("")){
                    if(txtNombreCargo.getLength() <= 45){
                        if(restriccionLetrasOEspacio(txtNombreCargo.getText()) == true){
                            actualizar();
                            btnEditar.setText("Editar");
                            btnCancelar.setDisable(true);
                            tipoDeOperacion = operaciones.NINGUNO;
                            btnNuevo.setDisable(false);
                            btnEliminar.setDisable(false);
                            cargarDatos();
                            limpiarControl();
                            desactivarControl();
                            tblCargos.getSelectionModel().clearSelection();
                        }else
                            JOptionPane.showMessageDialog(null,"Intento colocar números o caracteres especiales en el nombre de cargo, revíselo e inténtelo de nuevo");
                    }else
                        JOptionPane.showMessageDialog(null,"El nombre de cargo que ingreso supera el tamaño que puede ingresar");
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo");
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarCargo(?,?)}");
            Cargo registro = (Cargo)tblCargos.getSelectionModel().getSelectedItem();
            registro.setNombreCargo(txtNombreCargo.getText());
            procedimiento.setInt(1, registro.getCodigoCargo());
            procedimiento.setString(2, registro.getNombreCargo());
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
                tblCargos.getSelectionModel().clearSelection();
            break;
        }
    }
    
    public void desactivarControl(){
        txtNombreCargo.setEditable(false);
    }
    
    public void activarControl(){
        txtNombreCargo.setEditable(true);
    }
    
    public void limpiarControl(){
        txtNombreCargo.setText("");
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
