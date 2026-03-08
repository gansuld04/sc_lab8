package mn.edu.num.tms.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * SINGLETON: DatabaseConnection
 * 
 * Зорилго:
 * - Нэг л database connection үүсгэж, дахин ашиглана (resource хэмнэлт)
 * - database.properties файлаас тохиргоог уншина (нууцлал хамгаалалт)
 * - app.persistence.mode утгыг RepositoryFactory-д өгнө
 * 
 * Singleton Pattern:
 * - private constructor: гаднаас new DatabaseConnection() хийж болохгүй
 * - static getInstance(): нэг л instance буцаана
 */
public class DatabaseConnection {

    // Цорын ганц instance (class loader-ийн thread-safe initialization)
    private static DatabaseConnection instance;

    // Нэг connection объект - дахин ашиглана
    private Connection connection;

    // database.properties-аас унших утгууд
    private String url;
    private String user;
    private String password;
    private String persistenceMode;

    // ----------------------------------------------------------------
    // PRIVATE CONSTRUCTOR: Гаднаас үүсгэж болохгүй
    // ----------------------------------------------------------------
    private DatabaseConnection() {
        loadProperties();
    }

    // ----------------------------------------------------------------
    // getInstance(): Цорын ганц instance-ийг буцаана
    // ----------------------------------------------------------------
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    // ----------------------------------------------------------------
    // getConnection(): Connection авах (хэрэв хаагдсан бол шинэ нээнэ)
    // ----------------------------------------------------------------
    public Connection getConnection() throws SQLException {
        // Connection null эсвэл хаагдсан бол шинэ connection нээнэ
        if (connection == null || connection.isClosed()) {
            try {
                // H2 driver-ийг динамикаар бүртгэнэ
                // Class.forName() нь classpath-д driver байгааг шалгана
                Class.forName("org.h2.Driver");
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Database холболт амжилттай: " + url);

                // Database schema үүсгэх (анх удаа ажиллахад)
                initSchema();

            } catch (ClassNotFoundException e) {
                throw new SQLException("H2 Driver олдсонгүй! pom.xml-д H2 dependency нэмсэн үү?", e);
            }
        }
        return connection;
    }

    // ----------------------------------------------------------------
    // getPersistenceMode(): RepositoryFactory-д "DB" эсвэл "MEM" өгнө
    // ----------------------------------------------------------------
    public String getPersistenceMode() {
        return persistenceMode;
    }

    // ----------------------------------------------------------------
    // PRIVATE: database.properties файлаас тохиргоо унших
    // ----------------------------------------------------------------
    private void loadProperties() {
        Properties props = new Properties();
        // ClassLoader ашиглан resources хавтасаас унших
        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("database.properties")) {
            if (input == null) {
                throw new RuntimeException(
                    "database.properties файл олдсонгүй! " +
                    "src/main/resources/ хавтаст байрлуулна уу."
                );
            }
            props.load(input);
            this.url = props.getProperty("db.url");
            this.user = props.getProperty("db.user");
            this.password = props.getProperty("db.password", ""); // нууц үг хоосон байж болно
            this.persistenceMode = props.getProperty("app.persistence.mode", "MEM");
            System.out.println("Persistence mode: " + this.persistenceMode);
        } catch (IOException e) {
            throw new RuntimeException("database.properties уншихад алдаа гарлаа!", e);
        }
    }

    // ----------------------------------------------------------------
    // PRIVATE: Thesis хүснэгт байхгүй бол үүсгэнэ (CREATE TABLE IF NOT EXISTS)
    // ----------------------------------------------------------------
    private void initSchema() throws SQLException {
        String sql = """
                CREATE TABLE IF NOT EXISTS thesis (
                    id           INT AUTO_INCREMENT PRIMARY KEY,
                    title        VARCHAR(255) NOT NULL,
                    student_id   VARCHAR(100) NOT NULL,
                    supervisor_id VARCHAR(100),
                    status       VARCHAR(50)  NOT NULL DEFAULT 'DRAFT',
                    reject_reason VARCHAR(500)
                )
                """;
        // try-with-resources: Statement автоматаар хаагдана
        try (var stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("Database schema бэлэн болов.");
        }
    }
}
