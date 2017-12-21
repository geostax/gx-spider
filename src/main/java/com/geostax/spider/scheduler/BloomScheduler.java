package com.geostax.spider.scheduler;

import java.util.concurrent.LinkedBlockingQueue;

import com.geostax.spider.request.HttpGetRequest;


/**
 * 采用阻塞的先进先出队列，最大支持Integer.MAX_VALUE个元素 FIFO队列可以实现广度优先遍历的爬虫
 * 
 * @author huchengyi
 *
 */

public class BloomScheduler implements Scheduler {

	private BloomFilter<String> bloom;
	private LinkedBlockingQueue<HttpGetRequest> queue;
	
	public BloomScheduler() {
		bloom = new BloomFilter<String>(0.001, 200000);
		queue = new LinkedBlockingQueue<>();
	}

	@Override
	public void input(HttpGetRequest request) {
		String url = request.getUrl();
		
		if (bloom.contains(url)) {
			//System.out.println(url+",存在");
		} else {
			bloom.add(url);
			queue.add(request);
		}

	}
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return queue.size();
	}

	@Override
	public HttpGetRequest output() {
		try {
			HttpGetRequest request = queue.take();
			return request;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
