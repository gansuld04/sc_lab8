package mn.edu.num.tms.core.domain;

/**
 * Thesis-ийн төлөв байдал (workflow статус).
 * 
 * Зөвшөөрөгдсөн шилжилтүүд:
 *   DRAFT --> SUBMITTED  (submit хийхэд)
 *   SUBMITTED --> APPROVED  (approve хийхэд)
 *   SUBMITTED --> REJECTED  (reject хийхэд, шалтгаан заавал оруулна)
 * 
 * CORE layer-д байдаг тул энд Swing эсвэл JDBC import байж болохгүй!
 */
public enum ThesisStatus {
    DRAFT,      // Анхны төлөв: шинэ thesis үүсгэхэд автоматаар оноогдоно
    SUBMITTED,  // Хянуулахаар илгээсэн
    APPROVED,   // Зөвшөөрөгдсөн
    REJECTED    // Татгалзсан (rejectReason заавал байна)
}
