package com.demo;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class tomcat7 {

    private static ExecutorService threadPool = new ThreadPoolExecutor(10, 100, 3, TimeUnit.MINUTES, new LinkedBlockingDeque<>());

    public static void main(String[] args) throws IOException {
        // todo ServerSocket AIO保持硬件与JVM的连接性能不如NIO.tomcat8开始使用NIO的方式
        ServerSocket serverSocket = new ServerSocket(8081);//todo 配置文件
        System.out.println("启动8081端口");

        while (!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();
            threadPool.submit(() -> {
                try {
                    InputStream inputStream = socket.getInputStream();
                    System.out.println("接收到请求");
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));//todo UTF-8配置文件
                    StringBuilder stringBuilder = new StringBuilder();
                    String d = null;
                    while ((d = bufferedReader.readLine()) != null) {
                        if (d.length() == 0) {
                            break;
                        }
                        stringBuilder.append(d);
                    }
                    System.out.println("请求数据：" + stringBuilder);

                    //todo 根据url调用 servlet
                    String responseDate = "Hello Word";


                    //再看 https://www.wglbk.cn/post/47 这篇博客，通过 servlet
                    //HttpServletRequest req;
                    //HttpServletResponse resp;


                    //todo 返回数据
                    OutputStream outputStream = socket.getOutputStream();
                    //http协议 1.1 200是状态码
                    outputStream.write("HTTP/1.1 200 OK\r\n".getBytes());
                    //定义数据长度,防止拆包、丢包这些问题
                    outputStream.write(("Content-Length: " + responseDate.length() + "\r\n\r\n").getBytes());
                    outputStream.write(responseDate.getBytes());
                    socket.close();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }
    }

}

