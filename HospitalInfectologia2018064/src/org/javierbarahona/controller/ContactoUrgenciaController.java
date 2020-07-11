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
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javax.swing.JOptionPane;
import org.javierbarahona.bean.ContactoUrgencia;
import org.javierbarahona.bean.Paciente;
import org.javierbarahona.db.Conexion;
import org.javierbarahona.sistema.Principal;

public class ContactoUrgenciaController implements Initializable{
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<ContactoUrgencia> listaContactoUrgencia;
    private ObservableList<Paciente> listaPaciente;
    @FXML private TextField txtCodigoContacto;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtNumeroContacto;
    @FXML private ComboBox cmbCodigoPaciente;
    @FXML private TableView tblContactos;
    @FXML private TableColumn colCodigoContacto;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colNumeroContacto;
    @FXML private TableColumn colCodigoPaciente;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnCancelar;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
        cmbCodigoPaciente.setItems(getPacientes());
    }
    
    public void cargarDatos(){
        tblContactos.setItems(getContactos());
        colCodigoContacto.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, Integer>("codigoContactoUrgencia"));
        colNombres.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, String>("apellidos"));
        colNumeroContacto.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, String>("numeroContacto"));
        colCodigoPaciente.setCellValueFactory(new PropertyValueFactory<ContactoUrgencia, Integer>("codigoPaciente"));
    }
    
    public ObservableList<ContactoUrgencia> getContactos(){
        ArrayList<ContactoUrgencia> lista = new ArrayList<ContactoUrgencia>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarContactoUrgencia()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new ContactoUrgencia( resultado.getInt("codigoContactoUrgencia"),
                                                resultado.getString("nombres"),
                                                resultado.getString("apellidos"),
                                                resultado.getString("numeroContacto"),
                                                resultado.getInt("codigoPaciente")));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return listaContactoUrgencia = FXCollections.observableList(lista);
    }
    
    public ObservableList<Paciente> getPacientes(){
        ArrayList<Paciente> lista = new ArrayList<Paciente>();
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ListarPacientes()}");
            ResultSet resultado = procedimiento.executeQuery();
            while(resultado.next()){
                lista.add(new Paciente (resultado.getInt("codigoPaciente"),
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
        if(tblContactos.getSelectionModel().getSelectedItem() != null && tipoDeOperacion != operaciones.GUARDAR){
            txtCodigoContacto.setText(String.valueOf(((ContactoUrgencia)tblContactos.getSelectionModel().getSelectedItem()).getCodigoContactoUrgencia()));
            txtNombres.setText(((ContactoUrgencia)tblContactos.getSelectionModel().getSelectedItem()).getNombres());
            txtApellidos.setText(((ContactoUrgencia)tblContactos.getSelectionModel().getSelectedItem()).getApellidos());
            txtNumeroContacto.setText(((ContactoUrgencia)tblContactos.getSelectionModel().getSelectedItem()).getNumeroContacto());
            cmbCodigoPaciente.getSelectionModel().select(buscarPaciente(((ContactoUrgencia)tblContactos.getSelectionModel().getSelectedItem()).getCodigoPaciente()));     
        }
    }
    
    public ContactoUrgencia buscarContactoUrgencia(int codigoContactoUrgencia){
        ContactoUrgencia resultado = null;
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_BuscarContactoUrgencia(?)}");
            procedimiento.setInt(1, codigoContactoUrgencia);
            ResultSet registro = procedimiento.executeQuery();
            while(registro.next()){
                resultado = new ContactoUrgencia(registro.getInt("codigoContactoUrgencia"),
                                                 registro.getString("nombres"),
                                                 registro.getString("apellidos"),
                                                 registro.getString("numeroContacto"),
                                                 registro.getInt("codigoPaciente"));
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
                resultado = new Paciente (registro.getInt("codigoPaciente"),
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
                tipoDeOperacion = operaciones.NINGUNO;
                tblContactos.getSelectionModel().clearSelection();
            break;
            default:
                if(tblContactos.getSelectionModel().getSelectedItem() != null){
                    int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el registro?","Eliminar Contacto Urgencia",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                    if (respuesta == JOptionPane.YES_OPTION){
                        try{
                            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarContactoUrgencia(?)}");
                            procedimiento.setInt(1, ((ContactoUrgencia)tblContactos.getSelectionModel().getSelectedItem()).getCodigoContactoUrgencia());
                            procedimiento.execute();
                            listaContactoUrgencia.remove(tblContactos.getSelectionModel().getSelectedIndex());
                            limpiarControles();
                            tblContactos.getSelectionModel().clearSelection();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        tblContactos.getSelectionModel().clearSelection();
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
                tblContactos.getSelectionModel().clearSelection();
            break;
            case GUARDAR:
                if(!txtNombres.getText().equals("") && !txtApellidos.getText().equals("") && !txtNumeroContacto.getText().equals("") && cmbCodigoPaciente.getSelectionModel().getSelectedItem() != null){
                    if(txtNombres.getLength() <= 100 && txtApellidos.getLength() <= 100){
                        if(txtNumeroContacto.getLength() == 8){    
                            if(restriccionLetrasOEspacio(txtNombres.getText()) == true && restriccionLetrasOEspacio(txtApellidos.getText()) == true){    
                                if(getRestriccion() == true){
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
                                    JOptionPane.showMessageDialog(null,"El número de contacto que ingreso contiene letras");
                            }else
                                 JOptionPane.showMessageDialog(null,"Intento colocar números o caracteres especiales en los nombres o apellidos, revíselos y vuelva a ingresarlos");
                        }else
                            JOptionPane.showMessageDialog(null,"La cantidad de dígitos ingresados en el número de contacto es incorrecto");
                    }else
                        JOptionPane.showMessageDialog(null,"No ingreso correctamente los datos");
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo");
            break;
        }
    }
    
   
    public boolean getRestriccion(){
        boolean restriccion;
        try{
            Integer.parseInt(txtNumeroContacto.getText());
            restriccion = true;
        }catch(Exception e){
            restriccion = false;
        }
        return restriccion;
    }
    
    public boolean restriccionNumero(){
        boolean restriccion = true;
        for(int i = 0; i<getContactos().size();i ++){
            if(getContactos().get(i).getCodigoPaciente() != ((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente()){
                restriccion = true;
            }else{
                restriccion = false;
                JOptionPane.showMessageDialog(null,"El paciente que usted selecciono ya cuenta con un número de contacto, no puede generar otro número de contacto para el mismo paciente");
                i = getContactos().size();
            }
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
    
    public void guardar(){
            ContactoUrgencia registro = new ContactoUrgencia();
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setNumeroContacto(txtNumeroContacto.getText());
            registro.setCodigoPaciente(((Paciente)cmbCodigoPaciente.getSelectionModel().getSelectedItem()).getCodigoPaciente());
            try{
                PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_AgregarContactoUrgencia(?,?,?,?)}");
                procedimiento.setString(1, registro.getNombres());
                procedimiento.setString(2, registro.getApellidos());
                procedimiento.setString(3, registro.getNumeroContacto());
                procedimiento.setInt(4, registro.getCodigoPaciente());
                procedimiento.execute();
                listaContactoUrgencia.add(registro);
            }catch(Exception e){
                e.printStackTrace();
            }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if(tblContactos.getSelectionModel().getSelectedItem() != null){
                    btnEditar.setText("Actualizar");
                    btnCancelar.setDisable(false);
                    btnNuevo.setDisable(true);
                    btnEliminar.setDisable(true);
                    activarControles();
                    cmbCodigoPaciente.setDisable(true);
                    tipoDeOperacion = operaciones.ACTUALIZAR;
                }else
                    JOptionPane.showMessageDialog(null,"Debe seleccionar un elemento");
            break;
            case ACTUALIZAR:
                if(!txtNombres.getText().equals("") && !txtApellidos.getText().equals("") && !txtNumeroContacto.getText().equals("") && cmbCodigoPaciente.getSelectionModel().getSelectedItem() != null){
                    if(txtNombres.getLength() <= 100 && txtApellidos.getLength() <= 100){    
                        if(txtNumeroContacto.getLength() == 8){  
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
                                    tblContactos.getSelectionModel().clearSelection();
                                }else
                                    JOptionPane.showMessageDialog(null,"El número de contacto que ingreso contiene letras");
                            }else
                                JOptionPane.showMessageDialog(null,"Intento colocar números o caracteres especiales en los nombres o apellidos, revíselos y vuelva a ingresarlos");
                        }else
                            JOptionPane.showMessageDialog(null,"La cantidad de dígitos ingresados en el número de contacto es incorrecto");
                    }else
                        JOptionPane.showMessageDialog(null,"No ingreso correctamente los datos");
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo");
            break;
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarConctactoUrgencia(?,?,?,?)}");
            ContactoUrgencia registro = (ContactoUrgencia)tblContactos.getSelectionModel().getSelectedItem();
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setNumeroContacto(txtNumeroContacto.getText());
            procedimiento.setInt(1, registro.getCodigoContactoUrgencia());
            procedimiento.setString(2, registro.getNombres());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getNumeroContacto());
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
                btnNuevo.setDisable(false);
                btnEliminar.setDisable(false);
                btnCancelar.setDisable(true);
                tipoDeOperacion = operaciones.NINGUNO;
                tblContactos.getSelectionModel().clearSelection();
            break;
        }
    }

    public void desactivarControles(){
        txtCodigoContacto.setEditable(false);
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtNumeroContacto.setEditable(false);
        cmbCodigoPaciente.setDisable(true);
    }
    
    public void activarControles(){
        txtCodigoContacto.setEditable(false);
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtNumeroContacto.setEditable(true);
        cmbCodigoPaciente.setDisable(false);
    }
    
    public void limpiarControles(){
        txtCodigoContacto.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtNumeroContacto.setText("");
        cmbCodigoPaciente.getSelectionModel().select(null);
    }
    
    
    public Principal getEscenarioPrincipal() {
        return escenarioPrincipal;
    }

    public void setEscenarioPrincipal(Principal escenarioPrincipal) {
        this.escenarioPrincipal = escenarioPrincipal;
    }
    
    public void ventanaPacientes(){
        escenarioPrincipal.ventanaPacientes();
    }
}
