package com.demo;

import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String[] args) {

        Lock reentrantLock = new MyReentrantLock();

        // write your code here
        AtomicAdd atomicAdd = new AtomicAdd();
        for (int i = 0; i < 1000; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int j = 0; j < 100; j++) {
                        reentrantLock.lock();
                        atomicAdd.add(1);
                        System.out.println(atomicAdd.i);
                        reentrantLock.unlock();
                    }
                }
            }).start();
        }
    }
}

class MyReentrantLock implements Lock {
    AtomicReference<Thread> owner = new AtomicReference<>();

    private LinkedBlockingQueue<Thread> waiters = new LinkedBlockingQueue<>();

    @Override
    public void lock() {
        //尝试获取锁
        if (!tryLock()) {
            //如果拿取失败
            //将队列放到等待队列
            waiters.offer(Thread.currentThread());

            for (; ; ) {
                //获取等待队列的第一个Therad
                Thread thread = waiters.peek();
                //如果第一个线程是当前队列尝试拿锁
                if (thread == Thread.currentThread()) {
                    //尝试拿锁
                    if (!tryLock()) {
                        //失败
                        LockSupport.park();
                    } else {
                        //成功
                        waiters.poll();
                        return;
                    }
                } else {
                    //如果不是就排队
                    LockSupport.park();
                }
            }

            //将锁挂起线程通信
            //suspend resume
            //wait notify
            //condition
            //lockSupport.part
        }
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return owner.compareAndSet(null, Thread.currentThread());
    }


    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        return owner.compareAndSet(null, Thread.currentThread());
    }

    private boolean tryUnlock() {
        if (owner.get() != Thread.currentThread()) {
            throw new IllegalMonitorStateException();
        } else {
            return owner.compareAndSet(Thread.currentThread(), null);
        }
    }


    @Override
    public void unlock() {
        if (tryUnlock()) {
            Thread thread = waiters.peek();
            if (thread != null) {
                LockSupport.unpark(thread);
            }
        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }

}

class AtomicAdd {
    volatile int i = 0;
    private static Unsafe unsafe = null;
    private static long valueOffset;

    static {
        try {
            //getDeclaredFiled 仅能获取类本身的属性成员（包括私有、共有、保护）
            Field field = Unsafe.class.getDeclaredField("theUnsafe");
            //theUnsafe再类中的成员变量为private,故必须进行此操作
            field.setAccessible(true);
            //字段不是静态字段的话,要传入反射类的对象.如果传null是会报,但是如果字段是静态字段的话,传入任何对象都是可以的,包括null
            unsafe = (Unsafe) field.get(null);

            //获取i的字段
            Field ifield = AtomicAdd.class.getDeclaredField("i");
            //获取i的内存偏移量
            valueOffset = unsafe.objectFieldOffset(ifield);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    //todo unsafe.getAndAddInt()
    public void add(int i) {
        while (true) {
            //拿到旧的值
            int current = unsafe.getIntVolatile(this, valueOffset);
            //加1
            int nextValue = current + i;
            //写回去
            if (unsafe.compareAndSwapInt(this, valueOffset, current, nextValue)) {
                break;
            }
        }
    }
}
