/*
    Programador:
        Javier Alejandro Barahona Pasan
    Creaciòn:
        14/07/2019
 */

package org.javierbarahona.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
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
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.javierbarahona.bean.MedicoEspecialidad;
import org.javierbarahona.bean.Paciente;
import org.javierbarahona.bean.ResponsableTurno;
import org.javierbarahona.bean.Turno;
import org.javierbarahona.db.Conexion;
import org.javierbarahona.sistema.Principal;


public class TurnoController implements Initializable{
    private enum operaciones {NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Turno> listaTurno;
    private ObservableList<MedicoEspecialidad> listaMedicoEspecialidad;
    private ObservableList<ResponsableTurno> listaResponsable;
    private ObservableList<Paciente> listaPaciente;
    private DatePicker fechaTurno;
    private DatePicker fechaCita;
    @FXML private GridPane grpFechaTurno;
    @FXML private GridPane grpFechaCita;
    @FXML private TextField txtValorCita;
    @FXML private ComboBox cmbCodigoMedicoEspecialidad;
    @FXML private ComboBox cmbCodigoResponsableTurno;
    @FXML private ComboBox cmbCodigoPaciente;
    @FXML private TableView tblTurnos;
    @FXML private TableColumn colCodigoTurno;
    @FXML private TableColumn colFechaTurno;
    @FXML private TableColumn colFechaCita;
    @FXML private TableColumn colValorCita;
    @FXML private TableColumn colCodigoMedicoEspecialidad;
    @FXML private TableColumn colCodigoResponsableTurno;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnCancelar;
     
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoMedicoEspecialidad.setItems(getMedicosEspecialidad());
        cmbCodigoResponsableTurno.setItems(getResponsablesTurno());
        cmbCodigoPaciente.setItems(getPacientes());
        fechaTurno = new DatePicker(Locale.ENGLISH);
        fechaTurno.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fechaTurno.getCalendarView().todayButtonTextProperty().set("Today");
        fechaTurno.getCalendarView().setShowWeeks(false);
        fechaTurno.getStylesheets().add("/org/javierbarahona/resource/DatePicker.css");
        grpFechaTurno.add(fechaTurno,0,0);
        fechaTurno.setDisable(true);
        
        fechaCita = new DatePicker(Locale.ENGLISH);
        fechaCita.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fechaCita.getCalendarView().todayButtonTextProperty().set("Today");
        fechaCita.getCalendarView().setShowWeeks(false);
        fechaCita.getStylesheets().add("/org/javierbarahona/resource/DatePicker.css");
        grpFechaCita.add(fechaCita,0,0);
        fechaCita.setDisable(true);
    }
    
    public void cargarDatos(){
        tblTurnos.setItems(getTurnos());
        colCodigoTurno.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoTurno"));
        colFechaTurno.setCellValueFactory(new PropertyValueFactory<Turno, Date>("fechaTurno"));
        colFechaCita.setCellValueFactory(new PropertyValueFactory<Turno, Date>("fechaCita"));
        colValorCita.setCellValueFactory(new PropertyValueFactory<Turno, Double>("valorCita"));
        colCodigoMedicoEspecialidad.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoMedicoEspecialidad"));
        colCodigoResponsableTurno.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoTurnoResponsable"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<Turno, Integer>("codigoPaciente"));
    }
    
