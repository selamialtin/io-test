/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.supercommlistener;

import gnu.io.SerialPort;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import javax.naming.directory.InvalidAttributesException;

/**
 *
 * @author selami
 */
public class Main {

    public static final String PARAM_IP = "ip";
    public static final String PARAM_MOD = "mod";
    public static final String PARAM_PORT = "port";
    public static final String PARAM_SERIAL = "serial";
    public static final String PARAM_BAUD = "baud";
    public static final String PARAM_DATABIT = "databit";
    public static final String PARAM_STOPBIT = "stopbit";
    public static final String PARAM_PARITY = "parity";
    public static final String PARAM_DTR = "dtr";
    public static final String PARAM_RTS = "rts";
    
    
    static final Pattern KEY_VAL_PATTERN = Pattern.compile("(\\w+)=\"*((?<=\")[^\"]+(?=\")|([^\\s]+))\"*");

    static final String DEFAULT_IP = "10.10.10.25";
    static final String DEFAULT_SERIAL = "/dev/ttyS0";
    static final String DEFAULT_MOD = "serial";
    static int DEFAULT_PORT = 4001;
    static int DEFAULT_BAUDRATE = 115200;
    
    private static Map<String, String> params = new HashMap<String, String>();
    
    
    static {
        params.put(PARAM_IP, DEFAULT_IP);
        params.put(PARAM_MOD, DEFAULT_MOD);
        params.put(PARAM_PORT, DEFAULT_PORT+"");
        params.put(PARAM_SERIAL, DEFAULT_SERIAL);
        params.put(PARAM_BAUD, DEFAULT_BAUDRATE+"");
        params.put(PARAM_DATABIT, SerialPort.DATABITS_8+"");
        params.put(PARAM_STOPBIT, SerialPort.STOPBITS_1+"");
        params.put(PARAM_PARITY, SerialPort.PARITY_NONE+"");
        params.put(PARAM_DTR, "false");
        params.put(PARAM_RTS, "false");
    }
    

    private static void setAttribute(String param) {
        Matcher m = KEY_VAL_PATTERN.matcher(param);
        while (m.find()) {
            String key = m.group(1);
            String value = m.group(2);
            params.put(key, value);
        }
    }

    public static void main(String[] args) throws Exception {

        if (args != null) {
            Stream.of(args).forEach(t -> {
                setAttribute(t);
            });
        }
        System.out.println("test starting, parameters:");
        System.out.println("mod = " + params.get(PARAM_MOD));
        System.out.println("ip = " + params.get(PARAM_IP));
        System.out.println("port = " + params.get(PARAM_PORT));
        System.out.println("serial = " + params.get(PARAM_SERIAL));

        switch (params.get(PARAM_MOD)) {
            case "serial":
                new Thread(new Serial(params)).start();
                break;
            case "server":
                new Thread(new Server(Integer.parseInt(params.get(PARAM_PORT)))).start();
                break;
            case "client":
                new Thread(new Client(params.get(PARAM_IP), Integer.parseInt(params.get(PARAM_PORT)))).start();
                break;
            default:
                throw new InvalidAttributesException("mod " + params.get(PARAM_MOD) + " is not valid");
        }

        Thread.sleep(Long.MAX_VALUE);
    }
}
