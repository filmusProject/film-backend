// File: com/filmus/backend/movie/sync/PageState.java
package com.filmus.backend.movie.sync;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "page_state")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PageState {
    @Id @Column(name = "singleton_id")
    private Integer id;
    @Column(name = "last_Page")
    private int lastPage;  // 이전 동기화에서 끝낸 startCount

}