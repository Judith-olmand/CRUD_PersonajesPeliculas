package DAO;

import Clases.Personaje;
import Util.AlertUtils;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.Arrays;


public class PersonajeDAO implements IPersonajesDAO {

//------------------------------------------------------------------------------------------------------------
//                                  OBTENER LISTADO DE LOS PERSONAJES
//------------------------------------------------------------------------------------------------------------
    public ObservableList<Personaje> obtenerPersonajes(MongoDatabase baseDatos){
        ObservableList<Personaje> listaPersonajes = FXCollections.observableArrayList();

        try {
            // Conectamos con la colección "personajes" de la base de datos
            MongoCollection<Document> coleccionPersonajes = baseDatos.getCollection("personajes");

            // Obtenemos todos los documentos de esa colección
            FindIterable<Document> resultados = coleccionPersonajes.find();

            // Recorremos los resultados
            for (Document doc : resultados) {
                //Es sensible a mayúsculas y minúsculas
                int idPersonaje = doc.getInteger("_id");
                String nombre = doc.getString("nombre");
                String creadorNombre = doc.getString("creadorNombre");
                String creadorApellido = doc.getString("creadorApellido");
                String mascota = doc.getString("mascota");

                listaPersonajes.add(new Personaje(idPersonaje, nombre, creadorNombre, creadorApellido, mascota));
            }
        } catch (Exception e) {
            AlertUtils.mostrarError("Error al obtener los personajes", "ERROR", "Error al obtener los personajes.");
            //System.out.println("Error al obtener los personajes: " + e.getMessage());
        }

        return listaPersonajes;
    }



//------------------------------------------------------------------------------------------------------------
//                                  COMPROBAR SI EXISTE EL ID
//------------------------------------------------------------------------------------------------------------
    public Boolean comprobarExistePersonaje(MongoDatabase baseDatos, int idPersonaje){
        try {
            MongoCollection<Document> coleccionPersonajes = baseDatos.getCollection("personajes");

            Bson filtro = Filters.eq("_id",idPersonaje);

            // Pedimos el primer documento que coincida con el ID
            Document resultado = coleccionPersonajes.find(filtro).first();

            // Si el resultado NO es null, el personaje existe (true). Si es null, no existe (false).
            return resultado != null;

        } catch (Exception e){
            AlertUtils.mostrarError("Error al obtener los id", "ERROR", "Error al obtener los id de los personajes.");
            //System.out.println("Error al obtener los id de los personajes: " + e.getMessage());
            return false;
        }
    }



//------------------------------------------------------------------------------------------------------------
//                                  AÑADIR UN PERSONAJE
//------------------------------------------------------------------------------------------------------------
    public void aniadirPersonaje(MongoDatabase baseDatos, Personaje personaje) {
        try {
            // Conectamos con la colección "personajes" de la base de datos
            MongoCollection<Document> coleccionPersonajes = baseDatos.getCollection("personajes");

            //Si el personaje no tiene mascota
            if (personaje.getMascota() == null || personaje.getMascota().isEmpty()){
                Document nuevoPersonaje = new Document ("_id", personaje.getId())
                        .append("nombre", personaje.getNombre())
                        .append("creadorNombre", personaje.getCreadorNombre())
                        .append("creadorApellido", personaje.getCreadorApellido());
                coleccionPersonajes.insertOne(nuevoPersonaje);

            } else {    //Si el personaje tiene mascota
                Document nuevoPersonaje = new Document ("_id", personaje.getId())
                        .append("nombre", personaje.getNombre())
                        .append("creadorNombre", personaje.getCreadorNombre())
                        .append("creadorApellido", personaje.getCreadorApellido())
                        .append("mascota", personaje.getMascota());
                coleccionPersonajes.insertOne(nuevoPersonaje);
            }

        }catch (Exception e){
            AlertUtils.mostrarError("Error al añadir el personaje", "ERROR", "Error al añadir el personaje.");
            //System.out.println("Error al añadir personaje: " + e.getMessage());
        }

    }



//------------------------------------------------------------------------------------------------------------
//                                  ELIMINAR UN PERSONAJE
//------------------------------------------------------------------------------------------------------------
    public void eliminarPersonaje(MongoDatabase baseDatos, int id) {
        try {
            // Conectamos con la colección "personajes" de la base de datos
            MongoCollection<Document> coleccionPersonajes = baseDatos.getCollection("personajes");

            Bson filtro = Filters.eq("_id", id);
            coleccionPersonajes.deleteOne(filtro);

        }catch (Exception e){
            AlertUtils.mostrarError("Error al eliminar personaje", "ERROR", "Error al eliminar personaje.");
        }
    }




//------------------------------------------------------------------------------------------------------------
//                                  ACTUALIZAR UN PERSONAJE
//------------------------------------------------------------------------------------------------------------
    public void actualizarPersonaje(MongoDatabase baseDatos, Personaje personaje) {
        try {
            // Conectamos con la colección "personajes" de la base de datos
            MongoCollection<Document> coleccionPersonajes = baseDatos.getCollection("personajes");

            Bson filtro = Filters.eq("_id", personaje.getId());
            Bson modificacion = Updates.combine(Updates.set("nombre", personaje.getNombre()),
                    Updates.set("creadorNombre", personaje.getCreadorNombre()),
                    Updates.set("creadorApellido", personaje.getCreadorApellido()),
                    Updates.set("mascota", personaje.getMascota()));

            coleccionPersonajes.updateOne(filtro, modificacion);

        }catch (Exception e){
            AlertUtils.mostrarError("Error al actualizar", "ERROR", "Error al actualizar el personaje.");
            System.out.println("Error al actualizar personaje: " + e.getMessage());
        }
    }



//------------------------------------------------------------------------------------------------------------
//                                  OBTENER EL MAX ID
//------------------------------------------------------------------------------------------------------------
    public int obtenerIdPersonaje(MongoDatabase baseDatos) {
        try {
            // Conectamos con la colección "personajes" de la base de datos
            MongoCollection<Document> coleccionPersonajes = baseDatos.getCollection("personajes");

            if (coleccionPersonajes.countDocuments() == 0) {
                return 1;
            } else {
                // Filtramos para mostrar todos los id
                Bson filtro = Filters.gte("_id", 0);
                // Ordenamos el listado anterior por orden descendente y nos quedamos con el primer resultad
                Document resultado = coleccionPersonajes.find(filtro).sort(Sorts.descending("_id")).first();

                // Devuelvo el resultado +1 para crear el id del nuevo personaje
                return resultado.getInteger("_id")+1;
            }

        }catch (Exception e){
            AlertUtils.mostrarError("Error al obtener los id", "ERROR", "Error al obtener los id de los personajes.");
            //System.out.println("Error al obtener los id de los personajes: " + e.getMessage());
            return -1;
        }
    }

