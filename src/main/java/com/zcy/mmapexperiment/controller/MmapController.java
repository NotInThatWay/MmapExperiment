package com.zcy.mmapexperiment.controller;

import com.zcy.mmapexperiment.service.SnapshotService;
import com.zcy.mmapexperiment.service.TickService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MmapController {

    @Autowired
    SnapshotService snapshotService;

    @Autowired
    TickService tickService;

    /**
     * 从内存中读取测试
     *
     * @return 单次读取时间平均值
     */
    @GetMapping("/readFromMemory")
    public long test() {
        return snapshotService.readFromMemoryTest(20000);
    }

}
