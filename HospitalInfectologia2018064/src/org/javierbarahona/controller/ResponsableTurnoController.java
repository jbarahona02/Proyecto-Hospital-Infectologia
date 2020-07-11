/*
    Programador: 
        Javier Alejandro Barahona Pasan 
    Creación
        26/06/2019
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.javierbarahona.bean.Area;
import org.javierbarahona.bean.Cargo;
import org.javierbarahona.bean.ResponsableTurno;
import org.javierbarahona.db.Conexion;
import org.javierbarahona.sistema.Principal;

public class ResponsableTurnoController implements Initializable {
    private enum operaciones {NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<ResponsableTurno> listaResponsable;
    private ObservableList<Area> listaArea;
    private ObservableList<Cargo> listaCargo;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtTelefonoPersonal;
    @FXML private ComboBox cmbCodigoArea;
    @FXML private ComboBox cmbCodigoCargo;
    @FXML private TableView tblResponsablesTurnos;
    @FXML private TableColumn colCodigoResponsable;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colTelefonoPersonal;
    @FXML private TableColumn colCodigoArea;
    @FXML private TableColumn colCodigoCargo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnCancelar;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
     cargarDatos();
     cmbCodigoArea.setItems(getAreas());
     cmbCodigoCargo.setItems(getCargos());
    }
    
    public void cargarDatos(){
        tblResponsablesTurnos.setItems(getResponsablesTurno());
        colCodigoResponsable.setCellValueFactory(new PropertyValueFactory <ResponsableTurno, Integer>("codigoResponsableTurno"));
        colNombres.setCellValueFactory(new PropertyValueFactory <ResponsableTurno, String>("nombreResponsable"));
        colApellidos.setCellValueFactory(new PropertyValueFactory <ResponsableTurno, String>("apellidosResponsable"));
        colTelefonoPersonal.setCellValueFactory(new PropertyValueFactory <ResponsableTurno, String>("telefonoPersonal"));
        colCodigoArea.setCellValueFactory (new PropertyValueFactory <ResponsableTurno, Integer>("codigoArea"));
        colCodigoCargo.setCellValueFactory (new PropertyValueFactory <ResponsableTurno, Integer> ("codigoCargo"));
    }
    
    public ObservableList<ResponsableTurno> getResponsablesTurno(){
        ArrayList<ResponsableTurno> lista = new ArrayList<ResponsableTurno>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarResponsablesTurno()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new ResponsableTurno (resultado.getInt("codigoResponsableTurno"),
                                                resultado.getString("nombreResponsable"),
                                                resultado.getString("apellidosResponsable"),
                                                resultado.getString("telefonoPersonal"),
                                                resultado.getInt("codigoArea"),
                                                resultado.getInt("codigoCargo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaResponsable = FXCollections.observableArrayList(lista);
    }
    
    public ObservableList<Area> getAreas(){
        ArrayList<Area> lista = new ArrayList<Area>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarAreas()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Area (resultado.getInt("codigoArea"),
                                    resultado.getString("nombreArea")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaArea = FXCollections.observableList(lista);
    }
    
    public ObservableList<Cargo> getCargos(){
        ArrayList<Cargo> lista = new ArrayList<Cargo>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarCargos()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Cargo (resultado.getInt("codigoCargo"),
                                     resultado.getString("nombreCargo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaCargo = FXCollections.observableList(lista);
    }
        
    public void seleccionarElementos(){
        if(tblResponsablesTurnos.getSelectionModel().getSelectedItem() != null && tipoDeOperacion != operaciones.GUARDAR){
            txtNombres.setText(((ResponsableTurno)tblResponsablesTurnos.getSelectionModel().getSelectedItem()).getNombreResponsable());
            txtApellidos.setText(((ResponsableTurno)tblResponsablesTurnos.getSelectionModel().getSelectedItem()).getApellidosResponsable());
            txtTelefonoPersonal.setText(((ResponsableTurno)tblResponsablesTurnos.getSelectionModel().getSelectedItem()).getTelefonoPersonal());
            cmbCodigoArea.getSelectionModel().select(buscarArea(((ResponsableTurno)tblResponsablesTurnos.getSelectionModel().getSelectedItem()).getCodigoArea()));
            cmbCodigoCargo.getSelectionModel().select(buscarCargo(((ResponsableTurno)tblResponsablesTurnos.getSelectionModel().getSelectedItem()).getCodigoCargo()));
        }
    }
    
    public ResponsableTurno buscarResponsableTurno(int codigoResponsable){
        ResponsableTurno resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarResponsableTurno(?)}");
            procedimiento.setInt(1, codigoResponsable);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new ResponsableTurno (registro.getInt("codigoResponsableTurno"),
                                                  registro.getString("nombreResponsable"),
                                                  registro.getString("apellidosResponsable"),
                                                  registro.getString("telefonoPersonal"),
                                                  registro.getInt("codigoArea"),
                                                  registro.getInt("codigoCargo"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
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
    
    public Cargo buscarCargo(int codigoCargo){
        Cargo resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarCargo(?)}");
            procedimiento.setInt(1, codigoCargo);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Cargo (registro.getInt("codigoCargo"),
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
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                tblResponsablesTurnos.getSelectionModel().clearSelection();
            break;
            default:
                if(tblResponsablesTurnos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el registro?","Eliminar Responsable Turno",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarResponsableTurno(?)}");
                            procedimiento.setInt(1, ((ResponsableTurno)tblResponsablesTurnos.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
                            procedimiento.execute();
                            listaResponsable.remove(tblResponsablesTurnos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblResponsablesTurnos.getSelectionModel().clearSelection();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        tblResponsablesTurnos.getSelectionModel().clearSelection();
                        limpiarControles();
                    }
                }else
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
            break;
        }
    }
    
    public boolean getRestriccion(){
        boolean restriccion;
        try{
            Integer.parseInt(txtTelefonoPersonal.getText());
            restriccion = true;
        }catch(Exception e){
            restriccion = false;
        }
        return restriccion;
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
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                limpiarControles();
                tblResponsablesTurnos.getSelectionModel().clearSelection();     
            break;
            case GUARDAR:
                if(!txtNombres.getText().equals("") && !txtApellidos.getText().equals("") && !txtTelefonoPersonal.getText().equals("") && cmbCodigoArea.getSelectionModel().getSelectedItem() != null && cmbCodigoCargo.getSelectionModel().getSelectedItem() != null){
                    if(txtTelefonoPersonal.getLength() == 8 && txtNombres.getLength() <= 75 && txtApellidos.getLength() <= 45){
                        if(restriccionLetrasOEspacio(txtNombres.getText()) == true && restriccionLetrasOEspacio(txtApellidos.getText()) == true){
                            if(getRestriccion() == true){
                                guardar();
                                desactivarControles();
                                limpiarControles();
                                btnNuevo.setText("Nuevo");
                                btnEliminar.setText("Eliminar");
                                btnEditar.setDisable(false);
                                tipoDeOperacion = operaciones.NINGUNO;
                                cargarDatos();
                            }else
                                JOptionPane.showMessageDialog(null,"El número de telèfono que ingreso contiene letras");
                        }else
                            JOptionPane.showMessageDialog(null,"Intento colocar números o caracteres especiales en los nombres o apellidos, revíselo e inténtelo de nuevo");
                    }else
                        JOptionPane.showMessageDialog(null,"No ingreso correctamente los datos");
                }else
                   JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo"); 
            break;
        }
    }
    
    public void guardar(){
        ResponsableTurno registro = new ResponsableTurno();
        registro.setNombreResponsable(txtNombres.getText());
        registro.setApellidosResponsable(txtApellidos.getText());
        registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
        registro.setCodigoArea(((Area)cmbCodigoArea.getSelectionModel().getSelectedItem()).getCodigoArea());
        registro.setCodigoCargo(((Cargo)cmbCodigoCargo.getSelectionModel().getSelectedItem()).getCodigoCargo());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call  sp_AgregarResponsableTurno(?,?,?,?,?)}");
            procedimiento.setString(1, registro.getNombreResponsable());
            procedimiento.setString(2, registro.getApellidosResponsable());
            procedimiento.setString(3, registro.getTelefonoPersonal());
            procedimiento.setInt(4, registro.getCodigoArea());
            procedimiento.setInt(5, registro.getCodigoCargo());
            procedimiento.execute();
            listaResponsable.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblResponsablesTurnos.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnCancelar.setDisable(false);
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    cmbCodigoArea.setDisable(true);
                    cmbCodigoCargo.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
            break;
            case ACTUALIZAR:
                if(!txtNombres.getText().equals("") && !txtApellidos.getText().equals("") && !txtTelefonoPersonal.getText().equals("") && cmbCodigoArea.getSelectionModel().getSelectedItem() != null && cmbCodigoCargo.getSelectionModel().getSelectedItem() != null){
                    if(txtTelefonoPersonal.getLength() == 8 && txtNombres.getLength() <= 75 && txtApellidos.getLength() <= 45){
                        if(restriccionLetrasOEspacio(txtNombres.getText()) == true && restriccionLetrasOEspacio(txtApellidos.getText()) == true){   
                            if(getRestriccion() == true){
                                actualizar();
                                btnEditar.setText("Editar");
                                btnCancelar.setDisable(true);
                                tipoDeOperacion = operaciones.NINGUNO;
                                btnNuevo.setDisable(false);
                                btnEliminar.setDisable(false);
                                cargarDatos();
                                limpiarControles();
                                desactivarControles();
                                tblResponsablesTurnos.getSelectionModel().clearSelection();
                            }else
                                JOptionPane.showMessageDialog(null,"El número de télefono que ingreso contiene letras");
                        }else
                             JOptionPane.showMessageDialog(null,"Intento colocar números o caracteres especiales en los nombres o apellidos, revíselo e inténtelo de nuevo");
                    }else
                        JOptionPane.showMessageDialog(null,"No ingreso correctamente los datos");
                }else
                   JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo"); 
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarResponsableTurno(?,?,?,?)}");
            ResponsableTurno registro = ((ResponsableTurno)tblResponsablesTurnos.getSelectionModel().getSelectedItem());
            registro.setNombreResponsable(txtNombres.getText());
            registro.setApellidosResponsable(txtApellidos.getText());
            registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
            procedimiento.setInt(1, registro.getCodigoResponsableTurno());
            procedimiento.setString(2, registro.getNombreResponsable());
            procedimiento.setString(3, registro.getApellidosResponsable());
            procedimiento.setString(4, registro.getTelefonoPersonal());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void cancelar(){
        switch(tipoDeOperacion){
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                btnEditar.setText("Editar");
                btnCancelar.setDisable(true);
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                tblResponsablesTurnos.getSelectionModel().clearSelection();
            break;
        }
    }
    public void desactivarControles(){
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtTelefonoPersonal.setEditable(false);
        cmbCodigoArea.setDisable(true);
        cmbCodigoCargo.setDisable(true);
    }
    
    public void activarControles(){
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtTelefonoPersonal.setEditable(true);
        cmbCodigoArea.setDisable(false);
        cmbCodigoCargo.setDisable(false);
    }
    
    public void limpiarControles(){
        txtNombres.setText("");
        txtApellidos.setText("");
        txtTelefonoPersonal.setText("");
        cmbCodigoArea.getSelectionModel().select(null);
        cmbCodigoCargo.getSelectionModel().select(null);
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
