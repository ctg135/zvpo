package mtusi.zvpo.repositories;

import mtusi.zvpo.entites.AuditEntity;
import mtusi.zvpo.entites.SignatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntity, UUID> {
}
