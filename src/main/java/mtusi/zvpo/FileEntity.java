package mtusi.zvpo;

import jakarta.persistence.*;
import org.springframework.data.repository.ListCrudRepository;

@Entity
@Table(name = "files")
public class FileEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filename", nullable = false)
    private String filename;

    private String contentType;

    @Lob
    private byte[] data;

    // Id
    public Long getId() {
        return id;
    }

    public void  setId(Long value) {
        id = value;
    }

    // filename
    public String getFilename () {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }


    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
