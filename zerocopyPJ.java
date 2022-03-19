

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class zerocopyPJ {

    private final static String SERVER = "server";
    private final static String CLIENT = "client";

    public static void main(String[] args) throws IOException {
        File filepath = new File("C:\\Users\\phuth\\OneDrive\\เอกสาร\\NetBeansProjects\\zeroCopysentfile\\file");
        String path;
        Scanner sc = new Scanner(System.in);
        System.out.println("Copy file is ready");
        System.out.print("select server or client : ");
        String user = sc.nextLine();
        int port = 4458;

        if (SERVER.equals(user)) {
            System.out.println("wait client connection");
            ServerSocketChannel serversocketChannel = null;
            ServerSocket serverSocket = null;
            try {
                serverSocket = new ServerSocket(port + 1);
                serversocketChannel = ServerSocketChannel.open();
                serversocketChannel.bind(new InetSocketAddress(port)); //ระบุพอร์ต
                while (true) {
                    SocketChannel connection = serversocketChannel.accept(); // รอ client เชื่อม 
                    Socket socket = serverSocket.accept();
                    System.out.println("Client connection");
                    System.out.println("===================");
                    File[] fileToSend = filepath.listFiles(); //แสดงชื่อไฟล์ทั้งหมด (array)
                    for (int i = 0; i < fileToSend.length; i++) {
                        System.out.println(fileToSend[i].getName()); // ส่งชื่อไฟล์ทั้งหมดไปให้ 
                    }

                    System.out.println("===================");
                    System.out.print("Select file send : ");
                    path = sc.next();

                    String pathsum = filepath + "\\" + path;
                    //   System.out.println(pathsum);
                    System.out.print("sentfile by ZeroCopy : ");
                    String bool = sc.next();
                    System.out.println("Server PORT : " + port);
                    Thread serverthread = new Thread(new Sender(connection, port, pathsum, bool, socket));
                    serverthread.start();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        if (CLIENT.equals(user)) {      
            SocketChannel socketClient = SocketChannel.open(new InetSocketAddress("192.168.2.43", port));
            Socket client = new Socket("192.168.2.43", port + 1);
            System.out.println("Port connect : " + port);
            Receiver reciver = new Receiver(socketClient, port, client);
            reciver.runner();
        }
    }

}
