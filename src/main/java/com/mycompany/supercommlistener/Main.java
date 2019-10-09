/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.supercommlistener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.naming.directory.InvalidAttributesException;

/**
 *
 * @author selami
 */
public class Main {

    static final Pattern p = Pattern.compile("(\\w+)=\"*((?<=\")[^\"]+(?=\")|([^\\s]+))\"*");

    static String ip = "10.10.10.25";
    static String comm = "/dev/ttyS0";
    static String mod = "serial";
    static int port = 4001;

    private static void setAttribute(String param) {
        Matcher m = p.matcher(param);
        while (m.find()) {
            String key = m.group(1);
            String value = m.group(2);
            if (key.equals("mod")) {
                mod = value;
            } else if (key.equalsIgnoreCase("ip")) {
                ip = value;
            } else if (key.equalsIgnoreCase("port")) {
                port = Integer.parseInt(value);
            } else if (key.equalsIgnoreCase("serial")) {
                comm = value;
            }
        }
    }

    public static void main(String[] args) throws Exception {

        if (args != null) {
            Stream.of(args).forEach(t -> {
                setAttribute(t);
            });
        }
        System.out.println("test starting, parameters:");
        System.out.println("mod = " + mod);
        System.out.println("ip = " + ip);
        System.out.println("port = " + port);
        System.out.println("serial = " + comm);

        switch (mod) {
            case "serial":
                new Thread(new Serial(comm)).start();
                break;
            case "server":
                new Thread(new Server(4001)).start();
                break;
            case "client":
                new Thread(new Client(ip, port)).start();
                break;
            default:
                throw new InvalidAttributesException("mod " + mod + " is not valid");
        }

        Thread.sleep(Long.MAX_VALUE);
    }
}
