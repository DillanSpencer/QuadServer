package com.quad.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

import com.quad.net.packets.Packet;
import com.quad.net.packets.Packet.PacketTypes;
import com.quad.net.packets.Packet00Login;
import com.quad.net.packets.Packet01Disconnect;
import com.quad.net.packets.Packet02Move;
import com.quad.net.packets.Packet10Start;
import com.quad.net.users.User;

public class GameServer extends Thread{
	
	private DatagramSocket socket;
	private List<User> connectedPlayers = new ArrayList<User>();

	public GameServer() {
		try {
			this.socket = new DatagramSocket(1331);
		} catch (SocketException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		while (true) {
			byte[] data = new byte[1024];
			DatagramPacket packet = new DatagramPacket(data, data.length);
			try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
		}
	}

	private void parsePacket(byte[] data, InetAddress address, int port) {
		String message = new String(data).trim();
		PacketTypes type = Packet.lookupPacket(message.substring(0, 2));
		Packet packet = null;
		switch (type) {
		default:
		case INVALID:
			break;
		case LOGIN:
			packet = new Packet00Login(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "] "
					+ ((Packet00Login) packet).getUsername() + " has connected...");
			User user = new User(((Packet00Login)packet).getUsername() , address, port);
			this.addConnection(user, (Packet00Login) packet);
			break;
		case DISCONNECT:
			packet = new Packet01Disconnect(data);
			System.out.println("[" + address.getHostAddress() + ":" + port + "] "
					+ ((Packet01Disconnect) packet).getUsername() + " has left...");
			this.removeConnection((Packet01Disconnect) packet);
			break;
        case MOVE:
            packet = new Packet02Move(data);
            this.handleMove(((Packet02Move) packet));
        case START:
        	packet = new Packet10Start(data);
        	sendDataToAllClients(packet.getData());
		}
	}

	public void addConnection(User user, Packet00Login packet) {
		boolean alreadyConnected = false;
		for (User p : this.connectedPlayers) {
			if (user.getUserName().equalsIgnoreCase(p.getUserName())) {
				if (p.ipAddress == null) {
					p.ipAddress = user.ipAddress;
				}
				if (p.port == -1) {
					p.port = user.port;
				}
				alreadyConnected = true;
			} else {
				// relay to the current connected player that there is a new
				// player
				sendData(packet.getData(), p.ipAddress, p.port);

				// relay to the new player that the currently connect player
				// exists
				packet = new Packet00Login(p.getUserName());
				sendData(packet.getData(), user.ipAddress, user.port);
			}
		}
		if (!alreadyConnected) {
			this.connectedPlayers.add(user);
		}
	}

	public void removeConnection(Packet01Disconnect packet) {
		this.connectedPlayers.remove(getUserIndex(packet.getUsername()));
		packet.writeData(this);
	}

	public User getUser(String username) {
		for (User player : this.connectedPlayers) {
			if (player.getUserName().equals(username)) {
				return player;
			}
		}
		return null;
	}

	public int getUserIndex(String username) {
		int index = 0;
		for (User player : this.connectedPlayers) {
			if (player.getUserName().equals(username)) {
				break;
			}
			index++;
		}
		return index;
	}

	public void sendData(byte[] data, InetAddress ipAddress, int port) {
		DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
		try {
			this.socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void sendDataToAllClients(byte[] data) {
		for (User p : connectedPlayers) {
			sendData(data, p.ipAddress, p.port);
		}
	}

	private void handleMove(Packet02Move packet) {
		
	}

}
