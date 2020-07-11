 -- Programador: Javier Alejandro Barahona Pasan 2018064 IN5AM 
 -- Control de versiones
 -- Creado 13/04/2019
 -- Modificaciones 
 -- Creación de trigger y función para calculo de edad 05/06/2019
 -- Creación de trigger y función para calculo turno máximo 11/06/2016
 -- Colocar la opción on delete cascade a la llave foranea en la tabla TelefonosMedico 11/06/2019
 -- Colocar la opción on delete cascade a la llave foranea en la tabla Contacto Urgencia 11/06/2019
 -- Colocar la opción on delete cascade a las llave foraneas de las tablas restantes 06/07/2019
 -- Creación de procedimiento ListarMedicosConTelefono 06/07/2019
 -- Creación de procedimiento ListarPacientesConTelefono 06/07/2019
 -- Implementación de instrucción UNIQUE al campo codigoPaciente de la tabla ContactoUrgencia 08/07/2019
 -- Implementación de instrucción UNIQUE al campo codigoMedico de la tabla TelefonosMedico 08/07/2019
 -- Creación de trigger tr_Medico_Especialidad_After_Update necesario para actualizar el campo turno máximo  15/07/2019
 -- Creación de trigger tr_Medicos_After_Update necesario para actualizar el campo turno máximo  15/07/2019
 
Create Database DBHospitalInfectologia2018064;

USE DBHospitalInfectologia2018064;

Create Table Pacientes (
	codigoPaciente int not null auto_increment,
    DPI varchar(20) not null,
    apellidos varchar(100) not null,
    nombres varchar(100) not null,
    fechaNacimiento date not null,
    edad int,
    direccion varchar(150) not null,
    ocupacion varchar(50) not null,
    sexo varchar(15) not null,
	primary key PK_codigoPaciente (codigoPaciente)
);

Create Table Areas (
	codigoArea int not null auto_increment,
    nombreArea varchar(45) not null,
    primary key PK_codigoArea (codigoArea)
);

Create Table Cargos (
	codigoCargo int not null auto_increment,
    nombreCargo varchar(45) not null,
    primary key PK_codigoCargo (codigoCargo)
);

Create Table Medicos (
	codigoMedico int not null auto_increment,
    licenciaMedica integer not null,
    nombres varchar(100) not null,
    apellidos varchar(100) not null,
    horaEntrada varchar(10) not null,
    horaSalida varchar(10) not null,
    turnoMaximo int default 0 not null,
    sexo varchar(20) not null,
    primary key PK_codigoMedico (codigoMedico)
);

Create Table Horarios (
	codigoHorario int not null auto_increment,
    horarioInicio varchar(10) not null,
    horarioSalida varchar(10) not null,
    lunes boolean,
    martes boolean,
    miercoles boolean,
    jueves boolean,
    viernes boolean,
    primary key PK_codigoHorario (codigoHorario)
);

Create Table Especialidades (
	codigoEspecialidad int  not null auto_increment,
    nombreEspecialidad varchar(45) not null,
    primary key PK_codigoEspecialidad (codigoEspecialidad)
);

Create Table ContactoUrgencia (
	codigoContactoUrgencia int not null auto_increment,
    nombres varchar(100) not null,
    apellidos varchar(100) not null,
    numeroContacto varchar(10) not null,
    codigoPaciente int not null UNIQUE,
    primary key PK_codigoContactoUrgencia (codigoContactoUrgencia),
    Constraint FK_ContactoUrgencia_Pacientes foreign key (codigoPaciente) References Pacientes (codigoPaciente) ON DELETE CASCADE
);

Create Table TelefonosMedico (
	codigoTelefonoMedico int not null auto_increment,
    telefonoPersonal varchar(15) not null,
    telefonoTrabajo varchar(15),
    codigoMedico int not null UNIQUE,
    primary key PK_codigoTelefonoMedico (codigoTelefonoMedico),
    Constraint FK_TelefonosMedico_Medicos foreign key (codigoMedico) References Medicos (codigoMedico) ON DELETE CASCADE
);

