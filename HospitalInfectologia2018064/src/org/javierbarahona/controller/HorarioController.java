/* 
    Programador: 
        Javier Alejandro Barahona Pasan
    Creación: 
        06/07/2019
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
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.javierbarahona.bean.Horario;
import org.javierbarahona.db.Conexion;
import org.javierbarahona.sistema.Principal;


public class HorarioController implements Initializable {
    private enum operaciones {NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Horario> listaHorario;
    @FXML private TextField txtHoraInicio;
    @FXML private TextField txtHoraSalida;
    @FXML private CheckBox chkLunes;
    @FXML private CheckBox chkMartes;
    @FXML private CheckBox chkMiercoles;
    @FXML private CheckBox chkJueves;
    @FXML private CheckBox chkViernes;
    @FXML private TableView tblHorarios;
    @FXML private TableColumn colCodigoHorario;
    @FXML private TableColumn colHoraInicio;
    @FXML private TableColumn colHoraSalida;
    @FXML private TableColumn colLunes;
    @FXML private TableColumn colMartes;
    @FXML private TableColumn colMiercoles;
    @FXML private TableColumn colJueves;
    @FXML private TableColumn colViernes;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnCancelar;
    

    @Override
    public void initialize(URL location, ResourceBundle resources) {
       cargarDatos();
    }
    
    public void cargarDatos(){
      tblHorarios.setItems(getHorarios());
      colCodigoHorario.setCellValueFactory(new PropertyValueFactory<Horario, Integer>("codigoHorario"));
      colHoraInicio.setCellValueFactory(new PropertyValueFactory<Horario, String>("horarioInicio"));
      colHoraSalida.setCellValueFactory(new PropertyValueFactory<Horario, String>("horarioSalida"));
      colLunes.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("lunes"));
      colMartes.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("martes"));
      colMiercoles.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("miercoles"));
      colJueves.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("jueves"));
      colViernes.setCellValueFactory(new PropertyValueFactory<Horario, Boolean>("viernes"));
    }
    
    public ObservableList<Horario> getHorarios(){
        ArrayList<Horario> lista = new ArrayList<Horario>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarHorarios()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add( new Horario(resultado.getInt("codigoHorario"),
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
        if(tblHorarios.getSelectionModel().getSelectedItem() != null && tipoDeOperacion != operaciones.GUARDAR){
            txtHoraInicio.setText(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).getHorarioInicio());
            txtHoraSalida.setText(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).getHorarioSalida());
            chkLunes.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isLunes());
            chkMartes.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isMartes());
            chkMiercoles.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isMiercoles());
            chkJueves.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isJueves());
            chkViernes.setSelected(((Horario)tblHorarios.getSelectionModel().getSelectedItem()).isViernes());
        }
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
                btnEditar.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                tblHorarios.getSelectionModel().clearSelection();
            break;
            default:
                if(tblHorarios.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el registro?","Eliminar Horario",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                       try{
                           PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarHorario(?)}");
                           procedimiento.setInt(1, ((Horario)tblHorarios.getSelectionModel().getSelectedItem()).getCodigoHorario());
                           procedimiento.execute();
                           listaHorario.remove(tblHorarios.getSelectionModel().getSelectedIndex());
                           limpiarControles();
                           tblHorarios.getSelectionModel().clearSelection();
                       }catch(Exception e){
                           e.printStackTrace();
                       } 
                    }else
                        tblHorarios.getSelectionModel().clearSelection();
                        limpiarControles();
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
                tblHorarios.getSelectionModel().clearSelection();
            break;
            case GUARDAR:
                if(!txtHoraInicio.getText().equals("") && !txtHoraSalida.getText().equals("")){
                    if(txtHoraInicio.getLength() <= 10 && txtHoraSalida.getLength() <= 10){
                        guardar();
                        desactivarControles();
                        limpiarControles();
                        btnNuevo.setText("Nuevo");
                        btnEliminar.setText("Eliminar");
                        btnEditar.setDisable(false);
                        tipoDeOperacion = operaciones.NINGUNO;
                        cargarDatos();
                    }else
                        JOptionPane.showMessageDialog(null,"No ingreso correctamente los datos");
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo");
            break;
        }
    }
    
    public void guardar(){
       Horario registro = new Horario();
       registro.setHorarioInicio(txtHoraInicio.getText());
       registro.setHorarioSalida(txtHoraSalida.getText());
       registro.setLunes(chkLunes.isSelected());
       registro.setMartes(chkMartes.isSelected());
       registro.setMiercoles(chkMiercoles.isSelected());
       registro.setJueves(chkJueves.isSelected());
       registro.setViernes(chkViernes.isSelected());
       try{
           PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarHorario(?,?,?,?,?,?,?)}");
           procedimiento.setString(1, registro.getHorarioInicio());
           procedimiento.setString(2, registro.getHorarioSalida());
           procedimiento.setBoolean(3, registro.isLunes());
           procedimiento.setBoolean(4, registro.isMartes());
           procedimiento.setBoolean(5, registro.isMiercoles());
           procedimiento.setBoolean(6, registro.isJueves());
           procedimiento.setBoolean(7, registro.isViernes());
           procedimiento.execute();
           listaHorario.add(registro);
       }catch(Exception e){
           e.printStackTrace();
       }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblHorarios.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnCancelar.setDisable(false);
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
            break;
            case ACTUALIZAR:
                if(!txtHoraInicio.getText().equals("") && !txtHoraSalida.getText().equals("")){
                    if(txtHoraInicio.getLength() <= 10 && txtHoraSalida.getLength() <= 10){
                        actualizar();
                        btnEditar.setText("Editar");
                        btnCancelar.setDisable(true);
                        tipoDeOperacion = operaciones.NINGUNO;
                        btnNuevo.setDisable(false);
                        btnEliminar.setDisable(false);
                        cargarDatos();
                        limpiarControles();
                        desactivarControles();
                        tblHorarios.getSelectionModel().getSelectedItem();
                    }else
                        JOptionPane.showMessageDialog(null,"No ingreso correctamente los datos");
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo");
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarHorario(?,?,?,?,?,?,?,?)}");
            Horario registro = ((Horario)tblHorarios.getSelectionModel().getSelectedItem());
            registro.setHorarioInicio(txtHoraInicio.getText());
            registro.setHorarioSalida(txtHoraSalida.getText());
            registro.setLunes(chkLunes.isSelected());
            registro.setMartes(chkMartes.isSelected());
            registro.setMiercoles(chkMiercoles.isSelected());
            registro.setJueves(chkJueves.isSelected());
            registro.setViernes(chkViernes.isSelected());
            procedimiento.setInt(1, registro.getCodigoHorario());
            procedimiento.setString(2, registro.getHorarioInicio());
            procedimiento.setString(3, registro.getHorarioSalida());
            procedimiento.setBoolean(4, registro.isLunes());
            procedimiento.setBoolean(5, registro.isMartes());
            procedimiento.setBoolean(6, registro.isMiercoles());
            procedimiento.setBoolean(7, registro.isJueves());
            procedimiento.setBoolean(8, registro.isViernes());
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
                tblHorarios.getSelectionModel().clearSelection();
            break;
        }
    }
    
    public void desactivarControles(){
        txtHoraInicio.setEditable(false);
        txtHoraSalida.setEditable(false);
        chkLunes.setDisable(true);
        chkMartes.setDisable(true);
        chkMiercoles.setDisable(true);
        chkJueves.setDisable(true);
        chkViernes.setDisable(true);
    }
    
    public void activarControles(){
        txtHoraInicio.setEditable(true);
        txtHoraSalida.setEditable(true);
        chkLunes.setDisable(false);
        chkMartes.setDisable(false);
        chkMiercoles.setDisable(false);
        chkJueves.setDisable(false);
        chkViernes.setDisable(false);
    }
    
    public void limpiarControles(){
        txtHoraInicio.setText("");
        txtHoraSalida.setText("");
        chkLunes.setSelected(false);
        chkMartes.setSelected(false);
        chkMiercoles.setSelected(false);
        chkJueves.setSelected(false);
        chkViernes.setSelected(false);
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