package org.shopping.models.board.config;


import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.shopping.controllers.admins.BoardSearch;
import org.shopping.entities.Board;
import org.shopping.entities.QBoard;
import org.shopping.repositories.BoardRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import static org.springframework.data.domain.Sort.Order.desc;

/**
 * 게시판 설정 목록
 *
 */
@Service
@RequiredArgsConstructor
public class BoardConfigListService {

    private final BoardRepository boardRepository;

    public Page<Board> gets(BoardSearch boardSearch) {
        QBoard board = QBoard.board;

        BooleanBuilder andBuilder = new BooleanBuilder();
        BooleanBuilder orBuilder = new BooleanBuilder();

        int page = boardSearch.getPage();
        int limit = boardSearch.getLimit();

        page = page < 1 ? 1 : page;
        limit = limit < 1 ? 20 : limit;

        /** 검색 조건 처리 S */
        String sopt = boardSearch.getSopt();
        String skey = boardSearch.getSkey();
        if (sopt != null && !sopt.isBlank() && skey != null && !skey.isBlank()) {
            skey = skey.trim();
            sopt = sopt.trim();

            /* 통합 검색 - bId, bName */
            if (sopt.equals("all")) {
                orBuilder.or(board.bId.contains(skey))
                        .or(board.bName.contains(skey));
                andBuilder.and(orBuilder);

                /* 게시판 아이디 bId */
            } else if (sopt.equals("bId")) {
                andBuilder.and(board.bId.contains(skey));

                /* 게시판명 bName */
            } else if (sopt.equals("bName")) {
                andBuilder.and(board.bName.contains(skey));
            }
        }
        /** 검색 조건 처리 E */

        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(desc("createdAt")));
        Page<Board> data = boardRepository.findAll(andBuilder, pageable);

        return data;
    }
}