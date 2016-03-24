package com.files.server;

import java.io.*;
import java.net.Socket;

/**
 * Created by azyubenko on 23.03.16.
 */
public class Client {
    private final int port;
    private final String hostname;
    private File filesStore;

    public Client(String host, int port, File filesStore) {
        this.hostname = host;
        this.port = port;
        this.filesStore = filesStore;
    }

    public void start() {
        Socket s;
        try {
            s = new Socket(hostname, port);
            System.out.println("Connected to " + hostname + ":" + port);
        } catch (IOException e) {
            System.out.println(e);
            e.printStackTrace();
            return;
        }

        String input = null;
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        try {
            DataOutputStream out = new DataOutputStream(s.getOutputStream());
            new Thread(new SocketProcessor(s, filesStore)).start();

            while ((input = in.readLine()) != null) {
                out.writeUTF(input);
            }
        } catch (Exception e) {
            System.out.println(e);
            e.printStackTrace();
            return;
        }
    }

    private static class SocketProcessor implements Runnable {
        private final Socket socket;
        private final DataInputStream in;
        private File filesStore;

        public SocketProcessor(Socket socket, File filesStore) throws IOException {
            this.socket = socket;
            this.in = new DataInputStream(socket.getInputStream());
            //this.out = new PrintWriter(socket.getOutputStream());
            this.filesStore = filesStore;
        }

        @Override
        public void run() {
            String input = null;
            try {
                while ((input = in.readUTF()) != null) {
                    if ("list".equalsIgnoreCase(input)) {
                        int size = in.readInt();
                        for (int i = 0; i < size; i++) {
                            System.out.println(in.readUTF());
                        }
                    } else if (input.startsWith("get")) {
                        String fileName = in.readUTF();
                        OutputStream output = new FileOutputStream(new File(filesStore, fileName));
                        long size = in.readLong();
                        byte[] buffer = new byte[1024];
                        int bytesRead = 0;
                        while (size > 0 && (bytesRead = in.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1)
                        {
                            output.write(buffer, 0, bytesRead);
                            size -= bytesRead;
                        }

                    } else if ("error".equalsIgnoreCase(input)) {
                        String message = in.readUTF();
                        System.out.println(message);
                    } else {
                        System.out.println(input);
                    }
                }
            } catch (EOFException eof) {
                System.out.println("Connection is closed by server");
                close();
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
                close();
                System.exit(1);
            }

        }

        private void close() {
            try {
              //  out.close();
            } catch (Exception e) {
            }
            try {
                in.close();
            } catch (Exception e) {
            }
            try {
                socket.close();
            } catch (Exception e) {
            }
        }
    }
}
