package Phonesonal.PhoneBE.domain.common.exercise;

import Phonesonal.PhoneBE.domain.enums.exercise.BodyCategory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BodyPart {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String nameEn; // 부위 영어 이름

    @Column(nullable = false, length = 20)
    private String nameKo; // 부위 한국어 이름

    @Column(nullable = false, length = 20)
    private BodyCategory bodyCategory;
}