Create Table ResponsableTurno (
	codigoResponsableTurno int not null auto_increment,
    nombreResponsable varchar(75) not null,
    apellidosResponsable varchar(45) not null,
    telefonoPersonal varchar(10) not null,
    codigoArea int not null,
    codigoCargo int not null,
    primary key PK_codigoResponsableTurno (codigoResponsableTurno),
    Constraint FK_ResponsableTurno_Areas foreign key (codigoArea) References Areas (codigoArea) ON DELETE CASCADE,
    Constraint FK_ResponsableTurno_Cargos foreign key (codigoCargo) References Cargos (codigoCargo) ON DELETE CASCADE
);

Create Table Medico_Especialidad (
	codigoMedicoEspecialidad int not null auto_increment,
    codigoMedico int not null,
    codigoEspecialidad int not null,
    codigoHorario int not null,
    primary key PK_codigoMedicoEspecialidad (codigoMedicoEspecialidad),
    Constraint FK_Medico_Especialidad_Medicos foreign key (codigoMedico) References Medicos (codigoMedico) ON DELETE CASCADE,
    Constraint FK_Medico_Especialidad_Especialidades foreign key (codigoEspecialidad) References Especialidades(codigoEspecialidad) ON DELETE CASCADE,
    Constraint FK_Medico_Especialidad_Horarios foreign key (codigoHorario) References Horarios (codigoHorario) ON DELETE CASCADE
);

Create Table Turno (
	codigoTurno int not null auto_increment,
    fechaTurno date not null,
    fechaCita date not null,
    valorCita decimal(10,2) not null,
    codigoMedicoEspecialidad int not null,
    codigoTurnoResponsable int not null,
    codigoPaciente int not null,
    primary key PK_codigoTurno (codigoTurno),
	Constraint FK_Turno_Medico_Especialidad foreign key (codigoMedicoEspecialidad) References Medico_Especialidad (codigoMedicoEspecialidad) ON DELETE CASCADE,
    Constraint FK_Turno_ResponsableTurno foreign key (codigoTurnoResponsable) References ResponsableTurno (codigoResponsableTurno) ON DELETE CASCADE,
    Constraint FK_Turno_Pacientes foreign key (codigoPaciente) References Pacientes (codigoPaciente) ON DELETE CASCADE
);

ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'admin';

-- Procedimientos de la tabla Pacientes -- 
Delimiter $$
Create Procedure sp_AgregarPaciente(IN DPI varchar(20),IN apell varchar(100),IN nom varchar(100),IN fecN date,IN dir varchar(150),IN ocup varchar(50),IN sexo varchar(15))
	Begin
		Insert into Pacientes (DPI,apellidos,nombres,fechaNacimiento,edad,direccion,ocupacion,sexo) values (DPI,apell,nom,fecN,fn_CalculoEdad(fecN),dir,ocup,sexo);
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ActualizarPaciente(IN codigoP int ,IN DPI varchar(20),IN apell varchar(100),IN nom varchar(100),IN fecN date,IN dir varchar(150),IN ocup varchar(50),IN sexo varchar(15))
	Begin
		Update Pacientes set DPI = DPI, apellidos = apell, nombres = nom,fechaNacimiento = fecN,edad = fn_CalculoEdad(fecN),direccion = dir,ocupacion = ocup,sexo = sexo Where codigoPaciente = codigoP;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_EliminarPaciente(IN codigoP int)
	Begin
		Delete From Pacientes Where codigoPaciente = codigoP;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_BuscarPaciente(IN codigoP int)
	Begin
		Select Pacientes.codigoPaciente,Pacientes.DPI,Pacientes.apellidos,Pacientes.nombres,Pacientes.fechaNacimiento,Pacientes.edad,Pacientes.direccion,Pacientes.ocupacion,Pacientes.sexo From Pacientes Where codigoPaciente = codigoP;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ListarPacientes()
	Begin
		Select Pacientes.codigoPaciente,Pacientes.DPI,Pacientes.apellidos,Pacientes.nombres,Pacientes.fechaNacimiento,Pacientes.edad,Pacientes.direccion,Pacientes.ocupacion,Pacientes.sexo From Pacientes;
    End$$
