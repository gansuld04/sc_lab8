package mn.edu.num.tms.core.application;

import mn.edu.num.tms.core.domain.ThesisStatus;

/**
 * DATA TRANSFER OBJECT (DTO): ThesisDTO
 * 
 * Зорилго:
 * - Core domain (Thesis.java) ба UI layer хооронд дамжуулах "дутуур" өгөгдлийн сав
 * - UI нь Thesis domain объектыг шууд харж болохгүй (layer тусгаарлалт)
 * - DTO нь "дүлий" (dumb) - зөвхөн өгөгдөл агуулна, логик байхгүй
 * 
 * Java Record ашиглаж байна (Java 16+): constructor, getter, equals, toString
 * автоматаар үүснэ.
 */
public record ThesisDTO(
    int id,
    String title,
    String studentId,
    String supervisorId,
    ThesisStatus status,
    String rejectReason
) {
    // Record нь автоматаар бүх constructor болон getter-ийг үүсгэнэ.
    // Тусгай логик шаардлагагүй.
}
