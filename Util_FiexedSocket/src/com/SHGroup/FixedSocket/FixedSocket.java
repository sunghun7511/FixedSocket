package com.SHGroup.FixedSocket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class FixedSocket extends Socket {
	private Timer t;
	private long lastGet = System.currentTimeMillis();
	private boolean isClose = false;
	private SocketEvent e;
	private Thread r;

	public FixedSocket(String ip, int port, SocketEvent e) throws IOException {
		super(ip, port);
		this.e = e;
		t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				if ((System.currentTimeMillis() - lastGet) <= 500l) {
					try {
						sendEmpty();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
					try {
						close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}, 80l, 80l);
		r = new Thread() {
			@Override
			public void run() {
				while (!isClose) {
					try {
						DataInputStream dis = new DataInputStream(
								getInputStream());
						DataOutputStream dos = new DataOutputStream(
								getOutputStream());
						byte r = dis.readByte();
						switch (r) {
						case 0:
							lastGet = System.currentTimeMillis();
							break;
						case 1:
							dos.writeByte(10);
							int length = dis.readInt();
							byte[] b = new byte[length];
							dos.writeByte(10);
							readFully(dis, b, 0, length);
							if (FixedSocket.this.e != null)
								FixedSocket.this.e.ReceiveByteArrayEvent(
										FixedSocket.this, getInetAddress(), b);
							break;
						case 2:
							dos.writeByte(10);
							String n = dis.readUTF();
							if (FixedSocket.this.e != null)
								FixedSocket.this.e.ReceiveStringEvent(
										FixedSocket.this, getInetAddress(), n);
							break;
						case 3:
							dos.writeByte(10);
							long l = dis.readLong();
							if (FixedSocket.this.e != null)
								FixedSocket.this.e.ReceiveLongEvent(
										FixedSocket.this, getInetAddress(), l);
							break;
						case 4:
							dos.writeByte(10);
							int i = dis.readInt();
							if (FixedSocket.this.e != null)
								FixedSocket.this.e.ReceiveIntEvent(
										FixedSocket.this, getInetAddress(), i);
							break;
						case 9:
							close();
							break;
						default:
							break;
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		};
		r.start();
		if (e != null)
			e.onStart();
	}

	public void startSetup(SocketEvent e) {
		this.e = e;
		t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				if ((System.currentTimeMillis() - lastGet) <= 500l) {
					try {
						sendEmpty();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				} else {
					try {
						close();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		}, 80l, 80l);
		r = new Thread() {
			@Override
			public void run() {
				while (!isClose) {
					try {
						DataInputStream dis = new DataInputStream(
								getInputStream());
						DataOutputStream dos = new DataOutputStream(
								getOutputStream());
						byte r = dis.readByte();
						switch (r) {
						case 0:
							lastGet = System.currentTimeMillis();
							break;
						case 1:
							dos.writeByte(10);
							int length = dis.readInt();
							byte[] b = new byte[length];
							dos.writeByte(10);
							readFully(dis, b, 0, length);
							if (FixedSocket.this.e != null)
								FixedSocket.this.e.ReceiveByteArrayEvent(
										FixedSocket.this, getInetAddress(), b);
							break;
						case 2:
							dos.writeByte(10);
							String n = dis.readUTF();
							if (FixedSocket.this.e != null)
								FixedSocket.this.e.ReceiveStringEvent(
										FixedSocket.this, getInetAddress(), n);
							break;
						case 3:
							dos.writeByte(10);
							long l = dis.readLong();
							if (FixedSocket.this.e != null)
								FixedSocket.this.e.ReceiveLongEvent(
										FixedSocket.this, getInetAddress(), l);
							break;
						case 4:
							dos.writeByte(10);
							int i = dis.readInt();
							if (FixedSocket.this.e != null)
								FixedSocket.this.e.ReceiveIntEvent(
										FixedSocket.this, getInetAddress(), i);
							break;
						case 9:
							close();
							break;
						default:
							break;
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		};
		r.start();
		if (e != null)
			e.onStart();
	}

	private final int readFully(InputStream in, byte b[], int off, int len)
			throws IOException {
		if (len < 0)
			throw new IndexOutOfBoundsException();
		int n = 0;
		while (n < len) {
			int count = in.read(b, off + n, len - n);
			if (count < 0)
				break;
			n += count;
		}
		return n;
	}

	@Override
	public void close() throws IOException {
		if (isClose) {
			throw new IOException("Socket is Already Close!");
		}
		lastGet = System.currentTimeMillis();
		try {
			DataOutputStream dos = new DataOutputStream(getOutputStream());
			dos.writeByte(9);
			dos.flush();
			dos.close();
		} catch (Exception ex) {}
		t.cancel();
		super.close();
		if (e != null)
			e.onClose();
		isClose = true;
	}

	public void sendEmpty() throws IOException {
		if (isClose) {
			throw new IOException("Socket is Already Close!");
		}
		DataOutputStream dos = new DataOutputStream(getOutputStream());
		dos.writeByte(0);
		dos.flush();
		dos.close();
	}

	public void send(String n) throws IOException {
		if (isClose) {
			throw new IOException("Socket is Already Close!");
		}
		DataOutputStream dos = new DataOutputStream(getOutputStream());
		DataInputStream dis = new DataInputStream(getInputStream());
		dos.writeByte(2);
		if (dis.readByte() != (byte) 10) {
			return;
		}
		dos.writeUTF(n);
		dos.flush();
		dos.close();
	}

	public void send(int i) throws IOException {
		if (isClose) {
			throw new IOException("Socket is Already Close!");
		}
		DataOutputStream dos = new DataOutputStream(getOutputStream());
		DataInputStream dis = new DataInputStream(getInputStream());
		dos.writeByte(4);
		if (dis.readByte() != (byte) 10) {
			return;
		}
		dos.writeInt(i);
		dos.flush();
		dos.close();
	}

	public void send(long l) throws IOException {
		if (isClose) {
			throw new IOException("Socket is Already Close!");
		}
		DataOutputStream dos = new DataOutputStream(getOutputStream());
		DataInputStream dis = new DataInputStream(getInputStream());
		dos.writeByte(3);
		if (dis.readByte() != (byte) 10) {
			return;
		}
		dos.writeLong(l);
		dos.flush();
		dos.close();
	}

	public void send(byte[] b) throws IOException {
		if (isClose) {
			throw new IOException("Socket is Already Close!");
		}
		DataOutputStream dos = new DataOutputStream(getOutputStream());
		DataInputStream dis = new DataInputStream(getInputStream());
		dos.writeByte(1);
		if (dis.readByte() != (byte) 10) {
			return;
		}
		dos.writeInt(b.length);
		if (dis.readByte() != (byte) 10) {
			return;
		}
		dos.write(b);
		dos.flush();
		dos.close();
	}
}
