ForoCineast: Módulo de Autenticación con Jetpack Compose

Este repositorio contiene el código fuente para el módulo de autenticación (Login y Registro) de la aplicación ForoCineast, un foro dedicado a la discusión cinematográfica.

La implementación está construida en Kotlin utilizando Jetpack Compose y sigue estrictamente el patrón de arquitectura MVVM (Model-View-ViewModel).

Tecnologías y Patrones Clave:

Lenguaje: Kotlin
Framework UI: Jetpack Compose (Modern UI Toolkit para Android).
Arquitectura: MVVM, asegurando una clara separación de la lógica de negocio y la interfaz de usuario.
Manejo de Estado: Uso de StateFlow y MutableStateFlow de Kotlin Flow para exponer el estado de la UI de manera reactiva.
Asincronía: Corrutinas de Kotlin (viewModelScope.launch) para simular las operaciones de red/base de datos sin bloquear el hilo principal.
Capa de Datos: Implementación de un repositorio local (UsuarioRepository) para simular la persistencia de los datos de usuario en memoria.

Características del Módulo:

El enfoque principal del proyecto es la robustez y la experiencia de usuario en el flujo de inicio de sesión y registro:

Validación de Formularios en Tiempo Real: Se aplican reglas de validación inmediatas para asegurar la calidad de los datos:

Nombre: No puede estar vacío.

Correo Electrónico: Debe tener un formato válido y ser único (en el caso de Registro).

Contraseña (Clave): Debe cumplir con una longitud mínima.

Edad: Requiere una edad mínima para el registro.

Manejo de Errores Detallado: Los errores específicos de validación se muestran directamente bajo el campo afectado.

Flujo de Éxito/Fallo: La lógica de Login y Registro utiliza callbacks (onSuccess/onError) para gestionar la navegación o mostrar mensajes modales de error, simulando una respuesta de API.

Estructura de la Arquitectura:

El proyecto está organizado de acuerdo con el patrón MVVM:

UsuarioViewModel.kt (ViewModel): Contiene el estado (UsuarioUiState) y toda la lógica de negocio. Es el puente entre la Vista y el Repositorio.

UsuarioRepository.kt (Modelo/Data): Simula la fuente de datos. Contiene los métodos login y registro, manejando la colección de usuarios en memoria.

UsuarioUiState.kt (Modelo/UI State): Data Class inmutable que define todos los datos de los campos de la UI y sus mensajes de error.

LoginScreen.kt / RegistroScreen.kt (Vista): Las funciones @Composable que observan el UsuarioViewModel y dibujan la interfaz.

Instrucciones de Uso

Para compilar y ejecutar el proyecto en Android Studio:

Clonar el Repositorio:

https://github.com/MartinIgnaci0/ForoCineast

Abrir Proyecto: Abrir Android Studio, seleccionar File -> Open y elegir la carpeta raíz de ForoCineast.

Ejecutar: Sincronizar Gradle si es necesario, seleccionar un emulador o dispositivo y presionar el botón de Run.

Desarrollado por Martin Pizarro.