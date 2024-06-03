package kube.services;

// Import java classes
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server extends Network {

    /**********
     * ATTRIBUTES
     **********/

    private ServerSocket serverSocket;
    private Socket clientSocket;

    /**********
     * CONSTRUCTOR
     **********/

    /** 
     * Constructor of the class Server
     * 
     * @param port the port
     */
    public Server(int port) throws IOException {    
        try {
            init(port);
        } catch (IOException e) {
            throw new IOException("Could not initialize the server.");
        }
    }
    
    /**********
     * INITIALIZATION
     **********/

    /**
     * Initialize the server
     * 
     * @param port the port
     */
    public final void init(int port) throws IOException {
        try {
            setServerSocket(new ServerSocket(port));
            setClientSocket(getServerSocket().accept());
            setOut(new ObjectOutputStream(getClientSocket().getOutputStream()));
            setIn(new ObjectInputStream(getClientSocket().getInputStream()));
        } catch (IOException e) {
            throw new IOException("Could not listen on port: " + port + ".");
        }
    }
    
    /**********
     * SETTERS
     **********/

    public final void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public final void setServerSocket(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    /**********
     * GETTERS
     **********/

    public ServerSocket getServerSocket() {
        return serverSocket;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    /**********
     * METHODS
     **********/

    /**
     * Connect to the server
     * 
     * @param ip the IP address
     * @param port the port
     * @return true if the connection is successful, false otherwise
     */
    @Override
    public boolean connect(String ip, int port) {
        return clientSocket.isConnected();
    }

    /**
     * Disconnect from the server
     * 
     * @return true if the disconnection is successful, false otherwise
     */
    @Override
    public boolean disconnect() {
        
        try {
            getOut().close();
            getIn().close();
            getClientSocket().close();
            getServerSocket().close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    /**
     * Check if the current instance is a server
     * 
     * @return true
     */
    @Override
    public boolean isServer() {
        return true;
    }
}