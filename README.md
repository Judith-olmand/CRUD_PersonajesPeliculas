# 🎬 CRUD de Personajes de Películas Infantiles (JavaFX + MongoDB)

Aplicación de escritorio desarrollada en Java con JavaFX que implementa un CRUD completo sobre una colección de personajes de películas infantiles almacenada en una base de datos NoSQL MongoDB.

> ⚠️ **NOTA SOBRE EL ENUNCIADO (contradicción y cómo se ha resuelto):**
>
> El enunciado de la tarea se contradice a sí mismo en lo que respecta al `id`:
>
> * **Al principio** dice: *"El id ha de ser un número entero. Lo tienes que calcular. NO se introduce. No puede repetirse (índice único)."*
> * **Al final** dice: *"Insertar: crea un personaje con los datos del formulario. Si el ID ya existe, se avisa y no se inserta."*
>
> Si el ID **no se introduce** y se **calcula** siempre, nunca podría "ya existir" porque lo generaría la propia aplicación. Y si el formulario aporta un ID, entonces sí se está introduciendo a mano.
>
> **Solución adoptada (combina las dos ideas):**
>
> 1. El campo `ID` del formulario es **editable solo cuando no hay ninguna fila seleccionada** (modo "nuevo personaje"). En cuanto se selecciona un personaje de la tabla, el campo pasa a ser **no editable**, tal y como pide la parte de *Actualizar* del enunciado.
> 2. Al insertar se valida que el ID sea un **entero positivo (> 0)**.
> 3. Si el ID escrito **ya existe**, se **avisa** al usuario con una alerta y, en lugar de rechazar la inserción, la aplicación **calcula automáticamente el siguiente ID libre** (el ID más alto + 1) y lo usa para el nuevo personaje. De esta forma se respeta que el ID se calcule y no se repita.
> 4. Si el ID escrito **no existe**, se utiliza el introducido.
> 5. La **unicidad** está garantizada porque el `id` se guarda como `_id` de MongoDB, que ya tiene un índice único por defecto.

## 📋 Descripción

Este software permite gestionar un catálogo de personajes de películas infantiles: consultarlos en una tabla, añadir nuevos, modificar los existentes y eliminarlos. Todo ello respaldado por una base de datos MongoDB y con una arquitectura en capas que separa la interfaz, la lógica y el acceso a datos.

## 🌟 Características Principales

* 🎨 **Interfaz Gráfica (GUI)** desarrollada con JavaFX (FXML) y estilizada con CSS (hoja de estilos reutilizada de un ejercicio de programación del curso anterior).
* 🍃 **Persistencia de Datos** mediante el driver oficial síncrono de MongoDB (`mongodb-driver-sync`).
* 📝 **CRUD Completo** (Crear, Leer, Actualizar, Borrar) de personajes.
* 🔢 **ID único y calculado** automáticamente cuando hay conflicto (ver nota sobre el enunciado).
* ⚙️ **Conexión configurable** desde un fichero externo `db.properties`, sin credenciales escritas en el código.
* 🌱 **Datos de prueba automáticos** si la colección está vacía al arrancar.
* 🏗️ **Arquitectura Robusta** basada en el patrón MVC (Modelo-Vista-Controlador) y DAO (Data Access Object) con interfaz.
* 🛡️ **Validación de Datos** y alertas interactivas de información, error y confirmación.

## 🏗️ Estructura del Proyecto

```text
CRUD_PersonajesPeliculas/
│
├── src/main/java/
│   │
│   ├── Clases/                          # Modelo de datos (POJO)
│   │   └── Personaje.java               # Entidad personaje (mascota opcional)
│   │
│   ├── com.example.crud_personajespeliculas/   # Arranque de la aplicación
│   │   ├── Aplicacion.java              # Carga la escena JavaFX y los datos iniciales
│   │   └── Lanzador.java                # Clase main principal
│   │
│   ├── ConexionBBDD/                    # Conexión con MongoDB
│   │   ├── ConexionMongo.java           # Clase original proporcionada por el profesor
│   │   ├── ConexionMongoMejorada.java   # Versión mejorada (la que usa la app)
│   │   └── DBConfig.java                # Lector de db.properties
│   │
│   ├── Controladores/                   # Controladores de la interfaz (MVC)
│   │   └── PersonajeControlador.java    # Lógica de la pantalla y eventos del CRUD
│   │
│   ├── DAO/                             # Acceso a datos
│   │   ├── IPersonajesDAO.java          # Interfaz con las operaciones
│   │   └── PersonajeDAO.java            # Implementación sobre MongoDB
│   │
│   └── Util/                            # Utilidades generales
│       ├── AlertUtils.java              # Ventanas emergentes (error, info, confirmación)
│       └── R.java                       # Gestor de recursos (imágenes, properties, UI)
│
├── src/main/resources/
│   ├── com.example.crud_personajespeliculas/
│   │   └── PersonajeView.fxml           # Vista principal vinculada al controlador
│   ├── configuration/
│   │   └── db.properties                # Parámetros de conexión a MongoDB
│   └── CSS/
│       └── estilos.css                  # Hoja de estilos
│
├── pom.xml                              # Configuración de dependencias Maven
└── README.md                            # Documentación
```

