package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private static final List<String> allowedMethods = Main.allowedMethods;
    private static final List<String> validPaths = Main.validPaths;
    private static final HashMap<String, HashMap<String, Handler>> HANDLERS = Main.handlers;
    private final ExecutorService threadpool;
    private final HashMap<String, HashMap<String, Handler>> handlers;

    public Server() {
        threadpool = Executors.newFixedThreadPool(64);
        handlers = HANDLERS;
    }

    public void start(int port) throws IOException {
        try (var serverSocket = new ServerSocket(port)) {
            while (true) {
                var socket = serverSocket.accept();
                threadpool.submit(new Client(socket));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void addHandler(String method, String path, Handler handler) {
        handlers
                .computeIfAbsent(method, k -> new HashMap<>())
                .computeIfAbsent(path, k -> handler);
    }

        }


