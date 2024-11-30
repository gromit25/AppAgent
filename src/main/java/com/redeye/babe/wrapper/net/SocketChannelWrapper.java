package com.redeye.babe.wrapper.net;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;

public class SocketChannelWrapper {

	public static SocketChannel open() throws IOException {
		
		System.out.println("#########################################");
		System.out.println("### USING DEFAULT SOCKETCHANNEL ");
		System.out.println("#########################################");
		
		return SocketChannel.open();
	}
	
	public static SocketChannel open(SocketAddress remote) throws IOException {

		System.out.println("#########################################");
		System.out.println("### SOCKETCHANNEL:" + remote.toString());
		System.out.println("#########################################");
		
		return SocketChannel.open(remote);
	}

}
