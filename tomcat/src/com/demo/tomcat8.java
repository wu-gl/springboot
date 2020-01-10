package com.demo;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.*;

public class tomcat8 {

    private static ExecutorService threadPool = new ThreadPoolExecutor(10, 100, 3, TimeUnit.MINUTES, new LinkedBlockingDeque<>());

    public static void main(String[] args) throws Exception {
        new NioServer(8082).start();
    }

    public static class NioServer {

        public NioServer(int port) {
            this.port = port;
        }

        private int port;
        private Selector selector;

        public void init() throws Exception {
            //创建一个选择器
            selector = Selector.open();
            //创建一个ServerSocketChanel
            ServerSocketChannel channel = ServerSocketChannel.open();
            //设置非阻塞
            channel.configureBlocking(false);
            //打开一个ServerSocket
            ServerSocket serverSocket = channel.socket();
            //地址
            InetSocketAddress address = new InetSocketAddress(port);
            //绑定
            serverSocket.bind(address);
            //注册事件
            channel.register(this.selector, SelectionKey.OP_ACCEPT);
        }


        public void start() throws Exception {
            this.init();
            while (true) {
                this.selector.select();
                Iterator<SelectionKey> iterator = this.selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isAcceptable()) {
                        //这个请求是客户端的连接请求事件
                        accept(key);
                    } else if (key.isReadable()) {
                        //如果这个请求是读事件
                        threadPool.submit(new NioServerHandler(key));
                    }
                }
            }
        }

        public void accept(SelectionKey key) {
            try {
                ServerSocketChannel ssc = (ServerSocketChannel) key.channel();
                SocketChannel sc = ssc.accept();
                sc.configureBlocking(false);
                sc.register(selector, SelectionKey.OP_READ);
                System.out.println("accept a client : " + sc.socket().getInetAddress().getHostName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static class NioServerHandler implements Runnable {

        private SelectionKey selectionKey;

        public NioServerHandler(SelectionKey selectionKey) {
            this.selectionKey = selectionKey;
        }

        @Override
        public void run() {
            try {
                //创建一个缓冲区
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                SocketChannel channel = (SocketChannel) selectionKey.channel();
                //我们把通道的数据填入缓冲区
                channel.read(buffer);
                String request = new String(buffer.array()).trim();
                System.out.println("客户端的请求内容" + request);
                //把我们的html内容返回给客户端

                String outString = "HTTP/1.1 200 OK\n"
                        + "Content-Type:text/html; charset=UTF-8\n\n"
                        + "Hello Word";

                //String outString="HTTP/1.1 200 OK\n Hello Word";

                ByteBuffer outbuffer = ByteBuffer.wrap(outString.getBytes());
                channel.write(outbuffer);
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

