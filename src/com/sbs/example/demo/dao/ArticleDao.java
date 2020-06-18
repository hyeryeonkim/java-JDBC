package com.sbs.example.demo.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.sbs.example.demo.db.DBConnection;
import com.sbs.example.demo.dto.Article;
import com.sbs.example.demo.dto.Board;
import com.sbs.example.demo.factory.Factory;

// Dao
public class ArticleDao {
	private DBConnection dbConnection;

	public ArticleDao() {
		dbConnection = Factory.getDBConnection();
	}
	
	//board code 줄테니까 article
	public List<Article> getArticlesByBoardCode(String code) {
		StringBuilder sb = new StringBuilder();        //String str = "";   str +=  을 쓰지 않는 이유 : += 할 때마다 필요없는 것들을 계속 만들어 내면서 더해서 용량을 잡아먹는다.
		
		sb.append(String.format("SELECT A.* "));
		sb.append(String.format("FROM `article` AS A "));       
		sb.append(String.format("INNER JOIN `board` AS B "));  // A, B라는 별명은 이 순간 사용되고 사라진다. 2개의 테이블에 id가 존재하므로 구분하기 위함으로
		sb.append(String.format("ON A.boardId = B.id "));       // ★ JOIN은 알려주시지 않은 내용으로 내일 알려주신다고 하셨음.
		sb.append(String.format("WHERE 1 "));                     // 의미 : 참이라면.  의미없다.  늘 참이다.  AND를 쓰기 위해서 써줘야 하는데 미리 쓰고 시작하는 것.
		sb.append(String.format("AND B.`code` = '%s' ", code));   //A, B랑 합쳐진 새로운, 바로 사라질 테이블에서 던져준 code를 찾아라.   
		sb.append(String.format("ORDER BY A.id DESC "));             // 현재로써는 크게 의미 없는 코드라고 하셨음.

		List<Article> articles = new ArrayList<>(); // articles를 담는 무한 배열.
		List<Map<String, Object>> rows = dbConnection.selectRows(sb.toString()); //Map을 담는 무한 배열.
		//rows는 Map의 무한배열 리모콘을 가지고 있는 변수. Map 여러 타입의 변수를 가지고 있는 아이.
		
		for ( Map<String, Object> row : rows ) { // Map무한 배열을 무한배열에 하나씩 담는다???????
			articles.add(new Article(row));  // 그리고 articles에 Map을 가진 article 객체를 한개씩 담는다?????
		}
		
		return articles;
	}

	public List<Board> getBoards() {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM `board` "));
		sb.append(String.format("WHERE 1 "));
		sb.append(String.format("ORDER BY id DESC "));

		List<Board> boards = new ArrayList<>();
		List<Map<String, Object>> rows = dbConnection.selectRows(sb.toString());
		
		for ( Map<String, Object> row : rows ) {
			boards.add(new Board(row));
		}
		
		return boards;
	}

	public Board getBoardByCode(String code) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM `board` "));
		sb.append(String.format("WHERE 1 "));
		sb.append(String.format("AND `code` = '%s' ", code));

		Map<String, Object> row = dbConnection.selectRow(sb.toString());
		
		if ( row.isEmpty() ) {
			return null;
		}
		
		return new Board(row);
	}

	public int saveBoard(Board board) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("INSERT INTO board "));
		sb.append(String.format("SET regDate = '%s' ", board.getRegDate()));
		sb.append(String.format(", `code` = '%s' ", board.getCode()));
		sb.append(String.format(", `name` = '%s' ", board.getName()));

		return dbConnection.insert(sb.toString());
	}

	public int save(Article article) {
		StringBuilder sb = new StringBuilder();
		//String str += "";  ~~~~   이렇게하면 용량 낭비가 심하다. 상당히 심하다.   

		sb.append(String.format("INSERT INTO article "));
		sb.append(String.format("SET regDate = '%s' ", article.getRegDate()));
		sb.append(String.format(", `title` = '%s' ", article.getTitle()));
		sb.append(String.format(", `body` = '%s' ", article.getBody()));
		sb.append(String.format(", `view` = '%d' ", article.getView()));
		sb.append(String.format(", `memberId` = '%d' ", article.getMemberId()));
		sb.append(String.format(", `boardId` = '%d' ", article.getBoardId()));

		return dbConnection.insert(sb.toString());
	}

	public Board getBoard(int id) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM `board` "));
		sb.append(String.format("WHERE 1 "));
		sb.append(String.format("AND `id` = '%d' ", id));

		Map<String, Object> row = dbConnection.selectRow(sb.toString());
		
		if ( row.isEmpty() ) {
			return null;
		}
		
		return new Board(row);
	}

	public List<Article> getArticles() {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM `article` "));
		sb.append(String.format("WHERE 1 "));
		sb.append(String.format("ORDER BY id DESC "));

		List<Article> articles = new ArrayList<>();
		List<Map<String, Object>> rows = dbConnection.selectRows(sb.toString());
		
		for ( Map<String, Object> row : rows ) {
			articles.add(new Article(row));
		}
		
		return articles;
	}

	public int getArticleModify(int articleId, String title, String body) {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("UPDATE article "));
		sb.append(String.format("SET `title` = '%s' ", title));
		sb.append(String.format(", `body` = '%s' ", body));
		sb.append(String.format("WHERE 1 "));
		sb.append(String.format("AND `id` = '%d' ", articleId));

		return dbConnection.insert(sb.toString());
	}

	public int getArticleByBoardId(int articleId, int boardId) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("SELECT * "));
		sb.append(String.format("From `article` "));
		sb.append(String.format("WHERE id = %d ", articleId));
		
		Map<String, Object> row = dbConnection.selectRow(sb.toString());
		if ( row.isEmpty()) {
			return -1;
		}
		
		if ((int)row.get("boardId") != boardId) {
			return -2;
		}
		
		
		return 1;
	}

	public int articleDelete(int articleId) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(String.format("DELETE "));
		sb.append(String.format("FROM `article` "));
		sb.append(String.format("WHERE id = %d ", articleId));
		return dbConnection.delete(sb.toString());
		
	}

	public Article getArticleWriteByMemberId(int articleId) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("SELECT * "));
		sb.append(String.format("FROM `article` "));
		sb.append(String.format("WHERE 1 "));
		sb.append(String.format("AND `id` = %d", articleId));
		Map<String, Object> row = dbConnection.selectRow(sb.toString());
		
		return new Article(row); 
	}

	public void articleByView(int view, int articleId) {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("UPDATE `article` "));
		sb.append(String.format("SET `view` = %d", view));
		sb.append(String.format(" WHERE id = %d ", articleId));
		
		dbConnection.insert(sb.toString());
	}


}