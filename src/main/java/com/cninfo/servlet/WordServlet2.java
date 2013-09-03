package com.cninfo.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class WordServlet2 extends HttpServlet {
	private static final long serialVersionUID = -3362065005715783148L;

	public WordServlet2() {
		super();
	}
	
	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		

	}
	
	

	/* 
	 * url:add.wd/delete.wd
	 * post:{file:"",words:["word1","word2"]}
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
	}
	


	public void init() throws ServletException {
		
	}

}
