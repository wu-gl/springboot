package com.example.distlock.zk.finterface;

@FunctionalInterface
public interface ZkLockCallback<T> {
    T doInLock();
}
