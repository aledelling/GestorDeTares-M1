package com.example.gestordetares_m1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Actividad para crear y editar tareas.
 * 
 * Esta actividad funciona en dos modos:
 * - Modo CREACIÓN: Cuando no se recibe un ID de tarea
 * - Modo EDICIÓN: Cuando se recibe un ID de tarea existente
 * 
 * Características:
 * - Formulario con título (obligatorio) y descripción (opcional)
 * - Switch para cambiar estado pendiente/completada
 * - Botones para guardar, cancelar y eliminar (solo edición)
 * - Validación de campos antes de guardar
 * 
 * @author Desarrollador Junior
 * @version 1.0
 */
public class TaskFormActivity extends AppCompatActivity {
    
    // ============================================================================
    // CONSTANTES
    // ============================================================================
    
    /**
     * Clave para recibir el ID de la tarea desde MainActivity.
     */
    public static final String EXTRA_TASK_ID = "task_id";
    
    // ============================================================================
    // ATRIBUTOS DE LA ACTIVIDAD
    // ============================================================================
    
    /**
     * Vistas del formulario - Título
     */
    private TextInputLayout textInputLayoutTitle;
    private TextInputEditText editTextTitle;
    
    /**
     * Vistas del formulario - Descripción
     */
    private TextInputLayout textInputLayoutDescription;
    private TextInputEditText editTextDescription;
    
    /**
     * Vistas del formulario - Estado
     */
    private Switch switchStatus;
    private TextView textViewStatusValue;
    
    /**
     * Botones del formulario
     */
    private MaterialButton buttonSave;
    private MaterialButton buttonCancel;
    private MaterialButton buttonDelete;
    
    /**
     * Título del formulario
     */
    private TextView textViewFormTitle;
    
    /**
     * Helper para operaciones con base de datos.
     */
    private TaskDatabaseHelper databaseHelper;
    
    /**
     * ID de la tarea que se está editando.
     * -1 indica que es una nueva tarea (modo creación).
     */
    private int taskId = -1;
    
    /**
     * Tarea actual que se está editando.
     * Null en modo creación.
     */
    private Task currentTask;
    
    // ============================================================================
    // CICLO DE VIDA DE LA ACTIVIDAD
    // ============================================================================
    