Delimiter ;
 
-- Procedimientos de la tabla Areas --

Delimiter $$
Create Procedure sp_AgregarArea(IN nomA varchar(45))
	Begin
		Insert into Areas (nombreArea) values (nomA);
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ActualizarArea(IN codigoA int,IN nomA varchar(45))
	Begin
		Update Areas set nombreArea = nomA Where codigoArea = codigoA;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_EliminarArea(IN codigoA int)
	Begin
		Delete From Areas Where codigoArea = codigoA;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_BuscarArea(IN codigoA int)
	Begin
		Select Areas.codigoArea,Areas.nombreArea From Areas Where codigoArea = codigoA;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ListarAreas()
	Begin
		Select Areas.codigoArea,Areas.nombreArea From Areas;
    End$$
Delimiter ;

-- Procedimientos de la tabla Cargos --

Delimiter $$
Create Procedure sp_AgregarCargo(IN nomCa varchar(45))
	Begin
		Insert into Cargos (nombreCargo) values (nomCa);
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ActualizarCargo(IN codigoC int,IN nomCa varchar(45))
	Begin
		Update Cargos set nombreCargo = nomCa Where codigoCargo = codigoC;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_EliminarCargo(IN codigoC int)
	Begin
		Delete From Cargos Where codigoCargo = codigoC;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_BuscarCargo(IN codigoC int)
	Begin
		Select Cargos.codigoCargo,Cargos.nombreCargo From Cargos Where codigoCargo = codigoC; 
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ListarCargos()
	Begin
		Select Cargos.codigoCargo,Cargos.nombreCargo From Cargos;
    End$$
Delimiter ;

-- Procedimientos de la tabla Medicos --
Delimiter $$
Create Procedure sp_AgregarMedico(IN licM integer,IN nomM varchar(100),IN apellM varchar(100),IN horaE varchar(10),IN horaS varchar(10),IN sexo varchar(20))
	Begin
		Insert into Medicos (licenciaMedica,nombres,apellidos,horaEntrada,horaSalida,sexo) values (licM,nomM,apellM,horaE,horaS,sexo);
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ActualizarMedico(IN codigoM int,IN licM integer,IN nomM varchar(100),IN apellM varchar(100),IN horaE varchar(10),IN horaS varchar(10),IN sex varchar(20))
	Begin
		Update Medicos set licenciaMedica = licM,nombres = nomM,apellidos = apellM,horaEntrada = horaE,horaSalida = horaS,sexo = sex Where codigoMedico = codigoM;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_EliminarMedico(IN codigoM int)
	Begin
		Delete From Medicos Where codigoMedico = codigoM;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_BuscarMedico(IN codigoM int)
	Begin
		Select Medicos.codigoMedico,Medicos.licenciaMedica,Medicos.nombres,Medicos.apellidos,Medicos.horaEntrada,Medicos.horaSalida,Medicos.turnoMaximo,Medicos.sexo From Medicos Where codigoMedico = codigoM;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ListarMedicos()
	Begin
		Select Medicos.codigoMedico,Medicos.licenciaMedica,Medicos.nombres,Medicos.apellidos,Medicos.horaEntrada,Medicos.horaSalida,Medicos.turnoMaximo,Medicos.sexo From Medicos;
    End$$
Delimiter ;

-- Procedimientos de la tabla Horarios --

