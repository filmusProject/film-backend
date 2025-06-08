// File: com/filmus/backend/movie/sync/PageStateRepository.java
package com.filmus.backend.movie.sync;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PageStateRepository extends JpaRepository<PageState, Integer> {
    // id=1인 레코드를 조회·저장
}