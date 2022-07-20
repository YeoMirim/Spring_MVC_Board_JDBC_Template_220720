// 실질적으로 DB의 Data에 왔다갔다함
// Server => context.xml에 resource 작성

package com.mirim.springjdbctemp.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;

import com.mirim.springjdbctemp.dto.BDto;
import com.mirim.springjdbctemp.util.Constant;

public class BDao {
	
//	DataSource dataSource;		// Server의 context.xml 불러다 쓰겠다
	
	JdbcTemplate template;				// Server의 context.xml에 저장된 JdbcTemplate을 불러다 쓰겠다

	public BDao() {
/*		
		super();
		// TODO Auto-generated constructor stub
		try {			// 강제적 예외처리 해줘야함
			Context context = new InitialContext();
			dataSource = (DataSource) context.lookup("java:comp/env/jdbc/Oracle11g");	// java DateSource 초기화
		}
		catch (Exception e) {
			e.printStackTrace();
		}
*/		
		this.template = Constant.template;		// 초기화 필요	
		
		
	} // BDao 종료
	
	
	public void write(final String bname, final String btitle, final String bcontent) {  // 사용자가 입력한 3개의 값을 받음, 값이 변경되면 안되므로 final이 있어야함(새로운 깂X) 
		
		this.template.update(new PreparedStatementCreator() {	// 객체 생성
			
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				// TODO Auto-generated method stub
				
				String sql = "INSERT INTO mvc_board(bid, bname, btitle, bcontent, bhit, bgroup, bstep, bindent) "
						+ "VALUES (mvc_board_seq.nextval, ?, ?, ?, 0, mvc_board_seq.currval, 0, 0)";  // bid는 시퀀스, bgroup은 현재 글의 값
				
				PreparedStatement pstmt = con.prepareStatement(sql);
				pstmt.setString(1, bname);  
				pstmt.setString(2, btitle);
				pstmt.setString(3, bcontent);
				
				return pstmt;
			}
		});	// ?를 넣어야 하므로 구조가 살짝 다름
		
/*		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = dataSource.getConnection();
			String sql = "INSERT INTO mvc_board(bid, bname, btitle, bcontent, bhit, bgroup, bstep, bindent) "
					+ "VALUES (mvc_board_seq.nextval, ?, ?, ?, 0, mvc_board_seq.currval, 0, 0)";  // bid는 시퀀스, bgroup은 현재 글의 값
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bname);
			pstmt.setString(2, btitle);
			pstmt.setString(3, bcontent);
			
			pstmt.executeUpdate();		// integer 반환할 수 있음, 실헹문
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
*/				
	} // write 종료
	
	
	public ArrayList<BDto> list() {  
		
		String sql = "SELECT * FROM mvc_board ORDER BY bgroup DESC, bstep ASC";
		
		return (ArrayList<BDto>) template.query(sql, new BeanPropertyRowMapper<BDto>(BDto.class));	// mapping, 형변환 필요
		// row가 한줄의 rs같은 의미 
		
/*		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		ArrayList<BDto> dtos = new ArrayList<BDto>();
		
		try {
			conn = dataSource.getConnection();
		//	String sql = "SELECT * FROM mvc_board ORDER BY bid DESC"; // 내림차순 정렬, 덧글이 오면 나중에 순서 바꿔줘야함
			String sql = "SELECT * FROM mvc_board ORDER BY bgroup DESC, bstep ASC"; // 그룹명으로 내림차순 정렬하고, 댓글순으로 오름차순 정렬(원글이 위, 댓글이 아래)
			pstmt = conn.prepareStatement(sql);
			
			rs = pstmt.executeQuery();		// 실헹문 (select문은 Query로 실행해야함)
			
			while (rs.next()) {			// 더이상 글이 없을때까지 반복해 내용을 뽑고 배열에 차례로 쌓임 (있으면 true, 없으면 false) , 글의 개수만큼 반복
				int bid = rs.getInt("bid");
				String bname = rs.getString("bname");
				String btitle = rs.getString("btitle");
				String bcontent = rs.getString("bcontent");
				Timestamp bdate = rs.getTimestamp("bdate");
				int bhit = rs.getInt("bhit");
				int bgroup = rs.getInt("bgroup");
				int bstep = rs.getInt("bstep");
				int bindent = rs.getInt("bindent");
				
				BDto dto = new BDto(bid, bname, btitle, bcontent, bdate, bhit, bgroup, bstep, bindent);
				dtos.add(dto);	// 한바퀴 돌 때마다 한번씩 들어감
			}
		}	
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null ) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dtos;		// 만든 리스트를 반환
*/		
	}	// list 종료
	
	
	public BDto contentView(String strbid) {  	// 하나만 선택해서 보냄, 반환값 O
		
		this.upHit(strbid);  // this=>내 class내의 메소드를 사용하겠다. 해당 글 볼때마다 upHit 메소드를 호출하겠다. 
		
		String sql = "SELECT * FROM mvc_board WHERE bid=" + strbid;
		
		return template.queryForObject(sql, new BeanPropertyRowMapper<BDto>(BDto.class));	// id를 찾아서 Bdto (Object) 하나 반환
		
/*		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BDto dto = null;			// 생성
		
		
		try {
			conn = dataSource.getConnection();
			String sql = "SELECT * FROM mvc_board WHERE bid=?"; // 어느 값이 올지 모르므로 ?처리
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(strbid));// ? 셋팅 (String으로 들어온 strbid를 integer로 형변환=>  Wrapper class이용)
			
			rs = pstmt.executeQuery();		// 실헹문 (select문은 Query로 실행해야함)
			
			while (rs.next()) {			// 더이상 글이 없을때까지 반복해 내용을 뽑고 배열에 차례로 쌓임 (있으면 true, 없으면 false) 
				int bid = rs.getInt("bid");
				String bname = rs.getString("bname");
				String btitle = rs.getString("btitle");
				String bcontent = rs.getString("bcontent");
				Timestamp bdate = rs.getTimestamp("bdate");
				int bhit = rs.getInt("bhit");
				int bgroup = rs.getInt("bgroup");
				int bstep = rs.getInt("bstep");
				int bindent = rs.getInt("bindent");
				
				dto = new BDto(bid, bname, btitle, bcontent, bdate, bhit, bgroup, bstep, bindent);
			}
		}	
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null ) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dto;		
*/		
	}	// contentView 종료 
	
	
	public void delete(final String strbid) {  	// 반환값 X, 매개변수는 값 변동 없게 final처리 해야함
		
		String sql = "DELETE FROM mvc_board WHERE bid=?";
		
		this.template.update(sql, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException {
				// TODO Auto-generated method stub
				pstmt.setString(1, strbid);		
			}
		});
		
