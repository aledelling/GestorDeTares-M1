package com.example.gestordetares_m1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * Adapter personalizado para el RecyclerView de tareas.
 * 
 * Esta clase es responsable de:
 * - Crear las vistas individuales (ViewHolder)
 * - Vincular los datos de cada tarea con su vista correspondiente
 * - Manejar los clics en los elementos de la lista
 * 
 * Usa el patrón ViewHolder para optimizar el rendimiento,
 * evitando inflar layouts innecesariamente.
 * 
 * @author Desarrollador Junior
 * @version 1.0
 */
public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {
    
    // ============================================================================
    // ATRIBUTOS DEL ADAPTER
    // ============================================================================
    
    /**
     * Lista de tareas que se mostrarán en el RecyclerView.
     */
    private List<Task> taskList;
    
    /**
     * Interfaz para comunicar eventos de clic al Activity/Fragment.
     * Permite que el adapter notifique cuando se hace clic en una tarea.
     */
    private OnTaskClickListener listener;
    
    // ============================================================================
    // INTERFAZ PARA MANEJAR CLICS
    // ============================================================================
    
    /**
     * Interfaz que define los métodos para manejar clics en las tareas.
     * El Activity o Fragment que use este adapter debe implementarla.
     */
    public interface OnTaskClickListener {
        /**
         * Se llama cuando se hace clic en una tarea completa.
         * Útil para abrir el formulario de edición.
         * 
         * @param task La tarea en la que se hizo clic
         * @param position La posición en la lista
         */
        void onTaskClick(Task task, int position);
        
        /**
         * Se llama cuando se marca/desmarca el checkbox de completado.
         * Útil para actualizar rápidamente el estado.
         * 
         * @param task La tarea cuyo estado cambió
         * @param position La posición en la lista
         * @param isChecked El nuevo estado (true=completada, false=pendiente)
         */
        onTaskStatusChange(Task task, int position, boolean isChecked);
    }
    
    // ============================================================================
    // CONSTRUCTOR
    // ============================================================================
    
    /**
     * Constructor del adapter.
     * 
     * @param taskList La lista de tareas a mostrar
     * @param listener El listener para manejar los clics
     */
    public TaskAdapter(List<Task> taskList, OnTaskClickListener listener) {
        this.taskList = taskList;
        this.listener = listener;
    }
    
    // ============================================================================
    // MÉTODOS OBLIGATORIOS DEL VIEWHOLDER
    // ============================================================================
    
    /**
     * Crea un nuevo ViewHolder cuando no hay más ViewHolders reutilizables.
     * Este método solo se llama cuando es necesario crear nuevas vistas.
     * 
     * @param parent El ViewGroup padre donde se añadirá la vista
     * @param viewType El tipo de vista (útil si tenemos múltiples tipos)
     * @return Un nuevo TaskViewHolder
     */
    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflamos el layout individual para cada ítem de tarea
        // LayoutInflater convierte el XML en un objeto View
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        
        // Creamos y retornamos un nuevo ViewHolder con la vista inflada
        return new TaskViewHolder(view);
    }
    
    /**
     * Vincula los datos de una tarea con el ViewHolder en una posición dada.
     * Este método se llama para cada elemento visible en el RecyclerView.
     * 
     * @param holder El ViewHolder que contiene las vistas a actualizar
     * @param position La posición de la tarea en la lista
     */
    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        // Obtenemos la tarea en la posición actual
        Task currentTask = taskList.get(position);
        
        // Actualizamos el título de la tarea
        holder.textViewTitle.setText(currentTask.getTitle());
        
        // Actualizamos la descripción
        // Si está vacía, mostramos un texto por defecto
        if (currentTask.getDescription() != null && !currentTask.getDescription().isEmpty()) {
            holder.textViewDescription.setText(currentTask.getDescription());
            holder.textViewDescription.setVisibility(View.VISIBLE);
        } else {
            holder.textViewDescription.setVisibility(View.GONE);
        }
        
        // Actualizamos el estado del CheckBox
        // true = completada, false = pendiente
        holder.checkBoxComplete.setChecked(currentTask.isCompleted());
        
        // Cambiamos la apariencia visual según el estado
        updateTaskAppearance(holder, currentTask);
        
        // Configuramos el listener para el clic en toda la tarjeta
        // Esto permite abrir el formulario de edición
        holder.cardViewTask.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTaskClick(currentTask, position);
            }
        });
        
        // Configuramos el listener para el cambio de estado (checkbox)
        // Usamos setOnCheckedChangeListener para detectar cambios
        holder.checkBoxComplete.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onTaskStatusChange(currentTask, position, isChecked);
            }
            
            // Actualizamos la apariencia inmediatamente
            updateTaskAppearance(holder, currentTask);
        });
    }
    
    /**
     * Actualiza la apariencia visual de la tarea según su estado.
     * Método auxiliar para cambiar colores y estilos.
     * 
     * @param holder El ViewHolder cuyas vistas actualizar
     * @param task La tarea cuyo estado verificar
     */
    private void updateTaskAppearance(TaskViewHolder holder, Task task) {
        // Obtenemos el contexto para acceder a los recursos de color
        int contextColor;
        
        if (task.isCompleted()) {
            // Tarea completada: colores verdes y texto tachado
            contextColor = holder.itemView.getContext().getColor(R.color.task_completed);
            holder.textViewTitle.setPaintFlags(
                holder.textViewTitle.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            holder.textViewTitle.setAlpha(0.6f); // Más transparente
            holder.textViewDescription.setAlpha(0.6f);
        } else {
            // Tarea pendiente: colores naranjas y texto normal
            contextColor = holder.itemView.getContext().getColor(R.color.task_pending);
            holder.textViewTitle.setPaintFlags(
                holder.textViewTitle.getPaintFlags() & (~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG));
            holder.textViewTitle.setAlpha(1.0f); // Opaco
            holder.textViewDescription.setAlpha(1.0f);
        }
        
        // Actualizamos el color del indicador visual
        holder.viewStatusIndicator.setBackgroundColor(contextColor);
    }
    
    /**
     * Retorna el número total de elementos en la lista.
     * El RecyclerView usa este método para saber cuántos ítems mostrar.
     * 
     * @return El tamaño de la lista de tareas
     */
    @Override
    public int getItemCount() {
        return taskList.size();
    }
    
    // ============================================================================
    // MÉTODOS DE ACTUALIZACIÓN DE DATOS
    // ============================================================================
    
    /**
     * Actualiza la lista completa de tareas y notifica al RecyclerView.
     * Usar cuando se recargan todos los datos desde la base de datos.
     * 
     * @param newTaskList La nueva lista de tareas
     */
    public void updateTasks(List<Task> newTaskList) {
        taskList = newTaskList;
        notifyDataSetChanged(); // Notifica que todos los datos cambiaron
    }
    
    /**
     * Elimina una tarea de la lista en una posición específica.
     * Usar después de eliminar de la base de datos.
     * 
     * @param position La posición de la tarea a eliminar
     */
    public void removeTask(int position) {
        taskList.remove(position);
        notifyItemRemoved(position); // Notifica que un ítem fue eliminado
    }
    
    /**
     * Actualiza una tarea específica en la lista.
     * Usar después de modificar una tarea en la base de datos.
     * 
     * @param task La tarea actualizada
     * @param position La posición de la tarea en la lista
     */
    public void updateTask(Task task, int position) {
        taskList.set(position, task);
        notifyItemChanged(position); // Notifica que un ítem específico cambió
    }
    
    // ============================================================================
    // CLASE INTERNA VIEWHOLDER
    // ============================================================================
    
    /**
     * ViewHolder interno que mantiene referencias a las vistas del ítem.
     * 
     * El patrón ViewHolder evita llamar findViewById() repetidamente,
     * mejorando significativamente el rendimiento del RecyclerView.
     */
    static class TaskViewHolder extends RecyclerView.ViewHolder {
        
        // Referencias a las vistas del layout item_task.xml
        CardView cardViewTask;
        CheckBox checkBoxComplete;
        TextView textViewTitle;
        TextView textViewDescription;
        View viewStatusIndicator;
        
        /**
         * Constructor del ViewHolder.
         * Inicializa las referencias a todas las vistas necesarias.
         * 
         * @param itemView La vista raíz del ítem (inflada desde item_task.xml)
         */
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            
            // Inicializamos cada vista usando findViewById
            // Esto solo se hace UNA VEZ por ViewHolder creado
            cardViewTask = itemView.findViewById(R.id.cardViewTask);
            checkBoxComplete = itemView.findViewById(R.id.checkBoxComplete);
            textViewTitle = itemView.findViewById(R.id.textViewTitle);
            textViewDescription = itemView.findViewById(R.id.textViewDescription);
            viewStatusIndicator = itemView.findViewById(R.id.viewStatusIndicator);
        }
    }
}
