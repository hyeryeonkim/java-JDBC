package com.sbs.example.demo.controller;

import com.sbs.example.demo.dto.Member;
import com.sbs.example.demo.factory.Factory;
import com.sbs.example.demo.service.MemberService;

public class MemberController extends Controller {
	private MemberService memberService;

	public MemberController() {
		memberService = Factory.getMemberService();
	}

	public void doAction(Request reqeust) {
		if (reqeust.getActionName().equals("logout")) {
			actionLogout(reqeust);
		} else if (reqeust.getActionName().equals("login")) {
			actionLogin(reqeust);
		} else if (reqeust.getActionName().equals("whoami")) {
			actionWhoami(reqeust);
		} else if (reqeust.getActionName().equals("join")) {
			actionJoin(reqeust);
		}
	}

	private void actionJoin(Request reqeust) {
		System.out.println("== 회원가입 ==");
		if ( Factory.getSession().getLoginedMember() != null ) {
			System.out.println("[ 현재 접속 중으로 회원가입 할 수 없습니다. ]");
			return;
		}
		String loginId;
		String loginPw;
		String loginPwConfirm;
		String name;
		while ( true ) {
			System.out.printf("로그인 아이디 : ");
			loginId = Factory.getScanner().nextLine().trim();
			if ( memberService.isUsedLoginId(loginId)) {
				System.out.println("[ 중복되는 아이디 입니다. ]");
				continue;
			}
			if ( loginId.length() == 0 ) {
				System.out.println("[ 로그인 아이디를 입력바랍니다. ]");
				continue;
			}
			if ( loginId.length() < 2 ) {
				System.out.println("[ 로그인 아이디를 2자 이상 입력바랍니다. ]");
				continue;
			}
			
			break;
		}
		while ( true ) {
			boolean loginPwValid = true;
			while ( true ) {
				System.out.printf("로그인 비밀번호 : ");
				loginPw = Factory.getScanner().nextLine().trim();
				if ( loginPw.length() == 0 ) {
					System.out.println("[ 로그인 비밀번호를 입력바랍니다. ]");
					continue;
				}
				if ( loginPw.length() < 2 ) {
					System.out.println("[ 로그인 비밀번호를 2자 이상 입력바랍니다. ]");
					continue;
				}
				break;
			}
			while ( true ) {
				System.out.printf("로그인 비밀번호 확인 : ");
				loginPwConfirm = Factory.getScanner().nextLine().trim();
				if ( loginPwConfirm.length() == 0 ) {
					System.out.println("[ 로그인 비밀번호 확인을 입력바랍니다. ]");
					continue;
				}
				if ( loginPwConfirm.equals(loginPw) == false ) {
					System.out.println("[ 로그인 비밀번호와 비밀번호 확인이 일치하지 않습니다. 다시 입력바랍니다. ]");
					loginPwValid = false;
					break;
				}
				break;
			}
			if ( loginPwValid ) {
				break;
			}
		}
		while ( true ) {
			System.out.printf("이름 : ");
			name = Factory.getScanner().nextLine().trim();
			if ( name.length() == 0 ) {
				System.out.println("[ 이름을 입력바랍니다. ]");
				continue;
			}
			if ( name.length() < 2 ) {
				System.out.println("[ 이름을 2자 이상 입력바랍니다. ]");
				continue;
			}
			break;
		}
		int rs = memberService.join(loginId, loginPwConfirm, name);
		System.out.println("[ " + name + "님. 회원가입을 축하드립니다. ]");
	}

	private void actionWhoami(Request reqeust) {
		Member loginedMember = Factory.getSession().getLoginedMember();

		if (loginedMember == null) {
			System.out.println("나그네");
		} else {
			System.out.println(loginedMember.getName());
		}

	}

	private void actionLogin(Request reqeust) {
		System.out.println("== 로그인 시작 ==");
		if ( Factory.getSession().getLoginedMember() != null ) {
			System.out.println("[ 이미 접속 중입니다. ]");
			return;
		}
		System.out.printf("로그인 아이디 : ");
		String loginId = Factory.getScanner().nextLine().trim();

		System.out.printf("로그인 비번 : ");
		String loginPw = Factory.getScanner().nextLine().trim();

		Member member = memberService.getMemberByLoginIdAndLoginPw(loginId, loginPw);

		if (member == null) {
			System.out.println("[ 일치하는 회원이 없습니다. ]");
		} else {
			System.out.println(member.getName() + "님 환영합니다.");
			Factory.getSession().setLoginedMember(member);
		}
	}

	private void actionLogout(Request reqeust) {
		System.out.println(" == 로그아웃 ==");
		Member loginedMember = Factory.getSession().getLoginedMember();
		
		if (loginedMember != null) {
			Session session = Factory.getSession();
			System.out.println("[ 로그아웃 되었습니다. ]");
			session.setLoginedMember(null);
		}
		else {
			System.out.println("[ 현재 미접속 상태입니다. 로그인 바랍니다. ]");
		}

	}
}