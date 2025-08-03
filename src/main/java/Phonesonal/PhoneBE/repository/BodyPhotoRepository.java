package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.BodyPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface BodyPhotoRepository extends JpaRepository<BodyPhoto, Long> {
    @Query(value = "SELECT * FROM body_photo WHERE user_id = :userId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<BodyPhoto> findByUserId(Long userId);
}
