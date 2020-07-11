/* 
    Programador: 
        Javier Alejandro Barahona Pasan
    Creación: 
        29/05/2019
    Modificaciones:
        Validación solo letras en nombres y apellidos 15/07/2019
*/

package org.javierbarahona.controller;

import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
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
import org.javierbarahona.bean.Medico;
import org.javierbarahona.db.Conexion;
import org.javierbarahona.report.GenerarReporte;
import org.javierbarahona.sistema.Principal;


public class MedicoController implements Initializable{
    private enum operaciones{NUEVO,GUARDAR,ELIMINAR,EDITAR,ACTUALIZAR,CANCELAR,NINGUNO};
    private Principal escenarioPrincipal;
    private operaciones tipoDeOperacion = operaciones.NINGUNO;
    private ObservableList<Medico> listaMedico;
    @FXML private TextField txtLicenciaMedica;
    @FXML private TextField txtNombres;
    @FXML private TextField txtApellidos;
    @FXML private TextField txtHoraEntrada;
    @FXML private TextField txtHoraSalida;
    @FXML private TextField txtTurno;
    @FXML private TextField txtSexo;
    @FXML private TableView tblMedicos;
    @FXML private TableColumn colCodigoMedico;
    @FXML private TableColumn colLicenciaMedica;
    @FXML private TableColumn colNombres;
    @FXML private TableColumn colApellidos;
    @FXML private TableColumn colEntrada;
    @FXML private TableColumn colSalida;
    @FXML private TableColumn colTurno;
    @FXML private TableColumn colSexo;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;
    @FXML private Button btnEditar;
    @FXML private Button btnReporte;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cargarDatos();
    }
    
    public void cargarDatos(){
        tblMedicos.setItems(getMedicos());
        colCodigoMedico.setCellValueFactory(new PropertyValueFactory<Medico, Integer>("codigoMedico"));
        colLicenciaMedica.setCellValueFactory(new PropertyValueFactory<Medico, Integer>("licenciaMedica"));
        colNombres.setCellValueFactory(new PropertyValueFactory<Medico, String>("nombres"));
        colApellidos.setCellValueFactory(new PropertyValueFactory<Medico, String>("apellidos"));
        colEntrada.setCellValueFactory(new PropertyValueFactory<Medico, String>("horaEntrada"));
        colSalida.setCellValueFactory(new PropertyValueFactory<Medico, String>("horaSalida"));
        colTurno.setCellValueFactory(new PropertyValueFactory<Medico, Integer>("turnoMaximo"));
        colSexo.setCellValueFactory(new PropertyValueFactory<Medico, String>("sexo"));
        
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
    
    public void seleccionarElemento(){
        if(tblMedicos.getSelectionModel().getSelectedItem() != null && tipoDeOperacion != operaciones.GUARDAR){
            txtLicenciaMedica.setText(String.valueOf(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getLicenciaMedica()));
            txtNombres.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getNombres());
            txtApellidos.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getApellidos());
            txtHoraEntrada.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getHoraEntrada());
            txtHoraSalida.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getHoraSalida());
            txtTurno.setText(String.valueOf(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getTurnoMaximo()));
            txtSexo.setText(((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getSexo());
        }
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
                btnReporte.setDisable(false);
                tipoDeOperacion = operaciones.NINGUNO;
                tblMedicos.getSelectionModel().clearSelection();
            break;
            default:
                if (tblMedicos.getSelectionModel().getSelectedItem() != null){
                  int respuesta = JOptionPane.showConfirmDialog(null,"¿Está seguro de eliminar el registro?","Eliminar Médico",JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
                  if (respuesta == JOptionPane.YES_OPTION){
                      try{
                          PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_EliminarMedico(?)}");
                          procedimiento.setInt(1, ((Medico)tblMedicos.getSelectionModel().getSelectedItem()).getCodigoMedico());
                          procedimiento.execute();
                          listaMedico.remove(tblMedicos.getSelectionModel().getSelectedIndex());
                          limpiarControles();
                          tblMedicos.getSelectionModel().clearSelection();
                      }catch(Exception e){
                          e.printStackTrace();
                      }
                    }else{
                      tblMedicos.getSelectionModel().clearSelection();
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
           Integer.parseInt(txtLicenciaMedica.getText());
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
        switch (tipoDeOperacion){
            case NINGUNO:
                activarControles();
                btnNuevo.setText("Guardar");
                btnEliminar.setText("Cancelar");
                btnEditar.setDisable(true);
                btnReporte.setDisable(true);
                tipoDeOperacion = operaciones.GUARDAR;
                limpiarControles();
                tblMedicos.getSelectionModel().clearSelection();
                break;
            case GUARDAR:
                if(!txtLicenciaMedica.getText().equals("") && !txtNombres.getText().equals("") && !txtApellidos.getText().equals("") && !txtHoraEntrada.getText().equals("") && !txtHoraSalida.getText().equals("") && !txtSexo.getText().equals("")){
                    if(txtLicenciaMedica.getLength() <= 10 && txtNombres.getLength() <= 100 && txtApellidos.getLength() <= 100 && txtHoraEntrada.getLength() <= 10 && txtHoraSalida.getLength() <= 10 && txtSexo.getLength() <= 20){
                        if(restriccionLetrasOEspacio(txtNombres.getText()) == true && restriccionLetrasOEspacio(txtApellidos.getText()) == true && restriccionLetrasOEspacio(txtSexo.getText()) == true){
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
                               JOptionPane.showMessageDialog(null,"La licencia que ingreso contiene letras");
                        }else
                            JOptionPane.showMessageDialog(null,"Intento colocar números o caracteres especiales en los nombres, apellidos o sexo, revíselos y vuelva a ingresarlos");
                    }else
                        JOptionPane.showMessageDialog(null,"No ingreso correctamente los datos"); 
                }else
                   JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo"); 
            break;
        }
    }
     
    public void guardar(){
        Medico registro = new Medico();
        registro.setLicenciaMedica(Integer.parseInt(txtLicenciaMedica.getText()));
        registro.setNombres(txtNombres.getText());
        registro.setApellidos(txtApellidos.getText());
        registro.setHoraEntrada(txtHoraEntrada.getText());
        registro.setHoraSalida(txtHoraSalida.getText());
        registro.setSexo(txtSexo.getText());
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call  sp_AgregarMedico(?,?,?,?,?,?)}");
            procedimiento.setInt(1, registro.getLicenciaMedica());
            procedimiento.setString(2, registro.getNombres());
            procedimiento.setString(3, registro.getApellidos());
            procedimiento.setString(4, registro.getHoraEntrada());
            procedimiento.setString(5, registro.getHoraSalida());
            procedimiento.setString(6, registro.getSexo());
            procedimiento.execute();
            listaMedico.add(registro);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void editar(){
        switch(tipoDeOperacion){
            case NINGUNO:
                if (tblMedicos.getSelectionModel().getSelectedItem() != null){
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
                if(!txtLicenciaMedica.getText().equals("") && !txtNombres.getText().equals("") && !txtApellidos.getText().equals("") && !txtHoraEntrada.getText().equals("") && !txtHoraSalida.getText().equals("") && !txtSexo.getText().equals("")){
                    if(txtLicenciaMedica.getLength() <= 10 && txtNombres.getLength() <= 100 && txtApellidos.getLength() <= 100 && txtHoraEntrada.getLength() <= 10 && txtHoraSalida.getLength() <= 10 && txtSexo.getLength() <= 20){
                        if(restriccionLetrasOEspacio(txtNombres.getText()) == true && restriccionLetrasOEspacio(txtApellidos.getText()) == true && restriccionLetrasOEspacio(txtSexo.getText()) == true){
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
                                 tblMedicos.getSelectionModel().clearSelection();
                             }else
                                 JOptionPane.showMessageDialog(null,"La licencia que ingreso contiene letras");
                        }else
                            JOptionPane.showMessageDialog(null,"Intento colocar números o caracteres especiales en los nombres, apellidos o sexo, revíselos y vuelva a ingresarlos");
                     }else
                        JOptionPane.showMessageDialog(null,"No ingreso correctamente los datos"); 
                }else
                    JOptionPane.showMessageDialog(null,"No ingreso los datos suficientes inténtelo de nuevo"); 
            break;  
        }
    }
    
    public void actualizar(){
        try{
            PreparedStatement procedimiento = Conexion.getInstancia().getConexion().prepareCall("{call sp_ActualizarMedico(?,?,?,?,?,?,?)}");
            Medico registro = ((Medico)tblMedicos.getSelectionModel().getSelectedItem());
            registro.setLicenciaMedica(Integer.parseInt(txtLicenciaMedica.getText()));
            registro.setNombres(txtNombres.getText());
            registro.setApellidos(txtApellidos.getText());
            registro.setHoraEntrada(txtHoraEntrada.getText());
            registro.setHoraSalida(txtHoraSalida.getText());
            registro.setSexo(txtSexo.getText());
            procedimiento.setInt(1, registro.getCodigoMedico());
            procedimiento.setInt(2, registro.getLicenciaMedica());
            procedimiento.setString(3, registro.getNombres());
            procedimiento.setString(4, registro.getApellidos());
            procedimiento.setString(5, registro.getHoraEntrada());
            procedimiento.setString(6, registro.getHoraSalida());
            procedimiento.setString(7, registro.getSexo());
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
                tblMedicos.getSelectionModel().clearSelection();
            break;
        }
    }
    
    public void imprimirReporte(){
        Map parametros = new HashMap();
        parametros.put("codigoMedico",null);
        parametros.put("Logo",this.getClass().getResourceAsStream("/org/javierbarahona/images/Logo.jpg"));
        parametros.put("Encabezado",this.getClass().getResourceAsStream("/org/javierbarahona/images/Encabezado.jpg"));
        parametros.put("NombreySlogan",this.getClass().getResourceAsStream("/org/javierbarahona/images/Nombre y eslogan.jpg"));
        parametros.put("PieDePagina",this.getClass().getResourceAsStream("/org/javierbarahona/images/Pie de página.jpg"));
        GenerarReporte.mostrarReporte("ReporteMedicos.jasper","Reporte de Médicos", parametros);
    }
    
    public void desactivarControles(){
        txtLicenciaMedica.setEditable(false);
        txtNombres.setEditable(false);
        txtApellidos.setEditable(false);
        txtHoraEntrada.setEditable(false);
        txtHoraSalida.setEditable(false);
        txtTurno.setEditable(false);
        txtSexo.setEditable(false);
    }
    
    public void activarControles(){
        txtLicenciaMedica.setEditable(true);
        txtNombres.setEditable(true);
        txtApellidos.setEditable(true);
        txtHoraEntrada.setEditable(true);
        txtHoraSalida.setEditable(true);
        txtTurno.setEditable(false);
        txtSexo.setEditable(true);
    }
    
    public void limpiarControles(){
        txtLicenciaMedica.setText("");
        txtNombres.setText("");
        txtApellidos.setText("");
        txtHoraEntrada.setText("");
        txtHoraSalida.setText("");
        txtTurno.setText("");
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
    
    public void ventanaTelefonosMedico(){
        escenarioPrincipal.ventanaTelefonosMedico();
    }
    
}
