package com.mirim.springjdbctemp.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.mirim.springjdbctemp.command.BCommand;
import com.mirim.springjdbctemp.command.BContentCommand;
import com.mirim.springjdbctemp.command.BDeleteCommand;
import com.mirim.springjdbctemp.command.BListCommand;
import com.mirim.springjdbctemp.command.BModifyCommand;
import com.mirim.springjdbctemp.command.BReplyCommand;
import com.mirim.springjdbctemp.command.BReplyViewCommand;
import com.mirim.springjdbctemp.command.BWriteCommand;
import com.mirim.springjdbctemp.util.Constant;

@Controller
public class BController {			//command만 호출
	
	BCommand command;
	
	private JdbcTemplate template;
	// 요청이 들어와 실행되면 JdbcTemplate으로 객체 선언 => 초기화 필요
	
	@Autowired		// controller가 실행되면 자동으로 실행되게끔 (묶음)
	public void setTemplate(JdbcTemplate template) {  // setter 생성, 외부(servlet-context.xml)에서 설정한 bean이 자동으로 들어옴
		this.template = template;		// 초기화
		Constant.template = this.template;	
	}	

	@RequestMapping(value="/write_form")
	public String write_form() {
		
		return "write_form";
	}
	
	@RequestMapping(value="/write")
	public String write(HttpServletRequest request, Model model) {
		
		model.addAttribute("request", request);		// request 객체를 modal 담아서 command에 보내버림		
		
		command = new BWriteCommand();
		command.execute(model);
		
		return "redirect:list";	// redirect는 list 요청을 다시 돌려서 보여줌(reload)
	}
	
	@RequestMapping(value="/list")
	public String list(Model model) {		// 새로운 값을 넣어줄건 없으므로 model만 있으면 됨
		
		command = new BListCommand();
		command.execute(model);		// model에 list(ArrayList<BDto> dtos)가 탑재 
		
		return "list";
	}
	
	@RequestMapping(value="/content_view")
	public String content_view(HttpServletRequest request, Model model) {
		
		model.addAttribute("request", request);		// request 객체를 modal 담아서 command에 보내버림		
		
		command = new BContentCommand();
		command.execute(model);		// 실행하면 model에 dto란 이름으로 탑재 완료
		
		return "content_view";
	}
	
	@RequestMapping (value="/delete")
	public String delete(HttpServletRequest request, Model model) {
		
		model.addAttribute("request", request);		// request 객체를 modal 담아서 command에 보내버림		
		
		command = new BDeleteCommand();
		command.execute(model);		// 실행하면 model에 dao란 이름으로 탑재 완료
		
		
		return "redirect:list";		// reload된 list를 불러옴
	}
	
	
	@RequestMapping (value="/modify")
	public String modify(HttpServletRequest request, Model model) { // 매개변수 받아야함
		
		model.addAttribute("request", request);		// request 객체를 modal 담아서 command에 보내버림		
		
		command = new BModifyCommand();
		command.execute(model);		// 실행하면 model에 dao란 이름으로 탑재 완료
		
		return "redirect:list";		// list로 돌아가서 수정된 내용 확인 가능 (content_view로 돌아가도 됨)
	}
	
	
	@RequestMapping (value="/reply_view")
	public String reply_view(HttpServletRequest request, Model model) {
		
		model.addAttribute("request", request);		// request 객체를 modal 담아서 command에 보내버림		
		
		command = new BReplyViewCommand();
		command.execute(model);		// 실행하면 model에 dto란 이름으로 탑재 완료
		
		return "reply_view";
	}
	
	
	
	@RequestMapping (value="/reply")
	public String reply(HttpServletRequest request, Model model) {
		
		model.addAttribute("request", request);		// request 객체를 modal 담아서 command에 보내버림		
		
		command = new BReplyCommand();
		command.execute(model);
		
		
		return "redirect:list";
	}
}
