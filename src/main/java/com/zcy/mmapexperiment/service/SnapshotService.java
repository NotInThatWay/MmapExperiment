package com.zcy.mmapexperiment.service;

import com.zcy.mmapexperiment.dao.Tick;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
@EnableScheduling
public class SnapshotService {

    private final int FREQUENCY = 3000;
    @Value("${config.numStock}")
    private int numStock;

    @Value("${config.numThread}")
    private int numThread;

    public static BlockingQueue<Tick> tickQ = new LinkedBlockingQueue<>();

    @Autowired
    private TickService tickService;

    /**
     * 根据自定义频率，每间隔一段时间生成 Tick
     */
    @PostConstruct
    @Scheduled(fixedRate = FREQUENCY)
    private void generateTick() {
//        log.info("开始生成Tick");
        long time = System.currentTimeMillis();
        for (int i = 0; i < numStock; i++) {
            Tick tick = new Tick();
            String code = "sh" + String.format("%06d", i);
            tick.setStkCode(code);
            tick.setTime(time);
            tick.setId(code + time);
            tickQ.offer(tick);
        }
    }

    /**
     * 从 Tick Queue 里读取每个 Tick，并存放于内存中
     *
     * @throws InterruptedException
     */
    private void receiveToMemory() throws InterruptedException {
        while (true) {
            Tick tick = tickQ.take();
            String code = tick.getStkCode();
            List<Tick> tickList = TickService.tickMap.getOrDefault(code, new ArrayList<>());
            tickList.add(tick);
            TickService.tickMap.put(code, tickList);
//            System.out.println(tickQ.size());
        }
    }

    /**
     * 将Queue中的 Tick 通过内存映射，写入文件中
     *
     * @throws InterruptedException
     * @throws IOException
     */
    private void receiveToFile() throws InterruptedException, IOException {
        while (true) {
            Tick tick = tickQ.take();
            tickService.writeToFile(tick, 1024);
        }
    }

    /**
     * 通过内存映射写入文件测试
     *
     * @param count 循环次数
     * @return 写入的总时长
     * @throws InterruptedException
     * @throws IOException
     */
    private long receiveToFileTest(int count) throws InterruptedException, IOException {
        long total = 0L;
        while (count-- > 0) {
            Tick tick = tickQ.take();
            long start = System.currentTimeMillis();
            tickService.writeToFile(tick, 1024);
            long end = System.currentTimeMillis();
            total += end - start;

        }
        return total;
    }

    /**
     * 内存映射写入文件线程
     */
    private void receiveToFileThread() {
        for (int i = 0; i < numThread; i++) {
            ThreadPoolService.receiveToFileExecutor.execute(() -> {
                try {
//                    receiveToFile();
                    System.out.println(receiveToFileTest(100));
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    /**
     * 写入内存线程
     */
    private void receiveToMemoryThread() {
        for (int i = 0; i < numThread; i++) {
            ThreadPoolService.receiveToMemoryExecutor.execute(() -> {
                try {
                    receiveToMemory();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }


    /**
     * 接收 Tick 信息
     */
    @PostConstruct
    private void receive() {
//        receiveToFileThread();
        receiveToMemoryThread();
    }

    public long readFromMemoryTest(int numTest) {
        long total = 0L;
        for (int i = 0; i < numTest; i++) {
            total += tickService.readFromMemoryTest();
        }
        return total / numTest;
    }


}
