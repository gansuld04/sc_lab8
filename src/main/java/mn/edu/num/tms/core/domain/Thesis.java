package mn.edu.num.tms.core.domain;

/**
 * DOMAIN ENTITY: Thesis (Дипломын ажил)
 * 
 * Hexagonal Architecture-ийн дүрэм:
 * - Энэ класс бол системийн "зүрх" (core/domain)
 * - Бизнесийн дүрэм, workflow логик энд байна
 * - Swing, JDBC, SQL гэх мэт техникийн зүйл энд байж БОЛОХГҮЙ
 * 
 * Workflow дүрэм (core-д шалгана, UI-д биш!):
 *   DRAFT -> SUBMITTED: submit()
 *   SUBMITTED -> APPROVED: approve()
 *   SUBMITTED -> REJECTED: reject(reason)
 */
public class Thesis {

    private int id;
    private String title;
    private String studentId;
    private String supervisorId;     // Phase 1-д заавал биш
    private ThesisStatus status;
    private String rejectReason;     // Зөвхөн REJECTED үед л байна

    // ----------------------------------------------------------------
    // Constructor: Шинэ thesis үүсгэхэд DRAFT төлөвтэй эхэлнэ
    // ----------------------------------------------------------------
    public Thesis(String title, String studentId) {
        // Validation: хоосон байж болохгүй
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Thesis title хоосон байж болохгүй!");
        }
        if (studentId == null || studentId.isBlank()) {
            throw new IllegalArgumentException("Student ID хоосон байж болохгүй!");
        }
        this.title = title;
        this.studentId = studentId;
        this.status = ThesisStatus.DRAFT; // Анхны төлөв заавал DRAFT
    }

    // Database-аас уншихад хэрэглэх constructor (бүх талбартай)
    public Thesis(int id, String title, String studentId, String supervisorId,
                  ThesisStatus status, String rejectReason) {
        this.id = id;
        this.title = title;
        this.studentId = studentId;
        this.supervisorId = supervisorId;
        this.status = status;
        this.rejectReason = rejectReason;
    }

    // ----------------------------------------------------------------
    // WORKFLOW METHODS - Бизнесийн дүрэм энд шалгагдана
    // ----------------------------------------------------------------

    /**
     * DRAFT -> SUBMITTED шилжилт
     * Зөвхөн DRAFT байгаа thesis-ийг submit хийж болно.
     */
    public void submit() {
        if (this.status != ThesisStatus.DRAFT) {
            throw new IllegalStateException(
                "Submit хийж болохгүй! Одоогийн төлөв: " + this.status +
                ". Зөвхөн DRAFT төлөвтэй thesis submit хийж болно."
            );
        }
        this.status = ThesisStatus.SUBMITTED;
    }

    /**
     * SUBMITTED -> APPROVED шилжилт
     * Зөвхөн SUBMITTED байгаа thesis-ийг approve хийж болно.
     */
    public void approve() {
        if (this.status != ThesisStatus.SUBMITTED) {
            throw new IllegalStateException(
                "Approve хийж болохгүй! Одоогийн төлөв: " + this.status +
                ". Зөвхөн SUBMITTED төлөвтэй thesis approve хийж болно."
            );
        }
        this.status = ThesisStatus.APPROVED;
    }

    /**
     * SUBMITTED -> REJECTED шилжилт
     * Зөвхөн SUBMITTED байгаа thesis-ийг reject хийж болно.
     * rejectReason заавал оруулна!
     */
    public void reject(String reason) {
        if (this.status != ThesisStatus.SUBMITTED) {
            throw new IllegalStateException(
                "Reject хийж болохгүй! Одоогийн төлөв: " + this.status +
                ". Зөвхөн SUBMITTED төлөвтэй thesis reject хийж болно."
            );
        }
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Reject хийхэд шалтгаан (reason) заавал оруулна!");
        }
        this.status = ThesisStatus.REJECTED;
        this.rejectReason = reason;
    }

    // ----------------------------------------------------------------
    // Getters & Setters
    // ----------------------------------------------------------------

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getSupervisorId() { return supervisorId; }
    public void setSupervisorId(String supervisorId) { this.supervisorId = supervisorId; }

    public ThesisStatus getStatus() { return status; }

    public String getRejectReason() { return rejectReason; }

    @Override
    public String toString() {
        return "Thesis{id=" + id + ", title='" + title + "', student='" + studentId +
               "', status=" + status + "}";
    }
}
