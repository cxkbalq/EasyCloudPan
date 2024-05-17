package com.example.easycloudpan;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class server {

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(5); // 创建一个固定大小的线程池

        try (ServerSocket serverSocket = new ServerSocket(8081)) {
            System.out.println("服务器已启动，等待客户端连接...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("客户端连接成功：" + clientSocket);
                executor.execute(() -> {
                    try {
                        handleClientRequest(clientSocket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClientRequest(Socket clientSocket) throws IOException {
        try (InputStream inputStream = clientSocket.getInputStream();
             OutputStream outputStream = clientSocket.getOutputStream()) {
        }
    }
}