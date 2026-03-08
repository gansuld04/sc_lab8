package mn.edu.num.tms.infrastructure.persistence;

import mn.edu.num.tms.core.domain.Thesis;
import mn.edu.num.tms.core.domain.ThesisStatus;
import mn.edu.num.tms.core.ports.IThesisRepository;
import mn.edu.num.tms.infrastructure.config.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * PERSISTENCE ADAPTER (DAO): JdbcThesisRepository
 * 
 * Зорилго:
 * - IThesisRepository interface-ийг H2 + JDBC ашиглан хэрэгжүүлнэ
 * - SQL query-г бичиж, ResultSet-ийг Thesis domain object болгон хөрвүүлнэ
 * 
 * Аюулгүй байдал:
 * - SQL Injection-оос хамгаалахын тулд PreparedStatement ашиглана
 * - String concatenation ашиглахгүй ("+title+" гэх мэт ХОРИГЛОНО!)
 * 
 * ДҮРЭМ (Hexagonal Architecture):
 * - java.sql.* зөвхөн энд байна (infrastructure layer)
 * - Core layer руу java.sql объект дамжуулж болохгүй
 */
public class JdbcThesisRepository implements IThesisRepository {

    // ----------------------------------------------------------------
    // SAVE: INSERT эсвэл UPDATE
    // ----------------------------------------------------------------

    @Override
    public void save(Thesis thesis) {
        if (thesis.getId() == 0) {
            // ID = 0 => шинэ thesis => INSERT
            insert(thesis);
        } else {
            // ID > 0 => байгаа thesis => UPDATE
            update(thesis);
        }
    }

    private void insert(Thesis thesis) {
        // PreparedStatement: ? placeholder SQL injection-оос хамгаална
        String sql = "INSERT INTO thesis (title, student_id, supervisor_id, status, reject_reason) " +
                     "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             // RETURN_GENERATED_KEYS: автоматаар үүссэн ID-г буцааж авна
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, thesis.getTitle());
            pstmt.setString(2, thesis.getStudentId());
            pstmt.setString(3, thesis.getSupervisorId()); // null байж болно
            pstmt.setString(4, thesis.getStatus().name()); // enum -> String
            pstmt.setString(5, thesis.getRejectReason());  // null байж болно

            pstmt.executeUpdate();

            // Database-аас үүссэн ID-г thesis объектод оноох
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    thesis.setId(generatedKeys.getInt(1));
                }
            }
            System.out.println("INSERT амжилттай. ID=" + thesis.getId());

        } catch (SQLException e) {
            throw new RuntimeException("Thesis хадгалахад алдаа: " + e.getMessage(), e);
        }
    }

    private void update(Thesis thesis) {
        String sql = "UPDATE thesis SET title=?, student_id=?, supervisor_id=?, " +
                     "status=?, reject_reason=? WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, thesis.getTitle());
            pstmt.setString(2, thesis.getStudentId());
            pstmt.setString(3, thesis.getSupervisorId());
            pstmt.setString(4, thesis.getStatus().name());
            pstmt.setString(5, thesis.getRejectReason());
            pstmt.setInt(6, thesis.getId()); // WHERE id=?

            pstmt.executeUpdate();
            System.out.println("UPDATE амжилттай. ID=" + thesis.getId());

        } catch (SQLException e) {
            throw new RuntimeException("Thesis шинэчлэхэд алдаа: " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // FIND ALL: SELECT * FROM thesis
    // ----------------------------------------------------------------

    @Override
    public List<Thesis> findAll() {
        List<Thesis> theses = new ArrayList<>();
        String sql = "SELECT id, title, student_id, supervisor_id, status, reject_reason FROM thesis";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            // ResultSet-ийн мөр бүрийг Thesis объект болгон хөрвүүлнэ
            while (rs.next()) {
                theses.add(mapResultSet(rs)); // private mapper method
            }

        } catch (SQLException e) {
            throw new RuntimeException("Thesis жагсаалт авахад алдаа: " + e.getMessage(), e);
        }
        return theses;
    }

    // ----------------------------------------------------------------
    // FIND BY ID
    // ----------------------------------------------------------------

    @Override
    public Thesis findById(int id) {
        String sql = "SELECT id, title, student_id, supervisor_id, status, reject_reason " +
                     "FROM thesis WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSet(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Thesis хайхад алдаа. ID=" + id + ": " + e.getMessage(), e);
        }
        return null; // Олдоогүй
    }

    // ----------------------------------------------------------------
    // DELETE
    // ----------------------------------------------------------------

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM thesis WHERE id = ?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("DELETE амжилттай. ID=" + id);

        } catch (SQLException e) {
            throw new RuntimeException("Thesis устгахад алдаа. ID=" + id + ": " + e.getMessage(), e);
        }
    }

    // ----------------------------------------------------------------
    // PRIVATE MAPPER: ResultSet мөр -> Thesis domain object
    // java.sql.ResultSet зөвхөн энд (infrastructure layer) хэрэглэнэ
    // ----------------------------------------------------------------

    /**
     * Database-ийн нэг мөрийг (ResultSet) Thesis domain object болгон хөрвүүлнэ.
     * Энэ mapper-ийг зөвхөн энэ класс ашиглана.
     */
    private Thesis mapResultSet(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        String title = rs.getString("title");
        String studentId = rs.getString("student_id");
        String supervisorId = rs.getString("supervisor_id"); // null байж болно
        // DB-д "DRAFT" string хэлбэрээр хадгалсан -> enum болгон хөрвүүлнэ
        ThesisStatus status = ThesisStatus.valueOf(rs.getString("status"));
        String rejectReason = rs.getString("reject_reason"); // null байж болно

        return new Thesis(id, title, studentId, supervisorId, status, rejectReason);
    }
}
