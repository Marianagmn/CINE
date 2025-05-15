package DB;

/**
 * Una clase simple para probar la conexión a la base de datos desde la línea de comandos
 */
public class ConnectionTest {
    
    public static void main(String[] args) {
        System.out.println("Probando conexión a la base de datos MySQL/MariaDB...");
        
        try {
            // Cargar el driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Driver MySQL/MariaDB cargado correctamente");
            
            // Probar la conexión
            boolean connected = DatabaseConnectionTester.testConnection();
            
            if (connected) {
                System.out.println("\n¡CONEXIÓN EXITOSA!");
                System.out.println("La base de datos 'cine' está correctamente configurada.");
                System.out.println("La tabla 'tickets' está lista para ser utilizada.");
            } else {
                System.out.println("\n¡CONEXIÓN FALLIDA!");
                System.out.println("Verifica que:");
                System.out.println("1. XAMPP está en ejecución con el servicio MySQL/MariaDB iniciado");
                System.out.println("2. El usuario 'root' no tiene contraseña o la contraseña es correcta");
                System.out.println("3. El puerto predeterminado 3306 está disponible");
                System.out.println("4. No hay un firewall bloqueando la conexión");
            }
        } catch (ClassNotFoundException e) {
            System.out.println("\nERROR: No se encontró el driver JDBC de MySQL/MariaDB");
            System.out.println("Asegúrate de que el archivo mysql-connector-java-X.X.XX.jar está en el classpath");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("\nERROR INESPERADO: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
