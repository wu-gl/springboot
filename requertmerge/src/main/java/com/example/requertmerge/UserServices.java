package com.example.requertmerge;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

@Service
public class UserServices {

    class Request {
        Long id;
        CompletableFuture<Long> completableFuture;
    }

    LinkedBlockingQueue<Request> linkedBlockingQueues = new LinkedBlockingQueue();

    @PostConstruct
    void init() {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleAtFixedRate(() -> {
            int size = linkedBlockingQueues.size();
            if (size > 0) {
                try {
                    List<Request> operationLogs = new ArrayList<>();
                    HashMap<Long, Long> maps = new HashMap<>();
                    //递归查询所有的请求
                    for (int i = 0; i < size; i++) {
                        Request id = linkedBlockingQueues.poll();
                        operationLogs.add(id);
                        //合并请求，同样的请求只处理一次
                        if (!maps.containsKey(id.id)) {
                            maps.put(id.id, id.id);
                        }
                    }

                    System.out.println("operationLogs" + operationLogs.size());
                    System.out.println("maps" + maps.size());
                    //再将结果返回
                    for (Request request : operationLogs) {
                        Long map = maps.get(request.id);
                        request.completableFuture.complete(map);
                    }
                } catch (Exception ex) {
                    System.out.println(ex.toString());
                }
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }


    public Long getInfo(Long id) throws Exception {
        Request request = new Request();
        request.id = id;
        CompletableFuture<Long> completableFuture = new CompletableFuture<>();
        request.completableFuture = completableFuture;
        linkedBlockingQueues.add(request);
        return completableFuture.get();

    }

}
