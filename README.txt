ForoCineast - Plataforma de Discusion Cinematografica

DESCRIPCION GENERAL
ForoCineast es una aplicacion nativa de Android desarrollada en Kotlin utilizando Jetpack Compose. Su objetivo es ofrecer una plataforma donde los usuarios puedan explorar la cartelera actual de cine y television, gestionar sus favoritos y participar en un foro comunitario publicando resenas y calificaciones.

La aplicacion consume datos de la API publica de TMDB (The Movie Database) para la informacion de peliculas y series, y utiliza un backend propio alojado en AWS (Node.js + MySQL) para la gestion de usuarios y el foro.

CARACTERISTICAS PRINCIPALES

1. Autenticacion y Usuarios
- Registro de usuarios con validacion de campos (correo, longitud de contrasena).
- Inicio de sesion seguro contra base de datos remota (AWS).
- Perfil de usuario persistente.
- Gestion de Foto de Perfil: Permite tomar fotos con la camara o seleccionarlas de la galeria. Las imagenes se guardan localmente y persisten entre sesiones.

2. Cartelera y Exploracion (API TMDB)
- Visualizacion de cuatro categorias principales: Estrenos, Populares, Peliculas mejor valoradas y Series mejor valoradas.
- Buscador Universal: Permite buscar tanto PELICULAS como SERIES de television en tiempo real, combinando resultados por relevancia.
- Detalle de contenidos: Muestra sinopsis, calificacion, fecha de estreno y poster en alta resolucion.

3. Sistema de Favoritos (Offline)
- Los usuarios pueden marcar contenidos como favoritos pulsando el icono de corazon.
- Persistencia Local: La lista se guarda en el dispositivo (SharedPreferences) y funciona sin internet.
- Multi-usuario: Cada cuenta registrada tiene su propia lista de favoritos independiente.

4. Foro y Comunidad (Backend AWS)
- Listado de resenas publicadas por la comunidad en tiempo real.
- Publicacion de nuevas resenas con titulo, cuerpo, referencia a la pelicula, valoracion (estrellas) y alerta de Spoilers.
- Sistema de Spoilers: El contenido sensible se oculta por defecto hasta que el usuario decide verlo.
- Feedback Visual: Mensajes de error claros (Toast) y actualizaciones optimistas en la interfaz.

5. Moderacion (Rol de Administrador)
- Existe un rol de "Admin" (gestionado desde la base de datos) que puede eliminar cualquier publicacion del foro.
- Los usuarios normales solo pueden eliminar sus propias publicaciones.

ARQUITECTURA Y TECNOLOGIAS

- Lenguaje: Kotlin
- Interfaz de Usuario: Jetpack Compose (Material Design 3)
- Patron de Diseno: MVVM (Model-View-ViewModel)
- Navegacion: Navigation Compose
- Red y API: Retrofit 2 + Gson (Interceptor para Auth)
- Carga de Imagenes: Coil
- Asincronia: Kotlin Coroutines y StateFlow
- Persistencia Local: SharedPreferences y FileProvider.
- Calidad de Codigo (Testing): Suite de pruebas unitarias con JUnit 4 y Mockk cubriendo Modelos, ViewModels y Repositorios (~80% de cobertura).

CONFIGURACION PARA DESARROLLADORES

Para compilar y ejecutar este proyecto, asegurate de cumplir con los siguientes requisitos:

1. API Key de TMDB
El archivo 'ExternalRetrofitInstance.kt' requiere una API Key valida.
Ubicacion: data/remote/ExternalRetrofitInstance.kt

2. Conexion al Backend (AWS)
La aplicacion se conecta a un servidor EC2. La IP debe estar actualizada en 'RetrofitInstance.kt'.
Nota: Si el servidor AWS se reinicia, la IP Publica cambia y debe actualizarse en la App.

3. Base de Datos (MySQL)
Backend Node.js corriendo con PM2 y base de datos 'forocineast_db'.
Para modo Admin: UPDATE usuarios SET esAdmin = 1 WHERE correo = '...';

GUIA DE USO

1. Intro: Al abrir la app, se mostrara una Splash Screen animada.
2. Login/Registro: Crear una cuenta o ingresar con tus credenciales a la app.
3. Navegacion:
   - Cartelera: Explora y busca contenidos. Usa el corazon para guardar tud favoritos.
   - Foro: Comparte tus opiniones con la comunidad.
   - Favoritos: Accede a tu coleccion personal.
   - Perfil: Gestiona tu foto y sesion.

AUTORES
Trabajo realizado por:
- Martin Pizarro
- Daniel Palma