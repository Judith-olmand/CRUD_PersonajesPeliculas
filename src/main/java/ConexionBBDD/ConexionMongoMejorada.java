package ConexionBBDD;

import Util.AlertUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import ConexionBBDD.DBConfig;

public class ConexionMongoMejorada {

    public static MongoDatabase conectar() {
        try {
            String uri;
            String user = DBConfig.getUser();
            String pass = DBConfig.getPassword();

            // Si en db.properties hay usuario, montamos la URI con credenciales
            if (user != null && !user.isEmpty()) {
                uri = "mongodb://" + user + ":" + pass + "@" + DBConfig.getHost() + ":" + DBConfig.getPort() + "/?authSource=admin";
            } else {
                uri = "mongodb://" + DBConfig.getHost() + ":" + DBConfig.getPort();
            }

            // Creamos la conexión y devolvemos directamente la base de datos
            MongoClient cliente = MongoClients.create(uri);
            return cliente.getDatabase(DBConfig.getDatabase());

        } catch (Exception e) {
            AlertUtils.mostrarError("Error al conectar", "ERROR", "Error al conectar con la Base de Datos");
            System.out.println("Error al conectar: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}