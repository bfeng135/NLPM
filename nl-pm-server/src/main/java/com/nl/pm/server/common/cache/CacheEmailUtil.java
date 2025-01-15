package com.nl.pm.server.common.cache;

import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class CacheEmailUtil {
    public static final ExecutorService threadPool = Executors.newFixedThreadPool(3);
}
