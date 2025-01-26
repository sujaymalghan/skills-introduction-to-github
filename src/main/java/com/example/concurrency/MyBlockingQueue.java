package com.example.concurrency;

import java.util.LinkedList;
import java.util.Queue;

class MyBlockingQueue<T> {

    Queue<T> queue;
    int max;

    Object t1 = new Object();
    Object t2 = new Object();

    MyBlockingQueue() {
        queue = new LinkedList<>();
        max = 10;
    }

    public synchronized void add(T t) throws Exception {

        try {
            while (queue.size() == max) {
                t1.wait();
            }
            queue.add(t);
        } catch (Exception e) {
            throw new Exception();
        }
    }

    public synchronized T take() throws Exception {

        try{
            while(!queue.isEmpty()){
                return queue.poll();
            }
            t1.notifyAll();

        }catch (Exception e){
            throw  new Exception();
        }
        return null;
    }

}