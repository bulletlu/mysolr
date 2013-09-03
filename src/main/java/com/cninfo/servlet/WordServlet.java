package com.cninfo.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.wltea.analyzer.dic.Dictionary;

public class WordServlet extends HttpServlet {
	private static final long serialVersionUID = -3362065005715783148L;

	public WordServlet() {
		super();
	}
	
	public void destroy() {
		super.destroy();
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getRequestURI().replaceAll("^.*/", "").replaceAll("\\.wd", "");
		JSONObject res = null;
		if ("add".equals(action)) {
			res = processAddWords(request,response);
		} else if ("delete".equals(action)) {
			res = processDeleteWords(request,response);
		} else if ("iscontain".equals(action)) {
			res = processIsContainWord(request,response);
		}else {
			JSONResponse cc = new JSONResponse();
			cc.addResponseParam("status", "400");
			cc.addResponseParam("msg", action+" is not defined");
			res = cc.getResponseJSON();
		}
		
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(res.toString());
		out.flush();
		out.close();

	}
	
	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 */
	private JSONObject processAddWords(HttpServletRequest request, HttpServletResponse response) throws ServletException{
		String word = request.getParameter("wd");
		String file = request.getParameter("fi");
		JSONResponse res = new JSONResponse();
		res.addRequestParam("action", "add");
		file = file==null?"default.dic":file;
		res.addRequestParam("file", file);
		
		if(word == null){
			res.addResponseParam("status", "400");
			res.addResponseParam("msg", "Words(wd) parameter is null");
			return res.getResponseJSON();
		}		
		
		String[] wordArray = word.split("[;,]"); 
		Set<String> set = new HashSet<String>();
		JSONArray arr = new JSONArray();
		for(int i=0;i<wordArray.length;i++){
			set.add(wordArray[i]);
		}
		
		arr.addAll(set);
		res.addRequestParam("words", arr);
		
		try {
			this.addWordsToDictionary(set, file);
			res.addResponseParam("status", "200");
			res.addResponseParam("msg", "Add words successful");
		} catch (IOException e) {
			e.printStackTrace();
			res.addResponseParam("status", "400");
			res.addResponseParam("msg", "Unknow error");
		}
		return res.getResponseJSON();
	}
	
	/**
	 * @param request
	 * @param response
	 * @return
	 * @throws ServletException
	 */
	private JSONObject processIsContainWord(HttpServletRequest request, HttpServletResponse response) throws ServletException{
		String word = request.getParameter("wd");
		JSONResponse res = new JSONResponse();
		res.addRequestParam("action", "iscontain");
		
		if(word == null){
			res.addResponseParam("status", "400");
			res.addResponseParam("msg", "Words(wd) parameter is null");
			return res.getResponseJSON();
		}		
		
		String[] wordArray = word.split("[;,]"); 
		Set<String> set = new HashSet<String>();
		JSONArray arr = new JSONArray();
		for(int i=0;i<wordArray.length;i++){
			set.add(wordArray[i]);
		}
		
		arr.addAll(set);
		res.addRequestParam("words", arr);
		
		Map<String,Boolean> map = this.isContainWord(set);
		res.addResponseParam("status", "200");
		res.addResponseParam("msg", "successful");
		JSONArray array = new JSONArray();
		for(String wd : map.keySet()){
			boolean bo = map.get(wd);
			JSONObject obj = new JSONObject();
			obj.put("word", wd);
			obj.put("iscontain", bo?1:0);
			array.add(obj);
		}
		res.addResponseParam("result", array);
		return res.getResponseJSON();
	}
	
	/**
	 * @param request
	 * @param response
	 * @return
	 */
	private JSONObject processDeleteWords(HttpServletRequest request, HttpServletResponse response){
		String word = request.getParameter("wd");
		String file = request.getParameter("fi");
		JSONResponse res = new JSONResponse();
		res.addRequestParam("action", "delete");
		file = file==null?"default.dic":file;
		res.addRequestParam("file", file);
		
		if(word == null){
			res.addResponseParam("status", "400");
			res.addResponseParam("msg", "Words(wd) parameter is null");
			return res.getResponseJSON();
		}		
		
		String[] wordArray = word.split("[;,]"); 
		Set<String> set = new HashSet<String>();
		JSONArray arr = new JSONArray();
		for(int i=0;i<wordArray.length;i++){
			set.add(wordArray[i]);
		}
		
		arr.addAll(set);
		res.addRequestParam("words", arr);
		
		try {
			this.deleteExtWordsDictionary(set, file);
			res.addResponseParam("status", "200");
			res.addResponseParam("msg", "Delete words successful");
		} catch (IOException e) {
			e.printStackTrace();
			res.addResponseParam("status", "400");
			res.addResponseParam("msg", "Unknow error");
		}
		return res.getResponseJSON();
	}

	/* 
	 * url:add.wd/delete.wd
	 * post:{file:"",words:["word1","word2"]}
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String action = request.getRequestURI().replaceAll("^.*/", "").replaceAll("\\.wd", "");
		InputStream in = request.getInputStream();
		BufferedReader bin = new BufferedReader(new InputStreamReader(in,"UTF-8"));
		StringBuilder buf = new StringBuilder();
		String s = null;
		while( (s = bin.readLine()) != null){
			buf.append(s);
		}
		
