package com.SHGroup.FixedSocket;

import java.net.InetAddress;
import java.net.Socket;

public abstract class SocketEvent {
	public abstract void ReceiveEvent(Socket s, InetAddress ip,Object data);
	public abstract void ReceiveByteArrayEvent(Socket s, InetAddress ip, byte[] b);
	public abstract void ReceiveIntEvent(Socket s, InetAddress ip, int i);
	public abstract void ReceiveLongEvent(Socket s, InetAddress ip, long l);
	public abstract void ReceiveStringEvent(Socket s, InetAddress ip, String n);
	public abstract void onClose();
	public abstract void onStart();
}
