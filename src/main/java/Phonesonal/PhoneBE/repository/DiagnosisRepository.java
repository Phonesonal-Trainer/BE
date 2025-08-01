package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.Diagnosis;
import Phonesonal.PhoneBE.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DiagnosisRepository extends JpaRepository<Diagnosis, Long> {

    // 사용자별 진단 조회 (1:1 관계이므로 하나만)
    Optional<Diagnosis> findByUserId(Long userId);

    // 사용자별 진단 존재 여부 확인
    boolean existsByUserId(Long userId);

    Long user(User user);
}