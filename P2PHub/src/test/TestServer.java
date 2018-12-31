package test;

import hubFramework.Peer;

import java.net.*;
import java.util.*;
import java.io.*;

public class TestServer
{
    public static void main(String[] args) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(5555);
        while(true)
        {
            Socket connectedSocket = serverSocket.accept();
            System.out.println("Connection arrived: "+connectedSocket.getInetAddress().getHostName());
            //Peer peer = new Peer(connectedSocket);
            System.out.println("Streams initialized and objectified");

        }
    }
}