    public void insertarDatosIniciales(MongoDatabase baseDatos) {
        try {
            MongoCollection<Document> coleccion = baseDatos.getCollection("personajes");

            // COMPRUEBO SI LA COLECCIÓN ESTÁ VACÍA
            if (coleccion.countDocuments() == 0) {
                AlertUtils.mostrarInformacion("Base de datos vacía", "INFORMACIÓN", "Base de datos vacía. Se han añadido personajes de prueba");
                Document p1 = new Document("_id", 1)
                        .append("nombre", "Mickey Mouse")
                        .append("creadorNombre", "Walt")
                        .append("creadorApellido", "Disney")
                        .append("mascota", "Pluto");

                Document p2 = new Document("_id", 2)
                        .append("nombre", "Charlie Brown")
                        .append("creadorNombre", "Charles")
                        .append("creadorApellido", "Schulz")
                        .append("mascota", "Snoopy");

                // INSERTO LOS 2 PERSONAJES A LA VEZ
                coleccion.insertMany(Arrays.asList(p1, p2));
            }
        } catch (Exception e) {
            AlertUtils.mostrarError("Error al insertar datos", "ERROR", "Error al insertar datos iniciales.");
            //System.out.println("Error al insertar datos iniciales: " + e.getMessage());
        }
    }
}



