package com.quad.entity;

import java.net.InetAddress;

public class PlayerMP{
	
	public InetAddress ipAddress;
	public int port;
	
	private String username;
	private int x, y;

	public PlayerMP(String username, int x, int y, InetAddress ipAddress, int port) {
		this.username = username;
		this.ipAddress = ipAddress;
		this.port = port;
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String username) {
		this.username = username;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	

}
