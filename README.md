Agencia de Oportunidades — Aplicación Móvil Android

Aplicación móvil nativa desarrollada en **Kotlin** y **Jetpack Compose** bajo la arquitectura **MVVM**, diseñada para la gestión, consulta y acreditación mediante códigos QR de eventos, organizaciones y feriados legales, integrando consumo de **microservicios REST** y **persistencia local offline**.

---

##  Enlaces del Proyecto y Gestión Ágil

* **Repositorio Oficial (Rama Principal):** [GitHub - main](https://github.com/JoseIgnacio10/Agencia_oportunidades_APP_DATOS_MOVILES)
* **Rama Consolidada (Proyecto Completo):** [GitHub - Proyecto_Completo](https://github.com/JoseIgnacio10/Agencia_oportunidades_APP_DATOS_MOVILES/tree/Proyecto_Completo)
* **Tablero de Seguimiento Ágil (Trello):** [Tablero Trello - Agencia de Oportunidades](https://trello.com/b/lLzRvwK7)
* **APK Firmado de Producción:** `app/release/app-release.apk`

---

## Evolución del Proyecto: Diagnóstico Inicial vs. Solución Final

Durante el desarrollo e integración del sistema, se identificaron y resolvieron puntos críticos respecto a las entregas preliminares:

| Aspecto Técnico | Estado Inicial (Problemas Detectados) | Solución Implementada (Versión Final) |
| :--- | :--- | :--- |
| **Arquitectura** | Lógica mezclada entre vistas y llamadas de datos sin separación clara de capas. | Refactorización integral a patrón **MVVM** (`ui` → `viewmodel` → `repository` → `data`/`remote`). |
| **Conectividad y Microservicios** | Dependencia total de conexión a internet; fallos o pantallas vacías si el servicio externo no respondía. | Integración de **Retrofit** para microservicios + respaldo automático en base de datos local **Room (SQLite)**. |
| **Gestión de Estados** | Estados de carga y errores manejados de forma inconsistente en la interfaz. | Uso reactivo de `StateFlow` / `LiveData` y **Kotlin Coroutines** para actualización fluida en Jetpack Compose. |
| **Calidad y Testing** | Ausencia de validación automatizada sobre la lógica de negocio y renderizado. | Implementación de pruebas unitarias (`ViewModelTest`) y pruebas instrumentadas de UI (`EventosScreenTest`). |
| **Despliegue y Versionado** | Archivos sin consolidar y compilación solo en modo depuración (`debug`). | Historial limpio en GitHub (commits modulares + rama `Proyecto_Completo`) y generación de **APK firmado (`release`)**. |

---

##  Arquitectura de Microservicios y Persistencia Híbrida

La aplicación opera bajo un esquema híbrido (**Online / Offline-First**) que combina servicios distribuidos con almacenamiento local:

1. **Capa de Microservicios (`remote`):**
   * Comunicación mediante **Retrofit + Gson** hacia los endpoints externos para la obtención de **Eventos**, **Organizaciones**, **Resultados** y consulta a la API pública de **Feriados Legales**.
2. **Capa de Persistencia Local (`data` / Room Database):**
   * Base de datos SQLite gestionada por `AppDatabase` con 5 entidades y sus respectivos DAOs (`ActivacionDao`, `ContactoDao`, `GestionDao`, `OrganizacionDao`, `ResultadoDao`) para garantizar operatividad sin conexión.
3. **Módulo de Acreditación QR:**
   * Lectura y validación rápida de códigos QR en terreno (con códigos de prueba incluidos en `docs/qr_prueba`).

---

##  Estructura / Esqueleto del Proyecto

```text
Agencia_oportunidades_APP_DATOS_MOVILES/
├── app/
│   ├── release/
│   │   └── app-release.apk                  # APK firmado listo para instalación
│   └── src/
│       ├── main/java/com/example/agenciasoportunidades/
│       │   ├── data/                        # Base de datos Room (AppDatabase y DAOs)
│       │   │   ├── ActivacionDao.kt
│       │   │   ├── ContactoDao.kt
│       │   │   ├── GestionDao.kt
│       │   │   ├── OrganizacionDao.kt
│       │   │   └── ResultadoDao.kt
│       │   ├── model/                       # Entidades de BD y modelos de dominio
│       │   ├── navigation/                  # Rutas y grafo de navegación Compose
│       │   ├── remote/                      # Clientes Retrofit y endpoints de microservicios
│       │   ├── repository/                  # Mediadores entre API remota y Room local
│       │   ├── ui/                          # Pantallas Jetpack Compose (Login, Eventos, Feriados, QR)
│       │   └── viewmodel/                   # Gestión de estados (EventoViewModel, FeriadoViewModel)
│       ├── test/                            # Pruebas Unitarias (JUnit4 + Coroutines Test)
│       │   └── .../viewmodel/
│       │       ├── EventoViewModelTest.kt
│       │       └── FeriadoViewModelTest.kt
│       └── androidTest/                     # Pruebas Instrumentadas de Interfaz (Compose UI Test)
│           └── .../ui/
│               └── EventosScreenTest.kt
├── docs/
│   └── qr_prueba/                           # Códigos QR de ejemplo para validación funcional
├── gradle/                                  # Configuración de dependencias y Wrapper
├── build.gradle.kts
├── settings.gradle.kts
└── README.md
Stack Tecnológico
Lenguaje: Kotlin
UI Framework: Jetpack Compose (Material Design 3)
Arquitectura: MVVM + Repository Pattern
Asincronía: Kotlin Coroutines & StateFlow
Networking (Microservicios): Retrofit 2 + Gson Converter
Base de Datos Local: Android Room (SQLite)
Testing: JUnit 4, Kotlinx Coroutines Test, Compose UI Test
 Ejecución de Pruebas Automatizadas
Desde la terminal en la raíz del proyecto, ejecutar:
Pruebas Unitarias (ViewModels):
Bash
./gradlew testDebugUnitTest
Pruebas Instrumentadas de UI (requiere emulador o dispositivo conectado):
Bash
./gradlew connectedDebugAndroidTest

### Cómo pegarlo en GitHub:
1. Ve a tu archivo **`README.md`** en la rama **`main`** y haz clic en el ícono del **lápiz (`Edit this file`)**.
2. Borra el texto anterior, pega este bloque completo y haz clic en **`Commit changes...`**.
3. Si quieres que tu rama `Proyecto_Completo` también tenga este `README.md` actualizado, entra a esa rama en GitHub y haz clic en **`Sync fork` -> `Update branch`** (o crea un Pull Request rápido desde `main` hacia `Proyecto_Completo`).
