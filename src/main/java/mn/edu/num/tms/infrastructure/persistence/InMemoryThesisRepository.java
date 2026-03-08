package mn.edu.num.tms.infrastructure.persistence;

import mn.edu.num.tms.core.domain.Thesis;
import mn.edu.num.tms.core.ports.IThesisRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * IN-MEMORY REPOSITORY: RAM дотор хадгалах (тест болон хөгжүүлэлтэд хэрэглэнэ)
 * 
 * Зорилго:
 * - Database байхгүй үед эсвэл unit test хийхэд ашиглана
 * - database.properties-д app.persistence.mode=MEM гэж тохируулахад идэвхждэг
 * 
 * Анхааруулга:
 * - Програм дуусахад бүх өгөгдөл устна (RAM-д л байна)
 * - Persist хийхгүй - Sprint 05-ийн гол зорилго бол JdbcThesisRepository ашиглах!
 */
public class InMemoryThesisRepository implements IThesisRepository {

    // Thread-safe ID generator (AtomicInteger)
    private final AtomicInteger idGenerator = new AtomicInteger(1);

    // RAM дотор thesis-уудыг хадгалах жагсаалт
    private final List<Thesis> storage = new ArrayList<>();

    @Override
    public void save(Thesis thesis) {
        if (thesis.getId() == 0) {
            // Шинэ thesis: ID оноож жагсаалтад нэмнэ
            thesis.setId(idGenerator.getAndIncrement());
            storage.add(thesis);
        } else {
            // Байгаа thesis: ID-аар хайж солино
            for (int i = 0; i < storage.size(); i++) {
                if (storage.get(i).getId() == thesis.getId()) {
                    storage.set(i, thesis);
                    return;
                }
            }
        }
    }

    @Override
    public List<Thesis> findAll() {
        return new ArrayList<>(storage); // Хуулбар буцааж, гадна өөрчлөлтөөс хамгаална
    }

    @Override
    public Thesis findById(int id) {
        return storage.stream()
                .filter(t -> t.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void delete(int id) {
        storage.removeIf(t -> t.getId() == id);
    }
}
