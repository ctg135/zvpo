package mtusi.zvpo;

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
}
