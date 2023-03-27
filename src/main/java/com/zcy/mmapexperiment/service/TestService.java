package com.zcy.mmapexperiment.service;

import com.zcy.mmapexperiment.dao.Tick;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Random;

@Service
public class TestService {

    @Autowired
    SnapshotService snapshotService;

    @Autowired
    TickService tickService;

//    /**
//     * 通过内存映射写入文件测试
//     *
//     * @param count 循环次数
//     * @return 写入的总时长
//     * @throws InterruptedException
//     * @throws IOException
//     */
//    public long receiveToFileTest(int count) throws InterruptedException, IOException {
//        long total = 0L;
//        while (count-- > 0) {
//            Tick tick = SnapshotService.tickQ.take();
//            long start = System.currentTimeMillis();
//            tickService.writeToFile(tick, 1024);
//            long end = System.currentTimeMillis();
//            total += end - start;
//
//        }
//        return total;
//    }
//
//    /**
//     * 测试内存映射写入时间
//     *
//     * @param tick 要写入的 Tick 信息
//     * @return 写入耗时，单位纳秒
//     * @throws IOException
//     */
//    public long writeToFileTest(Tick tick) throws IOException {
//        long start = System.nanoTime();
//        writeToFile(tick);
//        long end = System.nanoTime();
//        return end - start;
//    }



//    /**
//     * 测试内存映射写入时间
//     *
//     * @param tick       要写入的 Tick 信息
//     * @param bufferSize 自定义内存映射尺寸
//     * @return 写入耗时，单位纳秒
//     * @throws IOException
//     */
//    public long writeToFileTest(Tick tick, long bufferSize) throws IOException {
//        long start = System.nanoTime();
//        writeToFile(tick, bufferSize);
//        long end = System.nanoTime();
//        return end - start;
//    }
//
//    /**
//     * 测试从内存随机读取的速度
//     *
//     * @return 读取时常，单位为纳秒
//     */
//    public long readFromMemoryTest() {
//        String stkCode = randomStkCode(numStock);
//        long start = System.nanoTime();
//        readFromMemory(stkCode);
//        long end = System.nanoTime();
//        return end - start;
//    }
//
//    /**
//     * 测试从内存随机读取的速度
//     *
//     * @param stkCode 要读取的股票代码
//     * @return 读取时间，单位为纳秒
//     */
//    public long readFromMemoryTest(String stkCode) {
//        long start = System.nanoTime();
//        readFromMemory(stkCode);
//        long end = System.nanoTime();
//        return end - start;
//    }


    //    public long readFromMemoryTest(int numTest) {
//        long total = 0L;
//        for (int i = 0; i < numTest; i++) {
//            total += tickService.readFromMemoryTest();
//        }
//        return total / numTest;
//    }



    /**
     * 随机股票代码
     *
     * @param numStock 股票的数量
     * @return 随机生成的股票代码
     */
    private String randomStkCode(int numStock) {
        Random rand = new Random(System.nanoTime());
        return "sh" + String.format("%06d", rand.nextInt(numStock));
    }


}
