package ConexionBBDD;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

/**
 * Clase encargada ÚNICAMENTE de la conexión con MongoDB.
 * Los alumnos de JavaFX podrán reutilizarla tal cual.
 */
public class ConexionMongo {

    private static final String URI = "mongodb://localhost:27017";
    // private static final String URI = "mongodb://admin:1234@localhost:27017/?authSource=admin";
    private static final String BASE_DATOS = "personajes_peliculas";
    private static final String COLECCION = "personajes";

    private MongoClient cliente;
    private MongoDatabase baseDatos;

    /** Abre la conexión con el servidor y selecciona la base de datos. */
    public void conectar() {
        cliente = MongoClients.create(URI);
        baseDatos = cliente.getDatabase(BASE_DATOS);
        System.out.println("Conexión correcta");
    }

    /** Devuelve la colección "personajes" (se crea sola al insertar el primer documento). */
    public MongoCollection<Document> getColeccion() {
        if (baseDatos == null) {
            throw new IllegalStateException("Primero debes llamar a conectar()");
        }
        return baseDatos.getCollection(COLECCION);
    }

    /** Cierra la conexión. */
    public void cerrar() {
        if (cliente != null) {
            cliente.close();
            cliente = null;
            baseDatos = null;
        }
    }
}
