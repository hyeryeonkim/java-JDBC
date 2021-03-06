package com.sbs.example.demo.controller;

import java.util.List;

import com.sbs.example.demo.dto.Article;
import com.sbs.example.demo.dto.ArticleReply;
import com.sbs.example.demo.dto.Board;
import com.sbs.example.demo.dto.Member;
import com.sbs.example.demo.factory.Factory;
import com.sbs.example.demo.service.ArticleService;
import com.sbs.example.demo.service.MemberService;

public class ArticleController extends Controller {
	private ArticleService articleService;
	private MemberService memberService;

	public ArticleController() {
		articleService = Factory.getArticleService();
	}

	public void doAction(Request reqeust) {
		if (reqeust.getActionName().equals("list")) {
			actionList(reqeust);
		} else if (reqeust.getActionName().equals("write")) {
			actionWrite(reqeust);
		} else if (reqeust.getActionName().equals("changeBoard")) {
			actionChangeBoard(reqeust);
		} else if (reqeust.getActionName().equals("currentBoard")) {
			actionCurrentBoard(reqeust);
		} else if (reqeust.getActionName().equals("modify")) {
			actionModify(reqeust);
		} else if (reqeust.getActionName().equals("delete")) {
			actionDelete(reqeust);
		} else if (reqeust.getActionName().equals("detail")) {
			actionDetail(reqeust);
		}
	}

	private void actionDetail(Request reqeust) {
		System.out.println("== 게시물 상세보기 ==\n");
		if ( reqeust.getArg1() == null ) {
			System.out.println("[ 상세보기 게시물 번호를 입력바랍니다. ]");
			return;
		}
		int articleId = Integer.parseInt(reqeust.getArg1());
		int boardId = articleService.getArticleByBoardId(articleId, Factory.getSession().getCurrentBoard().getId());
		if ( boardId == -1 ) {
			System.out.println("\n[ 해당 게시물은 존재하지 않습니다. ]");
			return;
		}
		if ( boardId == -2 ) {
			System.out.println("\n[ 현재 게시판에서 작성한 게시물이 아닙니다. ]");
			return;
		}
		
		Article article = articleService.getArticleWriteByMemberId(articleId);
		Member member = memberService.getMemberByArticleId(articleId);
		article.setView(article.getView() + 1);
		articleService.articleByView(article.getView(), articleId);
		System.out.printf("번호 : %d\n", article.getId());
		System.out.printf("작성일 : %s\n", article.getRegDate());
		System.out.printf("작성자 : %s\n", member.getName());
		System.out.printf("제목 : %s\n", article.getTitle());
		System.out.printf("내용 : %s\n", article.getBody());
		System.out.printf("조회수 : %d\n", article.getView());
		
		String body;
		Member member2 = Factory.getSession().getLoginedMember();
		System.out.println("\n[댓글 리스트]\n");
		// article의 id 와 articleReply의 articleId가 같고, article의 boardId와 현재 boardId가 같은 친구를 불러와야 한다.
		List<ArticleReply> articleReplys = articleService.getArticleReplysByBoardCode(Factory.getSession().getCurrentBoard().getId());
		
		System.out.println(" 번호 |          댓글 내용                     |     작성자                  ");
		for ( ArticleReply articleReply : articleReplys ) {
			if ( articleReply.getArticleId() == article.getId()) {
				System.out.printf(" %-3d  | %-25s     | %-10s   \n", articleReply.getId(), articleReply.getBody(), member2.getName());
			}
			
		}
		
		System.out.printf("\n[ 게시물 댓글을 입력하시겠습니까?(1 : 네, 1 외 아무거나 : 아니오) ] ");
		String yesOrNo = Factory.getScanner().nextLine().trim();
		if ( yesOrNo.equals("1")) {
			System.out.printf("댓글 : ");
			body = Factory.getScanner().nextLine().trim();
			int fail = articleService.articleReplySave(body, article.getId(), member.getId() );
		}
		else {
			return;
		}
		
		
	}

	private void actionDelete(Request reqeust) {
		//TODO : 수정하기. ( 삭제된 게시물 번호를 입력하면 NullPointerException 오류 발생 -> 해결하기) 
		System.out.println("== 게시물 삭제 ==\n");
		Member member = Factory.getSession().getLoginedMember();
		if ( member == null ) {
			System.out.println("[ 로그인 후 이용바랍니다. ]");
			return;
		}
		if ( reqeust.getArg1() == null ) {
			System.out.println("[ 삭제할 게시물 번호를 입력바랍니다. ]");
			return;
		}
		
		int articleId = Integer.parseInt(reqeust.getArg1());
		
		Article article = articleService.getArticleWriteByMemberId(articleId);
		if ( member.getLoginId().equals("admin")) {
			
		} else if ( article.getMemberId() != member.getId()) {
			System.out.println("[ 게시물 수정은 작성자 본인만 가능합니다. ]");
			return;
		}
		int boardId = articleService.getArticleByBoardId(articleId, Factory.getSession().getCurrentBoard().getId());
		
		if ( boardId == -3 ) {
			System.out.println("[ 해당게시물은 삭제된 게시물입니다. ]");
			return;
		}
		if ( boardId == -1 ) {
			System.out.println("[ 해당 게시물은 존재하지 않습니다. ]");
			return;
		}
		
		if ( boardId == -2 ) {
			System.out.println("[ 현재 게시판에서 작성한 게시물이 아닙니다. ]");
			return;
		}
		
		int num = articleService.articleDelete(articleId);
		System.out.printf("[ %d번 게시물 %d개를 삭제하였습니다. ]\n", articleId, num);
		
	}

