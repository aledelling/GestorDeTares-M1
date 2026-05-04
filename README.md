# Gestor de Tareas (To-Do List) 📱

![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Java](https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-07405E?style=for-the-badge&logo=sqlite&logoColor=white)

---

## 👤 Información del Estudiante

**Nombre completo:** Jhon Alejandro Diaz Jimenez

**Fecha de entrega:** 06 de Mayo de 2024

---

## 📝 Descripción de la Aplicación

Aplicación nativa Android que funciona como un **gestor de tareas personales**. Permite al usuario:

- ✅ **Registrar nuevas tareas** con título y descripción
- 📋 **Visualizar tareas** en una lista dinámica con RecyclerView
- ✏️ **Actualizar** el estado o información de las tareas existentes
- 🗑️ **Eliminar** tareas cuando ya no son necesarias

La aplicación utiliza **SQLite** para la persistencia local de datos, implementando todas las operaciones CRUD (Create, Read, Update, Delete).

---

## 🛠️ Características Técnicas

### Interfaz de Usuario (Layouts)
- **Pantalla Principal**: RecyclerView con lista de tareas + FloatingActionButton
- **Pantalla de Creación/Edición**: Formulario completo con validaciones
- **Diseño de Ítems**: CardView personalizado con indicador visual de estado
- **Diseño Minimalista**: Colores futuristas que transmiten tranquilidad y orden

### Persistencia de Datos (SQLite)
Tabla `tareas` con la siguiente estructura:
| Columna | Tipo | Descripción |
|---------|------|-------------|
| id | INTEGER | Primary Key, Autoincremental |
| titulo | TEXT | Título de la tarea (obligatorio) |
| descripcion | TEXT | Descripción breve (opcional) |
| estado | INTEGER | 0=Pendiente, 1=Completada |

### RecyclerView
- Adapter personalizado con patrón ViewHolder
- Optimización de recursos en pantalla
- Actualizaciones eficientes de ítems individuales

---

## 📁 Estructura del Proyecto

```
app/src/main/
├── java/com/example/gestordetares_m1/
│   ├── MainActivity.java          # Pantalla principal con RecyclerView
│   ├── TaskFormActivity.java      # Formulario de creación/edición
│   ├── TaskAdapter.java           # Adapter personalizado para RecyclerView
│   ├── TaskDatabaseHelper.java    # Gestión de base de datos SQLite
│   └── Task.java                  # Modelo de datos
│
├── res/
│   ├── layout/
│   │   ├── activity_main.xml      # Layout pantalla principal
│   │   ├── activity_task_form.xml # Layout formulario
│   │   └── item_task.xml          # Layout individual de tarea
│   │
│   ├── values/
│   │   ├── colors.xml             # Paleta de colores futuristas
│   │   └── strings.xml            # Cadenas de texto
│   │
│   └── drawable/
│       └── status_indicator_background.xml # Fondo indicador estado
│
└── AndroidManifest.xml
```

---

## 🎨 Paleta de Colores

La aplicación utiliza un diseño minimalista con colores futuristas:

| Color | Uso | Hex |
|-------|-----|-----|
| Azul Cián | Color principal | `#4FC3F7` |
| Verde Menta | Acento y completadas | `#26A69A` |
| Gris Suave | Textos secundarios | `#78909C` |
| Naranja | Tareas pendientes | `#FFB74D` |
| Blanco | Fondos de tarjetas | `#FFFFFF` |

---

## 🚀 Cómo Ejecutar el Proyecto

### Requisitos Previos
- Android Studio Arctic Fox o superior
- JDK 11 o superior
- SDK de Android (API 24+)

### Pasos de Instalación

1. **Clonar el repositorio**
   ```bash
   git clone <URL_DEL_REPOSITORIO>
   cd GestorDeTares-M1
   ```

2. **Abrir en Android Studio**
   - File → Open → Seleccionar la carpeta del proyecto

3. **Sincronizar Gradle**
   - Esperar a que Android Studio sincronice las dependencias

4. **Ejecutar la aplicación**
   - Conectar un dispositivo o iniciar un emulador
   - Click en el botón "Run" (▶️)

---

## 📦 Dependencias Principales

```kotlin
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.9.0'
implementation 'androidx.recyclerview:recyclerview:1.3.1'
implementation 'androidx.cardview:cardview:1.0.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
```

---

## 📄 Licencia

Este proyecto fue desarrollado con fines educativos como parte de un reto académico.

---
