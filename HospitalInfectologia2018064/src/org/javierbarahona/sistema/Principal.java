/* 
    Programador: 
        Javier Alejandro Barahona Pasan
    Creación: 
        27/05/2019
*/
package org.javierbarahona.sistema;

import java.io.InputStream;
import java.util.HashSet;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.javierbarahona.controller.AreaController;
import org.javierbarahona.controller.CargoController;
import org.javierbarahona.controller.ContactoUrgenciaController;
import org.javierbarahona.controller.ControlCitaController;
import org.javierbarahona.controller.EspecialidadController;
import org.javierbarahona.controller.HorarioController;
import org.javierbarahona.controller.MedicoController;
import org.javierbarahona.controller.MedicoEspecialidadController;
import org.javierbarahona.controller.MenuPrincipalController;
import org.javierbarahona.controller.PacienteController;
import org.javierbarahona.controller.ProgramadorController;
import org.javierbarahona.controller.RecetaController;
import org.javierbarahona.controller.ResponsableTurnoController;
import org.javierbarahona.controller.TelefonoMedicoController;
import org.javierbarahona.controller.TurnoController;


public class Principal extends Application {
    private final String PAQUETE_VISTA = "/org/javierbarahona/view/";
    private Stage escenarioPrincipal;
    private Scene escena;
    @Override
    public void start(Stage escenarioPrincipal) {
       this.escenarioPrincipal = escenarioPrincipal;
       escenarioPrincipal.setTitle("Hospital de Infectología");
       menuPrincipal();
       escenarioPrincipal.show();
       escenarioPrincipal.getIcons().add(new Image("/org/javierbarahona/images/Icono.png"));
    }
    
    public void menuPrincipal(){
        try{
            MenuPrincipalController menuPrincipal = (MenuPrincipalController)cambiarEscena("MenuPrincipalView.fxml",991,622);
            menuPrincipal.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaMedicos(){
        try{
            MedicoController medicoController = (MedicoController)cambiarEscena("MedicoView.fxml",1025,666);
            medicoController.setEscenarioPrincipal(this);
        }catch(Exception e){
                e.printStackTrace();
        }
    }
    
    public void ventanaProgramador(){
        try{
            ProgramadorController programadorController = (ProgramadorController)cambiarEscena("ProgramadorView.fxml",733,483);
            programadorController.setEscenarioPrincipal(this); 
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaPacientes(){
        try{
            PacienteController pacienteController = (PacienteController)cambiarEscena("PacienteView.fxml",1197,724);
            pacienteController.setEscenarioPrincipal(this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaTelefonosMedico(){
        try{
            TelefonoMedicoController telefonoMedicoController = (TelefonoMedicoController)cambiarEscena("TelefonosMedicoView.fxml",864,614);
            telefonoMedicoController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaContactoUrgencia(){
        try{
            ContactoUrgenciaController contactoUrgenciaController = (ContactoUrgenciaController)cambiarEscena("ContactoUrgenciaView.fxml",947,663);
            contactoUrgenciaController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaEspecialidades(){
        try{
            EspecialidadController especialidadController = (EspecialidadController)cambiarEscena("EspecialidadView.fxml",817,595);
            especialidadController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaCargos(){
        try{
            CargoController cargoController = (CargoController)cambiarEscena("CargoView.fxml",817,595);
            cargoController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaAreas(){
        try{
            AreaController areaController = (AreaController)cambiarEscena("AreaView.fxml",817,595);
            areaController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaResponsablesTurno(){
        try{
            ResponsableTurnoController responsableTurnoController = (ResponsableTurnoController) cambiarEscena("ResponsableTurnoView.fxml",1127,681);
            responsableTurnoController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaHorarios(){
        try{
            HorarioController horarioController = (HorarioController)cambiarEscena("HorarioView.fxml",984,578);
            horarioController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaTurnos(){
        try{
            TurnoController turnoController = (TurnoController)cambiarEscena("TurnoView.fxml",1244,666);
            turnoController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaMedicosEspecialidad(){
        try{
            MedicoEspecialidadController medicoEspecialidadController = (MedicoEspecialidadController)cambiarEscena("MedicoEspecialidadView.fxml",731,631);
            medicoEspecialidadController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaControlCitas(){
        try{
            ControlCitaController controlCitaController = (ControlCitaController)cambiarEscena("ControlCitaView.fxml",813,578);
            controlCitaController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public void ventanaRecetas(){
        try{
            RecetaController recetaController = (RecetaController)cambiarEscena("RecetaView.fxml",789,540);
            recetaController.setEscenarioPrincipal(this);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    
    public Initializable cambiarEscena(String fxml,int ancho,int alto)throws Exception {
        Initializable resultado = null;
        FXMLLoader cargadorFXML = new FXMLLoader();
        InputStream archivo = Principal.class.getResourceAsStream(PAQUETE_VISTA+fxml);
        cargadorFXML.setBuilderFactory(new JavaFXBuilderFactory());
        cargadorFXML.setLocation(Principal.class.getResource(PAQUETE_VISTA+fxml));
        escena = new Scene((AnchorPane) cargadorFXML.load(archivo),ancho,alto);
        escenarioPrincipal.setScene(escena);
        escenarioPrincipal.sizeToScene();
        resultado = (Initializable)cargadorFXML.getController();
        return resultado;
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
