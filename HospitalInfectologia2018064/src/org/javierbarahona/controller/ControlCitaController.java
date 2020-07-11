/*
    Programador
        Javier Alejandro Barahona Pasan
    Creación
        23/07/2019
*/
package org.javierbarahona.controller;

import eu.schudt.javafx.controls.calendar.DatePicker;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
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
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.javierbarahona.bean.ControlCita;
import org.javierbarahona.bean.Medico;
import org.javierbarahona.bean.Paciente;
import org.javierbarahona.db.Conexion;
import org.javierbarahona.report.GenerarReporte;
import org.javierbarahona.sistema.Principal;

public class ControlCitaController implements Initializable{
    private Principal escenarioPrincipal;
    private enum operaciones {NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<ControlCita> listaControlCita;
    private ObservableList<Medico> listaMedico;
    private ObservableList<Paciente> listaPaciente;
    private DatePicker fecha;
    @FXML private GridPane grpFecha;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraFin;
    @FXML private ComboBox cmbCodigoMedico;
    @FXML private ComboBox cmbCodigoPaciente;
    @FXML private TableView tblControlCitas;
    @FXML private TableColumn colCodigoControlCita;
    @FXML private TableColumn colFecha;
    @FXML private TableColumn colHoraInicio;
    @FXML private TableColumn colHoraFin;
    @FXML private TableColumn colCodigoMedico;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoMedico.setItems(getMedicos());
        cmbCodigoPaciente.setItems(getPacientes());
        
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(false);
        fecha.getStylesheets().add("/org/javierbarahona/resource/DatePicker.css");
        grpFecha.add(fecha,0,0);
        fecha.setDisable(true);
    }

