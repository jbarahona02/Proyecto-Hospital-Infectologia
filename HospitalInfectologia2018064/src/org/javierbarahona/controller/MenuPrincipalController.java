/* 
    Programador: 
        Javier Alejandro Barahona Pasan
    Creación: 
        27/05/2019
*/
package org.javierbarahona.controller;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javax.swing.JOptionPane;
import org.javierbarahona.report.GenerarReporte;
import org.javierbarahona.sistema.Principal;

public class MenuPrincipalController implements Initializable{
    private Principal escenarioPrincipal;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        
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
    
    public void ventanaProgramador(){
        escenarioPrincipal.ventanaProgramador();
    }
    
    public void ventanaPacientes(){
        escenarioPrincipal.ventanaPacientes();
    }
    
    public void ventanaContactoUrgencia(){
        escenarioPrincipal.ventanaContactoUrgencia();
    }
    
    public void ventanaEspecialidades(){
        escenarioPrincipal.ventanaEspecialidades();
    }
    
    public void ventanaCargos(){
        escenarioPrincipal.ventanaCargos();
    }
    
    public void ventanaAreas(){
        escenarioPrincipal.ventanaAreas();
    }
    
    public void ventanaResponsablesTurno(){
        escenarioPrincipal.ventanaResponsablesTurno();
    }
    
    public void ventanaHorarios(){
        escenarioPrincipal.ventanaHorarios();
    }
    
    public void ventanaTurnos(){
        escenarioPrincipal.ventanaTurnos();
    }
    
    public void ventanaMedicosEspecialidad(){
        escenarioPrincipal.ventanaMedicosEspecialidad();
    }
    
    public void ventanaControlCitas(){
        escenarioPrincipal.ventanaControlCitas();
    }
    
    public void imprimirReporteCitas(){
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
      
    public void imprimirReporteFinal(){
        Map parametros = new HashMap();
        String valor = JOptionPane.showInputDialog("Ingrese el código del médico del cual desea ver su reporte ");
        if(valor != null && !valor.equals("")){
            try{
                int codMedico = Integer.parseInt(valor);
                parametros.put("codigoMedico",null);
                parametros.put("Logo",this.getClass().getResourceAsStream("/org/javierbarahona/images/Logo.jpg"));
                parametros.put("Encabezado",this.getClass().getResourceAsStream("/org/javierbarahona/images/Encabezado.jpg"));
                parametros.put("NombreySlogan",this.getClass().getResourceAsStream("/org/javierbarahona/images/Nombre y eslogan.jpg"));
                parametros.put("PieDePagina",this.getClass().getResourceAsStream("/org/javierbarahona/images/Pie de página.jpg"));
                parametros.put("codMedico",codMedico);
                GenerarReporte.mostrarReporte("ReporteFinal.jasper","Reporte Final",parametros);
            }catch(Exception e){
                JOptionPane.showMessageDialog(null,"Debe de ingresar un número de código, inténtelo de nuevo");
            }
        }
    }
    
    public void imprimirReporteMedicos(){
        Map parametros = new HashMap();
        parametros.put("codigoMedico",null);
        parametros.put("Logo",this.getClass().getResourceAsStream("/org/javierbarahona/images/Logo.jpg"));
        parametros.put("Encabezado",this.getClass().getResourceAsStream("/org/javierbarahona/images/Encabezado.jpg"));
        parametros.put("NombreySlogan",this.getClass().getResourceAsStream("/org/javierbarahona/images/Nombre y eslogan.jpg"));
        parametros.put("PieDePagina",this.getClass().getResourceAsStream("/org/javierbarahona/images/Pie de página.jpg"));
        GenerarReporte.mostrarReporte("ReporteMedicos.jasper","Reporte Mèdicos",parametros);
    }
    
    public void imprimirReportePacientes(){
        Map parametros = new HashMap();
        parametros.put("codigoPaciente",null);
        parametros.put("Logo",this.getClass().getResourceAsStream("/org/javierbarahona/images/Logo.jpg"));
        parametros.put("Encabezado",this.getClass().getResourceAsStream("/org/javierbarahona/images/Encabezado.jpg"));
        parametros.put("NombreySlogan",this.getClass().getResourceAsStream("/org/javierbarahona/images/Nombre y eslogan.jpg"));
        parametros.put("PieDePagina",this.getClass().getResourceAsStream("/org/javierbarahona/images/Pie de página.jpg"));
        GenerarReporte.mostrarReporte("ReportePacientes.jasper","Reporte Pacientes",parametros);
    }
}