/*		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = dataSource.getConnection();
			String sql = "DELETE FROM mvc_board WHERE bid=?"; // 어느 값이 올지 모르므로 ?처리
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(strbid));// ? 셋팅 (String으로 들어온 strbid를 integer로 형변환=> weper class이용)
			
			pstmt.executeUpdate();		// 실헹문 (delete는 Update로 실행)
			
		}	
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}	
*/		
	}	// delete 종료 
	
	
	public void modify(final String bid, final String bname, final String btitle, final String bcontent) {  // 사용자가 입력한 3개의 값과 숨긴 bid 값을 받음
		
		String sql = "UPDATE mvc_board SET bname=?, btitle=?, bcontent=? WHERE bid=?";
		
		this.template.update(sql, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException {
				// TODO Auto-generated method stub
				pstmt.setString(1, bname);
				pstmt.setString(2, btitle);
				pstmt.setString(3, bcontent);
				pstmt.setInt(4, Integer.parseInt(bid));	
			}	
		});
		
		
/*		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = dataSource.getConnection();
			String sql = "UPDATE mvc_board SET bname=?, btitle=?, bcontent=? WHERE bid=?";  // 어느값이 들어올 지 모르므로 ? 처리함
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bname);
			pstmt.setString(2, btitle);
			pstmt.setString(3, bcontent);
			pstmt.setInt(4, Integer.parseInt(bid));		// integer로 변경해서 값을 넣어줌
			
			pstmt.executeUpdate();		// integer 반환할 수 있음, 실헹문
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
*/		
	} // modify 종료
	
	
	
	private void upHit(final String bid) {		// 조회수 조작 금지위해 비공개처리, 반환값 X, 매개변수 O(특정 글만 해당되므로)
	
		String sql = "UPDATE mvc_board SET bhit=bhit+1 WHERE bid=?"; 
		
		this.template.update(sql, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException {
				// TODO Auto-generated method stub
				
				pstmt.setInt(1, Integer.parseInt(bid));
			}
		});

