package com.zcy.mmapexperiment.controller;

import com.zcy.mmapexperiment.dao.Tick;
import com.zcy.mmapexperiment.dao.TickQuery;
import com.zcy.mmapexperiment.service.SnapshotService;
import com.zcy.mmapexperiment.service.TestService;
import com.zcy.mmapexperiment.service.TickService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class MmapController {

    @Autowired
    SnapshotService snapshotService;

    @Autowired
    TickService tickService;

    @Autowired
    TestService testService;

    public static List<Long> totalTime = new ArrayList<>();


    @PostMapping("/tickFromFile")
    public List<Tick> tickFromFile(@RequestBody TickQuery query) throws Exception {
        long start = System.nanoTime();
//        log.info("股票" + query.getStkCode() + "开始内存映射查询");
        List<Tick> list = snapshotService.readFromFile(query);
//        log.info("股票" + query.getStkCode() + "查询结束，用时：" + (System.nanoTime() - start)/1000 + "  μs");
        long end = System.nanoTime();
        totalTime.add(end - start);
        return list;
    }


    @GetMapping("/time")
    public String totalTime() {
        BigInteger total = new BigInteger("0");
        int totalSize = 0;
        String result = "";
        try {
            totalSize = totalTime.size();
            for (int i = 0; i < totalSize; i++) {
                total = total.add(new BigInteger(Long.toString(totalTime.get(i))));
            }
            result = total.divide(new BigInteger(Integer.toString(totalTime.size()))).toString();
        } catch (NullPointerException | ArithmeticException e) {
            log.info("Not ready ", e);
        }
        return ("平均时间：" + result + "\n个数：" + totalSize + "\n总时长：" + total);
    }

    @GetMapping("/clear")
    public void clear() {
        totalTime = new ArrayList<>();
    }


//    /**
//     * 从内存中读取测试
//     *
//     * @return 单次读取时间平均值
//     */
//    @GetMapping("/readFromMemory")
//    public long test() {
//        return testService.readFromMemoryTest(20000);
//    }

}
