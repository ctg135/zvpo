package mtusi.zvpo.repositories;

import mtusi.zvpo.entites.HistoryEntity;
import mtusi.zvpo.entites.SignatureEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface HistoryRepository extends JpaRepository<HistoryEntity, UUID> {
}