/*
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = dataSource.getConnection();
			String sql = "UPDATE mvc_board SET bhit=bhit+1 WHERE bid=?";  // 어느값이 들어올 지 모르므로 ? 처리함, 누를때마다 씩 증가하게 +1함
			pstmt = conn.prepareStatement(sql);
		
			pstmt.setInt(1, Integer.parseInt(bid));		// integer로 변경해서 값을 넣어줌
			
			pstmt.executeUpdate();		// integer 반환할 수 있음, 실헹문
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
*/
	} //upHit 종료 (조회수 증가)
	
	
	
	public BDto replyView(String strbid) {  // 글 반환 select문, 글 1개만 반환하므로 BDto
		
		String sql = "SELECT * FROM mvc_board WHERE bid=" + strbid;
		
		return template.queryForObject(sql, new BeanPropertyRowMapper<BDto>(BDto.class));
/*		
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		BDto dto = null;			// 생성
		
		try {
			conn = dataSource.getConnection();
			String sql = "SELECT * FROM mvc_board WHERE bid=?"; // 어느 값이 올지 모르므로 ?처리
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(strbid));// ? 셋팅 (String으로 들어온 strbid를 integer로 형변환=> Wrapper class이용)
			
			rs = pstmt.executeQuery();		// 실헹문 (select문은 Query로 실행해야함)
			
			while (rs.next()) {			// 더이상 글이 없을때까지 반복해 내용을 뽑고 배열에 차례로 쌓임 (있으면 true, 없으면 false) 
				int bid = rs.getInt("bid");
				String bname = rs.getString("bname");
				String btitle = rs.getString("btitle");
				String bcontent = rs.getString("bcontent");
				Timestamp bdate = rs.getTimestamp("bdate");
				int bhit = rs.getInt("bhit");
				int bgroup = rs.getInt("bgroup");
				int bstep = rs.getInt("bstep");
				int bindent = rs.getInt("bindent");
				
				dto = new BDto(bid, bname, btitle, bcontent, bdate, bhit, bgroup, bstep, bindent);
			}
		}	
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (rs != null ) {
					rs.close();
				}
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dto;		
*/			
	} // replyView 종료
	
	
	
	public void reply(final String bid, final String bname, final String btitle, final String bcontent, final String bgroup, final String bstep, final String bindent) {  // 원글의 값과 댓글의 값(bgroup, bstep, bindent)을 다 받아야함
		
		this.replyShape(bgroup, bstep);   // 댓글 정렬기능 메소드 호출
		
		String sql = "INSERT INTO mvc_board(bid, bname, btitle, bcontent, bgroup, bstep, bindent) "
				+ "VALUES (mvc_board_seq.nextval, ?, ?, ?, ?, ?, ?)"; 
		
		this.template.update(sql, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException {
				// TODO Auto-generated method stub
				pstmt.setString(1, bname);
				pstmt.setString(2, btitle);
				pstmt.setString(3, bcontent);
				pstmt.setInt(4, Integer.parseInt(bgroup));	// 원글과 통일시켜야함
				pstmt.setInt(5, Integer.parseInt(bstep)+1);	// 원글에서 1개가 늘어나야 댓글이 되므로
				pstmt.setInt(6, Integer.parseInt(bindent)+1); 
				// 원글에서 가져온 bgroup, bstep, bindent
			}	
		});
		
/*		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = dataSource.getConnection();
			String sql = "INSERT INTO mvc_board(bid, bname, btitle, bcontent, bgroup, bstep, bindent) "
					+ "VALUES (mvc_board_seq.nextval, ?, ?, ?, ?, ?, ?)";  // bid는 시퀀스, bgroup은 원본 글의 값
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, bname);
			pstmt.setString(2, btitle);
			pstmt.setString(3, bcontent);
			pstmt.setInt(4, Integer.parseInt(bgroup));	// 원글과 통일시켜야함
			pstmt.setInt(5, Integer.parseInt(bstep)+1);	// 원글에서 1개가 늘어나야 댓글이 되므로
			pstmt.setInt(6, Integer.parseInt(bindent)+1); 
			// 원글에서 가져온 bgroup, bstep, bindent
			
			pstmt.executeUpdate();		// integer 반환할 수 있음, 실헹문
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
*/
	} // reply 종료	
	
	
	private void replyShape(final String strGroup, final String strStep) {		// 댓글 재정렬,  반환값 X, 매개변수 O(특정 글만 해당되므로)
	
		String sql = "UPDATE mvc_board SET bstep=bstep+1 WHERE bgroup=? and bstep > ?"; 
		
		this.template.update(sql, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement pstmt) throws SQLException {
				// TODO Auto-generated method stub
				pstmt.setInt(1, Integer.parseInt(strGroup));		// integer로 변경해서 값을 넣어줌
				pstmt.setInt(2, Integer.parseInt(strStep));
			}
		});		
		
/*		
		Connection conn = null;
		PreparedStatement pstmt = null;
		
		try {
			conn = dataSource.getConnection();
			String sql = "UPDATE mvc_board SET bstep=bstep+1 WHERE bgroup=? and bstep > ?"; 
			// 같은 Group의 글에 댓글이 달리면 작성한 댓글의 다음 댓글들은 bstep값이 1씩 증가해서 작성한 댓글이 중간에 끼어들어갈 수 있게 함
			pstmt = conn.prepareStatement(sql);
		
			pstmt.setInt(1, Integer.parseInt(strGroup));		// integer로 변경해서 값을 넣어줌
			pstmt.setInt(2, Integer.parseInt(strStep));
			
			pstmt.executeUpdate();		// integer 반환할 수 있음, 실헹문
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (pstmt != null) {
					pstmt.close();
				}
				if (conn != null) {
					conn.close();
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
*/
	} // replyShape 종료 (조회수 증가)
}
