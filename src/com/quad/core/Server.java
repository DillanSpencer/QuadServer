package com.quad.core;

import javax.swing.JFrame;

import com.quad.net.GameServer;

public class Server {
	
	public static void main(String[] args) {
		JFrame f = new JFrame("Server Handler");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setVisible(true);
		f.setResizable(false);
		f.setSize(100, 100);
		GameServer server = new GameServer();
		server.start();
		
	}

}
