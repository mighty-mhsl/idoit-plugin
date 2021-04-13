package com.idoit.safe;

@FunctionalInterface
public interface SafeRunnable {
    void run() throws Exception;
}
