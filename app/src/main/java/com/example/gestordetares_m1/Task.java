package com.example.gestordetares_m1;

/**
 * Clase modelo que representa una Tarea en la aplicación.
 * 
 * Esta clase sigue el patrón JavaBean con getters y setters.
 * Cada instancia de Task corresponde a un registro en la tabla SQLite.
 * 
 * @author Desarrollador Junior
 * @version 1.0
 */
public class Task {
    
    // ============================================================================
    // ATRIBUTOS DE LA CLASE
    // ============================================================================
    
    /**
     * Identificador único de la tarea.
     * Es autoincremental en la base de datos SQLite.
     */
    private int id;
    
    /**
     * Título de la tarea.
     * Campo obligatorio, máximo 100 caracteres en la BD.
     */
    private String title;
    
    /**
     * Descripción breve de la tarea.
     * Campo opcional, máximo 500 caracteres en la BD.
     */
    private String description;
    
    /**
     * Estado de la tarea.
     * 0 = Pendiente, 1 = Completada
     * Usamos entero para facilitar el almacenamiento en SQLite.
     */
    private int status; // 0 = pendiente, 1 = completada
    
    // ============================================================================
    // CONSTRUCTORES
    // ============================================================================
    
    /**
     * Constructor vacío.
     * Necesario para crear objetos Task sin inicializar valores.
     * Útil cuando se recuperan datos de la base de datos.
     */
    public Task() {
        // Inicialización por defecto: tarea pendiente sin título ni descripción
        this.status = 0;
    }
    
    /**
     * Constructor completo con todos los parámetros.
     * 
     * @param id El identificador único de la tarea
     * @param title El título de la tarea
     * @param description La descripción breve de la tarea
     * @param status El estado de la tarea (0=pendiente, 1=completada)
     */
    public Task(int id, String title, String description, int status) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
    }
    
    /**
     * Constructor sin ID.
     * Se usa cuando creamos una nueva tarea que aún no tiene ID asignado.
     * El ID se generará automáticamente al insertar en SQLite.
     * 
     * @param title El título de la tarea
     * @param description La descripción breve de la tarea
     * @param status El estado de la tarea
     */
    public Task(String title, String description, int status) {
        this.title = title;
        this.description = description;
        this.status = status;
    }
    
    // ============================================================================
    // GETTERS Y SETTERS (MÉTODOS DE ACCESO)
    // ============================================================================
    
    /**
     * Obtiene el identificador único de la tarea.
     * 
     * @return El ID de la tarea como entero
     */
    public int getId() {
        return id;
    }
    
    /**
     * Establece el identificador único de la tarea.
     * 
     * @param id El nuevo ID para la tarea
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Obtiene el título de la tarea.
     * 
     * @return El título como String
     */
    public String getTitle() {
        return title;
    }
    
    /**
     * Establece el título de la tarea.
     * 
     * @param title El nuevo título para la tarea
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Obtiene la descripción de la tarea.
     * 
     * @return La descripción como String
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Establece la descripción de la tarea.
     * 
     * @param description La nueva descripción para la tarea
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Obtiene el estado de la tarea.
     * 
     * @return El estado como entero (0=pendiente, 1=completada)
     */
    public int getStatus() {
        return status;
    }
    
    /**
     * Establece el estado de la tarea.
     * 
     * @param status El nuevo estado (0=pendiente, 1=completada)
     */
    public void setStatus(int status) {
        this.status = status;
    }
    
    // ============================================================================
    // MÉTODOS DE UTILIDAD
    // ============================================================================
    
    /**
     * Verifica si la tarea está completada.
     * Método auxiliar para facilitar la lectura del código.
     * 
     * @return true si la tarea está completada, false si está pendiente
     */
    public boolean isCompleted() {
        return status == 1;
    }
    
    /**
     * Marca la tarea como completada.
     * Cambia el estado interno a 1 (completada).
     */
    public void markAsCompleted() {
        this.status = 1;
    }
    
    /**
     * Marca la tarea como pendiente.
     * Cambia el estado interno a 0 (pendiente).
     */
    public void markAsPending() {
        this.status = 0;
    }
    
    /**
     * Representación en texto de la tarea.
     * Útil para debugging y logs.
     * 
     * @return String con el formato "Task{id=X, title='Y'}"
     */
    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
