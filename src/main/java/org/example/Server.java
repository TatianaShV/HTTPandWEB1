package org.example;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final static int PORT = 9999;

   private final ExecutorService threadpool = Executors.newFixedThreadPool(64);
    public void start() throws IOException {
        while (true){
        try ( var serverSocket = new ServerSocket(PORT)) {
             var socket = serverSocket.accept();
            var thread = new Handlers(socket);
            threadpool.submit((Runnable) thread);
        }
        }

    }
}
