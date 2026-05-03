package com.example.gestordetares_m1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase helper para gestionar la base de datos SQLite de tareas.
 * 
 * Esta clase extiende de SQLiteOpenHelper y proporciona:
 * - Creación de la base de datos y tabla
 * - Operaciones CRUD completas (Create, Read, Update, Delete)
 * - Gestión de versión de la base de datos
 * 
 * Estructura de la tabla Tareas:
 * - id: Integer, Primary Key, Autoincremental
 * - titulo: Text, No nulo
 * - descripcion: Text
 * - estado: Integer (0=pendiente, 1=completada)
 * 
 * @author Desarrollador Junior
 * @version 1.0
 */
public class TaskDatabaseHelper extends SQLiteOpenHelper {
    
    // ============================================================================
    // CONSTANTES DE LA BASE DE DATOS
    // ============================================================================
    
    /**
     * Nombre de la base de datos.
     * Se almacena localmente en el dispositivo del usuario.
     */
    private static final String DATABASE_NAME = "tasks.db";
    
    /**
     * Versión de la base de datos.
     * Incrementar cuando se hagan cambios en el esquema.
     */
    private static final int DATABASE_VERSION = 1;
    
    /**
     * Nombre de la tabla principal donde se guardan las tareas.
     */
    private static final String TABLE_TASKS = "tareas";
    
    /**
     * Nombres de las columnas de la tabla.
     * Usamos constantes para evitar errores de escritura.
     */
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "titulo";
    private static final String COLUMN_DESCRIPTION = "descripcion";
    private static final String COLUMN_STATUS = "estado";
    
    // ============================================================================
    // CONSTRUCTOR
    // ============================================================================
    
    /**
     * Constructor de la clase.
     * 
     * @param context El contexto de la aplicación (necesario para SQLiteOpenHelper)
     */
    public TaskDatabaseHelper(Context context) {
        // Llamamos al constructor de la clase padre con:
        // - Contexto
        // - Nombre de la BD
        // - CursorFactory (null usa el default)
        // - Versión de la BD
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    // ============================================================================
    // MÉTODOS DE CREACIÓN Y ACTUALIZACIÓN DE LA BASE DE DATOS
    // ============================================================================
    
    /**
     * Se ejecuta automáticamente cuando se crea la base de datos por primera vez.
     * Aquí creamos la tabla de tareas con su estructura.
     * 
     * @param db La instancia de SQLiteDatabase que podemos usar para ejecutar SQL
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Creamos la sentencia SQL para crear la tabla
        // IF NOT EXISTS previene errores si la tabla ya existe
        String createTableSQL = "CREATE TABLE IF NOT EXISTS " + TABLE_TASKS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +  // ID autoincremental
                COLUMN_TITLE + " TEXT NOT NULL, " +                    // Título obligatorio
                COLUMN_DESCRIPTION + " TEXT, " +                       // Descripción opcional
                COLUMN_STATUS + " INTEGER DEFAULT 0" +                 // Estado por defecto: pendiente
                ")";
        
        // Ejecutamos la sentencia SQL
        db.execSQL(createTableSQL);
    }
    
    /**
     * Se ejecuta cuando cambia la versión de la base de datos.
     * Aquí deberíamos manejar migraciones de datos si es necesario.
     * 
     * @param db La base de datos
     * @param oldVersion La versión anterior
     * @param newVersion La nueva versión
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // En una aplicación real, aquí haríamos migraciones de datos
        // Por ahora, simplemente eliminamos la tabla y la creamos de nuevo
        // NOTA: Esto borra todos los datos existentes
        
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASKS);
        onCreate(db);
    }
    
    // ============================================================================
    // OPERACIONES CRUD - CREATE (INSERTAR)
    // ============================================================================
    
    /**
     * Inserta una nueva tarea en la base de datos.
     * 
     * @param task El objeto Task con los datos a insertar
     * @return El ID de la nueva tarea insertada, o -1 si hubo error
     */
    public long insertTask(Task task) {
        // Obtenemos la base de datos en modo escritura
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Creamos un ContentValues para almacenar los datos
        // ContentValues es como un mapa clave-valor especializado para SQLite
        ContentValues values = new ContentValues();
        
        // Agregamos los valores a insertar
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_STATUS, task.getStatus());
        
        // Insertamos en la tabla y obtenemos el ID generado
        // nullColumnHack: se usa cuando ContentValues está vacío (no es nuestro caso)
        long taskId = db.insert(TABLE_TASKS, null, values);
        
        // Cerramos la conexión para liberar recursos
        db.close();
        
        // Retornamos el ID de la tarea insertada
        return taskId;
    }
    
    // ============================================================================
    // OPERACIONES CRUD - READ (LEER)
    // ============================================================================
    
