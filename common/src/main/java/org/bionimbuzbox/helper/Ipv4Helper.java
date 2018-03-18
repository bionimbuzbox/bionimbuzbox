package org.bionimbuzbox.helper;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class Ipv4Helper {
	public static final boolean isPrivateIPAddress(String ipAddress) {
		InetAddress ia = null;

        try {
            InetAddress ad = InetAddress.getByName(ipAddress);
            byte[] ip = ad.getAddress();
            ia = InetAddress.getByAddress(ip);
        } catch (UnknownHostException e) {

            e.printStackTrace();
        }

        return ia.isSiteLocalAddress();
	}
}
