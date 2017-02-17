package com.SHGroup.FixedSocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class FixedServerSocket extends ServerSocket {
	private Timer t;
	private HashMap<FixedSocket, SocketData> sData = new HashMap<FixedSocket, SocketData>();
	private boolean isClose = false;
	private SocketEvent e;

	public FixedServerSocket(int port, SocketEvent e) throws IOException {
		super(port);
		this.e = e;
		e.onStart();
		t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				for (Map.Entry<FixedSocket, SocketData> e : sData.entrySet()) {
					if ((System.currentTimeMillis() - e.getValue().lastGet) <= 500l) {
						try {
							e.getKey().sendEmpty();
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
			}
		}, 80l, 80l);
	}

	@Override
	public void close() throws IOException {
		if (isClose) {
			throw new IOException("ServerSocket is Already Close!");
		}
		for (Map.Entry<FixedSocket, SocketData> e : sData.entrySet()) {
			e.getKey().close();
		}
		t.cancel();
		super.close();
		if (e != null)
			e.onClose();
		isClose = true;
		super.close();
	}

	@Override
	public FixedSocket accept() throws IOException {
		Socket s = super.accept();
		FixedSocket fs = (FixedSocket) s;
		fs.startSetup(e);
		return fs;
	}
	private class SocketData {
		long lastGet = System.currentTimeMillis();
	}
}
