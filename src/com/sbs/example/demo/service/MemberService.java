package com.sbs.example.demo.service;

import com.sbs.example.demo.dao.MemberDao;
import com.sbs.example.demo.dto.Member;
import com.sbs.example.demo.factory.Factory;

public class MemberService {
	private MemberDao memberDao;

	public MemberService() {
		memberDao = Factory.getMemberDao();
	}

	public Member getMemberByLoginIdAndLoginPw(String loginId, String loginPw) {
		return memberDao.getMemberByLoginIdAndLoginPw(loginId, loginPw);
	}

	public int join(String loginId, String loginPw, String name) {
		

		if (isUsedLoginId(loginId)) {
			return -1;
		}

		Member member = new Member(loginId, loginPw, name);
		return memberDao.save(member);
	}
	public boolean isUsedLoginId(String loginId) {
		Member oldMember = memberDao.getMemberByLoginId(loginId);
		if ( oldMember == null ) {
			return false;
		}
		return true;
	}
	public Member getMember(int id) {
		return memberDao.getMember(id);
	}

	public void makeAdminUserIfNotExists() {
		Member member = memberDao.getMemberByLoginId("admin");
		
		if (member == null) {
			join("admin", "admin", "관리자");
		}
	}
	//TODO : 게시물의 멤버 아이디와 같은 아이디의 멤버를 가져오게 하는 메서드. method 명이 다른지 자꼬 오류남. 해결하기.
	public Member getMemberByArticleId(int articleId) {
		return memberDao.getMemberByArticleId(articleId);
	}


	
}