Delimiter $$
Create Procedure sp_AgregarHorario(IN horarioI varchar(10),IN horarioS varchar(10),IN lunes boolean,IN martes boolean,IN miercoles boolean,IN jueves boolean,IN viernes boolean)
	Begin
		Insert into Horarios (horarioInicio,horarioSalida,lunes,martes,miercoles,jueves,viernes) values (horarioI,horarioS,lunes,martes,miercoles,jueves,viernes);
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ActualizarHorario(IN codigoH int,IN horarioI varchar(10),IN horarioS varchar(10),IN lun boolean,IN mar boolean,IN mierco boolean,IN jue boolean,IN vier boolean)
	Begin
		Update Horarios set horarioInicio = horarioI,horarioSalida = horarioS,lunes = lun,martes = mar,miercoles = mierco,jueves = jue,viernes = vier Where codigoHorario = codigoH;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_EliminarHorario(IN codigoH int)
	Begin
		Delete From Horarios Where codigoHorario = codigoH;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_BuscarHorario(IN codigoH int)
	Begin
		Select Horarios.codigoHorario,Horarios.horarioInicio,Horarios.horarioSalida,Horarios.lunes,Horarios.martes,Horarios.miercoles,Horarios.jueves,Horarios.viernes From Horarios Where codigoHorario = codigoH;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ListarHorarios()
	Begin
		Select Horarios.codigoHorario,Horarios.horarioInicio,Horarios.horarioSalida,Horarios.lunes,Horarios.martes,Horarios.miercoles,Horarios.jueves,Horarios.viernes From Horarios;
    End$$
Delimiter ;

-- Procedimientos de la tabla Especialidades --

Delimiter $$
Create Procedure sp_AgregarEspecialidad(IN nomEsp varchar(45))
	Begin
		Insert into Especialidades (nombreEspecialidad) values (nomEsp);
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ActualizarEspecialidad(IN codigoEsp int,IN nomEsp varchar(45))
	Begin
		Update Especialidades set nombreEspecialidad = nomEsp Where codigoEspecialidad = codigoEsp;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_EliminarEspecialidad(IN codigoEsp int)
	Begin
		Delete From Especialidades Where codigoEspecialidad = codigoEsp;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_BuscarEspecialidad(IN codigoEsp int)
	Begin
		Select Especialidades.codigoEspecialidad,Especialidades.nombreEspecialidad From Especialidades Where codigoEspecialidad = codigoEsp;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ListarEspecialidades()
	Begin
		Select Especialidades.codigoEspecialidad,Especialidades.nombreEspecialidad From Especialidades;
    End$$
Delimiter ;

-- Procedimientos de la tabla ContactoUrgencia --

Delimiter $$
Create Procedure sp_AgregarContactoUrgencia(IN nomC varchar(100) ,IN  apellC varchar(100),IN numC varchar(10),IN codigoP int)
	Begin
		Insert into ContactoUrgencia (nombres,apellidos,numeroContacto,codigoPaciente) values (nomC,apellC,numC,codigoP);
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ActualizarConctactoUrgencia(IN codigoCU int,IN nomC varchar(100),IN apellC varchar(100),IN numC varchar(10))
	Begin
		Update ContactoUrgencia set nombres = nomC,apellidos = apellC,numeroContacto = numC Where codigoContactoUrgencia = codigoCU;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_EliminarContactoUrgencia(IN codigoCU int)
	Begin
		Delete From ContactoUrgencia Where codigoContactoUrgencia = codigoCU;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_BuscarContactoUrgencia(IN codigoCU int)
	Begin
		Select ContactoUrgencia.codigoContactoUrgencia,ContactoUrgencia.nombres,ContactoUrgencia.apellidos,ContactoUrgencia.numeroContacto,ContactoUrgencia.codigoPaciente From ContactoUrgencia Where codigoContactoUrgencia = codigoCU;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ListarContactoUrgencia()
	Begin
		Select ContactoUrgencia.codigoContactoUrgencia,ContactoUrgencia.nombres,ContactoUrgencia.apellidos,ContactoUrgencia.numeroContacto,ContactoUrgencia.codigoPaciente From ContactoUrgencia;
    End$$
Delimiter ;

-- Procedimientos de la tabla TelefonosMedico --

