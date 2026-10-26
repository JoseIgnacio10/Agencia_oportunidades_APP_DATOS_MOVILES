package com.example.agenciasoportunidades.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

// Versión 2: se agrega la tabla de usuarios para el login
@Database(entities = [OportunidadEntity::class, UsuarioEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun oportunidadDao(): OportunidadDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Cuenta ficticia de demostración (el caso exige no usar datos reales)
        private const val INSERTAR_USUARIO_DEMO =
            "INSERT OR IGNORE INTO usuarios (nombre, correo, clave, perfil) VALUES " +
                    "('Ejecutivo Demo', 'ejecutivo@agenciaficticia.cl', 'Agencia2026', 'Ejecutivo comercial')"

        // De la versión 1 a la 2: crea la tabla de usuarios SIN borrar las oportunidades ya guardadas
        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `usuarios` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`nombre` TEXT NOT NULL, `correo` TEXT NOT NULL, " +
                            "`clave` TEXT NOT NULL, `perfil` TEXT NOT NULL)"
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_usuarios_correo` ON `usuarios` (`correo`)")
                db.execSQL(INSERTAR_USUARIO_DEMO)
            }
        }

        // En una instalación nueva la base se crea desde cero: ahí también se precarga la cuenta demo
        private val precargarUsuario = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL(INSERTAR_USUARIO_DEMO)
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agencia_database"
                )
                    .addMigrations(MIGRACION_1_2)
                    .addCallback(precargarUsuario)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
