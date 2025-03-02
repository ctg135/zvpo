package mtusi.zvpo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyFileRepository extends JpaRepository<FileEntity, Long> {
    FileEntity findByFilename(String filename);


}