Delimiter $$
Create Procedure sp_AgregarTelefonoMedico(IN telPer varchar(15),IN telTra varchar(15),IN codigoM int)
	Begin
		Insert into TelefonosMedico (telefonoPersonal,telefonoTrabajo,codigoMedico) values (telPer,telTra,codigoM);
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ActualizarTelefonoMedico(IN codigoTM int ,IN telPer varchar(15),IN telTra varchar(15))
	Begin
		Update TelefonosMedico set telefonoPersonal = telPer,telefonoTrabajo = telTra Where codigoTelefonoMedico = codigoTM;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_EliminarTelefonoMedico(IN codigoTM int)
	Begin
		Delete From TelefonosMedico Where codigoTelefonoMedico = codigoTM;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_BuscarTelefonoMedico(IN codigoTM int)
	Begin
		Select TelefonosMedico.codigoTelefonoMedico,TelefonosMedico.telefonoPersonal,TelefonosMedico.telefonoTrabajo,TelefonosMedico.codigoMedico From TelefonosMedico Where codigoTelefonoMedico = codigoTM;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ListarTelefonosMedico()
	Begin
		Select TelefonosMedico.codigoTelefonoMedico,TelefonosMedico.telefonoPersonal,TelefonosMedico.telefonoTrabajo,TelefonosMedico.codigoMedico From TelefonosMedico;
    End$$
Delimiter ;

-- Procedimientos de la tabla ResponsableTurno --

Delimiter $$
Create Procedure sp_AgregarResponsableTurno(IN nomR varchar(75),IN apellR varchar(45),IN telPerR varchar(10),IN codigoA int,IN codigoC int)
	Begin
		Insert into ResponsableTurno (nombreResponsable,apellidosResponsable,telefonoPersonal,codigoArea,codigoCargo) values (nomR,apellR,telPerR,codigoA,codigoC);
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ActualizarResponsableTurno(IN codigoRT int,IN nomR varchar(75),IN apellR varchar(45),IN telPerR varchar(10))
	Begin
		Update ResponsableTurno set nombreResponsable = nomR,apellidosResponsable = apellR,telefonoPersonal = telPerR Where codigoResponsableTurno = codigoRT;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_EliminarResponsableTurno(IN codigoRT int)
	Begin
		Delete From ResponsableTurno Where codigoResponsableTurno = codigoRT;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_BuscarResponsableTurno(IN codigoRT int)
	Begin
		Select ResponsableTurno.codigoResponsableTurno,ResponsableTurno.nombreResponsable,ResponsableTurno.apellidosResponsable,ResponsableTurno.telefonoPersonal,ResponsableTurno.codigoArea,ResponsableTurno.codigoCargo From ResponsableTurno Where codigoResponsableTurno = codigoRT;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ListarResponsablesTurno()
	Begin
		Select ResponsableTurno.codigoResponsableTurno,ResponsableTurno.nombreResponsable,ResponsableTurno.apellidosResponsable,ResponsableTurno.telefonoPersonal,ResponsableTurno.codigoArea,ResponsableTurno.codigoCargo From ResponsableTurno;
    End$$
Delimiter ;

-- Procedimientos de la tabla Medico_Especialidad
Delimiter $$
Create Procedure sp_AgregarMedicoEspecialidad(IN codigoM int,IN codigoEsp int,IN codigoH int)
	Begin
		Insert into Medico_Especialidad (codigoMedico,codigoEspecialidad,codigoHorario) values (codigoM,codigoEsp,codigoH);
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ActualizarMedicoEspecialidad(IN codigoME int,IN codigoM int,IN codigoEsp int,IN codigoH int)
	Begin
		Update Medico_Especialidad set codigoMedico = codigoM,codigoEspecialidad = codigoEsp,codigoHorario = codigoH Where codigoMedicoEspecialidad = codigoME;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_EliminarMedicoEspecialidad(IN codigoME int)
	Begin
		Delete From Medico_Especialidad Where codigoMedicoEspecialidad = codigoME;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_BuscarMedicoEspecialidad(IN codigoME int)
	Begin
		Select Medico_Especialidad.codigoMedicoEspecialidad,Medico_Especialidad.codigoMedico,Medico_Especialidad.codigoEspecialidad,Medico_Especialidad.codigoHorario From Medico_Especialidad Where codigoMedicoEspecialidad = codigoME;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ListarMedicoEspecialidad()
	Begin
		Select Medico_Especialidad.codigoMedicoEspecialidad,Medico_Especialidad.codigoMedico,Medico_Especialidad.codigoEspecialidad,Medico_Especialidad.codigoHorario From Medico_Especialidad;
    End$$
Delimiter ;

-- Procedimientos de la tabla Turno --

