package com.files.server;

import java.io.File;

public class Launcher {
    public static final void main(String[] args) throws Exception {
        File store = new File(args[2]);
        store.mkdirs();
        Client server = new Client(args[0], Integer.parseInt(args[1]), store);
        server.start();
    }
}