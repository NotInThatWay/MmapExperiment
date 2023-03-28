package com.zcy.mmapexperiment.service;

import com.zcy.mmapexperiment.dao.Tick;
import com.zcy.mmapexperiment.dao.TickQuery;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
@EnableScheduling
public class SnapshotService {
    public static BlockingQueue<Tick> tickQ = new LinkedBlockingQueue<>();  // 产生的 Tick 存放于此，等待被写入文件或内存
    @Value("${config.generationCount}")
    private int generationCount;    // 每支股票生成的总 Tick 数
    @Value("${config.frequency}")
    private int frequency;  // 生成 Tick 的频率
    @Value("${config.numStock}")
    private int numStock;   // 总共股票的数量
    @Value("${config.numThread}")
    private int numThread;  // 线程数量
    @Value("${config.bufferSize}")
    private int bufferSize;
    @Autowired
    private TickService tickService;


    /**
     * 根据自定义频率，每间隔一段时间生成 Tick
     */
//    @PostConstruct
    private void generateTick() {
        log.info("开始生成Tick");
        for (int g = 0; g < generationCount; g++) {
            long time = System.currentTimeMillis();
            for (int i = 0; i < numStock; i++) {
                Tick tick = new Tick();
                String code = "sh" + String.format("%06d", i);
                tick.setStkCode(code);
                tick.setTime(time);
                tick.setId(code + time);
                tickQ.offer(tick);
            }
            try {
                Thread.sleep(frequency);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            System.out.println("循环：" + g + ", Q大小：" + tickQ.size());
        }
    }

    @PostConstruct
    private void init() {
//        receiveToMemoryThread();
//        receiveToFileThread();
//        receiveToFileThread(bufferSize);
    }

    /**
     * 从 Tick Queue 里读取每个 Tick，并存放于内存中
     *
     * @throws InterruptedException
     */
    private void receiveToMemory() throws InterruptedException {
        while (true) {
            Tick tick = tickQ.take();   //  从 Queue 里提取 Tick
            tickService.writeToMemory(tick);
        }
    }

    /**
     * 多线程写入内存
     */
    private void receiveToMemoryThread() {
        for (int i = 0; i < numThread; i++) {
            ThreadPoolService.receiveToMemoryExecutor.execute(() -> {   // 写入内存线程池
                try {
                    receiveToMemory();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * 将 Queue 中的 Tick 通过内存映射，写入文件中
     *
     * @throws InterruptedException
     * @throws IOException
     */
    private void receiveToFile() throws InterruptedException, IOException {
        while (true) {
            Tick tick = tickQ.take();
            tickService.writeToFile(tick);
        }
    }


    /**
     * 将 Queue 中的 Tick 通过内存映射，写入文件中
     *
     * @param bufferSize 自定义的 Buffer 大小
     * @throws InterruptedException
     * @throws IOException
     */
    private void receiveToFile(long bufferSize) throws InterruptedException, IOException {
        while (true) {
            Tick tick = tickQ.take();
            tickService.writeToFile(tick, bufferSize);
        }
    }


    /**
     * 内存映射写入文件线程
     */
    private void receiveToFileThread() {
        for (int i = 0; i < numThread; i++) {
            ThreadPoolService.receiveToFileExecutor.execute(() -> {
                try {
                    receiveToFile();
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }


    /**
     * 内存映射写入文件线程的重载方式
     *
     * @param bufferSize 自定义的 Buffer 大小
     */
    private void receiveToFileThread(long bufferSize) {
        for (int i = 0; i < numThread; i++) {
            ThreadPoolService.receiveToFileExecutor.execute(() -> {
                try {
                    receiveToFile(bufferSize);
                } catch (InterruptedException | IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }


    /**
     * 通过内存映射读取文件中的 Tick 信息
     *
     * @param tickQuery Tick 的查询信息
     * @return 当前股票查询到的 Tick 信息
     * @throws Exception
     */
    public List<Tick> readFromFile(TickQuery tickQuery) throws Exception {
        String stkCode = tickQuery.getStkCode();
        int num = tickQuery.getNum();
        return tickService.readFromFile(stkCode, num);
    }

    /**
     * 从内存中读取 Tick 信息
     *
     * @param tickQuery Tick 的查询信息
     * @return 当前股票查询到的 Tick 信息
     */
    public List<Tick> readFromMemory(TickQuery tickQuery) {
        String stkCode = tickQuery.getStkCode();
        int num = tickQuery.getNum();
        return tickService.readFromMemory(stkCode, num);
    }


}