Delimiter $$
Create Procedure sp_AgregarTurno(IN fecT date,IN fecC date,IN valorC decimal(10,2),IN codigoME int,IN codigoTR int,IN codigoP int)
	Begin
		Insert into Turno (fechaTurno,fechaCita,valorCita,codigoMedicoEspecialidad,codigoTurnoResponsable,codigoPaciente) values (fecT,fecC,valorC,codigoME,codigoTR,codigoP);
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ActualizarTurno(IN codigoT int,IN fecT date,IN fecC date,IN valorC decimal(10,2))
	Begin
		Update Turno set fechaTurno = fecT,fechaCita = fecC,valorCita = valorC Where codigoTurno = codigoT;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_EliminarTurno(IN codigoT int)
	Begin
		Delete From Turno Where codigoTurno = codigoT;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_BuscarTurno(IN codigoT int)
	Begin
		Select Turno.codigoTurno,Turno.fechaTurno,Turno.fechaCita,Turno.valorCita,Turno.codigoMedicoEspecialidad,Turno.codigoTurnoResponsable,Turno.codigoPaciente From Turno Where codigoTurno = codigoT;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ListarTurnos()
	Begin
		Select Turno.codigoTurno,Turno.fechaTurno,Turno.fechaCita,Turno.valorCita,Turno.codigoMedicoEspecialidad,Turno.codigoTurnoResponsable,Turno.codigoPaciente From Turno;
    End$$
Delimiter ;

-- Función calculo edad --

Delimiter $$
Create Function fn_CalculoEdad (x date) returns int
Reads Sql data deterministic 
	Begin
		declare edad int;
        set edad = (Select timestampdiff(Year,x,curdate()));
        return edad;
    End$$
Delimiter ;

-- Funciòn Turno Maximo --
Delimiter $$
Create Function fn_CalculoTurno(lunes boolean,martes boolean,miercoles boolean,jueves boolean,viernes boolean) returns int
	Reads Sql data deterministic
		Begin
			Declare acumulador int;
            set acumulador = 0;
						
			if lunes = true then
            set acumulador = acumulador + 1;
            end if;
            
            if martes = true then
            set acumulador = acumulador + 1;
            end if;
            
            if miercoles = true then
            set acumulador = acumulador + 1;
            end if;
            
            if jueves = true then
            set acumulador = acumulador + 1;
            end if;
            
            if viernes = true then
            set acumulador = acumulador + 1;
            end if;
            
			return acumulador;
        End$$
Delimiter ;

-- Trigger para asignar edad --

Delimiter $$
Create Trigger tr_Pacientes_Before_Insert Before insert on Pacientes for each row
	Begin
		set new.edad = fn_CalculoEdad(new.fechaNacimiento); 
	End$$
Delimiter ; 


-- Trigger para asignar turno máximo al ingresar en la tabla Medico_Especialidad---

Delimiter $$
Create Trigger tr_Medicos_After_Insert After insert on Medico_Especialidad for each row
	Begin
		Declare turnos int default 0;
        Declare lunes boolean;
        Declare martes boolean;
        Declare miercoles boolean;
        Declare jueves boolean;
        Declare viernes boolean;
        
        set lunes = (select Horarios.lunes from Horarios Where Horarios.codigoHorario = new.codigoHorario);
        set martes = (select Horarios.martes from Horarios Where Horarios.codigoHorario = new.codigoHorario);
        set miercoles = (select Horarios.miercoles from Horarios Where Horarios.codigoHorario = new.codigoHorario);
        set jueves = (select Horarios.jueves from Horarios Where Horarios.codigoHorario = new.codigoHorario);
        set viernes = (select Horarios.viernes from Horarios Where Horarios.codigoHorario = new.codigoHorario);
        
        set turnos = fn_CalculoTurno(lunes,martes,miercoles,jueves,viernes);
        
        Update Medicos set turnoMaximo = turnos Where codigoMedico = new.codigoMedico;
    End$$
Delimiter ;

-- Trigger para captar cuando se actualice horarios y actualizar Medico_Especialidad --

