package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.Uuid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UuidRepository extends JpaRepository<Uuid, UUID> {
}
