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
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import eu.schudt.javafx.controls.calendar.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javafx.scene.layout.GridPane;
import javax.swing.JOptionPane;
import org.javierbarahona.bean.Paciente;
import org.javierbarahona.db.Conexion;
import org.javierbarahona.report.GenerarReporte;
import org.javierbarahona.sistema.Principal;

public class PacienteController implements Initializable{
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Paciente> listaPaciente;
    private DatePicker fecha;
    @FXML private TextField txtDPI;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtEdad;
    @FXML private TextField txtDireccion;
    @FXML private TextField txtOcupacion;
    @FXML private TextField txtSexo;
    @FXML private TableView tblPacientes;
    @FXML private GridPane grpFecha;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private TableColumn colDPI;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colFechaNacimiento;
    @FXML private TableColumn colEdad;
    @FXML private TableColumn colDireccion;
    @FXML private TableColumn colOcupacion;
    @FXML private TableColumn colSexo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        fecha = new DatePicker(Locale.ENGLISH);
        fecha.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
        fecha.getCalendarView().todayButtonTextProperty().set("Today");
        fecha.getCalendarView().setShowWeeks(false);
        fecha.getStylesheets().add("/org/javierbarahona/resource/DatePicker.css");
        grpFecha.add(fecha,0,0);
        fecha.setDisable(true);
    }
    
    public void cargarDatos(){
        tblPacientes.setItems(getPacientes());
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<Paciente, Integer>("codigoPaciente"));
        colDPI.setCellValueFactory(new PropertyValueFactory<Paciente,String>("dpi"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Paciente,String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Paciente, String>("apellidos"));
        colFechaNacimiento.setCellValueFactory(new PropertyValueFactory<Paciente, Date>("fechaNacimiento"));
        colEdad.setCellValueFactory(new PropertyValueFactory<Paciente,Integer>("edad"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<Paciente,String>("direccion"));
        colOcupacion.setCellValueFactory(new PropertyValueFactory<Paciente,String>("ocupacion"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Paciente,String>("sexo"));
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
    
    public void seleccionarElemento(){
        if (tblPacientes.getSelectionModel().getSelectedItem() != null && tipoDeOperacion != operaciones.GUARDAR){
            txtDPI.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getDpi());
            txtNombres.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getNombres());
            txtApellidos.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getApellidos());
            fecha.selectedDateProperty().set(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getFechaNacimiento());
            txtEdad.setText(String.valueOf(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getEdad())); 
            txtDireccion.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getDireccion());
            txtOcupacion.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getOcupacion());
            txtSexo.setText(((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getSexo());
        }
    }
    
    public Paciente buscarPaciente(int codigoPaciente){
        Paciente resultado = null;
        try{
           PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarPaciente(?)}");
           procedimiento.setInt(1, codigoPaciente);
           ResultSet registro = procedimiento.executeQuery();
           while(registro.next()){
               resultado = new Paciente ( registro.getInt("codigoPaciente"),
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
                desactivarControles();
                limpiarControles();
                btnNuevo.setText("Nuevo");
                btnEliminar.setText("Eliminar");
                btnEditar.setDisable(false);
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                tblPacientes.getSelectionModel().clearSelection();
            break;
            default:
                if (tblPacientes.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el registro?","Eliminar Paciente",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if(respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarPaciente(?)}");
                            procedimiento.setInt(1, ((Paciente)tblPacientes.getSelectionModel().getSelectedItem()).getCodigoPaciente());
                            procedimiento.execute();
                            listaPaciente.remove(tblPacientes.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblPacientes.getSelectionModel().clearSelection();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        tblPacientes.getSelectionModel().clearSelection();
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
            Long.parseLong(txtDPI.getText());
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
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                limpiarControles();
                tblPacientes.getSelectionModel().clearSelection();
            break;
            case GUARDAR:
                if(!txtDPI.getText().equals("") && !txtApellidos.getText().equals("") && !txtNombres.getText().equals("") && fecha.getSelectedDate() != null && !txtDireccion.getText().equals("") && !txtOcupacion.getText().equals("") && !txtSexo.getText().equals("")){
                    if(txtDPI.getLength() <= 19 && txtApellidos.getLength() <= 100 && txtNombres.getLength() <= 100 && txtDireccion.getLength() <= 150 && txtOcupacion.getLength() <= 50 && txtSexo.getLength() <= 15){    
                        if(restriccionLetrasOEspacio(txtNombres.getText()) == true && restriccionLetrasOEspacio(txtApellidos.getText()) == true && restriccionLetrasOEspacio(txtOcupacion.getText()) == true && restriccionLetrasOEspacio(txtSexo.getText()) == true){
                            if(getRestriccion() == true){
                                guardar();
                                desactivarControles();
                                limpiarControles();
                                btnNuevo.setText("Nuevo");
                                btnEliminar.setText("Eliminar");
                                btnEditar.setDisable(false);
                                btnReporte.setDisable(false);
                                tipoDeOperacion = operaciones.NINGUNO;
                                cargarDatos();
                            }else
                                 JOptionPane.showMessageDialog(null,"El dato que ingreso como DPI contiene letras");
                        }else
                            JOptionPane.showMessageDialog(null,"Intento colocar números o caracteres especiales en los nombres, apellidos, ocupación o sexo, revíselos y vuelva a ingresarlos");
                    }else
                        JOptionPane.showMessageDialog(null,"No ingreso correctamente los datos");
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo");
            break;    
        }
    }
    
   
    
    public void guardar(){
            Paciente registro = new Paciente();
            registro.setDpi(txtDPI.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setNombres(txtNombres.getText());
            registro.setFechaNacimiento(fecha.getSelectedDate());
            registro.setDireccion(txtDireccion.getText());
            registro.setOcupacion(txtOcupacion.getText());
            registro.setSexo(txtSexo.getText());
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarPaciente(?,?,?,?,?,?,?)}");
                procedimiento.setString(1, registro.getDpi());
                procedimiento.setString(2, registro.getApellidos());
                procedimiento.setString(3, registro.getNombres());
                procedimiento.setDate(4, new java.sql.Date(registro.getFechaNacimiento().getTime()));
                procedimiento.setString(5, registro.getDireccion());
                procedimiento.setString(6, registro.getOcupacion());
                procedimiento.setString(7, registro.getSexo());
                procedimiento.execute();
                listaPaciente.add(registro);
            } catch(Exception e){
                e.printStackTrace();
            }     
    }
  
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblPacientes.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnReporte.setText("Cancelar");
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else 
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
            break;
            case ACTUALIZAR:
                if(!txtDPI.getText().equals("") && !txtApellidos.getText().equals("") && !txtNombres.getText().equals("") && fecha.getSelectedDate() != null && !txtDireccion.getText().equals("") && !txtOcupacion.getText().equals("") && !txtSexo.getText().equals("")){
                    if(txtDPI.getLength() <= 20 && txtApellidos.getLength() <= 100 && txtNombres.getLength() <= 100 && txtDireccion.getLength() <= 150 && txtOcupacion.getLength() <= 50 && txtSexo.getLength() <= 15){ 
                        if(restriccionLetrasOEspacio(txtNombres.getText()) == true && restriccionLetrasOEspacio(txtApellidos.getText()) == true && restriccionLetrasOEspacio(txtOcupacion.getText()) == true && restriccionLetrasOEspacio(txtSexo.getText()) == true){
                            if(getRestriccion() == true){
                                actualizar();
                                btnEditar.setText("Editar");
                                btnReporte.setText("Reporte");
                                tipoDeOperacion = operaciones.NINGUNO;
                                btnNuevo.setDisable(false);
                                btnEliminar.setDisable(false);
                                cargarDatos();
                                limpiarControles();
                                desactivarControles();
                                tblPacientes.getSelectionModel().clearSelection();
                            }else
                                 JOptionPane.showMessageDialog(null,"El dato que ingreso como DPI contiene letras");
                        }else
                             JOptionPane.showMessageDialog(null,"Intento colocar números o caracteres especiales en los nombres, apellidos, ocupación o sexo, revíselos y vuelva a ingresarlos");
                    }else
                        JOptionPane.showMessageDialog(null,"No ingreso correctamente los datos");
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo");
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarPaciente(?,?,?,?,?,?,?,?)}");
            Paciente registro = (Paciente)tblPacientes.getSelectionModel().getSelectedItem();
            registro.setDpi(txtDPI.getText());
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setFechaNacimiento(fecha.getSelectedDate());
            registro.setDireccion(txtDireccion.getText());
            registro.setOcupacion(txtOcupacion.getText());
            registro.setSexo(txtSexo.getText());
            procedimiento.setInt(1, registro.getCodigoPaciente());
            procedimiento.setString(2, registro.getDpi());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getNombres());
            procedimiento.setDate(5, new java.sql.Date( registro.getFechaNacimiento().getTime()));
            procedimiento.setString(6, registro.getDireccion());
            procedimiento.setString(7, registro.getOcupacion());
            procedimiento.setString(8, registro.getSexo());
            procedimiento.execute();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void generarReporte(){
         switch(tipoDeOperacion){
            case NINGUNO:
                imprimirReporte();
                tipoDeOperacion = operaciones.NINGUNO;
            break;
            case ACTUALIZAR:
                desactivarControles();
                limpiarControles();
                btnEditar.setText("Editar");
                btnReporte.setText("Reporte");
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                tblPacientes.getSelectionModel().clearSelection();
            break;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoPaciente",null);
        parametros.put("Logo",this.getClass().getResourceAsStream("/org/javierbarahona/images/Logo.jpg"));
        parametros.put("Encabezado",this.getClass().getResourceAsStream("/org/javierbarahona/images/Encabezado.jpg"));
        parametros.put("NombreySlogan",this.getClass().getResourceAsStream("/org/javierbarahona/images/Nombre y eslogan.jpg"));
        parametros.put("PieDePagina",this.getClass().getResourceAsStream("/org/javierbarahona/images/Pie de página.jpg"));
        GenerarReporte.mostrarReporte("ReportePacientes.jasper","Reporte de Pacientes",parametros);
    }
    
    public void desactivarControles(){
        txtDPI.setEditable(false);
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        fecha.setDisable(true);
        txtEdad.setEditable(false);
        txtDireccion.setEditable(false);
        txtOcupacion.setEditable(false);
        txtSexo.setEditable(false);
    }
    
    public void activarControles(){
        txtDPI.setEditable(true);
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        fecha.setDisable(false);
        txtEdad.setEditable(false);
        txtDireccion.setEditable(true);
        txtOcupacion.setEditable(true);
        txtSexo.setEditable(true);
    }
    
    public void limpiarControles(){
        txtDPI.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        fecha.selectedDateProperty().set(null);
        txtEdad.setText("");
        txtDireccion.setText("");
        txtOcupacion.setText("");
        txtSexo.setText("");
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
   
    public void ventanaContactoUrgencia(){
        escenarioPrincipal.ventanaContactoUrgencia();
    }
}
