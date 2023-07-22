package top.smallway.lightsaberassistant.FTP;

import java.io.IOException;
import java.net.Socket;

public class FTPServerConnectionTester {

    public static boolean isFTPServerReachable(String IP) {
        String serverIP = IP;
        int serverPort = 8023;

        try {
            // 创建socket并尝试连接到FTP服务器
            Socket socket = new Socket(serverIP, serverPort);
            // 关闭socket
            socket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            // 如果连接失败
            return false;

        }
    }
}