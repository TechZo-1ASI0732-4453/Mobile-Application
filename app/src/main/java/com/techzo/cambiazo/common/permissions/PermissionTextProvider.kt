package com.techzo.cambiazo.common.permissions

abstract class PermissionTextProvider {
    abstract val permission: Permission
    abstract val title: String
    abstract val message: String
    open val confirmButtonText: String = "Abrir configuración"
    open val dismissButtonText: String = "Cancelar"
}

class LocationTextProvider : PermissionTextProvider() {
    override val permission = Permission.LOCATION
    override val title = "Permiso de ubicación bloqueado"
    override val message = "Debes habilitar el permiso de ubicación desde Configuración ⚙️"
}

class CameraTextProvider : PermissionTextProvider() {
    override val permission = Permission.CAMERA
    override val title = "Permiso de cámara bloqueado"
    override val message = "Debes habilitar el permiso de cámara desde Configuración ⚙️"
}

class AudioTextProvider : PermissionTextProvider() {
    override val permission = Permission.AUDIO
    override val title: String = "Permiso de micrófono requerido"
    override val message: String = "Debes habilitar el permiso de micrófono desde Configuración."
}

object PermissionTextProviderFactory {
    fun getProvider(permission: Permission): PermissionTextProvider = when(permission) {
        Permission.LOCATION -> LocationTextProvider()
        Permission.CAMERA -> CameraTextProvider()
        Permission.AUDIO -> AudioTextProvider()
    }
}