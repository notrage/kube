package kube.services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;



public class Client extends Network {

    private Socket socket;
    
    public Client() {
    }
    
    public Client(String ip, int port){
        connect(ip, port);
    }


    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    @Override
    public final boolean connect(String ip, int port) {
        try {
            setIp(ip);
            setPort(port);
            setSocket(new Socket(ip, port));
            setOut(new ObjectOutputStream(getSocket().getOutputStream()));
            setIn(new ObjectInputStream(getSocket().getInputStream()));
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean disconnect() {
        try {
            getOut().close();
            getIn().close();
            getSocket().close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }    
}