    /**
     * Método llamado cuando la actividad se crea.
     * Inicializa todas las vistas y configura el formulario.
     * 
     * @param savedInstanceState Estado guardado previamente (si existe)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Configuramos el layout del formulario
        setContentView(R.layout.activity_task_form);
        
        // Inicializamos el helper de base de datos
        databaseHelper = new TaskDatabaseHelper(this);
        
        // Obtenemos el ID de la tarea desde el Intent (si existe)
        getTaskIdFromIntent();
        
        // Inicializamos todas las vistas
        initializeViews();
        
        // Configuramos los listeners para los botones
        setupClickListeners();
        
        // Si hay un ID, cargamos los datos de la tarea (modo edición)
        if (taskId != -1) {
            loadTaskData();
        }
    }
    
    // ============================================================================
    // MÉTODOS DE INICIALIZACIÓN
    // ============================================================================
    
    /**
     * Obtiene el ID de la tarea desde el Intent que inició esta actividad.
     * Si no hay ID, significa que estamos en modo creación.
     */
    private void getTaskIdFromIntent() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            taskId = intent.getIntExtra(EXTRA_TASK_ID, -1);
        }
    }
    
    /**
     * Inicializa todas las vistas del layout.
     * Vincula las variables Java con los elementos XML.
     */
    private void initializeViews() {
        // Título del formulario
        textViewFormTitle = findViewById(R.id.textViewFormTitle);
        
        // Campos de texto - Título
        textInputLayoutTitle = findViewById(R.id.textInputLayoutTitle);
        editTextTitle = findViewById(R.id.editTextTitle);
        
        // Campos de texto - Descripción
        textInputLayoutDescription = findViewById(R.id.textInputLayoutDescription);
        editTextDescription = findViewById(R.id.editTextDescription);
        
        // Switch de estado
        switchStatus = findViewById(R.id.switchStatus);
        textViewStatusValue = findViewById(R.id.textViewStatusValue);
        
        // Botones
        buttonSave = findViewById(R.id.buttonSave);
        buttonCancel = findViewById(R.id.buttonCancel);
        buttonDelete = findViewById(R.id.buttonDelete);
        
        // Configuramos el título según el modo (creación o edición)
        if (taskId == -1) {
            // Modo creación
            textViewFormTitle.setText(R.string.form_title_new);
            buttonDelete.setVisibility(View.GONE); // Ocultamos botón eliminar
        } else {
            // Modo edición
            textViewFormTitle.setText(R.string.form_title_edit);
            buttonDelete.setVisibility(View.VISIBLE); // Mostramos botón eliminar
        }
        
        // Configuramos listener para el cambio de estado del switch
        setupStatusSwitchListener();
    }
    
    /**
     * Configura el listener para el switch de estado.
     * Actualiza el texto que muestra el estado actual.
     */
    private void setupStatusSwitchListener() {
        switchStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Actualizamos el texto según el estado
            if (isChecked) {
                textViewStatusValue.setText(R.string.status_completed);
            } else {
                textViewStatusValue.setText(R.string.status_pending);
            }
        });
    }
    
    /**
     * Configura los listeners para todos los botones del formulario.
     */
    private void setupClickListeners() {
        // Botón Guardar: valida y guarda la tarea
        buttonSave.setOnClickListener(v -> saveTask());
        
        // Botón Cancelar: cierra el formulario sin guardar
        buttonCancel.setOnClickListener(v -> cancelForm());
        
        // Botón Eliminar: muestra confirmación y elimina la tarea
        buttonDelete.setOnClickListener(v -> showDeleteConfirmation());
    }
    
    // ============================================================================
    // MÉTODOS DE CARGA DE DATOS (MODO EDICIÓN)
    // ============================================================================
    
    /**
     * Carga los datos de la tarea desde la base de datos.
     * Solo se ejecuta en modo edición.
     */
    private void loadTaskData() {
        // Obtenemos la tarea desde la base de datos
        currentTask = databaseHelper.getTaskById(taskId);
        
        // Verificamos que la tarea exista
        if (currentTask != null) {
            // Llenamos los campos del formulario con los datos existentes
            
            // Título
            editTextTitle.setText(currentTask.getTitle());
            
            // Descripción
            if (currentTask.getDescription() != null) {
                editTextDescription.setText(currentTask.getDescription());
            }
            
            // Estado (switch)
            boolean isCompleted = currentTask.isCompleted();
            switchStatus.setChecked(isCompleted);
            
            // Actualizamos el texto del estado
            if (isCompleted) {
                textViewStatusValue.setText(R.string.status_completed);
            } else {
                textViewStatusValue.setText(R.string.status_pending);
            }
        } else {
            // La tarea no existe, mostramos mensaje y cerramos
            Toast.makeText(this, "Tarea no encontrada", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    
    // ============================================================================
    // MÉTODOS DE GUARDADO Y VALIDACIÓN
    // ============================================================================
    
    /**
     * Valida los campos del formulario y guarda la tarea.
     * Se llama cuando el usuario presiona el botón Guardar.
     */
    private void saveTask() {
        // Obtenemos el texto del título
        String title = getTextFromEditText(editTextTitle);
        
        // Obtenemos el texto de la descripción
        String description = getTextFromEditText(editTextDescription);
        
        // VALIDACIÓN: El título es obligatorio
        if (title.isEmpty()) {
            // Mostramos error en el campo
            textInputLayoutTitle.setError(getString(R.string.error_empty_title));
            
            // Ponemos el foco en el campo con error
            editTextTitle.requestFocus();
            return;
        }
        
        // Limpiamos cualquier error previo
        textInputLayoutTitle.setError(null);
        
        // Obtenemos el estado del switch
        int status = switchStatus.isChecked() ? 1 : 0;
        
        // Guardamos según el modo (creación o edición)
        if (taskId == -1) {
            // MODO CREACIÓN: Insertamos nueva tarea
            createNewTask(title, description, status);
        } else {
            // MODO EDICIÓN: Actualizamos tarea existente
            updateExistingTask(title, description, status);
        }
    }
    
    /**
     * Crea una nueva tarea en la base de datos.
     * 
     * @param title El título de la tarea
     * @param description La descripción de la tarea
     * @param status El estado de la tarea
     */
    private void createNewTask(String title, String description, int status) {
        // Creamos un nuevo objeto Task
        Task newTask = new Task(title, description, status);
        
        // Insertamos en la base de datos
        long newRowId = databaseHelper.insertTask(newTask);
        
        // Verificamos si se insertó correctamente
        if (newRowId != -1) {
            // Éxito: retornamos resultado a MainActivity
            returnResultOk();
        } else {
            // Error al insertar
            Toast.makeText(this, "Error al guardar la tarea", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Actualiza una tarea existente en la base de datos.
     * 
     * @param title El nuevo título de la tarea
     * @param description La nueva descripción de la tarea
     * @param status El nuevo estado de la tarea
     */
    private void updateExistingTask(String title, String description, int status) {
        // Actualizamos los datos del objeto currentTask
        currentTask.setTitle(title);
        currentTask.setDescription(description);
        currentTask.setStatus(status);
        
        // Actualizamos en la base de datos
        int rowsAffected = databaseHelper.updateTask(currentTask);
        
        // Verificamos si se actualizó correctamente
        if (rowsAffected > 0) {
            // Éxito: retornamos resultado a MainActivity
            returnResultOk();
        } else {
            // Error al actualizar
            Toast.makeText(this, "Error al actualizar la tarea", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Obtiene el texto de un TextInputEditText.
     * Método auxiliar para simplificar el código.
     * 
     * @param editText El campo de texto del cual obtener el contenido
     * @return El texto como String, o string vacío si es null
     */
    private String getTextFromEditText(TextInputEditText editText) {
        if (editText.getText() != null) {
            return editText.getText().toString().trim();
        }
        return "";
    }
    
    // ============================================================================
    // MÉTODOS DE NAVEGACIÓN Y RESULTADOS
    // ============================================================================
    
    /**
     * Retorna resultado OK a MainActivity e indica que debe refrescar la lista.
     * Se llama después de guardar exitosamente.
     */
    private void returnResultOk() {
        // Creamos un Intent para retornar datos
        Intent resultIntent = new Intent();
        resultIntent.putExtra(EXTRA_REFRESH_LIST, true);
        
        // Establecemos el resultado como OK
        setResult(RESULT_OK, resultIntent);
        
        // Mostramos mensaje de éxito
        Toast.makeText(this, R.string.task_saved_successfully, Toast.LENGTH_SHORT).show();
        
        // Cerramos esta actividad
        finish();
    }
    
    /**
     * Cancela el formulario y cierra la actividad sin guardar.
     * Se llama cuando el usuario presiona Cancelar.
     */
    private void cancelForm() {
        // Simplemente cerramos la actividad sin retornar resultado
        finish();
    }
    
    // ============================================================================
    // MÉTODOS DE ELIMINACIÓN
    // ============================================================================
    
    /**
     * Muestra diálogo de confirmación antes de eliminar la tarea.
     * Se llama cuando el usuario presiona Eliminar en modo edición.
     */
    private void showDeleteConfirmation() {
        // Creamos un AlertDialog para confirmar
        new AlertDialog.Builder(this)
            .setTitle(R.string.confirm_delete)
            .setMessage("¿Estás seguro de eliminar \"" + currentTask.getTitle() + "\"?")
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setPositiveButton(R.string.yes, (dialog, which) -> {
                // Usuario confirmó: eliminamos la tarea
                deleteTask();
            })
            .setNegativeButton(R.string.no, null) // null cierra sin hacer nada
            .show();
    }
    
    /**
     * Elimina la tarea actual de la base de datos.
     * Se llama después de confirmar la eliminación.
     */
    private void deleteTask() {
        // Eliminamos de la base de datos
        int rowsAffected = databaseHelper.deleteTask(taskId);
        
        // Verificamos si se eliminó correctamente
        if (rowsAffected > 0) {
            // Retornamos resultado OK para refrescar la lista
            returnResultOk();
            
            // Mostramos mensaje específico de eliminación
            Toast.makeText(this, R.string.task_deleted_successfully, Toast.LENGTH_SHORT).show();
        } else {
            // Error al eliminar
            Toast.makeText(this, "Error al eliminar la tarea", Toast.LENGTH_SHORT).show();
        }
    }
}
