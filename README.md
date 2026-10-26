# Agencia Oportunidades

App Android para el equipo comercial de una agencia de convenios y beneficios corporativos.
Permite registrar, buscar y dar seguimiento a oportunidades de activación en eventos corporativos,
ferias empresariales y actividades municipales, centralizando información que hoy está dispersa
entre redes sociales, correos y registros personales.

Proyecto de la **Evaluación Parcial 2** · DSY1105 Desarrollo de Aplicaciones Móviles · Duoc UC.

## Integrantes

- José Ignacio Zúñiga Zúñiga
- Marco Leufuman

## Funcionalidades implementadas

- **Login con cuenta ficticia**: validación de correo y clave por campo, aviso si las credenciales son
  incorrectas, nombre del usuario en la barra superior y botón para cerrar sesión.
- **Lista de oportunidades** con buscador por título, organización, tipo de evento o ubicación.
- **Formulario de nueva oportunidad** con validación por campo: cada error muestra un ícono y un mensaje,
  y el botón Guardar se habilita solo cuando el formulario es válido.
- **Tipos de evento del caso**: evento corporativo, feria empresarial, actividad municipal, jornada de
  beneficios, feria institucional, actividad de trabajadores, activación comercial y otra instancia.
- **Persistencia local con Room**: las oportunidades se mantienen al cerrar y volver a abrir la app.
- **Cámara (recurso nativo 1)**: foto de evidencia del evento, guardada en la carpeta privada de la app
  y mostrada en el formulario y en la tarjeta de la lista.
- **Lector QR (recurso nativo 2)**: al escanear el QR del afiche de un evento se autocompleta el formulario.
- **Permisos**: la cámara se pide en tiempo de ejecución; si el usuario la rechaza, la app lo informa y
  sigue funcionando.
- **Navegación con transiciones animadas** entre pantallas.
- **Animaciones de retroalimentación**: los íconos y mensajes de error aparecen y desaparecen animados.

## Arquitectura (MVVM)

```
app/src/main/java/com/example/agenciasoportunidades/
├── data/        Room: entidades, DAOs y repositorios de oportunidades y usuarios (AppDatabase v2)
├── model/       Estados de pantalla (OportunidadFormState, OportunidadListState, LoginUiState) y DatosQrEvento
├── viewmodel/   OportunidadViewModel, LoginViewModel (validaciones y sesión) y ViewModelFactory
├── device/      Recursos nativos: CameraManager y QrScannerManager
├── navigation/  Rutas y NavHost con animaciones (NavGraph)
└── ui/          Pantallas (screens), componentes reutilizables (components) y tema Material 3 (theme)
```

Las pantallas solo muestran el estado y avisan las acciones del usuario; la validación vive en el
ViewModel y el acceso a datos en el repositorio.

## Tecnologías

Kotlin · Jetpack Compose · Material 3 · MVVM · Room (con KSP) · Navigation Compose · CameraX ·
ML Kit Barcode Scanning

## Cómo ejecutar

1. Clonar el repositorio:
   ```
   git clone https://github.com/JoseIgnacio10/Agencia_oportunidades_APP_DATOS_MOVILES.git
   ```
2. Abrir la carpeta en **Android Studio** y esperar a que termine el **Gradle Sync**.
3. Elegir un emulador o un celular con **Android 7.0 o superior** (API 24) con depuración USB activada.
4. Pulsar **▶ Run**.
5. Ingresar con la cuenta de prueba (ficticia):

   | Correo | Clave |
   |---|---|
   | `ejecutivo@agenciaficticia.cl` | `Agencia2026` |

## Cómo probar el lector QR

En [`docs/qr_prueba`](docs/qr_prueba) hay códigos QR de ejemplo con datos ficticios:

| Archivo | Resultado esperado |
|---|---|
| `qr_feria_beneficios.png` | Completa todo el formulario |
| `qr_jornada_salud.png` | Completa todo el formulario con otro evento |
| `qr_no_es_evento.png` | Muestra "Este QR no corresponde a un evento" |

Abrir una imagen en la pantalla del computador y escanearla con la app desde
**Nueva → Escanear QR del evento**. Formato que entiende la app (una línea por dato):

```
titulo=Feria de Beneficios 2026
organizacion=Municipalidad Ficticia de Puente Alto
tipo=Feria empresarial
publico=Trabajadores municipales y sus familias
ubicacion=Puente Alto, RM
fecha=15/11/2026
```

## Organización del trabajo

- Cada funcionalidad se desarrolló en una rama `feature/*` y se unió a `main` mediante Pull Request.
- Planificación de tareas en Trello: [tablero del proyecto](https://trello.com/b/qd8zFsZx).
- Todos los nombres, organizaciones y datos usados son **ficticios**, como exige el caso.
