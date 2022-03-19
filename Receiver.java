
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import java.net.Socket;

import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class Receiver { //client

    private SocketChannel socketChannel;
    int port;
    private DataInputStream datainput;
    Socket client;
    String to = "C:\\Users\\phuth\\OneDrive\\เอกสาร\\NetBeansProjects\\zeroCopysentfile\\file\\copyfile2.mp4";

    public Receiver(SocketChannel socket, int port, Socket client) {
        this.socketChannel = socket; //client
        this.port = port;
        this.client = client;
    }

    public void runner() {
        try {
            datainput = new DataInputStream(client.getInputStream());
            String path = datainput.readUTF(); //ชื่อไฟล์
            long size = datainput.readLong(); //รับขนาดไฟล์
            int zeroornolmal = datainput.readInt(); //ดูว่าเป็น แบบไหน
            System.out.println(zeroornolmal);
            if (zeroornolmal == 0) {
                int start = (int) System.currentTimeMillis();
                System.out.println("Waiting ...");
                zeroCopy( size);
                int end = (int) System.currentTimeMillis();
                System.out.println("Zero Copy Finished : " + (end - start) + "ms.");
            } else if (zeroornolmal == 1) {
                int start = (int) System.currentTimeMillis();
                System.out.println("Waiting ...");
                normalCopy( size);
                int end = (int) System.currentTimeMillis();
                System.out.println("Normal Copy Finished : " + (end - start) + "ms.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void zeroCopy(long sizeFile) {

        try {
            FileChannel fileChannel = new FileOutputStream(to).getChannel();
            int currentRead = 0;
            while (currentRead < sizeFile) {
                long read = fileChannel.transferFrom(socketChannel, currentRead, sizeFile - currentRead);
                currentRead += read;
                System.out.println("Waiting... : " + read);
            }
            fileChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void normalCopy( long sizeFile) {
        try {
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(to));
            byte[] buffer = new byte[4096];
            long currentRead = 0;
            while (currentRead < sizeFile) {
                int read = datainput.read(buffer);
                currentRead += read;
                bos.write(buffer, 0, read);
                System.out.println("Waiting... : " + read);
            }
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
