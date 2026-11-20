package com.example.agenciasoportunidades.model.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [OportunidadEntity::class, UsuarioEntity::class], version = 4)
abstract class AppDatabase : RoomDatabase() {
    abstract fun oportunidadDao(): OportunidadDao
    abstract fun usuarioDao(): UsuarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private const val INSERTAR_USUARIOS_DEMO =
            "INSERT OR IGNORE INTO usuarios (nombre, correo, clave, perfil) VALUES " +
                    "('Ejecutivo Demo', 'ejecutivo@agenciaficticia.cl', 'Agencia2026', 'Ejecutivo comercial'), " +
                    "('Supervisor Demo', 'supervisor@agenciaficticia.cl', 'Agencia2026', 'Supervisor comercial'), " +
                    "('Activaciones Demo', 'activaciones@agenciaficticia.cl', 'Agencia2026', 'Gestión de activaciones')"

        private val MIGRACION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `usuarios` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`nombre` TEXT NOT NULL, `correo` TEXT NOT NULL, " +
                            "`clave` TEXT NOT NULL, `perfil` TEXT NOT NULL)"
                )
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_usuarios_correo` ON `usuarios` (`correo`)")
                db.execSQL(INSERTAR_USUARIOS_DEMO)
            }
        }

        private val MIGRACION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(INSERTAR_USUARIOS_DEMO)
            }
        }

        private val MIGRACION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE oportunidades ADD COLUMN estado TEXT NOT NULL DEFAULT 'Pendiente'")
            }
        }

        private val precargarUsuario = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                db.execSQL(INSERTAR_USUARIOS_DEMO)
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "agencia_database"
                )
                    .addMigrations(MIGRACION_1_2, MIGRACION_2_3, MIGRACION_3_4)
                    .addCallback(precargarUsuario)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
