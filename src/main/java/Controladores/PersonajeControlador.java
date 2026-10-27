package Controladores;

import Clases.Personaje;
import ConexionBBDD.ConexionMongoMejorada;
import DAO.PersonajeDAO;
import Util.AlertUtils;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

import java.util.Comparator;

public class PersonajeControlador {
    private Personaje personajeSelec;
    PersonajeDAO personajeDAO = new PersonajeDAO();

//--------------------------------------------------------------------------------------------------------------------
//              CARGAR LOS DATOS EN LA TABLA
//--------------------------------------------------------------------------------------------------------------------
    public void iniciarDatos() {
        //AL INICIAR LLAMO AL MÉTODO QUE CARGA LOS DATOS DE PRUEBA
        personajeDAO.insertarDatosIniciales(ConexionMongoMejorada.conectar());

        ObservableList<Personaje> listaPersonajes = personajeDAO.obtenerPersonajes(ConexionMongoMejorada.conectar());

        if (!listaPersonajes.isEmpty()) {
            //ORDENO LA LISTA POR ID EN ORDEN ASCENDENTE
            listaPersonajes.sort(Comparator.comparing(Personaje::getId));

            tableCID.setCellValueFactory(new PropertyValueFactory<>("id"));
            tableCNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
            tableCNomCreador.setCellValueFactory(new PropertyValueFactory<>("creadorNombre"));
            tableCApeCreador.setCellValueFactory(new PropertyValueFactory<>("creadorApellido"));
            tableCMascota.setCellValueFactory(new PropertyValueFactory<>("mascota"));

            tvTabla.setItems(listaPersonajes);
        }else {
            tvTabla.setItems(null);
            AlertUtils.mostrarInformacion("No existen personajes", "INFORMACIÓN", "No existen personajes.");
        }
    }


    @FXML
    private Button btActualizar;

    @FXML
    private Button btEliminar;

    @FXML
    private Button btInsertar;

    @FXML
    private Button btLimpiar;

    @FXML
    private TableColumn<Personaje, String> tableCApeCreador;

    @FXML
    private TableColumn<Personaje, Integer> tableCID;

    @FXML
    private TableColumn<Personaje, String> tableCMascota;

    @FXML
    private TableColumn<Personaje, String> tableCNomCreador;

    @FXML
    private TableColumn<Personaje, String> tableCNombre;

    @FXML
    private TextField textApeCreador;

    @FXML
    private TextField textID;

    @FXML
    private TextField textMascota;

    @FXML
    private TextField textNomCreador;

    @FXML
    private TextField textNombre;

