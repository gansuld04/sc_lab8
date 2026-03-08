package mn.edu.num.tms.core.ports;

import mn.edu.num.tms.core.domain.Thesis;
import java.util.List;

/**
 * OUTBOUND PORT: IThesisRepository (Interface)
 * 
 * Hexagonal Architecture-ийн "гадагшаа" гарц.
 * 
 * Зорилго:
 * - Core layer нь database-тай шууд ярьж болохгүй.
 * - Харин энэ interface-ийн цаана юу байгааг (H2, MySQL, RAM) мэдэхгүй.
 * - Infrastructure layer (JdbcThesisRepository эсвэл InMemoryThesisRepository)
 *   энэ interface-ийг хэрэгжүүлнэ (implements).
 * 
 * ДҮРЭМ: Энд java.sql.* import байж БОЛОХГҮЙ! (Core нь DB-г мэдэхгүй)
 */
public interface IThesisRepository {

    /**
     * Шинэ thesis хадгалах (INSERT) эсвэл байгааг шинэчлэх (UPDATE).
     * @param thesis Хадгалах thesis объект
     */
    void save(Thesis thesis);

    /**
     * Бүх thesis-ийг авах (SELECT *)
     * @return Thesis объектуудын жагсаалт
     */
    List<Thesis> findAll();

    /**
     * ID-аар thesis хайх
     * @param id Хайх thesis-ийн ID
     * @return Олдсон thesis, олдоогүй бол null
     */
    Thesis findById(int id);

    /**
     * Thesis устгах (DELETE)
     * @param id Устгах thesis-ийн ID
     */
    void delete(int id);
}
