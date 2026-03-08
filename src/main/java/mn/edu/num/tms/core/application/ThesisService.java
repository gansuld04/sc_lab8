package mn.edu.num.tms.core.application;

import mn.edu.num.tms.core.domain.Thesis;
import mn.edu.num.tms.core.ports.IThesisRepository;
import java.util.List;
import java.util.stream.Collectors;

/**
 * APPLICATION SERVICE: ThesisService
 * 
 * Зорилго:
 * - Use case-уудыг гүйцэтгэх (CRUD + Workflow)
 * - UI layer ба Domain layer хооронд "дирижёр" болж ажиллана
 * - Repository interface-ийг ашиглан persistence-тай харилцана
 * 
 * ДҮРЭМ:
 * - Энд Swing import байж БОЛОХГҮЙ
 * - Энд java.sql import байж БОЛОХГҮЙ
 * - Зөвхөн core.domain болон core.ports ашиглана
 */
public class ThesisService {

    // Interface-ийг ашиглана (JdbcRepository эсвэл InMemoryRepository - мэдэхгүй)
    private final IThesisRepository repository;

    // Constructor Injection: dependency-г гаднаас дамжуулна (DI pattern)
    public ThesisService(IThesisRepository repository) {
        this.repository = repository;
    }

    // ----------------------------------------------------------------
    // CREATE
    // ----------------------------------------------------------------

    /**
     * Шинэ thesis үүсгэж хадгална.
     * Анхны төлөв: DRAFT (Thesis.java-д автоматаар оноогдоно)
     */
    public void createThesis(String title, String studentId) {
        // Domain object үүсгэнэ - validation Thesis.java-д хийгдэнэ
        Thesis thesis = new Thesis(title, studentId);
        repository.save(thesis);
    }

    /**
     * SupervisorId-тай thesis үүсгэх
     */
    public void createThesis(String title, String studentId, String supervisorId) {
        Thesis thesis = new Thesis(title, studentId);
        thesis.setSupervisorId(supervisorId);
        repository.save(thesis);
    }

    // ----------------------------------------------------------------
    // READ
    // ----------------------------------------------------------------

    /**
     * Бүх thesis-ийг DTO болгон буцаана (UI layer-т дамжуулах)
     */
    public List<ThesisDTO> getAllTheses() {
        return repository.findAll()
                .stream()
                .map(this::toDTO)  // Thesis -> ThesisDTO хөрвүүлнэ
                .collect(Collectors.toList());
    }

    // ----------------------------------------------------------------
    // UPDATE
    // ----------------------------------------------------------------

    /**
     * Thesis-ийн мэдээллийг шинэчлэх
     */
    public void updateThesis(int id, String newTitle, String newStudentId) {
        Thesis thesis = repository.findById(id);
        if (thesis == null) {
            throw new IllegalArgumentException("Thesis олдсонгүй. ID: " + id);
        }
        thesis.setTitle(newTitle);
        thesis.setStudentId(newStudentId);
        repository.save(thesis);
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------

    /**
     * Thesis устгах
     */
    public void deleteThesis(int id) {
        repository.delete(id);
    }

    // ----------------------------------------------------------------
    // WORKFLOW - Бизнесийн дүрэм domain object-д шалгагдана
    // ----------------------------------------------------------------

    /**
     * DRAFT -> SUBMITTED
     * Шилжилтийн дүрмийг Thesis.submit() шалгана.
     */
    public void submitThesis(int id) {
        Thesis thesis = findOrThrow(id);
        thesis.submit();          // IllegalStateException шиднэ хэрэв буруу төлөв бол
        repository.save(thesis);  // Өөрчлөлтийг хадгална
    }

    /**
     * SUBMITTED -> APPROVED
     */
    public void approveThesis(int id) {
        Thesis thesis = findOrThrow(id);
        thesis.approve();
        repository.save(thesis);
    }

    /**
     * SUBMITTED -> REJECTED (шалтгаантай)
     */
    public void rejectThesis(int id, String reason) {
        Thesis thesis = findOrThrow(id);
        thesis.reject(reason);    // IllegalArgumentException хэрэв reason хоосон бол
        repository.save(thesis);
    }

    // ----------------------------------------------------------------
    // PRIVATE HELPERS
    // ----------------------------------------------------------------

    /**
     * ID-аар thesis хайж, олдоогүй бол exception шидэх
     */
    private Thesis findOrThrow(int id) {
        Thesis thesis = repository.findById(id);
        if (thesis == null) {
            throw new IllegalArgumentException("Thesis олдсонгүй. ID: " + id);
        }
        return thesis;
    }

    /**
     * Thesis domain object -> ThesisDTO хөрвүүлэх (mapper)
     * Core layer UI-г мэдэхгүй, гэхдээ DTO дамжуулж болно.
     */
    private ThesisDTO toDTO(Thesis thesis) {
        return new ThesisDTO(
            thesis.getId(),
            thesis.getTitle(),
            thesis.getStudentId(),
            thesis.getSupervisorId(),
            thesis.getStatus(),
            thesis.getRejectReason()
        );
    }
}