    /**
     * Obtiene todas las tareas de la base de datos.
     * 
     * @return Una lista con todos los objetos Task almacenados
     */
    public List<Task> getAllTasks() {
        // Lista para almacenar las tareas
        List<Task> tasks = new ArrayList<>();
        
        // Obtenemos la base de datos en modo lectura
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Definimos la consulta SQL
        // SELECT * FROM tareas ORDER BY id DESC (más recientes primero)
        String selectQuery = "SELECT * FROM " + TABLE_TASKS + " ORDER BY " + COLUMN_ID + " DESC";
        
        // Ejecutamos la consulta y obtenemos un Cursor
        // Cursor es como un puntero a los resultados de la consulta
        Cursor cursor = db.rawQuery(selectQuery, null);
        
        // Iteramos sobre todos los resultados del Cursor
        if (cursor.moveToFirst()) {
            do {
                // Extraemos los datos de cada fila
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                int status = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
                
                // Creamos un objeto Task con los datos
                Task task = new Task(id, title, description, status);
                
                // Agregamos la tarea a la lista
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        
        // Cerramos el Cursor y la base de datos
        cursor.close();
        db.close();
        
        // Retornamos la lista de tareas
        return tasks;
    }
    
    /**
     * Obtiene una tarea específica por su ID.
     * 
     * @param taskId El ID de la tarea a buscar
     * @return El objeto Task encontrado, o null si no existe
     */
    public Task getTaskById(int taskId) {
        SQLiteDatabase db = this.getReadableDatabase();
        
        // Consulta con WHERE para filtrar por ID
        String selectQuery = "SELECT * FROM " + TABLE_TASKS + 
                            " WHERE " + COLUMN_ID + " = ?";
        
        // Ejecutamos la consulta con el parámetro
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(taskId)});
        
        Task task = null;
        
        // Si encontramos resultados, extraemos los datos
        if (cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TITLE));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            int status = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
            
            task = new Task(id, title, description, status);
        }
        
        cursor.close();
        db.close();
        
        return task;
    }
    
    // ============================================================================
    // OPERACIONES CRUD - UPDATE (ACTUALIZAR)
    // ============================================================================
    
    /**
     * Actualiza una tarea existente en la base de datos.
     * 
     * @param task El objeto Task con los datos actualizados
     * @return El número de filas afectadas (debería ser 1)
     */
    public int updateTask(Task task) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Creamos ContentValues con los nuevos valores
        ContentValues values = new ContentValues();
        values.put(COLUMN_TITLE, task.getTitle());
        values.put(COLUMN_DESCRIPTION, task.getDescription());
        values.put(COLUMN_STATUS, task.getStatus());
        
        // Actualizamos donde el ID coincida
        // Usamos selection y selectionArgs para evitar SQL injection
        int rowsAffected = db.update(
            TABLE_TASKS,
            values,
            COLUMN_ID + " = ?",
            new String[]{String.valueOf(task.getId())}
        );
        
        db.close();
        
        return rowsAffected;
    }
    
    /**
     * Actualiza solo el estado de una tarea.
     * Método útil para cambiar rápidamente entre pendiente/completado.
     * 
     * @param taskId El ID de la tarea a actualizar
     * @param newStatus El nuevo estado (0=pendiente, 1=completada)
     * @return El número de filas afectadas
     */
    public int updateTaskStatus(int taskId, int newStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, newStatus);
        
        int rowsAffected = db.update(
            TABLE_TASKS,
            values,
            COLUMN_ID + " = ?",
            new String[]{String.valueOf(taskId)}
        );
        
        db.close();
        
        return rowsAffected;
    }
    
    // ============================================================================
    // OPERACIONES CRUD - DELETE (ELIMINAR)
    // ============================================================================
    
    /**
     * Elimina una tarea de la base de datos por su ID.
     * 
     * @param taskId El ID de la tarea a eliminar
     * @return El número de filas afectadas (debería ser 1)
     */
    public int deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        
        // Eliminamos donde el ID coincida
        int rowsAffected = db.delete(
            TABLE_TASKS,
            COLUMN_ID + " = ?",
            new String[]{String.valueOf(taskId)}
        );
        
        db.close();
        
        return rowsAffected;
    }
    
    /**
     * Elimina todas las tareas de la base de datos.
     * ¡Usar con precaución!
     * 
     * @return El número de filas eliminadas
     */
    public int deleteAllTasks() {
        SQLiteDatabase db = this.getWritableDatabase();
        
        int rowsAffected = db.delete(TABLE_TASKS, null, null);
        
        db.close();
        
        return rowsAffected;
    }
    
    // ============================================================================
    // MÉTODOS DE UTILIDAD
    // ============================================================================
    
    /**
     * Obtiene el conteo total de tareas en la base de datos.
     * 
     * @return El número total de tareas
     */
    public int getTasksCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        
        String countQuery = "SELECT COUNT(*) FROM " + TABLE_TASKS;
        Cursor cursor = db.rawQuery(countQuery, null);
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        
        cursor.close();
        db.close();
        
        return count;
    }
}