Delimiter $$
Create Trigger tr_Medico_Especialidad_After_Update After Update on Horarios for each row
	Begin
		Update Medico_Especialidad set codigoHorario = new.codigoHorario Where codigoHorario = new.codigoHorario;
    End$$
Delimiter ;

-- Trigger para actualizar el campo de turno máximo ---

Delimiter $$
Create Trigger tr_Medicos_After_Update After Update on Medico_Especialidad for each row
	Begin
		Declare turnos int default 0;
        Declare lunes boolean;
        Declare martes boolean;
        Declare miercoles boolean;
        Declare jueves boolean;
        Declare viernes boolean;
        
        set lunes = (select Horarios.lunes From Horarios Where codigoHorario = new.codigoHorario);
        set martes = (select Horarios.martes From Horarios Where codigoHorario = new.codigoHorario);
        set miercoles = (select Horarios.miercoles From Horarios Where codigoHorario = new.codigoHorario);
        set jueves = (select Horarios.jueves From Horarios Where codigoHorario = new.codigoHorario);
        set viernes = (select Horarios.viernes From Horarios Where codigoHorario = new.codigoHorario);
        
        set turnos = fn_CalculoTurno(lunes,martes,miercoles,jueves,viernes);
        
        Update Medicos set turnoMaximo = turnos Where codigoMedico = new.codigoMedico;
	
    End$$
Delimiter ;

-- Procedimientos para reportería --
		
Delimiter $$
Create Procedure sp_ListarMedicosConTelefono()
	Begin
		Select M.codigoMedico,M.licenciaMedica,M.nombres,M.apellidos,M.horaEntrada,M.horaSalida,M.turnoMaximo,M.sexo,TM.telefonoPersonal,TM.telefonoTrabajo From Medicos M Inner join TelefonosMedico TM on M.codigoMedico = TM.codigoMedico;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ListarPacientesConTelefono()
	Begin
		Select P.codigoPaciente,P.DPI,P.apellidos,P.nombres,P.edad,P.sexo,CU.numeroContacto From Pacientes P Inner join ContactoUrgencia CU on CU.codigoPaciente = P.codigoPaciente; 
    End$$
Delimiter ;

-- Procedimiento almacenado para reporte final--
Delimiter $$
Create Procedure sp_ListarMedicosConDatosParaReporteFinal(In codMedico int)
	Begin
		Select  Medicos.codigoMedico,Medicos.licenciaMedica,Medicos.nombres,Medicos.apellidos,Medicos.horaEntrada,Medicos.horaSalida,Medicos.turnoMaximo,Medicos.sexo,Horarios.lunes,Horarios.martes,Horarios.miercoles,Horarios.jueves,Horarios.viernes,Especialidades.nombreEspecialidad,concat(Pacientes.nombres,' , ',Pacientes.apellidos) as Paciente_Atendido,ContactoUrgencia.numeroContacto,ResponsableTurno.nombreResponsable,ResponsableTurno.apellidosResponsable,Areas.nombreArea,Cargos.nombreCargo From Medicos 
								Inner join Medico_Especialidad on Medico_Especialidad.codigoMedico = Medicos.codigoMedico 
                                Inner join Especialidades on Especialidades.codigoEspecialidad = Medico_Especialidad.codigoEspecialidad 
								Inner join Horarios on Horarios.codigoHorario = Medico_Especialidad.codigoHorario
                                Inner join Turno on Turno.codigoMedicoEspecialidad = Medico_Especialidad.codigoMedicoEspecialidad 
                                Inner join ResponsableTurno  on ResponsableTurno.codigoResponsableTurno = Turno.codigoTurnoResponsable
                                Inner join Pacientes on Pacientes.codigoPaciente = Turno.codigoPaciente
                                Left join ContactoUrgencia on ContactoUrgencia.codigoPaciente = Pacientes.codigoPaciente
                                Inner join Areas on Areas.codigoArea = ResponsableTurno.codigoArea 
                                Inner join Cargos on Cargos.codigoCargo = ResponsableTurno.codigoCargo Where Medicos.codigoMedico = codMedico;
    End$$
Delimiter ;