## 🔌 ¿Por qué `ConexionMongoMejorada` y no `ConexionMongo`?

El proyecto incluye las dos clases. `ConexionMongo` es la que entregó el profesor y funciona correctamente, pero para esta aplicación se ha preferido una versión mejorada, `ConexionMongoMejorada`, por las siguientes razones:

| Aspecto | `ConexionMongo` (profesor) | `ConexionMongoMejorada` |
| --- | --- | --- |
| **Configuración** | URI, base de datos y colección escritas directamente en el código (`static final`). Para cambiar de servidor hay que **editar y recompilar**. | Lee host, puerto, base de datos, usuario y contraseña de `db.properties` mediante `DBConfig`. Se cambia de servidor **sin tocar el código**. |
| **Credenciales** | Hay que comentar/descomentar la línea de la URI con el usuario y la contraseña **dentro del código fuente**, con el riesgo de subirlas a un repositorio. | Si `username` está vacío conecta sin autenticación; si tiene valor, monta automáticamente la URI con credenciales (`authSource=admin`). |
| **Uso** | Requiere instanciar la clase y llamar a `conectar()` antes de `getColeccion()`; si se olvida, lanza `IllegalStateException`. Hay que gestionar además el estado interno (`cliente`, `baseDatos`). | Método **estático** `conectar()` que devuelve directamente el `MongoDatabase`. No hay estado interno ni orden de llamadas que recordar. |
| **Responsabilidad** | Fija el nombre de la colección `"personajes"` en la clase de conexión, mezclando conexión y datos. | Solo se ocupa de la **conexión**. Qué colección usar lo decide el DAO. |
| **Gestión de errores** | Los fallos se propagan sin tratar, de forma que el usuario no ve nada en la interfaz. | Captura la excepción, muestra una alerta al usuario (`AlertUtils`), la imprime por consola y relanza un `RuntimeException`. |
| **Reutilización** | Pensada como ejemplo de clase de conexión de consola. | Encaja con el patrón DAO: cada método del DAO recibe el `MongoDatabase` por parámetro. |

En resumen: la versión mejorada **separa la configuración del código**, **evita errores de uso**, **protege las credenciales** e **informa al usuario** cuando algo falla.

> 💡 **Posible mejora futura:** actualmente cada operación abre un `MongoClient` nuevo. Lo ideal sería crear un único cliente compartido durante toda la ejecución y cerrarlo al salir de la aplicación.

## 🗄️ Base de Datos y Modelo

El sistema funciona sobre MongoDB con la base de datos `personajes_peliculas` y una única colección llamada `personajes`.

### 🎭 Personaje

Cada documento de la colección tiene la siguiente forma:

```json
{
  "_id": 1,
  "nombre": "Mickey Mouse",
  "creadorNombre": "Walt",
  "creadorApellido": "Disney",
  "mascota": "Pluto"
}
```

| Campo | Tipo | Obligatorio | Descripción |
| --- | --- | --- | --- |
| `_id` | Entero | ✅ | Identificador del personaje. Único por definición en MongoDB. |
| `nombre` | Texto | ✅ | Nombre del personaje. |
| `creadorNombre` | Texto | ✅ | Nombre del creador. |
| `creadorApellido` | Texto | ✅ | Apellido del creador. |
| `mascota` | Texto | ❌ | Mascota del personaje. Si no se rellena, **no se guarda el campo** al insertar. |

### 🌱 Datos de prueba

Al arrancar, si la colección está vacía, se insertan automáticamente dos personajes y se avisa al usuario con una alerta:

* 🐭 **1** – Mickey Mouse (Walt Disney) – mascota: Pluto
* 🧒 **2** – Charlie Brown (Charles Schulz) – mascota: Snoopy

## 🎮 Mecánicas de Uso

### Carga inicial

1. `Aplicacion` carga `PersonajeView.fxml` y muestra la ventana.
2. Se llama a `iniciarDatos()`, que inserta los datos de prueba si hace falta y carga la tabla **ordenada por ID ascendente**.
3. Si no hay personajes, se muestra una alerta informativa.

### Gestión de Personajes (CRUD)

* **Ver**: la `TableView` muestra todos los personajes. Al hacer clic en una fila, sus datos se cargan en el formulario y el campo `ID` **pasa a no ser editable**.
* **Insertar**: pide confirmación, valida que el ID sea un entero mayor que 0 y comprueba si ya existe:
    * *No existe* → se inserta con el ID escrito.
    * *Ya existe* → se avisa y se inserta con el siguiente ID libre (máximo + 1).
    * *No es numérico* → alerta de error.
