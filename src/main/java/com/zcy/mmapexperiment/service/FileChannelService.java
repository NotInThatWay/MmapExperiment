package com.zcy.mmapexperiment.service;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class FileChannelService {
    public Map<String, FileChannel> fcMap = new ConcurrentHashMap<>();
    @Value("${config.numStock}")
    private int numStock;   // 股票的数量

    @Value("${config.path}")
    private String path;    // Tick 信息的文件存储路径

    @Value("${config.bufferSize}")
    private int bufferSize;

    /**
     * 提前初始化FileChannel，提升实际映射时的速度
     *
     * @throws IOException
     */
    @PostConstruct
    private void initMap() throws IOException {
        for (int i = 0; i < numStock; i++) {
            File directory = new File(this.path + bufferSize + "//Tick");
            if (!directory.exists()) {
                directory.mkdirs();
            }

            String stkCode = "sh" + String.format("%06d", i);
            String filePath = this.path + bufferSize + "//Tick//" + stkCode + ".csv";  // 文件名
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            FileChannel fc = new RandomAccessFile(filePath, "r").getChannel();
            fcMap.put(stkCode, fc);
        }
    }
}