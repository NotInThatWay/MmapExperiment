package com.zcy.mmapexperiment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class ThreadPoolService {
    public static ExecutorService receiveToMemoryExecutor = Executors.newFixedThreadPool(50);

    public static ExecutorService readFromMemoryExecutor = Executors.newFixedThreadPool(50);

    public static ExecutorService receiveToFileExecutor = Executors.newFixedThreadPool(50);

    public static ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();

    public static ExecutorService writeExecutor = Executors.newFixedThreadPool(2);

}
