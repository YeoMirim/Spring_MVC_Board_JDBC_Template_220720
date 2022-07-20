package com.mirim.springjdbctemp.util;

import org.springframework.jdbc.core.JdbcTemplate;

public class Constant {			 
	public static JdbcTemplate template;		// 객체를 전역변수로 선언 => 다른 클래스들에서 접근 가능
}
