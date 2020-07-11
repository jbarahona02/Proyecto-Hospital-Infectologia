/*
    Programador:
        Javier Alejandro Barahona Pasan
    Creación
       14/07/2019 
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
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.javierbarahona.bean.Especialidad;
import org.javierbarahona.bean.Horario;
import org.javierbarahona.bean.Medico;
import org.javierbarahona.bean.MedicoEspecialidad;
import org.javierbarahona.db.Conexion;
import org.javierbarahona.sistema.Principal;


public class MedicoEspecialidadController implements Initializable{
    private enum operaciones {NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<MedicoEspecialidad> listaMedicoEspecialidad;
    private ObservableList<Medico> listaMedico;
    private ObservableList<Especialidad> listaEspecialidad;
    private ObservableList<Horario> listaHorario;
    @FXML ComboBox cmbCodigoMedico;
    @FXML ComboBox cmbCodigoEspecialidad;
    @FXML ComboBox cmbCodigoHorario;
    @FXML TableView tblMedicosEspecialidad;
    @FXML TableColumn colCodigoMedicoEspecialidad;
    @FXML TableColumn colCodigoMedico;
    @FXML TableColumn colCodigoEspecialidad;
    @FXML TableColumn colCodigoHorario;
    @FXML Button btnNuevo;
    @FXML Button btnEliminar;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoMedico.setItems(getMedicos());
        cmbCodigoEspecialidad.setItems(getEspecialidades());
        cmbCodigoHorario.setItems(getHorarios());
    }
    
    public void cargarDatos(){
        tblMedicosEspecialidad.setItems(getMedicosEspecialidad());
        colCodigoMedicoEspecialidad.setCellValueFactory(new PropertyValueFactory<MedicoEspecialidad, Integer>("codigoMedicoEspecialidad"));
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<MedicoEspecialidad,Integer>("codigoMedico"));
        colCodigoEspecialidad.setCellValueFactory(new PropertyValueFactory<Especialidad, Integer>("codigoEspecialidad"));
        colCodigoHorario.setCellValueFactory(new PropertyValueFactory<MedicoEspecialidad, Integer>("codigoHorario"));
    }
    
    public ObservableList<MedicoEspecialidad> getMedicosEspecialidad(){
        ArrayList<MedicoEspecialidad> lista = new ArrayList<MedicoEspecialidad>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarMedicoEspecialidad()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new MedicoEspecialidad(resultado.getInt("codigoMedicoEspecialidad"),
                                                 resultado.getInt("codigoMedico"),
                                                 resultado.getInt("codigoEspecialidad"),
                                                 resultado.getInt("codigoHorario")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaMedicoEspecialidad = FXCollections.observableList(lista);
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
    
    public ObservableList<Especialidad> getEspecialidades(){
        ArrayList<Especialidad> lista = new ArrayList<Especialidad>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarEspecialidades()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Especialidad(resultado.getInt("codigoEspecialidad"),
                                           resultado.getString("nombreEspecialidad")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaEspecialidad = FXCollections.observableList(lista);
    } 
    
    public ObservableList<Horario> getHorarios(){
        ArrayList<Horario> lista = new ArrayList<Horario>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarHorarios()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Horario(resultado.getInt("codigoHorario"),
                                      resultado.getString("horarioInicio"),
                                      resultado.getString("horarioSalida"),
                                      resultado.getBoolean("lunes"),
                                      resultado.getBoolean("martes"),
                                      resultado.getBoolean("miercoles"),
                                      resultado.getBoolean("jueves"),
                                      resultado.getBoolean("viernes")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaHorario = FXCollections.observableList(lista);
    }
    
    public void seleccionarElementos(){
        if(tblMedicosEspecialidad.getSelectionModel().getSelectedItem() != null && tipoDeOperacion != operaciones.GUARDAR){
            cmbCodigoMedico.getSelectionModel().select(buscarMedico(((MedicoEspecialidad)tblMedicosEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedico()));
            cmbCodigoEspecialidad.getSelectionModel().select(buscarEspecialidad(((MedicoEspecialidad)tblMedicosEspecialidad.getSelectionModel().getSelectedItem()).getCodigoEspecialidad()));
            cmbCodigoHorario.getSelectionModel().select(buscarHorario(((MedicoEspecialidad)tblMedicosEspecialidad.getSelectionModel().getSelectedItem()).getCodigoHorario()));
        }
    }
    
    public MedicoEspecialidad buscarMedicoEspecialiad(int codigoMedicoEspecialidad){
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
    
    public Especialidad buscarEspecialidad(int codigoEspecialidad){
        Especialidad resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarEspecialidad(?)}");
            procedimiento.setInt(1, codigoEspecialidad);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Especialidad(registro.getInt("codigoEspecialidad"),
                                             registro.getString("nombreEspecialidad"));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return resultado;
    }
    
    public Horario buscarHorario(int codigoHorario){
        Horario resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarHorario(?)}");
            procedimiento.setInt(1, codigoHorario);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new Horario(registro.getInt("codigoHorario"),
                                        registro.getString("horarioInicio"),
                                        registro.getString("horarioSalida"),
                                        registro.getBoolean("lunes"),
                                        registro.getBoolean("martes"),
                                        registro.getBoolean("miercoles"),
                                        registro.getBoolean("jueves"),
                                        registro.getBoolean("viernes"));
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
                tipoDeOperacion = operaciones.NINGUNO;
                tblMedicosEspecialidad.getSelectionModel().clearSelection();
            break;
            default:
                if(tblMedicosEspecialidad.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Està seguro de eliminar el registro?","Eliminar Mèdico Especialidad",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarMedicoEspecialidad(?)}");
                            procedimiento.setInt(1, ((MedicoEspecialidad)tblMedicosEspecialidad.getSelectionModel().getSelectedItem()).getCodigoMedicoEspecialidad());
                            procedimiento.execute();
                            listaMedicoEspecialidad.remove(tblMedicosEspecialidad.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblMedicosEspecialidad.getSelectionModel().clearSelection();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        tblMedicosEspecialidad.getSelectionModel().clearSelection();
                        limpiarControles();
                    }
                        
                }else
                    JOptionPane.showMessageDialog(null,"Debe selecciona un elemento");
            break;
        }
    }
    
    public void nuevo(){
        switch(tipoDeOperacion){
            case NINGUNO:
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                tipoDeOperacion = operaciones.GUARDAR;
                limpiarControles();
                tblMedicosEspecialidad.getSelectionModel().clearSelection();
            break;
            case GUARDAR:
                if(cmbCodigoMedico.getSelectionModel().getSelectedItem() != null && cmbCodigoEspecialidad.getSelectionModel().getSelectedItem() != null && cmbCodigoHorario.getSelectionModel().getSelectedItem() != null){
                    guardar();
                    limpiarControles();
                    desactivarControles();
                    btnNuevo.setText("Nuevo");
                    btnEliminar.setText("Eliminar");
                    tipoDeOperacion = operaciones.NINGUNO;
                    cargarDatos();
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo");
            break;
        }
    }
    
    public void guardar(){
        MedicoEspecialidad registro = new MedicoEspecialidad();
        registro.setCodigoMedico(((Medico)cmbCodigoMedico.getSelectionModel().getSelectedItem()).getCodigoMedico());
        registro.setCodigoEspecialidad(((Especialidad)cmbCodigoEspecialidad.getSelectionModel().getSelectedItem()).getCodigoEspecialidad());
        registro.setCodigoHorario(((Horario)cmbCodigoHorario.getSelectionModel().getSelectedItem()).getCodigoHorario());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarMedicoEspecialidad(?,?,?)}");
            procedimiento.setInt(1, registro.getCodigoMedico());
            procedimiento.setInt(2, registro.getCodigoEspecialidad());
            procedimiento.setInt(3, registro.getCodigoHorario());
            procedimiento.execute();
            listaMedicoEspecialidad.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void desactivarControles(){
        cmbCodigoMedico.setDisable(true);
        cmbCodigoEspecialidad.setDisable(true);
        cmbCodigoHorario.setDisable(true);
    }
    
    public void activarControles(){
        cmbCodigoMedico.setDisable(false);
        cmbCodigoEspecialidad.setDisable(false);
        cmbCodigoHorario.setDisable(false);
    }
    
    public void limpiarControles(){
        cmbCodigoMedico.getSelectionModel().select(null);
        cmbCodigoEspecialidad.getSelectionModel().select(null);
        cmbCodigoHorario.getSelectionModel().select(null);
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
