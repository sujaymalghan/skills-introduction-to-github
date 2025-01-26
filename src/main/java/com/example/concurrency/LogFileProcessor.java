package com.example.concurrency;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class LogFileProcessor {
	public static void main(String[] args) throws Exception {

		ArrayBlockingQueue<Integer> arrayBlockingQueue = new ArrayBlockingQueue<Integer>(10);

		ExecutorService executorService = Executors.newFixedThreadPool(6);

		executorService.submit(()->{
			for(int i=0;i<10;i++){
				try {
					arrayBlockingQueue.put(i);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			}
		});

		executorService.submit(()->
		{
			try{
				arrayBlockingQueue.take();
			}
			catch (InterruptedException e){
				throw new RuntimeException();
			}
		});
	}
}
