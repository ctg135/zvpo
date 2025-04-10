package mtusi.zvpo.entites;

import jakarta.persistence.*;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "history")
public class HistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    public UUID id;

    @Column
    public Date version_created_at;

    @Column(name = "signature_id", nullable = false)
    public UUID signatureId;

    @Column(name = "threat_name")
    public String threatName;

    @Column(name = "first_bytes")
    public String firstBytes;

    @Column(name = "first_bytes_hash")
    public int firstBytesHash;

    @Column(name = "remainder_hash")
    public String remainderHash;

    @Column(name = "remainder_length")
    public int remainderLength;

    @Column(name = "file_type")
    public String fileType;

    @Column(name = "offset_start")
    public int offsetStart;

    @Column(name = "offset_end")
    public int offsetEnd;

    @Column(name = "updated_at")
    public Date updatedAt;

    @Column(name = "status")
    public String status;

    public static HistoryEntity generate(SignatureEntity signature){
        // Статический метод для создания исторической копии экземпляра сигнатуры
        var result = new HistoryEntity();

        result.version_created_at   = new Date();
        result.signatureId          = signature.id;
        result.threatName           = signature.threatName;
        result.remainderLength      = signature.remainderLength;
        result.remainderHash        = signature.remainderHash;
        result.firstBytes           = signature.firstBytes;
        result.firstBytesHash       = signature.firstBytesHash;
        result.fileType             = signature.fileType;
        result.offsetStart          = signature.offsetStart;
        result.offsetEnd            = signature.offsetEnd;
        result.updatedAt            = signature.updatedAt;
        result.status               = signature.status;

        return result;
    }
}
