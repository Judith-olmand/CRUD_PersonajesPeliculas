package DAO;

import Clases.Personaje;
import com.mongodb.client.MongoDatabase;
import javafx.collections.ObservableList;

public interface IPersonajesDAO {
//------------------------------------------------------------------------------------------------------------
//                                  OBTENER LISTADO DE LOS PERSONAJES
//------------------------------------------------------------------------------------------------------------
    ObservableList<Personaje> obtenerPersonajes(MongoDatabase baseDatos);

//------------------------------------------------------------------------------------------------------------
//                                  COMPROBAR SI EXISTE EL ID
//------------------------------------------------------------------------------------------------------------
    Boolean comprobarExistePersonaje(MongoDatabase baseDatos, int idPersonaje);

//------------------------------------------------------------------------------------------------------------
//                                  AÑADIR UN PERSONAJE
//------------------------------------------------------------------------------------------------------------
    void aniadirPersonaje(MongoDatabase baseDatos, Personaje personaje);

//------------------------------------------------------------------------------------------------------------
//                                  ELIMINAR UN PERSONAJE
//------------------------------------------------------------------------------------------------------------
    void eliminarPersonaje(MongoDatabase baseDatos, int id);

//------------------------------------------------------------------------------------------------------------
//                                  ACTUALIZAR UN PERSONAJE
//------------------------------------------------------------------------------------------------------------
    void actualizarPersonaje(MongoDatabase baseDatos, Personaje personaje);

//------------------------------------------------------------------------------------------------------------
//                                  OBTENER EL MAX ID
//------------------------------------------------------------------------------------------------------------
    int obtenerIdPersonaje(MongoDatabase baseDatos);
}
