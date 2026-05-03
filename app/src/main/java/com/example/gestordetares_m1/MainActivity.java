package com.example.gestordetares_m1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Actividad principal de la aplicación Gestor de Tareas.
 * 
 * Esta actividad es el punto de entrada de la aplicación y muestra:
 * - Un RecyclerView con la lista de todas las tareas
 * - Un FloatingActionButton para agregar nuevas tareas
 * - Un mensaje cuando no hay tareas registradas
 * 
 * Funcionalidades principales:
 * - Listar todas las tareas desde SQLite
 * - Abrir formulario para crear nueva tarea
 * - Abrir formulario para editar tarea existente
 * - Eliminar tareas
 * - Cambiar estado de tareas (pendiente/completada)
 * 
 * @author Desarrollador Junior
 * @version 1.0
 */
public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskClickListener {
    
    // ============================================================================
    // CONSTANTES
    // ============================================================================
    
    /**
     * Código de petición para abrir el formulario de tareas.
     * Usado para identificar el resultado cuando regresamos del formulario.
     */
    private static final int REQUEST_CODE_TASK_FORM = 100;
    
    /**
     * Clave para pasar el ID de la tarea al formulario.
     */
    public static final String EXTRA_TASK_ID = "task_id";
    
    /**
     * Clave para indicar si se debe refrescar la lista.
     */
    public static final String EXTRA_REFRESH_LIST = "refresh_list";
    
    // ============================================================================
    // ATRIBUTOS DE LA ACTIVIDAD
    // ============================================================================
    
    /**
     * RecyclerView para mostrar la lista de tareas.
     * Componente eficiente para mostrar colecciones grandes de datos.
     */
    private RecyclerView recyclerViewTasks;
    
    /**
     * Adapter que conecta los datos con el RecyclerView.
     */
    private TaskAdapter taskAdapter;
    
    /**
     * Lista de tareas que se mostrará en el adapter.
     */
    private List<Task> taskList;
    
    /**
     * Helper para operaciones con la base de datos SQLite.
     */
    private TaskDatabaseHelper databaseHelper;
    
    /**
     * TextView para mostrar mensaje cuando la lista está vacía.
     */
    private TextView emptyListTextView;
    
    /**
     * Botón flotante para agregar nueva tarea.
     */
    private FloatingActionButton fabAddTask;
    
    // ============================================================================
    // CICLO DE VIDA DE LA ACTIVIDAD
    // ============================================================================
    
    /**
     * Método llamado cuando la actividad se crea por primera vez.
     * Aquí inicializamos todos los componentes de la interfaz.
     * 
     * @param savedInstanceState Estado guardado previamente (si existe)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Configuramos el layout principal de esta actividad
        setContentView(R.layout.activity_main);
        
        // Inicializamos el helper de base de datos
        // Esto crea la BD si no existe
        databaseHelper = new TaskDatabaseHelper(this);
        
        // Inicializamos las vistas del layout
        initializeViews();
        
        // Configuramos el RecyclerView con su adapter
        setupRecyclerView();
        
        // Configuramos los listeners para los botones
        setupClickListeners();
        
        // Cargamos las tareas desde la base de datos
        loadTasksFromDatabase();
    }
    
    /**
     * Método llamado cuando la actividad vuelve a estar en primer plano.
     * Refrescamos la lista por si hubo cambios en el formulario.
     */
    @Override
    protected void onResume() {
        super.onResume();
        // Recargamos las tareas cada vez que volvemos a esta pantalla
        loadTasksFromDatabase();
    }
    
    // ============================================================================
    // MÉTODOS DE INICIALIZACIÓN
    // ============================================================================
    
    /**
     * Inicializa todas las vistas del layout.
     * Vincula las variables Java con los elementos XML.
     */
    private void initializeViews() {
        // Obtenemos referencia al RecyclerView
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        
        // Obtenemos referencia al TextView de lista vacía
        emptyListTextView = findViewById(R.id.emptyListTextView);
        
        // Obtenemos referencia al botón flotante
        fabAddTask = findViewById(R.id.fabAddTask);
    }
    
    /**
     * Configura el RecyclerView con LayoutManager y Adapter.
     */
    private void setupRecyclerView() {
        // Inicializamos la lista vacía
        taskList = new ArrayList<>();
        
        // Creamos el adapter pasando la lista y el listener (esta actividad)
        taskAdapter = new TaskAdapter(taskList, this);
        
        // Configuramos un LinearLayoutManager para mostrar los ítems en vertical
        // Esto hace que las tareas se muestren una debajo de otra
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewTasks.setLayoutManager(layoutManager);
        
        // Asignamos el adapter al RecyclerView
        recyclerViewTasks.setAdapter(taskAdapter);
    }
    
    /**
     * Configura los listeners para los botones de la interfaz.
     */
    private void setupClickListeners() {
        // Listener para el botón flotante de agregar tarea
        fabAddTask.setOnClickListener(v -> openTaskForm(-1));
    }
    
    // ============================================================================
    // MÉTODOS DE CARGA DE DATOS
    // ============================================================================
    
    /**
     * Carga todas las tareas desde la base de datos y actualiza la UI.
     */
    private void loadTasksFromDatabase() {
        // Obtenemos todas las tareas desde SQLite
        List<Task> tasks = databaseHelper.getAllTasks();
        
        // Actualizamos la lista del adapter
        taskList.clear(); // Limpiamos la lista actual
        taskList.addAll(tasks); // Agregamos las nuevas tareas
        
        // Notificamos al adapter que los datos cambiaron
        taskAdapter.notifyDataSetChanged();
        
        // Mostramos u ocultamos el mensaje de lista vacía
        updateEmptyListVisibility();
    }
    
    /**
     * Actualiza la visibilidad del mensaje de lista vacía.
     * Muestra el mensaje si no hay tareas, lo oculta si hay tareas.
     */
    private void updateEmptyListVisibility() {
        if (taskList.isEmpty()) {
            // Si no hay tareas, mostramos el mensaje y ocultamos el RecyclerView
            emptyListTextView.setVisibility(View.VISIBLE);
            recyclerViewTasks.setVisibility(View.GONE);
        } else {
            // Si hay tareas, ocultamos el mensaje y mostramos el RecyclerView
            emptyListTextView.setVisibility(View.GONE);
            recyclerViewTasks.setVisibility(View.VISIBLE);
        }
    }
    
    // ============================================================================
    // MÉTODOS PARA ABRIR EL FORMULARIO
    // ============================================================================
    
    /**
     * Abre el formulario de creación/edición de tareas.
     * 
     * @param taskId El ID de la tarea a editar, o -1 si es nueva tarea
     */
    private void openTaskForm(int taskId) {
        // Creamos un Intent para abrir TaskFormActivity
        Intent intent = new Intent(MainActivity.this, TaskFormActivity.class);
        
        // Si es edición, pasamos el ID de la tarea
        if (taskId != -1) {
            intent.putExtra(EXTRA_TASK_ID, taskId);
        }
        
        // Iniciamos la actividad esperando un resultado
        startActivityForResult(intent, REQUEST_CODE_TASK_FORM);
    }
    
    /**
     * Maneja el resultado que regresa del formulario de tareas.
     * 
     * @param requestCode El código de petición que identifica la actividad
     * @param resultCode El código de resultado (OK o CANCELLED)
     * @param data Los datos extras retornados por la actividad
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // Verificamos que sea el resultado del formulario de tareas
        if (requestCode == REQUEST_CODE_TASK_FORM) {
            // Si el resultado es OK, significa que se guardó una tarea
            if (resultCode == RESULT_OK) {
                // Mostramos mensaje de éxito
                Toast.makeText(this, R.string.task_saved_successfully, Toast.LENGTH_SHORT).show();
                
                // Recargamos la lista para mostrar los cambios
                loadTasksFromDatabase();
            }
        }
    }
    
    // ============================================================================
    // IMPLEMENTACIÓN DE LA INTERFAZ OnTaskClickListener
    // ============================================================================
    
    /**
     * Se llama cuando se hace clic en una tarea de la lista.
     * Abre el formulario en modo edición.
     * 
     * @param task La tarea en la que se hizo clic
     * @param position La posición en la lista
     */
    @Override
    public void onTaskClick(Task task, int position) {
        // Abrimos el formulario con el ID de la tarea para editarla
        openTaskForm(task.getId());
    }
    
    /**
     * Se llama cuando cambia el estado de una tarea (checkbox).
     * Actualiza el estado directamente en la base de datos.
     * 
     * @param task La tarea cuyo estado cambió
     * @param position La posición en la lista
     * @param isChecked El nuevo estado (true=completada, false=pendiente)
     */
    @Override
    public void onTaskStatusChange(Task task, int position, boolean isChecked) {
        // Actualizamos el estado de la tarea en el objeto
        if (isChecked) {
            task.markAsCompleted();
        } else {
            task.markAsPending();
        }
        
        // Actualizamos en la base de datos
        databaseHelper.updateTaskStatus(task.getId(), task.getStatus());
        
        // Actualizamos solo este ítem en el adapter
        taskAdapter.updateTask(task, position);
    }
    
    // ============================================================================
    // MÉTODOS DE UTILIDAD
    // ============================================================================
    
    /**
     * Muestra un diálogo de confirmación antes de eliminar una tarea.
     * 
     * @param task La tarea a eliminar
     * @param position La posición en la lista
     */
    private void showDeleteConfirmationDialog(Task task, int position) {
        // Creamos un AlertDialog para confirmar la eliminación
        new AlertDialog.Builder(this)
            .setTitle(R.string.confirm_delete)
            .setMessage("¿Estás seguro de que deseas eliminar \"" + task.getTitle() + "\"?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(R.string.yes, (dialog, which) -> {
                // Usuario confirmó: eliminamos la tarea
                deleteTask(task, position);
            })
            .setNegativeButton(R.string.no, null) // null cierra el diálogo sin hacer nada
            .show();
    }
    
    /**
     * Elimina una tarea de la base de datos y actualiza la UI.
     * 
     * @param task La tarea a eliminar
     * @param position La posición en la lista
     */
    private void deleteTask(Task task, int position) {
        // Eliminamos de la base de datos
        int rowsAffected = databaseHelper.deleteTask(task.getId());
        
        // Verificamos si se eliminó correctamente
        if (rowsAffected > 0) {
            // Eliminamos de la lista y notificamos al adapter
            taskAdapter.removeTask(position);
            
            // Actualizamos la visibilidad de la lista vacía
            updateEmptyListVisibility();
            
            // Mostramos mensaje de éxito
            Toast.makeText(this, R.string.task_deleted_successfully, Toast.LENGTH_SHORT).show();
        }
    }
}