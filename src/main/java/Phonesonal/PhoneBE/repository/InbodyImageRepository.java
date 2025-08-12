package Phonesonal.PhoneBE.repository;

import Phonesonal.PhoneBE.domain.Inbody;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InbodyImageRepository extends JpaRepository<Inbody,Long> {
}
