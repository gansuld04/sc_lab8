package mn.edu.num.tms.core.application;

import mn.edu.num.tms.core.domain.ThesisStatus;
import mn.edu.num.tms.infrastructure.persistence.InMemoryThesisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UNIT TEST: ThesisService
 * 
 * Зорилго:
 * - Core logic-ийг database байхгүйгээр тест хийнэ
 * - InMemoryThesisRepository ашиглан быстро тест хийнэ
 * 
 * Тестийн зарчим:
 * - Arrange: тест хийх нөхцөл бэлдэнэ
 * - Act: тест хийх үйлдэл хийнэ
 * - Assert: үр дүнг шалгана
 */
class ThesisServiceTest {

    private ThesisService service;

    @BeforeEach
    void setUp() {
        // Тест бүр шинэ InMemory repository-тай эхэлнэ (цэвэр тест)
        service = new ThesisService(new InMemoryThesisRepository());
    }

    // ----------------------------------------------------------------
    // CREATE тест
    // ----------------------------------------------------------------

    @Test
    void createThesis_validData_shouldSaveSuccessfully() {
        // Arrange & Act
        service.createThesis("Agile in Java", "student001");

        // Assert
        List<ThesisDTO> all = service.getAllTheses();
        assertEquals(1, all.size());
        assertEquals("Agile in Java", all.get(0).title());
        assertEquals(ThesisStatus.DRAFT, all.get(0).status()); // Анхны төлөв DRAFT байх ёстой
    }

    @Test
    void createThesis_emptyTitle_shouldThrowException() {
        // Хоосон гарчигтай thesis үүсгэхэд exception шидэх ёстой
        assertThrows(IllegalArgumentException.class, () ->
            service.createThesis("", "student001")
        );
    }

    @Test
    void createThesis_emptyStudentId_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () ->
            service.createThesis("Some Title", "")
        );
    }

    // ----------------------------------------------------------------
    // WORKFLOW тест
    // ----------------------------------------------------------------

    @Test
    void submitThesis_fromDraft_shouldBeSubmitted() {
        // Arrange
        service.createThesis("Test Thesis", "s001");
        int id = service.getAllTheses().get(0).id();

        // Act
        service.submitThesis(id);

        // Assert
        assertEquals(ThesisStatus.SUBMITTED, service.getAllTheses().get(0).status());
    }

    @Test
    void submitThesis_fromSubmitted_shouldThrowException() {
        // Arrange: SUBMITTED төлөвтэй thesis
        service.createThesis("Test Thesis", "s001");
        int id = service.getAllTheses().get(0).id();
        service.submitThesis(id); // DRAFT -> SUBMITTED

        // Act & Assert: SUBMITTED -> SUBMITTED дахин хийж болохгүй
        assertThrows(IllegalStateException.class, () ->
            service.submitThesis(id)
        );
    }

    @Test
    void approveThesis_fromSubmitted_shouldBeApproved() {
        // Arrange
        service.createThesis("Test Thesis", "s001");
        int id = service.getAllTheses().get(0).id();
        service.submitThesis(id);

        // Act
        service.approveThesis(id);

        // Assert
        assertEquals(ThesisStatus.APPROVED, service.getAllTheses().get(0).status());
    }

    @Test
    void approveThesis_fromDraft_shouldThrowException() {
        // Arrange: DRAFT төлөвтэй thesis-ийг шууд approve хийж болохгүй
        service.createThesis("Test Thesis", "s001");
        int id = service.getAllTheses().get(0).id();

        // Assert
        assertThrows(IllegalStateException.class, () ->
            service.approveThesis(id)
        );
    }

    @Test
    void rejectThesis_fromSubmitted_withReason_shouldBeRejected() {
        // Arrange
        service.createThesis("Test Thesis", "s001");
        int id = service.getAllTheses().get(0).id();
        service.submitThesis(id);

        // Act
        service.rejectThesis(id, "Дутуу судалгаа");

        // Assert
        ThesisDTO dto = service.getAllTheses().get(0);
        assertEquals(ThesisStatus.REJECTED, dto.status());
        assertEquals("Дутуу судалгаа", dto.rejectReason());
    }

    @Test
    void rejectThesis_withEmptyReason_shouldThrowException() {
        // Шалтгаангүйгээр reject хийж болохгүй
        service.createThesis("Test Thesis", "s001");
        int id = service.getAllTheses().get(0).id();
        service.submitThesis(id);

        assertThrows(IllegalArgumentException.class, () ->
            service.rejectThesis(id, "") // Хоосон шалтгаан
        );
    }

    // ----------------------------------------------------------------
    // DELETE тест
    // ----------------------------------------------------------------

    @Test
    void deleteThesis_shouldRemoveFromList() {
        // Arrange
        service.createThesis("To Delete", "s002");
        int id = service.getAllTheses().get(0).id();

        // Act
        service.deleteThesis(id);

        // Assert
        assertTrue(service.getAllTheses().isEmpty());
    }
}
