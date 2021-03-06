package com.community.web.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.community.web.domain.Board;
import com.community.web.domain.Comment;
import com.community.web.domain.User;
import com.community.web.domain.enums.BoardType;
import com.community.web.payload.request.BoardRequest;
import com.community.web.payload.response.MessageResponse;
import com.community.web.repository.BoardRepository;
import com.community.web.repository.UserRepository;
import com.community.web.service.BoardService;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/board")
public class BoardController {
	
	@Autowired
    private BoardService boardService;
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private UserRepository userRepository;

	/**
	 * 사용자 게시판
	 * @return
	 */
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public Map<String, Object> userBoardList() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Board> boardList = boardService.findBoardList();
		map.put("content", "User Board.");
		map.put("items", boardList);
		return map;
	}
	
	/**
	 * 사용자 게시판 세부
	 * @param boardNo
	 * @return
	 */
	@GetMapping("/user/detail")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
    public Map<String, Object> userBoardDetail(@RequestParam(value = "boardNo", defaultValue = "1") Long boardNo) {
		Map<String, Object> map = new HashMap<String, Object>();
		Board board = boardService.findBoardByBoardNo(boardNo);
		List<Comment> commentList = boardService.findCommentList(board);
		map.put("board", board);
		map.put("comments", commentList);
        return map;
    }
	
	/**
	 * 운영자 게시판
	 * @return
	 */
	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public String moderatorAccess() {
		return "Moderator Board.";
	}

	/**
	 * 관리자 게시판
	 * @return
	 */
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public Map<String, Object> adminBoardList() {
		Map<String, Object> map = new HashMap<String, Object>();
		List<Board> boardList = boardService.findBoardList();
		map.put("content", "Admin Board.");
		map.put("items", boardList);
		return map;
	}
	
	/**
	 * 관리자 게시판 세부
	 * @param boardNo
	 * @return
	 */
	@GetMapping("/admin/detail")
	@PreAuthorize("hasRole('ADMIN')")
    public Board adminBoardDetail(@RequestParam(value = "boardNo", defaultValue = "1") Long boardNo) {
		Board board = boardService.findBoardByBoardNo(boardNo);
        return board;
    }
	
	/**
	 * 게시판 등록
	 * @param boardRequest
	 * @return
	 */
	@PostMapping("/create")
	public ResponseEntity<?> createBoard(@RequestBody BoardRequest boardRequest) {
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
		User user = userRepository.findByUsername(username).get();
		if(user == null) {
			return ResponseEntity.ok(new MessageResponse("Board created fail!"));
		}
		
		BoardType boardTye = null;
		if(boardRequest.getBoardType().equals(BoardType.FREE.name())) {
			boardTye = BoardType.FREE;
		} else if(boardRequest.getBoardType().equals(BoardType.NOTICE.name())) {
			boardTye = BoardType.NOTICE;
		}
		if(boardTye == null) {
			return ResponseEntity.ok(new MessageResponse("Board created fail!"));
		}
		
		Board board = new Board();
		board.setUser(user);
		board.setBoardTitle(boardRequest.getBoardTitle());
		board.setBoardSubTitle(boardRequest.getBoardSubTitle());
		board.setBoardContent(boardRequest.getBoardContent());	
		board.setBoardType(boardTye);
		board.setCreatedDate(LocalDateTime.now());
		boardRepository.save(board);
		
		return ResponseEntity.ok(new MessageResponse("Board created successfully!"));
	}
	
	/**
	 * 게시판 수정
	 * @param boardRequest
	 * @return
	 */
	@PostMapping("/update")
	public ResponseEntity<?> updateBoard(@Valid @RequestBody BoardRequest boardRequest) {
		Long boardNo = Long.parseLong(boardRequest.getBoardNo());
		
		BoardType boardTye = null;
		if(boardRequest.getBoardType().equals(BoardType.FREE.name())) {
			boardTye = BoardType.FREE;
		} else if(boardRequest.getBoardType().equals(BoardType.NOTICE.name())) {
			boardTye = BoardType.NOTICE;
		}
		Board board = boardRepository.findByBoardNo(boardNo);
		board.setBoardTitle(boardRequest.getBoardTitle());
		board.setBoardSubTitle(boardRequest.getBoardSubTitle());
		board.setBoardContent(boardRequest.getBoardContent());	
		board.setBoardType(boardTye);
		board.setUpdatedDate(LocalDateTime.now());
		boardRepository.save(board);
		return ResponseEntity.ok(new MessageResponse("Board updated successfully!"));
	}
	
	/**
	 * 게시판 삭제
	 * @param boardRequest
	 * @return
	 */
	@PostMapping("/delete")
	public ResponseEntity<?> deleteBoard(@Valid @RequestBody BoardRequest boardRequest) {
		Long boardNo = Long.parseLong(boardRequest.getBoardNo());
		boardRepository.delete(boardRepository.findByBoardNo(boardNo));
		return ResponseEntity.ok(new MessageResponse("Board deleted successfully!"));
	}
}
