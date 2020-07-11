/* 
    Programador: 
        Javier Alejandro Barahona Pasan
    Creación: 
        01/06/2019
*/
package org.javierbarahona.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
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
import org.javierbarahona.bean.Medico;
import org.javierbarahona.bean.TelefonoMedico;
import org.javierbarahona.db.Conexion;
import org.javierbarahona.sistema.Principal;

public class TelefonoMedicoController implements Initializable {
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<TelefonoMedico> listaTelefonoMedico;
    private ObservableList<Medico> listaMedico;
    @FXML private TextField txtCodigoTelefonoMedico;
    @FXML private TextField txtTelefonoPersonal;
    @FXML private TextField txtTelefonoTrabajo;
    @FXML private ComboBox cmbCodigoMedico;
    @FXML private TableView tblTelefonosMedicos;
    @FXML private TableColumn colCodigoTelefono;
    @FXML private TableColumn colTelefonoPersonal;
    @FXML private TableColumn colTelefonoTrabajo;
    @FXML private TableColumn colCodigoMedico;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnCancelar;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
       cargarDatos();
       cmbCodigoMedico.setItems(getMedicos());
    }
    
    public void cargarDatos(){
        tblTelefonosMedicos.setItems(getTelefonoMedicos());
        colCodigoTelefono.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, Integer>("codigoTelefonoMedico"));
        colTelefonoPersonal.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, String>("telefonoPersonal"));
        colTelefonoTrabajo.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, String>("telefonoTrabajo"));
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<TelefonoMedico, Integer>("codigoMedico"));
        
    }
    
    public ObservableList<TelefonoMedico> getTelefonoMedicos(){
        ArrayList<TelefonoMedico> lista = new ArrayList<TelefonoMedico>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarTelefonosMedico()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add (new TelefonoMedico (resultado.getInt("codigoTelefonoMedico"),
                                               resultado.getString("telefonoPersonal"),
                                               resultado.getString("telefonoTrabajo"),
                                               resultado.getInt("codigoMedico")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaTelefonoMedico = FXCollections.observableList(lista);
    }
    
    public ObservableList<Medico> getMedicos(){
        ArrayList<Medico> lista = new ArrayList<Medico>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedicos()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Medico(resultado.getInt("codigoMedico"),
                                     resultado.getInt("licenciaMedica"),
                                     resultado.getString("nombres"),
                                     resultado.getString("apellidos"),
                                     resultado.getString("horaEntrada"),
                                     resultado.getString("horaSalida"),
                                     resultado.getInt("turnoMaximo"),
                                     resultado.getString("sexo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaMedico = FXCollections.observableList(lista);
    }
    
    public void seleccionarElementos(){
        if (tblTelefonosMedicos.getSelectionModel().getSelectedItem() != null && tipoDeOperacion != operaciones.GUARDAR){
            txtCodigoTelefonoMedico.setText(String.valueOf(((TelefonoMedico)tblTelefonosMedicos.getSelectionModel().getSelectedItem()).getCodigoTelefonoMedico()));
            txtTelefonoPersonal.setText(((TelefonoMedico)tblTelefonosMedicos.getSelectionModel().getSelectedItem()).getTelefonoPersonal());
            txtTelefonoTrabajo.setText(((TelefonoMedico)tblTelefonosMedicos.getSelectionModel().getSelectedItem()).getTelefonoTrabajo());
            cmbCodigoMedico.getSelectionModel().select(buscarMedico(((TelefonoMedico)tblTelefonosMedicos.getSelectionModel().getSelectedItem()).getCodigoMedico()));
        }
    }
    
    public TelefonoMedico buscarTelefonoMedico(int codigoTelefonoMedico){
        TelefonoMedico resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarTelefonoMedico(?)}");
            procedimiento.setInt(1, codigoTelefonoMedico);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new TelefonoMedico (registro.getInt("codigoTelefonoMedico"),
                                                registro.getString("telefonoPersonal"),
                                                registro.getString("telefonoTrabajo"),
                                                registro.getInt("codigoMedico"));
            }
        }catch(Exception e){
            e.printStackTrace();
        } 
        return resultado;
    }
    
    public Medico buscarMedico(int codigoMedico){
        Medico resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedico(?)}");
            procedimiento.setInt(1, codigoMedico);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                    resultado = new Medico(registro.getInt("codigoMedico"),
                                           registro.getInt("licenciaMedica"),
                                           registro.getString("nombres"),
                                           registro.getString("apellidos"),
                                           registro.getString("horaEntrada"),
                                           registro.getString("horaSalida"),
                                           registro.getInt("turnoMaximo"),
                                           registro.getString("sexo"));
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
                tblTelefonosMedicos.getSelectionModel().clearSelection();
            break;
            default:
                if(tblTelefonosMedicos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el registro?","Eliminar Teléfono Médico",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarTelefonoMedico(?)}");
                            procedimiento.setInt(1, ((TelefonoMedico)tblTelefonosMedicos.getSelectionModel().getSelectedItem()).getCodigoTelefonoMedico());
                            procedimiento.execute();
                            listaTelefonoMedico.remove(tblTelefonosMedicos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblTelefonosMedicos.getSelectionModel().clearSelection();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        tblTelefonosMedicos.getSelectionModel().clearSelection();
                        limpiarControles();
                    }
                }else
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
                limpiarControles();
                tblTelefonosMedicos.getSelectionModel().clearSelection();
            break;
            case GUARDAR:
                if(!txtTelefonoPersonal.getText().equals("") && cmbCodigoMedico.getSelectionModel().getSelectedItem() != null){
                    if(getRestriccion() == true){   
                        if(txtTelefonoPersonal.getLength() == 8 && (txtTelefonoTrabajo.getLength() == 8 || txtTelefonoTrabajo.getLength() == 0)){
                            if(restriccionNumero() == true){
                                guardar();
                                desactivarControles();
                                limpiarControles();
                                btnNuevo.setText("Nuevo");
                                btnEliminar.setText("Eliminar");
                                btnEditar.setDisable(false);
                                tipoDeOperacion = operaciones.NINGUNO;
                                cargarDatos();
                            }
                        }else
                             JOptionPane.showMessageDialog(null,"La cantidad de dígitos ingresados en alguno de los números de teléfono es incorrecto");
                    }else
                        JOptionPane.showMessageDialog(null,"Alguno de los números de teléfono que ingreso contiene letras");
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo");     
            break;
        }
    }
    
     public boolean getRestriccion(){
        boolean restriccion = false;
           try{
               Integer.parseInt(txtTelefonoPersonal.getText());
               if(!txtTelefonoTrabajo.getText().equals("")){
                    Integer.parseInt(txtTelefonoTrabajo.getText());
               }
               restriccion = true;
           }catch(Exception e){
                restriccion = false;
           }
        
        return restriccion;
    }
     
    public boolean restriccionNumero(){
        boolean restriccion = true;
        for(int i = 0; i<getTelefonoMedicos().size();i ++){
            if(getTelefonoMedicos().get(i).getCodigoMedico() != ((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico()){
                restriccion = true;
            }else{
                restriccion = false;
                JOptionPane.showMessageDialog(null,"El médico que usted selecciono ya cuenta con números de teléfono ingresados, no puede generar otros números o número de teléfono para el mismo médico");
                i = getTelefonoMedicos().size();
            }
        }
        return restriccion;
    }

    public void guardar(){
        TelefonoMedico registro = new TelefonoMedico();
        registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
        registro.setTelefonoTrabajo(txtTelefonoTrabajo.getText());
        registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
        try{
        PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarTelefonoMedico(?,?,?)}");
        procedimiento.setString(1, registro.getTelefonoPersonal());
        procedimiento.setString(2, registro.getTelefonoTrabajo());
        procedimiento.setInt(3, registro.getCodigoMedico());
        procedimiento.execute();
        listaTelefonoMedico.add(registro);
        }catch(Exception e){
          e.printStackTrace();
        }
    }
     
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblTelefonosMedicos.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnCancelar.setDisable(false);
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    cmbCodigoMedico.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;   
                }else
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
            break;
            case ACTUALIZAR:
                if(!txtTelefonoPersonal.getText().equals("") && cmbCodigoMedico.getSelectionModel().getSelectedItem() != null){
                    if(getRestriccion() == true){  
                        if(txtTelefonoPersonal.getLength() == 8 && (txtTelefonoTrabajo.getLength() == 8 || txtTelefonoTrabajo.getLength() == 0)){
                            actualizar();
                            btnEditar.setText("Editar");
                            btnCancelar.setDisable(true);
                            tipoDeOperacion = operaciones.NINGUNO;
                            btnNuevo.setDisable(false);
                            btnEliminar.setDisable(false);
                            cargarDatos();
                            limpiarControles();
                            desactivarControles();
                            tblTelefonosMedicos.getSelectionModel().clearSelection();
                        }else
                            JOptionPane.showMessageDialog(null,"La cantidad de dígitos ingresados en alguno de los números de teléfono es incorrecto");
                    }else
                        JOptionPane.showMessageDialog(null,"Alguno de los números de teléfono que ingreso contiene letras");
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo");   
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarTelefonoMedico(?,?,?)}");
            TelefonoMedico registro = ((TelefonoMedico)tblTelefonosMedicos.getSelectionModel().getSelectedItem());
            registro.setTelefonoPersonal(txtTelefonoPersonal.getText());
            registro.setTelefonoTrabajo(txtTelefonoTrabajo.getText());
            procedimiento.setInt(1 , registro.getCodigoTelefonoMedico());
            procedimiento.setString(2, registro.getTelefonoPersonal());
            procedimiento.setString(3, registro.getTelefonoTrabajo());
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
                tblTelefonosMedicos.getSelectionModel().clearSelection();
            break;
        }
    }
    
    public void desactivarControles(){
        txtCodigoTelefonoMedico.setEditable(false);
        txtTelefonoPersonal.setEditable(false);
        txtTelefonoTrabajo.setEditable(false);
        cmbCodigoMedico.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoTelefonoMedico.setEditable(false);
        txtTelefonoPersonal.setEditable(true);
        txtTelefonoTrabajo.setEditable(true);
        cmbCodigoMedico.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoTelefonoMedico.setText("");
        txtTelefonoPersonal.setText("");
        txtTelefonoTrabajo.setText("");
        cmbCodigoMedico.getSelectionModel().select(null);
    }
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaMedicos(){
        escenarioPrincipal.ventanaMedicos();
    }
}
