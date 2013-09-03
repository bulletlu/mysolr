package com.cninfo.servlet;

import net.sf.json.JSONObject;

/**
 * @author lunianping
 *
 */
public class JSONResponse{
	
	private JSONObject response;	
	
	public JSONResponse(){
		response = new JSONObject();
		response.put("request", new JSONObject());
		response.put("response", new JSONObject());
	} 
	
	public void addRequestParam(String key,Object obj){
		this.response.getJSONObject("request").put(key, obj);
	}
	
	public void addResponseParam(String key,Object obj){
		this.response.getJSONObject("response").put(key, obj);
	}
	
	public JSONObject getResponseJSON(){
		return this.response;
	}
}