-- Creación de tablas --
Create Table ControlCitas (
	codigoControlCita int auto_increment,
    fecha Date not null,
    horaInicio varchar(45) not null,
    horaFin varchar(45) not null,
    codigoMedico int not null,
    codigoPaciente int not null,
    primary key PK_codigoControlCita (codigoControlCita),
    Constraint FK_ControlCitas_Medicos foreign key (codigoMedico) References Medicos (codigoMedico) ON DELETE CASCADE,
	Constraint FK_ControlCitas_Pacientes foreign key (codigoPaciente) References Pacientes (codigoPaciente) ON DELETE CASCADE
);

Create Table Recetas (
	codigoReceta int auto_increment not null,
    descripcionReceta varchar(45) not null,
    codigoControlCita int not null,
    primary key PK_codigoReceta (codigoReceta),
    Constraint FK_Recetas_ControlCitas foreign key (codigoControlCita) References ControlCitas (codigoControlCita) ON DELETE CASCADE
);


-- Procedimientos para la tabla ControlCitas --

Delimiter $$
Create Procedure sp_AgregarControlCita(IN fec Date,IN horaI varchar(45),IN horaF varchar(45),IN codM int,IN codP int)
	Begin
		Insert into ControlCitas (fecha,horaInicio,horaFin,codigoMedico,codigoPaciente) values (fec,horaI,horaF,codM,codP);
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ActualizarControlCita(IN codControlC int,IN fec Date,IN horaI varchar(45),IN horaF varchar(45))
	Begin
		Update ControlCitas set fecha = fec,horaInicio = horaI,horaFin = horaF Where codigoControlCita = codControlC;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_EliminarControlCita (IN codControlC int)
	Begin
		Delete From ControlCitas Where codigoControlCita = codControlC;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_BuscarControlCita (IN codControlC int)
	Begin
		Select ControlCitas.codigoControlCita,ControlCitas.fecha,ControlCitas.horaInicio,ControlCitas.horaFin,ControlCitas.codigoMedico,ControlCitas.codigoPaciente From ControlCitas Where codigoControlCita = codControlC;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ListarControlCitas()
	Begin
		Select ControlCitas.codigoControlCita,ControlCitas.fecha,ControlCitas.horaInicio,ControlCitas.horaFin,ControlCitas.codigoMedico,ControlCitas.codigoPaciente From ControlCitas;
    End$$
Delimiter ;


-- Procedimientos para la tabla Recetas --

Delimiter $$
Create Procedure sp_AgregarReceta(IN descripR varchar(45),IN codControlC int)
	Begin
		Insert into Recetas (descripcionReceta,codigoControlCita) values (descripR,codControlC);
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ActualizarReceta(IN codR int,IN descripR varchar(45))
	Begin
		Update Recetas set descripcionReceta = descripR Where codigoReceta = codR;
	End$$
Delimiter ;

Delimiter $$
Create Procedure sp_EliminarReceta(IN codR int)
	Begin
		Delete From Recetas Where codigoReceta = codR;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_BuscarReceta (IN codR int)
	Begin
		Select Recetas.codigoReceta,Recetas.descripcionReceta,Recetas.codigoControlCita From Recetas Where codigoReceta = codR;
    End$$
Delimiter ;

Delimiter $$
Create Procedure sp_ListarRecetas()
	Begin
		Select Recetas.codigoReceta,Recetas.descripcionReceta,Recetas.codigoControlCita From Recetas;
    End$$
Delimiter ;

-- Procedimiento almaceado necesario para el reporte --

Delimiter $$
Create Procedure sp_ListarDatosCitas(In codP int)
	Begin
		Select P.nombres,P.apellidos,P.DPI,concat(M.nombres,' ',M.apellidos) as Medico,M.licenciaMedica,R.descripcionReceta From Pacientes P
				Inner join ControlCitas CC on CC.codigoPaciente = P.codigoPaciente
                Inner join Medicos M on M.codigoMedico = CC.codigoMedico
                inner join Recetas R on R.codigoControlCita = CC.codigoControlCita Where P.codigoPaciente = codP;
    End$$
Delimiter ;