		JSONObject json = JSONObject.fromObject(buf.toString());
		JSONObject res = null;
		if("add".equals(action)){
			res = this.processAddWords(json);
		} else if("delete".equals(action)){
			res = this.processDeleteWords(json);
		} else if("iscontain".equals(action)){
			res = this.processIsContainWord(json);
		} else {
			JSONResponse cc = new JSONResponse();
			cc.addResponseParam("status", "400");
			cc.addResponseParam("msg", action+" is not defined");
			res = cc.getResponseJSON();
		}
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		
		out.write(res.toString());
		out.flush();
		out.close();
	}
	
	/**
	 * @param json
	 * @return
	 */
	private JSONObject processAddWords(JSONObject json){
		JSONResponse response = new JSONResponse();
		String file = this.getJSONKey(json, "file", "default.dic");
		Collection<String> set = this.getJSONWords(json, "words");
		response.addRequestParam("action", "add");
		response.addRequestParam("file", file);
		response.addRequestParam("words", json.getJSONArray("words"));
		try {
			this.addWordsToDictionary(set, file);
			response.addResponseParam("status", "200");
			response.addResponseParam("msg", "Add words successful");
		} catch (IOException e) {
			e.printStackTrace();
			response.addResponseParam("status", "400");
			response.addResponseParam("msg", "Unknow error");
		}
		return response.getResponseJSON();
	}
	
	/**
	 * @param json
	 * @return
	 */
	private JSONObject processDeleteWords(JSONObject json){
		JSONResponse response = new JSONResponse();
		String file = this.getJSONKey(json, "file", "default.dic");
		Collection<String> set = this.getJSONWords(json, "words");
		response.addRequestParam("action", "add");
		response.addRequestParam("file", file);
		response.addRequestParam("words", json.getJSONArray("words"));
		try {
			this.deleteExtWordsDictionary(set, file);
			response.addResponseParam("status", "200");
			response.addResponseParam("msg", "Delete words successful");
		} catch (IOException e) {
			e.printStackTrace();
			response.addResponseParam("status", "400");
			response.addResponseParam("msg", "Unknow error");
		}
		return response.getResponseJSON();
	}
	
	/**
	 * @param json
	 * @return
	 */
	private JSONObject processIsContainWord(JSONObject json){
		JSONResponse response = new JSONResponse();
		Collection<String> set = this.getJSONWords(json, "words");
		response.addRequestParam("action", "iscontain");
		response.addRequestParam("words", json.getJSONArray("words"));
		
		Map<String,Boolean> map = this.isContainWord(set);
		response.addResponseParam("status", "200");
		response.addResponseParam("msg", "successful");

		JSONArray array = new JSONArray();
		for(String wd : map.keySet()){
			boolean bo = map.get(wd);
			JSONObject obj = new JSONObject();
			obj.put("word", wd);
			obj.put("iscontain", bo?1:0);
			array.add(obj);
		}
		response.addResponseParam("result", array);
		return response.getResponseJSON();
	}
	
	/**
	 * @param json
	 * @param key
	 * @param defaultVal
	 * @return
	 */
	private String getJSONKey(JSONObject json,String key,String defaultVal){
		String value = defaultVal;
		try{
			value = json.getString(key);
		}catch(Exception e){
			
		}
		return value;
	}
	
	/**
	 * @param json
	 * @param key
	 * @return
	 */
	private Collection<String> getJSONWords(JSONObject json,String key){
		Set<String> set = null;
		try{
			set = new HashSet<String>();
			JSONArray words = json.getJSONArray(key);
			for(int i=0;i<words.size();i++){
				String word = words.getString(i);
				set.add(word);
			}
		}catch(Exception e){
			throw new IllegalArgumentException("JSON中不存在"+key);
		}
		return set;
	}
	
	/**
	 * @param coll
	 * @param file
	 * @throws IOException
	 */
	private void addWordsToDictionary(Collection<String> coll,String file) throws IOException{
		Dictionary dic = Dictionary.getSingleton();
		dic.addWords(coll);
		dic.saveToExtDictionary(coll, file);
	}
	
	/**
	 * @param coll
	 * @param file
	 * @throws IOException
	 */
	private void deleteExtWordsDictionary(Collection<String> coll,String file) throws IOException{
		Dictionary dic = Dictionary.getSingleton();
		dic.disableWords(coll);
		dic.deleteExtDictionaryWords(coll, file);
	}
	
	/**
	 * @param words
	 * @return
	 */
	private Map<String,Boolean> isContainWord(Collection<String> words){
		Dictionary dic = Dictionary.getSingleton();
		Map<String,Boolean> map = new HashMap<String,Boolean>();
		
		for(String word:words){
			map.put(word, dic.matchInMainDict(word.trim().toLowerCase().toCharArray()).isMatch());
		}
		return map;
	}

	public void init() throws ServletException {
		
	}

}
