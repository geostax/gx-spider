package com.geostax.spider.scheduler;

import com.geostax.spider.request.HttpGetRequest;

/**
 * 需要下载的请求都放在这里管理，可以认为这里是一个队列，保存了所有待抓取的请求。系统默认采用FIFO的方式管理请求。
 * 
 * @author huchengyi
 *
 */
public interface Scheduler {
	
	public HttpGetRequest output();
	
	public void input(HttpGetRequest request);
	
	public int size();
	
}
