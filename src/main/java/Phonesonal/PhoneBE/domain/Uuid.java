package Phonesonal.PhoneBE.domain;

import Phonesonal.PhoneBE.domain.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Uuid extends BaseEntity {

    @Id
    private Long id;

    @Column(unique = true)
    private String uuid;
}
