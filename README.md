# 📱 Barbería App - Gestión de Turnos

Aplicación móvil desarrollada en Android que permite gestionar turnos en una barbería, incluyendo registro de usuarios, login, visualización de turnos y un panel de administración.

> 📚 Este proyecto fue desarrollado con fines académicos, aplicando conceptos de desarrollo móvil, gestión de datos y arquitectura básica de aplicaciones Android.

---

## 🚀 Funcionalidades principales

### 👤 Usuario

- Registro de cuenta
- Inicio de sesión
- Ver turnos disponibles
- Reservar turnos
- Ver "Mis turnos"

### 🧑‍💼 Administrador

- Panel de administración
- CRUD de usuarios
- CRUD de turnos
- Gestión completa del sistema

---

## 🧱 Arquitectura del proyecto

El proyecto sigue una estructura típica de Android (Java + XML):

```
Barberia/
│
├── app/
│   ├── src/main/
│   │   ├── java/com/example/barberia/
│   │   │   ├── activities/   # Pantallas principales
│   │   │   ├── adapters/     # Adaptadores para listas
│   │   │   ├── models/       # Clases de datos
│   │   │   ├── dialogs/      # Formularios emergentes
│   │   │
│   │   ├── res/
│   │   │   ├── layout/       # Interfaces XML
│   │   │   ├── drawable/     # Iconos e imágenes
│   │
│   ├── AndroidManifest.xml
│
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🛠️ Tecnologías utilizadas

| Tecnología             | Uso                            |
| ---------------------- | ------------------------------ |
| ☕ Java                | Lógica principal               |
| 📱 Android SDK         | Framework móvil                |
| 🖼️ XML                 | Diseño de interfaces (layouts) |
| ⚙️ Gradle (Kotlin DSL) | Build system                   |
| 🔥 Firebase            | Backend y autenticación        |

---

## 📲 Pantallas principales

| Activity                    | Descripción                    |
| --------------------------- | ------------------------------ |
| `LoginActivity`             | Inicio de sesión               |
| `RegisterActivity`          | Registro de usuarios           |
| `UserHomeActivity`          | Menú principal del usuario     |
| `AdminHomeActivity`         | Panel de administración        |
| `TurnosDisponiblesActivity` | Listado de turnos libres       |
| `MisTurnosActivity`         | Turnos del usuario autenticado |
| `CrudTurnosActivity`        | Gestión de turnos (admin)      |
| `CrudUsuariosActivity`      | Gestión de usuarios (admin)    |

---

## 🧩 Componentes clave

### 📦 Modelos

- `Usuario` → datos del usuario
- `Turno` → información del turno (fecha, estado, etc.)

### 🔁 Adapters

- `TurnosAdapter`
- `UsuariosAdapter`

### 🧾 Diálogos

- `AddEditTurnoDialog`
- `AddEditUsuarioDialog`

> 💡 El uso de diálogos dinámicos permite una UI más fluida sin necesidad de navegar entre pantallas para cada acción.

---

## ⚙️ Cómo ejecutar el proyecto

### 1. Clonar el repositorio

```bash
git clone https://github.com/tuusuario/barberia-app.git
```

### 2. Abrir en Android Studio

Importar el proyecto desde la carpeta raíz.

### 3. Sincronizar Gradle

Android Studio lo hará automáticamente, o podés hacerlo manualmente desde **File → Sync Project with Gradle Files**.

### 4. Ejecutar la aplicación

- 📲 **Emulador Android** (recomendado: API 30+)
- 📱 **Dispositivo físico** con depuración USB activada

---

## 🔐 Roles del sistema

| Rol              | Permisos                                    |
| ---------------- | ------------------------------------------- |
| 👤 Usuario       | Reservar y ver sus propios turnos           |
| 🧑‍💼 Administrador | Gestionar usuarios y turnos (CRUD completo) |

---

## 📌 Objetivos de aprendizaje

- Desarrollo de aplicaciones Android nativas
- Manejo del ciclo de vida de Activities
- Uso de `RecyclerView` y Adapters
- Diseño de interfaces con XML
- Implementación de CRUD de datos
- Separación básica en capas (modelo / vista / lógica)

---

## 📄 Licencia

Proyecto de uso académico. Libre para modificar y reutilizar con fines educativos.

---
