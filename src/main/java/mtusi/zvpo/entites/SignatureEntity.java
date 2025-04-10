package mtusi.zvpo.entites;

import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "signatures")
public class SignatureEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false)
    public UUID id;

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

    public SignatureEntity clone(){
        var result = new SignatureEntity();

        result.id = this.id;
        result.threatName = this.threatName;
        result.firstBytes = this.firstBytes;
        result.firstBytesHash = this.firstBytesHash;
        result.remainderHash = this.remainderHash;
        result.remainderLength = this.remainderLength;
        result.fileType = this.fileType;
        result.offsetStart = this.offsetStart;
        result.offsetEnd = this.offsetEnd;
        result.updatedAt = this.updatedAt;
        result.status = this.status;

        return result;
    }
}
