package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zerock.b01.domain.Board;
import org.zerock.b01.domain.BoardLike;
import org.zerock.b01.domain.Member;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {

    Optional<BoardLike> findByBoardAndMember(Board board, Member member);
    int countByBoard(Board board);
}