* **Actualizar**: requiere tener un personaje seleccionado. Tras confirmar, modifica nombre, creador y mascota del registro. El ID no se puede cambiar.
* **Eliminar**: requiere tener un personaje seleccionado y elimina el registro tras una confirmación.
* **Limpiar**: vacía el formulario, deselecciona el personaje y vuelve a hacer editable el campo `ID`.

Después de cada operación la tabla se recarga y el formulario se limpia.

## 🚀 Ejecución y Configuración

### Requisitos

* Java JDK 17 o superior (JavaFX 21).
* Maven.
* Servidor MongoDB ejecutándose (por defecto en `localhost:27017`).

### Dependencias principales (`pom.xml`)

* `mongodb-driver-sync` 5.2.0
* `javafx-controls` y `javafx-fxml` 21.0.6
* `controlsfx`, `formsfx-core`, `bootstrapfx-core`
* `junit-jupiter` (pruebas)

### Configuración de la conexión

Edita el archivo `src/main/resources/configuration/db.properties`:

```properties
host=localhost
port=27017
name=personajes_peliculas
username=
password=
```

* Si `username` está **vacío**, se conecta sin autenticación.
* Si `username` tiene valor, se conecta con usuario y contraseña (`authSource=admin`).

### Arranque

```bash
mvn clean javafx:run
```

O ejecutando directamente la clase `Lanzador` desde el IDE.

> La colección y la base de datos se crean solas al insertar el primer documento, por lo que no hace falta ningún script previo.

## 📊 Flujo del Programa

```text
┌───────────────────────────┐
│   Arranque (Aplicacion)   │
│  • Carga PersonajeView    │
│  • Datos de prueba si     │
│    la colección está vacía│
└─────────────┬─────────────┘
              ▼
┌───────────────────────────┐
│   TableView de personajes │
│   (ordenada por ID)       │
└─────────────┬─────────────┘
              │
     ┌────────┼─────────┬──────────┐
     ▼        ▼         ▼          ▼
┌────────┐ ┌─────────┐ ┌────────┐ ┌─────────┐
│Insertar│ │Actualiz.│ │Eliminar│ │ Limpiar │
└───┬────┘ └────┬────┘ └───┬────┘ └────┬────┘
    │           │          │           │
    ▼           ▼          ▼           ▼
 ¿ID existe?  Requiere   Requiere   Deselecciona
 ├ No → usa   selección  selección  y habilita ID
 │   el ID
 └ Sí → aviso
     + ID auto
    │           │          │
    └───────────┴──────────┘
              │ (recarga y limpia)
              ▼
┌───────────────────────────┐
│  TableView sincronizada   │
└───────────────────────────┘
```

## 🎓 Conceptos de Programación Aplicados

### Arquitectura y Patrones

* 🏗️ **MVC (Model-View-Controller)**: separación entre la vista (FXML + CSS), el controlador (`PersonajeControlador`) y el modelo (`Personaje`).
* 📦 **Patrón DAO con interfaz**: `IPersonajesDAO` define las operaciones y `PersonajeDAO` las implementa sobre MongoDB, aislando el acceso a datos del resto de la aplicación.
* ⚙️ **Configuración externa**: `DBConfig` carga `db.properties` en un bloque `static`, evitando datos de conexión en el código.

### MongoDB

* 🍃 Uso de `MongoClients`, `MongoDatabase`, `MongoCollection<Document>`.
* 🔎 Filtros con `Filters.eq` y `Filters.gte`.
* ↕️ Ordenación con `Sorts.descending` para obtener el ID máximo.
* ✏️ Actualizaciones con `Updates.set` y `Updates.combine`.
* 📥 Operaciones `insertOne`, `insertMany`, `updateOne`, `deleteOne` y `countDocuments`.
* 🔑 Uso de `_id` entero como clave única.

### JavaFX y UI

* 🪟 Inyección de componentes con `@FXML`.
* 🔄 Uso de `ObservableList` y `PropertyValueFactory` para rellenar la tabla.
* 🔒 Control dinámico de `setEditable` en el campo ID según haya o no selección.
* 🚦 Gestión de eventos de ratón (`MouseEvent`).
* 🔔 Alertas centralizadas en `AlertUtils` (información, error y confirmación).

## ⚠️ Manejo de Errores

El sistema cuenta con prevención y notificaciones visuales (`Alerts`) para:

* ⛔ ID no numérico.
* ⛔ ID menor o igual que 0.
* ⚠️ ID ya existente (se avisa y se asigna uno automático).
* ⛔ Acciones de actualizar o eliminar sin haber seleccionado un personaje.
* ⛔ Errores de conexión con la base de datos.
* ⛔ Errores al leer, insertar, actualizar o eliminar documentos.
* ℹ️ Base de datos vacía o sin personajes.