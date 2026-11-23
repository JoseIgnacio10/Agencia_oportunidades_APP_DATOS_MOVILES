# Agencia de Oportunidades - Aplicación Móvil Android

Aplicación móvil nativa desarrollada en **Kotlin** y **Jetpack Compose** bajo arquitectura **MVVM** para la gestión de perfiles de usuario, postulaciones a oportunidades, eventos y consulta de feriados nacionales.

---

## 1. Estructura del Proyecto

```text
Agencias_Oportunidades/
├── app/
│   ├── release/
│   │   └── app-release.apk                  # APK firmado listo para distribución
│   └── src/
│       ├── main/
│       │   └── java/com/example/agenciasoportunidades/
│       │       ├── model/                   # Modelos de datos y DTOs
│       │       ├── network/                 # Clientes Retrofit y servicios API
│       │       ├── ui/
│       │       │   ├── screens/             # Pantallas Jetpack Compose (Login, Eventos, QR, etc.)
│       │       │   └── theme/               # Paleta de colores, tipografía y temas
│       │       ├── viewmodel/               # ViewModels (Login, Evento, Feriado, Oportunidad)
│       │       └── MainActivity.kt          # Punto de entrada y navegación principal
│       ├── test/
│       │   └── java/com/example/agenciasoportunidades/
│       │       ├── EventoViewModelTest.kt   # Pruebas unitarias de EventoViewModel
│       │       └── FeriadoViewModelTest.kt  # Pruebas unitarias de FeriadoViewModel
│       └── androidTest/
│           └── java/com/example/agenciasoportunidades/
│               └── EventosScreenTest.kt     # Pruebas de interfaz UI en Compose
├── Agencia_oportunidades_APP_DATOS_MOVILES-MAIN3.jks  # Llave Keystore de firma
├── build.gradle.kts                         # Configuración global de Gradle
├── settings.gradle.kts                      # Configuración de módulos
└── README.md                                # Documentación del proyecto



2. Requisitos del Sistema y Ejecución
IDE: Android Studio Ladybug / Koala o superior.
Lenguaje: Kotlin 2.0+ / JDK 17.
SDK Mínimo (minSdk): API 24 (Android 7.0).
SDK Objetivo (targetSdk): API 34 / 35.
Ejecución desde Android Studio:
Abrir la carpeta raíz del proyecto en Android Studio (Agencias_Oportunidades).
Esperar a que Gradle sincronice todas las dependencias.
Encender un emulador en Device Manager (ej. Pixel API 34) o conectar un dispositivo físico con depuración USB.
Ejecutar la aplicación con Run (Shift + F10).
3. Arquitectura y Funcionalidades Principales
El proyecto implementa el patrón arquitectónico MVVM (Model - View - ViewModel) con gestión reactiva de estado mediante StateFlow y UI declarativa en Jetpack Compose:
Autenticación y Perfil (LoginScreen, LoginViewModel): Inicio de sesión, registro, validación de campos (incluyendo formato de RUT) y manejo de sesión.
Gestión de Eventos y Oportunidades (EventosScreen, EventoViewModel, OportunidadViewModel): Visualización, registro y administración de eventos y ofertas disponibles.
Integración con APIs y Microservicios (Retrofit):
Conexión con microservicio propio para gestión de usuarios, perfiles y eventos.
Consumo de API externa de feriados (FeriadoViewModel) para consulta de días festivos.
Lector QR (QrScannerScreen): Escaneo de códigos QR mediante cámara integrada (CameraX / ML Kit) para validación rápida en eventos.
4. Comandos de Terminal (Compilación y Pruebas)
Desde la terminal integrada de Android Studio (o terminal del sistema en la raíz del proyecto), se pueden ejecutar los siguientes comandos con Gradle Wrapper (./gradlew):
Compilar el proyecto completo (incluyendo clases de test):
Bash
./gradlew assembleDebug assembleDebugUnitTest assembleDebugAndroidTest
Ejecutar pruebas unitarias locales (src/test):
Valida la lógica de negocio y manejo de estados en EventoViewModelTest y FeriadoViewModelTest sobre la JVM sin requerir emulador:
Bash
./gradlew testDebugUnitTest
Ejecutar pruebas de interfaz UI (src/androidTest):
Valida el renderizado de componentes visuales en EventosScreenTest (requiere tener un emulador encendido o dispositivo Android conectado):
Bash
./gradlew connectedDebugAndroidTest
Generar APK de producción (release) desde terminal:
Bash
./gradlew assembleRelease
5. Artefactos de Distribución (APK Firmado y Keystore)
El repositorio incluye los archivos necesarios para instalación y verificación de firma:
Ruta del APK Firmado: app/release/app-release.apk
Archivo Keystore (.jks): Agencia_oportunidades_APP_DATOS_MOVILES-MAIN3.jks
Alias de firma: agencia_key
Contraseña Keystore / Key: agencia2026

---

### Comandos sugeridos para hacer tus commits por partes en terminal

Cuando pegues el `README.md` y verifiques que el `.jks` y el `app-release.apk` estén en sus carpetas, puedes subir todo ordenado por partes con estos comandos:

```bash
# 1. Subir las pruebas unitarias y de UI
git add app/src/test/ app/src/androidTest/
git commit -m "test: agregar pruebas unitarias de ViewModels y test UI de EventosScreen"

# 2. Subir el APK firmado y la llave Keystore
git add -f app/release/app-release.apk Agencia_oportunidades_APP_DATOS_MOVILES-MAIN3.jks
git commit -m "build: agregar APK firmado release y archivo keystore .jks"

# 3. Subir el README actualizado
git add README.md
git commit -m "docs: actualizar README con estructura, comandos de terminal, tests y firma APK"

# 4. Subir todos los commits al repositorio remoto
git push


Como realizar comandos o pruebas de TEST por terminal 
 Comandos de Terminal (Compilación y Pruebas)

Abre la terminal en la raíz del proyecto (`Agencias_Oportunidades`) y ejecuta los comandos según tu sistema operativo:

### 1. Dar permisos de ejecución (solo la primera vez en macOS / Linux):
```bash
chmod +x gradlew
2. Ejecutar pruebas unitarias locales (EventoViewModelTest y FeriadoViewModelTest):
Corre sobre la JVM local y no requiere emulador encendido:
macOS / Linux:
Bash
./gradlew testDebugUnitTest
Windows:
DOS
gradlew.bat testDebugUnitTest
3. Ejecutar pruebas de interfaz UI (EventosScreenTest):
Valida los componentes visuales en Jetpack Compose (requiere tener un emulador encendido o teléfono conectado):
macOS / Linux:
Bash
./gradlew connectedDebugAndroidTest
Windows:
DOS
gradlew.bat connectedDebugAndroidTest
4. Verificar compilación completa de código y tests:
Bash
./gradlew assembleDebug assembleDebugUnitTest assembleDebugAndroidTest

Con ese cambio en la sección 4, cualquier persona que descargue tu repositorio (en Mac o en Windows) sabrá exactamente qué comando correr para que los tests funcionen a la primera. Ya puedes pegar el README y tirar tus commits por partes tranquilo.
