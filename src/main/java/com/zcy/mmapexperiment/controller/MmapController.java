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

    @GetMapping("/readFromMemory")
    public long test(){
        return snapshotService.readFromMemoryTest(20000);
    }

}
