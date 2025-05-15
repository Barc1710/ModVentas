package Operaciones.aSeguridad;

public class SesionUsuario {
    private static String usuario;
    private static String rol;

    public static void iniciarSesion(String usuarioLogueado, String rolUsuario) {
        usuario = usuarioLogueado;
        rol = rolUsuario;
    }

    public static String getUsuario() {
        return usuario;
    }

    public static String getRol() {
        return rol;
    }

    public static void cerrarSesion() {
        usuario = null;
        rol = null;
    }
}
