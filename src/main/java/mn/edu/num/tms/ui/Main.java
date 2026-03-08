package mn.edu.num.tms.ui;

import mn.edu.num.tms.core.application.ThesisService;
import mn.edu.num.tms.core.ports.IThesisRepository;
import mn.edu.num.tms.infrastructure.persistence.RepositoryFactory;

import javax.swing.*;

/**
 * COMPOSITION ROOT: Main.java
 * 
 * Зорилго:
 * - Програмын эхлэх цэг
 * - Бүх dependency-г "угсарна" (Dependency Injection / Wiring)
 * 
 * Hexagonal Architecture-ийн холболт:
 * 
 *   RepositoryFactory
 *        |
 *        v (IThesisRepository)
 *   ThesisService (core/application)
 *        |
 *        v
 *   MainFrame (ui/swing - inbound adapter)
 * 
 * ГУРВАН ДАВХАРГА:
 * 1. Infrastructure: RepositoryFactory -> JdbcThesisRepository / InMemoryThesisRepository
 * 2. Core: ThesisService (бизнесийн логик)
 * 3. UI: MainFrame (Swing)
 */
public class Main {

    public static void main(String[] args) {

        // SwingUtilities.invokeLater: UI-г Event Dispatch Thread (EDT)-д эхлүүлнэ
        // Swing thread-safe ажиллахын тулд заавал энэ аргаар эхлүүлнэ!
        SwingUtilities.invokeLater(() -> {

            System.out.println("=== NUM Thesis Management System ===");
            System.out.println("Тохиргоо уншиж байна...");

            // 1. Infrastructure layer: Тохиргооноос repository сонгоно
            //    database.properties -> "DB" эсвэл "MEM"
            IThesisRepository repository = RepositoryFactory.create();

            // 2. Core layer: ThesisService-д repository inject хийнэ
            ThesisService thesisService = new ThesisService(repository);

            // 3. UI layer: MainFrame-д service inject хийнэ
            new MainFrame(thesisService);

            System.out.println("Програм ажиллаж байна...");
        });
    }
}