    public ObservableList<Turno> getTurnos(){
        ArrayList<Turno> lista = new ArrayList<Turno>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarTurnos()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Turno(resultado.getInt("codigoTurno"),
                                    resultado.getDate("fechaTurno"),
                                    resultado.getDate("fechaCita"),
                                    resultado.getDouble("valorCita"),
                                    resultado.getInt("codigoMedicoEspecialidad"),
                                    resultado.getInt("codigoTurnoResponsable"),
                                    resultado.getInt("codigoPaciente")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaTurno = FXCollections.observableList(lista);
    }
    
    public ObservableList<MedicoEspecialidad> getMedicosEspecialidad(){
        ArrayList<MedicoEspecialidad> lista  = new ArrayList<MedicoEspecialidad>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedicoEspecialidad()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new MedicoEspecialidad( resultado.getInt("codigoMedicoEspecialidad"),
                                                  resultado.getInt("codigoMedico"),
                                                  resultado.getInt("codigoEspecialidad"),
                                                  resultado.getInt("codigoHorario")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaMedicoEspecialidad = FXCollections.observableList(lista);
    }
    
    public ObservableList<ResponsableTurno> getResponsablesTurno(){
        ArrayList<ResponsableTurno> lista = new ArrayList<ResponsableTurno>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarResponsablesTurno()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new ResponsableTurno(resultado.getInt("codigoResponsableTurno"),
                                               resultado.getString("nombreResponsable"),
                                               resultado.getString("apellidosResponsable"),
                                               resultado.getString("telefonoPersonal"),
                                               resultado.getInt("codigoArea"),
                                               resultado.getInt("codigoCargo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaResponsable = FXCollections.observableList(lista);
    }
    
    public ObservableList<Paciente> getPacientes(){
        ArrayList<Paciente> lista = new ArrayList<Paciente>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarPacientes()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Paciente(resultado.getInt("codigoPaciente"),
                                       resultado.getString("DPI"),
                                       resultado.getString("apellidos"),
                                       resultado.getString("nombres"),
                                       resultado.getDate("fechaNacimiento"),
                                       resultado.getInt("edad"),
                                       resultado.getString("direccion"),
                                       resultado.getString("ocupacion"),
                                       resultado.getString("sexo")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaPaciente = FXCollections.observableList(lista);
    }
    
    public void seleccionarElementos(){
        if(tblTurnos.getSelectionModel().getSelectedItem() != null && tipoDeOperacion != operaciones.GUARDAR){
            fechaTurno.selectedDateProperty().set(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getFechaTurno());
            fechaCita.selectedDateProperty().set(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getFechaCita());
            txtValorCita.setText(String.valueOf(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getValorCita()));
            cmbCodigoMedicoEspecialidad.getSelectionModel().select(buscarMedicoEspecialidad(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad()));
            cmbCodigoResponsableTurno.getSelectionModel().select(buscarResponsableTurno(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoTurnoResponsable()));
            cmbCodigoPaciente.getSelectionModel().select(buscarPaciente(((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoPaciente()));
        }
    }
    
    public Turno buscarTurno(int codigoTurno){
        Turno resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarTurno(?)}");
            procedimiento.setInt(1, codigoTurno);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Turno(registro.getInt("codigoTurno"),
                                      registro.getDate("fechaTurno"),
                                      registro.getDate("fechaCita"),
                                      registro.getDouble("valorCita"),
                                      registro.getInt("codigoMedicoEspecialidad"),
                                      registro.getInt("codigoTurnoResponsable"),
                                      registro.getInt("codigoPaciente"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public MedicoEspecialidad buscarMedicoEspecialidad(int codigoMedicoEspecialidad){
        MedicoEspecialidad resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarMedicoEspecialidad(?)}");
            procedimiento.setInt(1, codigoMedicoEspecialidad);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new MedicoEspecialidad(registro.getInt("codigoMedicoEspecialidad"),
                                                   registro.getInt("codigoMedico"),
                                                   registro.getInt("codigoEspecialidad"),
                                                   registro.getInt("codigoHorario"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public ResponsableTurno buscarResponsableTurno(int codigoResponsable){
        ResponsableTurno resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarResponsableTurno(?)}");
            procedimiento.setInt(1, codigoResponsable);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new ResponsableTurno(registro.getInt("codigoResponsableTurno"),
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
    
    public Paciente buscarPaciente(int codigoPaciente){
        Paciente resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarPaciente(?)}");
            procedimiento.setInt(1, codigoPaciente);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Paciente(registro.getInt("codigoPaciente"),
                                         registro.getString("DPI"),
                                         registro.getString("apellidos"),
                                         registro.getString("nombres"),
                                         registro.getDate("fechaNacimiento"),
                                         registro.getInt("edad"),
                                         registro.getString("direccion"),
                                         registro.getString("ocupacion"),
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
                tblTurnos.getSelectionModel().clearSelection();
            break;
            default:
                if(tblTurnos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el registro?","Eliminar Turno",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarTurno(?)}");
                            procedimiento.setInt(1, ((Turno)tblTurnos.getSelectionModel().getSelectedItem()).getCodigoTurno());
                            procedimiento.execute();
                            listaTurno.remove(tblTurnos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblTurnos.getSelectionModel().clearSelection();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        tblTurnos.getSelectionModel().clearSelection();
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
            Double.parseDouble(txtValorCita.getText());
            restriccion = true;
        }catch(Exception e){
            restriccion = false;
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
                tblTurnos.getSelectionModel().clearSelection();
            break;
            case GUARDAR:
                if(fechaTurno.getSelectedDate() != null && fechaCita.getSelectedDate() != null && !txtValorCita.getText().equals("") && cmbCodigoMedicoEspecialidad.getSelectionModel().getSelectedItem() != null && cmbCodigoResponsableTurno.getSelectionModel().getSelectedItem() != null && cmbCodigoPaciente.getSelectionModel().getSelectedItem() != null){
                    if(txtValorCita.getLength() < 14){
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
                            JOptionPane.showMessageDialog(null,"El valor que ingreso como valor de cita contiene letras o caracteres especiales");
                    }else
                        JOptionPane.showMessageDialog(null,"El valor que ingreso como valor de cita excede el tamaño de dígitos que puede ingresar");
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo");
            break;
        }
    }

    public void guardar(){
        Turno registro = new Turno();
        registro.setFechaTurno(fechaTurno.getSelectedDate());
        registro.setFechaCita(fechaCita.getSelectedDate());
        registro.setValorCita(Double.parseDouble(txtValorCita.getText()));
        registro.setCodigoMedicoEspecialidad(((MedicoEspecialidad)cmbCodigoMedicoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
        registro.setCodigoTurnoResponsable(((ResponsableTurno)cmbCodigoResponsableTurno.getSelectionModel().getSelectedItem()).getCodigoResponsableTurno());
        registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarTurno(?,?,?,?,?,?)}");
            procedimiento.setDate(1, new java.sql.Date(registro.getFechaTurno().getTime()));
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaCita().getTime()));
            procedimiento.setDouble(3, registro.getValorCita());
            procedimiento.setInt(4, registro.getCodigoMedicoEspecialidad());
            procedimiento.setInt(5, registro.getCodigoTurnoResponsable());
            procedimiento.setInt(6, registro.getCodigoPaciente());
            procedimiento.execute();
            listaTurno.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblTurnos.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnCancelar.setDisable(false);
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    cmbCodigoMedicoEspecialidad.setDisable(true);
                    cmbCodigoResponsableTurno.setDisable(true);
                    cmbCodigoPaciente.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else
                    JOptionPane.showMessageDialog(null,"Debe selecionar un elemento");
            break;
            case ACTUALIZAR:
                 if(fechaTurno.getSelectedDate() != null && fechaCita.getSelectedDate() != null && !txtValorCita.getText().equals("") && cmbCodigoMedicoEspecialidad.getSelectionModel().getSelectedItem() != null && cmbCodigoResponsableTurno.getSelectionModel().getSelectedItem() != null && cmbCodigoPaciente.getSelectionModel().getSelectedItem() != null){
                    if(txtValorCita.getLength() < 14){
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
                            tblTurnos.getSelectionModel().clearSelection();
                        }else
                            JOptionPane.showMessageDialog(null,"El valor que ingreso como valor de cita contiene letras o caracteres especiales");
                    }else
                        JOptionPane.showMessageDialog(null,"El valor que ingreso como valor de cita excede el tamaño de dígitos que puede ingresar");
                 }else
                     JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo");
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarTurno(?,?,?,?)}");
            Turno registro = ((Turno)tblTurnos.getSelectionModel().getSelectedItem());
            registro.setFechaTurno(fechaTurno.getSelectedDate());
            registro.setFechaCita(fechaCita.getSelectedDate());
            registro.setValorCita(Double.parseDouble(txtValorCita.getText()));
            procedimiento.setInt(1, registro.getCodigoTurno());
            procedimiento.setDate(2, new java.sql.Date(registro.getFechaTurno().getTime()));
            procedimiento.setDate(3, new java.sql.Date(registro.getFechaCita().getTime()));
            procedimiento.setDouble(4, registro.getValorCita());
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
                tblTurnos.getSelectionModel().clearSelection();
            break;
        }
    }
    
    public void desactivarControles(){
        fechaTurno.setDisable(true);
        fechaCita.setDisable(true);
        txtValorCita.setEditable(false);
        cmbCodigoMedicoEspecialidad.setDisable(true);
        cmbCodigoResponsableTurno.setDisable(true);
        cmbCodigoPaciente.setDisable(true);
    }
    
    public void activarControles(){
        fechaTurno.setDisable(false);
        fechaCita.setDisable(false);
        txtValorCita.setEditable(true);
        cmbCodigoMedicoEspecialidad.setDisable(false);
        cmbCodigoResponsableTurno.setDisable(false);
        cmbCodigoPaciente.setDisable(false);
    }
    
    public void limpiarControles(){
        fechaTurno.selectedDateProperty().set(null);
        fechaCita.selectedDateProperty().set(null);
        txtValorCita.setText("");
        cmbCodigoMedicoEspecialidad.getSelectionModel().select(null);
        cmbCodigoResponsableTurno.getSelectionModel().select(null);
        cmbCodigoPaciente.getSelectionModel().select(null);
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