    @FXML
    private TableView<Personaje> tvTabla;


//--------------------------------------------------------------------------------------------------------------------
//              ACTUALIZAR UN PERSONAJE
//--------------------------------------------------------------------------------------------------------------------
    @FXML
    void onActualizar(MouseEvent event) {
        //COMPRUEBO SI HAY UN PERSONAJE SELECCIONADO
        if (personajeSelec != null) {
            //PREGUNTO MEDIANTE UN ALERT QUE RECOGE LA OPCIÓN CLICADA
            boolean confirmado = AlertUtils.mostrarAlerta("Confirmación actualización personaje",
                    "¿Desea actualizar al personaje?", "");
            //SI PULSA QUE ACEPTA
            if (confirmado) {
                //CREO UN PERSONAJE DE LA CLASE PERSONAJE CON TODOS LOS CAMPOS
                Personaje personaje = new Personaje(Integer.parseInt(textID.getText()), textNombre.getText(),textNomCreador.getText(),textApeCreador.getText(),textMascota.getText());
                // LLAMO AL MÉTODO DE LA CLASE PERSONAJE Y PASANDO LA CONEXION Y EL PERSONAJE
                personajeDAO.actualizarPersonaje(ConexionMongoMejorada.conectar(),personaje);
                // MUESTRO MENSAJE DE INFORMACIÓN SI TODO SALE BIEN
                AlertUtils.mostrarInformacion("Personaje actualizado", "INFORMACIÓN", "Personaje actualizado con éxito.");
            }
        //SI NO SE HA SELECCIONADO NINGUNO LANZO UN ALERT DE INFORMACIÓN
        } else {
            AlertUtils.mostrarInformacion("Personaje no seleccionado", "INFORMACIÓN","No hay ningún personaje seleccionado.");
        }
        //VUELVO A CARGAR LA TABLA Y LIMPIO LOS CAMPOS DE TEXTO
        iniciarDatos();
        onLimpiar(event);
    }


//--------------------------------------------------------------------------------------------------------------------
//              ELIMINAR UN PERSONAJE
//--------------------------------------------------------------------------------------------------------------------
    @FXML
    void onEliminar(MouseEvent event) {
        //COMPRUEBO SI HAY UN PERSONAJE SELECCIONADO
        if (personajeSelec != null) {
            //PREGUNTO MEDIANTE UN ALERT QUE RECOGE LA OPCIÓN CLICADA
            boolean confirmado = AlertUtils.mostrarAlerta("Confirmación eliminar personaje",
                    "¿Desea eliminar al personaje?", "");
            //SI PULSA QUE ACEPTA ELIMINO AL PERSONAJE
            if (confirmado) {
                if (personajeSelec != null) {
                    // LLAMO AL MÉTODO DE LA CLASE PERSONAJE Y PASANDO LA CONEXION Y EL ID DEL PERSONAJE SELECCIONADO
                    personajeDAO.eliminarPersonaje(ConexionMongoMejorada.conectar(),personajeSelec.getId());
                    // MUESTRO MENSAJE DE INFORMACIÓN SI TODO SALE BIEN
                    AlertUtils.mostrarInformacion("Personaje eliminado", "INFORMACIÓN", "Personaje borrado con éxito.");
                }
            }
        //SI NO SE HA SELECCIONADO NINGUNO LANZO UN ALERT DE INFORMACIÓN
        } else {
            AlertUtils.mostrarInformacion("Personaje no seleccionado", "INFORMACIÓN","No hay ningún personaje seleccionado.");
        }
        //VUELVO A CARGAR LA TABLA Y LIMPIO LOS CAMPOS DE TEXTO
        iniciarDatos();
        onLimpiar(event);
    }


//--------------------------------------------------------------------------------------------------------------------
//              INSERTAR UN NUEVO PERSONAJE
//--------------------------------------------------------------------------------------------------------------------
    @FXML
    void onInsertar(MouseEvent event) {
        boolean confirmado = AlertUtils.mostrarAlerta("Confirmación de nuevo personaje",
                "¿Desea añadir el nuevo personaje?", "");
        if (confirmado) {
            //HAGO UN TRY PARA PASAR EL CAMPO TEXTO A NÚMERO POR SI SE INTRODUCE ALGO QUE NO SEA NÚMERO CAPTURAR EL ERROR Y MOSTRAR UNA ALERTA
            try {
                int idEscrito = Integer.parseInt(textID.getText());
                if (idEscrito <= 0){
                    AlertUtils.mostrarInformacion("ID no válido", "INFORMACIÓN", "El id del personaje ha de ser un número entero positivo, mayor que 0.");
                } else {
                    //BUSCO EL ID
                    boolean existe = personajeDAO.comprobarExistePersonaje(ConexionMongoMejorada.conectar(), idEscrito);
                    // SI EXISTE BUSCO LANZO UN ALERT DE INFORMACIÓN
                    if (existe) {
                        AlertUtils.mostrarInformacion("ID ya existe", "INFORMACIÓN", "El id del personaje ya existe. Se asignará un valor automático");
                        // Y BUSCO EL ID MÁS ALTO +1 PARA ASIGNAR AL PERSONAJE QUE VOY A AÑADIR
                        int id = personajeDAO.obtenerIdPersonaje(ConexionMongoMejorada.conectar());
                        if (id > 0) {
                            //COMPRUEBO SI SE HA AÑADIDO LA MASCOTA QUE ES UN CAMPO NO OBLIGATORIO
                            if (textMascota.equals("")){
                                Personaje personaje = new Personaje(id,textNombre.getText(),textNomCreador.getText(),textApeCreador.getText());
                                personajeDAO.aniadirPersonaje(ConexionMongoMejorada.conectar(), personaje);
                            } else {
                                Personaje personaje = new Personaje(id,textNombre.getText(),textNomCreador.getText(),textApeCreador.getText(),textMascota.getText());
                                personajeDAO.aniadirPersonaje(ConexionMongoMejorada.conectar(), personaje);
                            }
                            // MUESTRO MENSAJE DE INFORMACIÓN SI TODO SALE BIEN
                            AlertUtils.mostrarInformacion("Personaje añadido", "INFORMACIÓN", "Personaje añadido con éxito.");
                        }
                        // SI NO EXISTE EL ID USO EL INTRODUCIDO PARA AÑADIR AL NUEVO PERSONAJE
                    } else{
                        //COMPRUEBO SI SE HA AÑADIDO LA MASCOTA QUE ES UN CAMPO NO OBLIGATORIO
                        if (textMascota.equals("")){
                            Personaje personaje = new Personaje(Integer.parseInt(textID.getText()),textNombre.getText(),textNomCreador.getText(),textApeCreador.getText());
                            personajeDAO.aniadirPersonaje(ConexionMongoMejorada.conectar(), personaje);
                        } else {
                            Personaje personaje = new Personaje(Integer.parseInt(textID.getText()),textNombre.getText(),textNomCreador.getText(),textApeCreador.getText(),textMascota.getText());
                            personajeDAO.aniadirPersonaje(ConexionMongoMejorada.conectar(), personaje);
                        }
                        // MUESTRO MENSAJE DE INFORMACIÓN SI TODO SALE BIEN
                        AlertUtils.mostrarInformacion("Personaje añadido", "INFORMACIÓN", "Personaje añadido con éxito.");
                    }
                }

            // LANZO UN ERROR CON UN MENSAJE SI NO SE HA PODIDO PASAR EL CAMPO ID A NUMERO POR HABER INTRODUCIDO TEXTO TAMBIÉN
            }catch (Exception e){
                AlertUtils.mostrarError("ID no válido", "ERROR", "El id solo puede ser numérico");
            }
        }
        //VUELVO A CARGAR LA TABLA Y LIMPIO LOS CAMPOS DE TEXTO
        iniciarDatos();
        onLimpiar(event);
    }


//--------------------------------------------------------------------------------------------------------------------
//              LIMPIAR LOS CAMPOS TEXTFIELD Y LA SELECCIÓN DEL PERSONAJE
//--------------------------------------------------------------------------------------------------------------------
    @FXML
    void onLimpiar(MouseEvent event) {
        personajeSelec = null;
        textID.setText("");
        textNombre.setText("");
        textNomCreador.setText("");
        textApeCreador.setText("");
        textMascota.setText("");
        //SI NO HAY SELECCIONADO ALGÚN PERSONAJE EL ID PASA A SER EDITABLE
        textID.setEditable(true);
    }


//--------------------------------------------------------------------------------------------------------------------
//              CARGAR DATOS SELECCIONADOS DEL PERSONAJE SELECCIONADO
//--------------------------------------------------------------------------------------------------------------------
    @FXML
    void onPersonajeSeleccionado(MouseEvent event) {
        personajeSelec = tvTabla.getSelectionModel().getSelectedItem();
        cargarDatos();
    }


//--------------------------------------------------------------------------------------------------------------------
//              CARGAR DATOS DEL PERSONAJE SELECCIONADO DE LA TABLA EN LOS TEXTFIEL
//--------------------------------------------------------------------------------------------------------------------
    private void cargarDatos() {
        if (personajeSelec != null) {
            textID.setText(String.valueOf(personajeSelec.getId()));
            textNombre.setText(personajeSelec.getNombre());
            textNomCreador.setText(personajeSelec.getCreadorNombre());
            textApeCreador.setText(personajeSelec.getCreadorApellido());
            textMascota.setText(personajeSelec.getMascota());
            //SI HAY SELECCIONADO ALGÚN PERSONAJE EL ID PASA A NO SER EDITABLE
            textID.setEditable(false);
        }
    }
}
