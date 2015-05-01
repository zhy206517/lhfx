/*
 * Copyright 2011 Sina.
 *
 * Licensed under the Apache License and Weibo License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.open.weibo.com
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.weibo.net;


/**
 * An AcessToken class contains accesstoken and tokensecret.Child class of com.weibo.net.Token.
 * 
 * @author  (luopeng@staff.sina.com.cn zhangjie2@staff.sina.com.cn 官方微博：WBSDK  http://weibo.com/u/2791136085)
 */
public class AccessToken extends Token {
	/**
	 * userid 为非新浪官方提供的sdk中的变量
	 */
	private String userid;
	
	public AccessToken(String rlt){
		super(rlt);
		responseStr = rlt.split("&");
		userid =  getParameter("user_id");
	}
	
	public AccessToken(String token , String secret){
		super(token, secret);
	}

	//
	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	
}