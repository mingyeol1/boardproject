package org.zerock.b01.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.Board;
import org.zerock.b01.repository.search.BoardSearch;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> , BoardSearch {
                                                        //title로 값을 넘겨주면 Pageable 가 값을 정렬
    Page<Board> findByTitleContainingOrderByBnoDesc(String keyword, Pageable pageable);

    // @Query 어노테이션에서 사용하는 구문은 JPQL을 이용한다.
    // JPQL은 SQL과 유사하게 JPA에서 사용하는 쿼리 언어이다.
    // @Query 를 이용하는 경우
    //1. 조인과 같이 복잡한 쿼리를 실행하려고 할 때
    //2. 원하는 속성만 추출해서 Object[]로 처리하거나 DTO로 처리 할 수 있음.

    @EntityGraph(attributePaths = {"imageSet"})
    @Query("select b from Board b where b.bno=:bno")
    Optional<Board> findByIdWithImages(Long bno);



    //             //Board: JPA에 있는 엔티티 객체                :keyword//특정 맵핑값.
    @Query("select b from Board b where  b.title like concat('%',:keyword,'%') ") //검색기능
    Page<Board> findKeyword(String keyword, Pageable pageable);

    //3. 속성 값 중 nativeQuery속성 값을 ture로 지정하면 SQL구문으로 사용이 가능함.
    @Query(value = "select now()", nativeQuery = true)
    String getTime();

}
