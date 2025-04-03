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
    private UUID id;

    @Column(name = "threat_name")
    private String threatName;

    @Column(name = "first_bytes")
    private String firstBytes;

    @Column(name = "remainder_hash")
    private String remainderHash;

    @Column(name = "remainder_length")
    private int remainderLength;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "offset_start")
    private int offsetStart;

    @Column(name = "offset_end")
    private int offsetEnd;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "status")
    private String status;
}
