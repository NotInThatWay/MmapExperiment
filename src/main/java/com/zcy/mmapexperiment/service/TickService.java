package com.zcy.mmapexperiment.service;

import com.alibaba.fastjson.JSONObject;
import com.zcy.mmapexperiment.dao.Tick;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import sun.nio.ch.FileChannelImpl;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class TickService {
    public static Map<String, List<Tick>> tickMap = new ConcurrentHashMap<>();

    @Autowired
    FileChannelService fileChannelService;
    @Value("${config.numStock}")
    private int numStock;

    @Value("${config.bufferSize}")
    private int bufferSize;

    @Value("${config.path}")
    private String path;

    @PostConstruct
    public void init() {    // 初始化项
        // 创建目录
        File directory = new File(this.path + bufferSize + "//Tick");
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
    public List<Tick> readFromMemory(String stkCode, int num) {
        List<Tick> tickList = tickMap.get(stkCode);
        if (tickList != null && num <= tickList.size()) {
            Stream<Tick> stream = tickList.stream();
            ArrayList<Tick> result = stream.skip(tickList.size() - num).collect(Collectors.toCollection(ArrayList::new));
            return result;
        } else {
            return tickList;
        }
    }

    /**
     * 通过内存映射读取当前股票的所有 Tick 信息
     *
     * @param stkCode 所要读取的股票代码
     * @param num     要查询的 Tick 的 数量
     * @return 当前查询的股票的 Tick
     * @throws Exception
     */
    public List<Tick> readFromFile(String stkCode, int num) throws Exception {
        FileChannel fc = fileChannelService.fcMap.get(stkCode);
        long querySize = (long) bufferSize * num;
        long fileSize = fc.size();
        MappedByteBuffer buffer;
        byte[] bytes;
        if (fileSize >= querySize) { // 文件中 Tick 数量 >= 请求读取 Tick 数量
            buffer = fc.map(FileChannel.MapMode.READ_ONLY, fileSize - querySize, querySize);
            bytes = new byte[(int) querySize];
        } else {    // 文件中 Tick 数量 < 请求读取 Tick 数量
            buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fileSize);
            bytes = new byte[(int) fileSize];
        }

        buffer.get(bytes);
        StringBuffer sb = new StringBuffer("[");
        String content = new String(bytes);
        sb.append(content);
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");

//        Method m = FileChannelImpl.class.getDeclaredMethod("unmap", MappedByteBuffer.class);
//        m.setAccessible(true);
//        m.invoke(FileChannelImpl.class, buffer);

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
        String filePath = this.path + bufferSize + "//Tick//" + stkCode + ".csv";  // 文件名
        FileChannel fc = new RandomAccessFile(filePath, "rw").getChannel();
        String tickJSONStr = JSONObject.toJSONString(tick) + ",";
        long bufferSize = tickJSONStr.getBytes().length;
        MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_WRITE, fc.size(), bufferSize);
        buffer.put(tickJSONStr.getBytes());
        fc.close();
    }

    /**
     * 通过内存映射写入文件的重载方法，自定义 Buffer 大小
     *
     * @param tick 要写入的 Tick 信息
     * @param size 自定义内存映射的尺寸，必须大于 Tick 的大小
     * @throws IOException
     */
    public void writeToFile(Tick tick, long size) throws IOException {
        String stkCode = tick.getStkCode();
        String filePath = this.path + bufferSize + "//Tick//" + stkCode + ".csv";  // 文件名
        FileChannel fc = new RandomAccessFile(filePath, "rw").getChannel();
        String tickJSONStr = JSONObject.toJSONString(tick) + ",";
        long strLength = tickJSONStr.getBytes().length;
        long bufferSize = size >= strLength ? size : strLength;
        MappedByteBuffer buffer = fc.map(FileChannel.MapMode.READ_WRITE, fc.size(), bufferSize);
        buffer.put(tickJSONStr.getBytes());
        fc.close();
    }

    /**
     * 将 Tick 写入内存
     *
     * @param tick 获取到的 Tick 信息
     */
    public void writeToMemory(Tick tick) {
        String code = tick.getStkCode();
        List<Tick> tickList = tickMap.getOrDefault(code, new ArrayList<>());
        tickList.add(tick);
        tickMap.put(code, tickList);
    }
}