	private void actionModify(Request reqeust) {
		System.out.println("== 게시물 수정 시작 ==\n");
		//TODO : 수정할 일 
		if ( reqeust.getArg1() == null ) { //★ try, catch 로 숫자가 입력되었을 경우 명령어 실패 사유 출력하는걸로 바꾸기.
			System.out.println("[ 수정  게시물 번호를 입력바랍니다. ]");
			return;
		}
		int articleId = Integer.parseInt(reqeust.getArg1());
		
		Article article = articleService.getArticleWriteByMemberId(articleId);
		Member member = Factory.getSession().getLoginedMember();
		
		if ( member == null ) {
			System.out.println("[ 로그인 후 이용바랍니다. ]");
			return;
		}
		if ( member.getLoginId().equals("admin")) {
			
		} else if ( article.getMemberId() != member.getId()) {
			System.out.println("[ 게시물 수정은 작성자 본인만 가능합니다. ]");
			return;
		} 
		int boardId = articleService.getArticleByBoardId(articleId, Factory.getSession().getCurrentBoard().getId());
		
		if ( boardId == -1 ) {
			System.out.println("[ 해당 게시물은 존재하지 않습니다. ]");
			return;
		}
		if ( boardId == -2 ) {
			System.out.println("[ 현재 게시판에서 작성한 게시물이 아닙니다. ]");
			return;
		}
		
		
		System.out.printf("제목 : ");
		String title = Factory.getScanner().nextLine().trim();
		System.out.printf("내용 : ");
		String body = Factory.getScanner().nextLine().trim();
		
		System.out.printf("\n== %d번 게시물 수정 끝 ==\n", articleId);
		articleService.getArticleModify(articleId, title, body);
		
	}

	private void actionCurrentBoard(Request reqeust) {
		
		Board board = Factory.getSession().getCurrentBoard();
		System.out.printf("[ 현재 게시판 : %s 게시판 ]\n", board.getName());
	}

	private void actionChangeBoard(Request reqeust) {
		String boardCode = reqeust.getArg1();

		Board board = articleService.getBoardByCode(boardCode);

		if (board == null) {
			System.out.println("[ 해당 게시판이 존재하지 않습니다. ]");
		} else if ( boardCode.equals(Factory.getSession().getCurrentBoard().getCode())) {
			System.out.printf("[ 이미 %s 게시판 입니다. ]\n ", board.getName());
		} else {
			System.out.printf("[ [%s게시판]으로 변경되었습니다. ]\n ", board.getName());
			Factory.getSession().setCurrentBoard(board);
		}
	}

	private void actionList(Request reqeust) {
		
		if ( reqeust.getArg1() == null ) {
			System.out.println("[ 페이지 번호를 입력바랍니다. ]");
			return;
		}
		/*if ( reqeust.getArg1() == "" ) {
			System.out.println("[ 페이지 번호를 입력바랍니다. ]");
			return;
		}*/  // 숫자가 아닌 String 이 입력되었을 경우, 오류 잡아내지 못함...
		
		int pageId = Integer.parseInt(reqeust.getArg1());
		String googling = "";
		if ( reqeust.getArg2() != null ) {
			googling = reqeust.getArg2();
		}
		
		Board currentBoard = Factory.getSession().getCurrentBoard();
		List<Article> articles = articleService.getArticlesByBoardCode(currentBoard.getCode());
		int totalltems = articles.size();
		int itemsInAPage = 5;
		int totalPage = (int)Math.ceil((double)totalltems / itemsInAPage);
		int currentPage = pageId;
		if ( pageId > totalPage ) {
			System.out.println("[ 존재하지 않는 페이지 입니다. ]");
			return;
		}
		int startIndex = (itemsInAPage*currentPage)-itemsInAPage;
		int until = (itemsInAPage*currentPage)-1;

		if ( totalltems < until ) {
			int num = until - totalltems;
			until = until - num-1;
		}
		System.out.printf("== %s 게시물 리스트 시작 ==\n\n", currentBoard.getName());
		System.out.println(" 번호 |         날짜           |          제목                  \n");
		for ( int i = startIndex; i <= until; i++ ) {
			if (articles.get(i).getBoardId() == currentBoard.getId() &&  articles.get(i).getTitle().contains(googling)) {
				//System.out.println(articles.get(i).toStringList());
				
					System.out.printf("  %-3d | %-15s    |  %-15s\n", articles.get(i).getId(), articles.get(i).getRegDate(), articles.get(i).getTitle());
			}
		}
		System.out.println();
		System.out.printf("페이지 ");
		for ( int i = 1; i <= totalPage; i++ ) {
				
				String a = "" + i;
				
				if ( i == currentPage) {
					a = "[" + a + "]";
				}
				if ( i < totalPage ) {
					System.out.printf("%s ", a);
				}
				if ( i == totalPage ) {
					System.out.printf("%s \n", a);
				}
		//System.out.printf("== %s 게시물 리스트 끝 ==\n", currentBoard.getName());
		}
	}

	private void actionWrite(Request reqeust) {
		System.out.println("== 게시물 작성 ==\n");
		System.out.printf("제목 : ");
		String title = Factory.getScanner().nextLine();
		System.out.printf("내용 : ");
		String body = Factory.getScanner().nextLine();
		int view = 0;

		// 현재 게시판 id 가져오기
		int boardId = Factory.getSession().getCurrentBoard().getId();

		// 현재 로그인한 회원의 id 가져오기
		int memberId = Factory.getSession().getLoginedMember().getId();
		int newId = articleService.write(boardId, memberId, title, body, view);

		System.out.printf("\n[ %d번 글이 생성되었습니다. ]\n", newId);
	}
}