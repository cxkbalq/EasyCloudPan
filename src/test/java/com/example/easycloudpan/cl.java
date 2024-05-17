package com.example.easycloudpan;

import java.io.IOException;
import java.net.Socket;

public class cl {
    public static void main(String[] args) {
        try {
            Socket socket=new Socket("127.0.0.1",8081);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
