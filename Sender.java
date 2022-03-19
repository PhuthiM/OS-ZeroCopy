
import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

public class Sender extends Thread { //server

    ServerSocketChannel serverSocketChannel = null;
    SocketChannel socketChannel;
    int port;
    Sender sender;
    boolean conecsuccess = false;
    String path;
    String pathsum;
    String zeroCopy;
    private DataOutputStream dataoutput; //ส่ง
    Socket socket;
    File[] fileToSend;
    int zeroornolmal;

    public Sender(SocketChannel socketserver, int port, String pathsum, String zeroCopy, Socket socket) {
        this.socketChannel = socketserver;
        this.port = port;
        this.pathsum = pathsum;
        this.zeroCopy = zeroCopy;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            
            dataoutput = new DataOutputStream(socket.getOutputStream()); //ส่งข้อมูล
            File filepath = new File(pathsum);
   
            dataoutput.writeUTF(pathsum); // ส่งไฟล์ 
            dataoutput.writeLong(filepath.length()); //ขนาดไฟล์
            
            if ("true".equals(zeroCopy)) {
                zeroornolmal = 0;
                dataoutput.writeInt(zeroornolmal);
                 System.out.println("File Size "+filepath.length());
                System.out.println("ZERO COPY");
                zeroCopy(pathsum, filepath.length());
                System.out.println(filepath.length());
                System.out.println("SUCCESS");
            } else if ("false".equals(zeroCopy)) {
                zeroornolmal = 1;
                dataoutput.writeInt(zeroornolmal);
                System.out.println("File Size "+filepath.length());
                System.out.println("NORMAL COPY");
                normalcopy(pathsum, filepath.length());
                System.out.println("SUCCESS");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void zeroCopy(String pathFile, long sizeFile) {
        try {
            FileChannel source = new FileInputStream(pathFile).getChannel();
            long sendByte = 0;
            while (sendByte < sizeFile) {
                long send = source.transferTo(sendByte, sizeFile - sendByte, socketChannel);
                sendByte += send;
            }
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void normalcopy(String pathFile, long sizeFile) {
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(pathFile));  //เก็บลงที่ buffer
            byte[] buffer = new byte[4096]; 
            long currentRead = 0;
            while (currentRead < sizeFile) {
                int read = bis.read(buffer); //อ่านที่ละ 4
                currentRead += read;  //ทำการบวกค่าข้อมูลที่อ่านแล้ว
                dataoutput.write(buffer, 0, read); //เขียนข้อมูลตั้งแต่เริ่มต้นจนถึง read   
            }
            bis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
