package com.zcy.mmapexperiment.service;

import com.alibaba.fastjson.JSONObject;
import com.zcy.mmapexperiment.dao.Tick;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class TickService {
    public static Map<String, List<Tick>> tickMap = new ConcurrentHashMap<>();

    @Value("${config.numStock}")
    private int numStock;

    @Value("${config.path}")
    private String path;

    @PostConstruct
    public void init() {
        // 创建目录
        File directory = new File(this.path + "Tick");
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }

    /**
     * 从内存中读取该股票的Tick信息
     *
     * @param stkCode 股票代码
     * @return 该股票所有的Tick信息
     */
    public List<Tick> readFromMemory(String stkCode) {
        return tickMap.get(stkCode);
    }

    /**
     * 通过内存映射读取当前股票的所有 Tick 信息
     *
     * @param stkCode 股票代码
     * @return 当前股票的 Tick 信息，存于List中
     * @throws Exception
     */
    public List<Tick> readFromFile(String stkCode) throws Exception {
        String filePath = this.path + "Tick//" + stkCode + ".csv";  // 文件名
        FileChannel fc = new RandomAccessFile(filePath, "r").getChannel();
        MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
        int length = (int) fc.size();
        byte[] bytes = new byte[length];
        buffer.get(bytes);
        StringBuffer sb = new StringBuffer("[");
        String content = new String(bytes);
        sb.append(content);
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return JSONObject.parseArray(sb.toString(), Tick.class);
    }

    /**
     * 通过内存映射写入文件
     *
     * @param tick 要写入的 Tick 信息
     * @throws IOException
     */
    public void writeToFile(Tick tick) throws IOException {
        String stkCode = tick.getStkCode();
        String filePath = this.path + "Tick//" + stkCode + ".csv";  // 文件名
        FileChannel fc = new RandomAccessFile(filePath, "rw").getChannel();
        String tickJSString = JSONObject.toJSONString(tick) + ",";
        MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_WRITE, fc.size(), tickJSString.getBytes().length);
        buffer.put(tickJSString.getBytes());
        fc.close();
    }

    /**
     * 通过内存映射写入文件的重载方法
     *
     * @param tick       要写入的 Tick 信息
     * @param bufferSize 自定义内存映射的尺寸，必须大于 Tick 尺寸
     * @throws IOException
     */
    public void writeToFile(Tick tick, long bufferSize) throws IOException {
//        long start = System.nanoTime();
        String stkCode = tick.getStkCode();
        String filePath = this.path + "Tick//" + stkCode + ".csv";  // 文件名
        FileChannel fc = new RandomAccessFile(filePath, "rw").getChannel();
        String tickJSString = JSONObject.toJSONString(tick) + "\n";
        MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_WRITE, fc.size(), bufferSize);
        buffer.put(tickJSString.getBytes());
        fc.close();
//        System.out.println(System.nanoTime()-start);
    }

    /**
     * 测试内存映射写入时间
     *
     * @param tick 要写入的 Tick 信息
     * @return 写入耗时，单位纳秒
     * @throws IOException
     */
    public long writeToFileTest(Tick tick) throws IOException {
        long start = System.nanoTime();
        writeToFile(tick);
        long end = System.nanoTime();
        return end - start;
    }

    /**
     * 测试内存映射写入时间
     *
     * @param tick       要写入的 Tick 信息
     * @param bufferSize 自定义内存映射尺寸
     * @return 写入耗时，单位纳秒
     * @throws IOException
     */
    public long writeToFileTest(Tick tick, long bufferSize) throws IOException {
        long start = System.nanoTime();
        writeToFile(tick, bufferSize);
        long end = System.nanoTime();
        return end - start;
    }

    /**
     * 测试从内存随机读取的速度
     *
     * @return 读取时常，单位为纳秒
     */
    public long readFromMemoryTest() {
        String stkCode = randomStkCode(numStock);
        long start = System.nanoTime();
        readFromMemory(stkCode);
        long end = System.nanoTime();
        return end - start;
    }

    /**
     * 测试从内存随机读取的速度
     *
     * @param stkCode 要读取的股票代码
     * @return 读取时间，单位为纳秒
     */
    public long readFromMemoryTest(String stkCode) {
        long start = System.nanoTime();
        readFromMemory(stkCode);
        long end = System.nanoTime();
        return end - start;
    }


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
