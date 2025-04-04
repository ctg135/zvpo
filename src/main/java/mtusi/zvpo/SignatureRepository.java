package mtusi.zvpo;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Repository
public interface SignatureRepository extends JpaRepository<SignatureEntity, UUID> {
    List<SignatureEntity> findByStatus(String status);
    List<SignatureEntity> findByUpdatedAtAfter(Date since);
    List<SignatureEntity> findByIdIn(List<UUID> uuids);
}
