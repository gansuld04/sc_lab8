package mn.edu.num.tms.infrastructure.persistence;

import mn.edu.num.tms.core.ports.IThesisRepository;
import mn.edu.num.tms.infrastructure.config.DatabaseConnection;

/**
 * FACTORY: RepositoryFactory
 * 
 * Зорилго:
 * - database.properties-д тохируулсан mode-оос хамаарч аль repository ашиглахыг шийднэ
 * - "DB"  -> JdbcThesisRepository  (H2 database)
 * - "MEM" -> InMemoryThesisRepository (RAM, тест)
 * 
 * Factory Pattern давуу тал:
 * - Main.java эсвэл Core layer өөрчлөлтгүйгээр storage сонгоно
 * - Нэг газарт "switch" хийнэ
 */
public class RepositoryFactory {

    /**
     * Тохиргооны файлыг уншиж, зохих repository-г буцаана.
     * 
     * @return IThesisRepository - JdbcThesisRepository эсвэл InMemoryThesisRepository
     */
    public static IThesisRepository create() {
        // DatabaseConnection Singleton-оос persistence mode авна
        String mode = DatabaseConnection.getInstance().getPersistenceMode();

        System.out.println("Repository mode: " + mode);

        if ("DB".equalsIgnoreCase(mode)) {
            // H2 database ашиглах - өгөгдөл файлд хадгалагдана (persist)
            System.out.println("JdbcThesisRepository ашиглаж байна (H2 database)");
            return new JdbcThesisRepository();
        } else {
            // RAM ашиглах - програм дуусахад өгөгдөл устна
            System.out.println("InMemoryThesisRepository ашиглаж байна (RAM - тест горим)");
            return new InMemoryThesisRepository();
        }
    }
}