    public void cargarDatos(){
        tblControlCitas.setItems(getControlCitas());
        colCodigoControlCita.setCellValueFactory(new PropertyValueFactory<ControlCita, Integer>("codigoControlCita"));
        colFecha.setCellValueFactory(new PropertyValueFactory<ControlCita, Date>("fecha"));
        colHoraInicio.setCellValueFactory(new PropertyValueFactory<ControlCita, String>("horaInicio"));
        colHoraFin.setCellValueFactory(new PropertyValueFactory<ControlCita, String>("horaFin"));
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<ControlCita, Integer>("codigoMedico"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<ControlCita, Integer>("codigoPaciente"));
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
    
    public ObservableList<Paciente> getPacientes(){
        ArrayList<Paciente> lista = new ArrayList<Paciente>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarPacientes()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Paciente(resultado.getInt("codigoPaciente"),
                                     resultado.getString("dpi"),
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
        if(tblControlCitas.getSelectionModel().getSelectedItem() != null && tipoDeOperacion != operaciones.GUARDAR){
            fecha.selectedDateProperty().set(((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem()).getFecha());
            txtHoraInicio.setText(((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem()).getHoraInicio());
            txtHoraFin.setText(((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem()).getHoraFin());
            cmbCodigoMedico.getSelectionModel().select(buscarMedico(((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem()).getCodigoMedico()));
            cmbCodigoPaciente.getSelectionModel().select(buscarPaciente(((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem()).getCodigoPaciente())); 
        }
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
    
    public Paciente buscarPaciente(int codigoPaciente){
        Paciente resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarPaciente(?)}");
            procedimiento.setInt(1, codigoPaciente);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Paciente(registro.getInt("codigoPaciente"),
                                     registro.getString("dpi"),
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
                limpiarControles();
                desactivarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                tblControlCitas.getSelectionModel().clearSelection();
            break;
            default:
                if(tblControlCitas.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el registro?","Eliminar Cita",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarControlCita(?)}");
                            procedimiento.setInt(1, ((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem()).getCodigoControlCita());
                            procedimiento.execute();
                            listaControlCita.remove(tblControlCitas.getSelectionModel().getSelectedIndex());
                            tblControlCitas.getSelectionModel().clearSelection();
                            limpiarControles();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else
                        tblControlCitas.getSelectionModel().clearSelection();
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
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                tblControlCitas.getSelectionModel().clearSelection();
                limpiarControles();
            break;
            case GUARDAR:
                if(fecha.getSelectedDate() != null && !txtHoraInicio.getText().equals("") && !txtHoraFin.getText().equals("") && cmbCodigoMedico.getSelectionModel().getSelectedItem() != null && cmbCodigoPaciente.getSelectionModel().getSelectedItem() != null){
                    if(txtHoraInicio.getLength() <= 45 && txtHoraFin.getLength() <= 45){
                        guardar();
                        btnNuevo.setText("Nuevo");
                        btnEliminar.setText("Eliminar");
                        btnEditar.setDisable(false);
                        btnReporte.setDisable(false);
                        tipoDeOperacion = operaciones.NINGUNO;
                        tblControlCitas.getSelectionModel().clearSelection();
                        limpiarControles();
                        desactivarControles();
                        cargarDatos();
                    }else
                         JOptionPane.showMessageDialog(null,"No ingreso correctamente los datos, exceden de la cantidad de caracteres posibles");
                }else
                     JOptionPane.showMessageDialog(null,"No ingreso lo datos suficientes, inténtelo de nuevo");
            break;
        }
    }
    
    public void guardar(){
        ControlCita registro = new ControlCita();
        registro.setFecha(fecha.getSelectedDate());
        registro.setHoraInicio(txtHoraInicio.getText());
        registro.setHoraFin(txtHoraFin.getText());
        registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
        registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarControlCita(?,?,?,?,?)}");
            procedimiento.setDate(1, new java.sql.Date(registro.getFecha().getTime()));
            procedimiento.setString(2, registro.getHoraInicio());
            procedimiento.setString(3, registro.getHoraFin());
            procedimiento.setInt(4, registro.getCodigoMedico());
            procedimiento.setInt(5, registro.getCodigoPaciente());
            procedimiento.execute();
            listaControlCita.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblControlCitas.getSelectionModel().getSelectedItem() != null){
                    activarControles();
                    cmbCodigoMedico.setDisable(true);
                    cmbCodigoPaciente.setDisable(true);
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                }else
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
            break;
            case ACTUALIZAR:
                if(fecha.getSelectedDate() != null && !txtHoraInicio.getText().equals("") && !txtHoraFin.getText().equals("") && cmbCodigoMedico.getSelectionModel().getSelectedItem() != null && cmbCodigoPaciente.getSelectionModel().getSelectedItem() != null){
                    if(txtHoraInicio.getLength() <= 45 && txtHoraFin.getLength() <= 45){
                        actualizar();
                        limpiarControles();
                        desactivarControles();
                        btnEditar.setText("Editar");
                        btnReporte.setText("Reporte");
                        btnNuevo.setDisable(false);
                        btnEliminar.setDisable(false);
                        tipoDeOperacion = operaciones.NINGUNO;
                        tblControlCitas.getSelectionModel().clearSelection();
                        cargarDatos();
                    }else
                         JOptionPane.showMessageDialog(null,"No ingreso correctamente los datos, exceden de la cantidad de caracteres posibles");
                }else
                     JOptionPane.showMessageDialog(null,"No ingreso lo datos suficientes, inténtelo de nuevo");
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarControlCita(?,?,?,?)}");
            ControlCita registro = ((ControlCita)tblControlCitas.getSelectionModel().getSelectedItem());
            registro.setFecha(fecha.getSelectedDate());
            registro.setHoraInicio(txtHoraInicio.getText());
            registro.setHoraFin(txtHoraFin.getText());
            registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
            registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
            procedimiento.setInt(1, registro.getCodigoControlCita());
            procedimiento.setDate(2, new java.sql.Date(registro.getFecha().getTime()));
            procedimiento.setString(3, registro.getHoraInicio());
            procedimiento.setString(4, registro.getHoraFin());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void reporte(){
        switch(tipoDeOperacion){
            case NINGUNO:
                imprimirReporte();
                tipoDeOperacion = operaciones.NINGUNO;
            break;
            case ACTUALIZAR:
                    limpiarControles();
                    desactivarControles();
                    btnEditar.setText("Editar");
                    btnReporte.setText("Reporte");
                    btnNuevo.setDisable(false);
                    btnEliminar.setDisable(false);
                    tipoDeOperacion = operaciones.NINGUNO;
                    tblControlCitas.getSelectionModel().clearSelection();
            break;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        String valor = JOptionPane.showInputDialog("Ingrese el código del paciente del cual desea ver su reporte de citas");
        if(valor != null && !valor.equals("")){
            try{
                int codPaciente = Integer.parseInt(valor);
                parametros.put("codigoControlCita",null);
                parametros.put("Logo",this.getClass().getResourceAsStream("/org/javierbarahona/images/Logo.jpg"));
                parametros.put("Encabezado",this.getClass().getResourceAsStream("/org/javierbarahona/images/Encabezado.jpg"));
                parametros.put("NombreySlogan",this.getClass().getResourceAsStream("/org/javierbarahona/images/Nombre y eslogan.jpg"));
                parametros.put("PieDePagina",this.getClass().getResourceAsStream("/org/javierbarahona/images/Pie de página.jpg"));
                parametros.put("codPaciente",codPaciente);
                GenerarReporte.mostrarReporte("ReporteControlCitas.jasper","Reporte Control de Citas",parametros);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null,"Debe de ingresar un número de código, inténtelo de nuevo");
            }
        }
    }
    
    public void desactivarControles(){
        fecha.setDisable(true);
        txtHoraInicio.setEditable(false);
        txtHoraFin.setEditable(false);
        cmbCodigoMedico.setDisable(true);
        cmbCodigoPaciente.setDisable(true);
    }
    
    public void activarControles(){
        fecha.setDisable(false);
        txtHoraInicio.setEditable(true);
        txtHoraFin.setEditable(true);
        cmbCodigoMedico.setDisable(false);
        cmbCodigoPaciente.setDisable(false);
    }
    
    public void limpiarControles(){
        fecha.selectedDateProperty().set(null);
        txtHoraInicio.setText("");
        txtHoraFin.setText("");
        cmbCodigoMedico.getSelectionModel().select(null);
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
    
    public void ventanaRecetas(){
        escenarioPrincipal.ventanaRecetas();
    }
}
