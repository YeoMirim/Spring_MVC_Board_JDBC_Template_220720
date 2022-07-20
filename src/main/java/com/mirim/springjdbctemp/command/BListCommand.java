package com.mirim.springjdbctemp.command;

import java.util.ArrayList;

import org.springframework.ui.Model;

import com.mirim.springjdbctemp.dao.BDao;
import com.mirim.springjdbctemp.dto.BDto;

public class BListCommand implements BCommand {

	@Override
	public void execute(Model model) {
		// TODO Auto-generated method stub
		BDao dao = new BDao();
		
		ArrayList<BDto> btos = dao.list();
		
		model.addAttribute("list", btos);
	}

}
