package com.geostax.spider.leg;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.geostax.spider.SpiderEngine;
import com.geostax.spider.request.HttpGetRequest;
import com.geostax.spider.scheduler.BloomScheduler;
import com.geostax.spider.scheduler.Scheduler;

/**
 * 一个爬虫引擎可以包含多个爬虫，每个爬虫可以认为是一个单独线程，爬虫会从Scheduler中获取需要待抓取的请求。
 * 爬虫的任务就是下载网页并渲染相应的JavaBean。
 * 
 * @author xiaofei
 *
 */

public class DomainSpider implements Runnable {

	private SpiderEngine engine;

	private Scheduler spiderScheduler;

	private ExecutorService executor;
	
	private final String domain;

	private String start_url;
	
	
	
	public DomainSpider(SpiderEngine engine,String domain,String start_url) {
		this.engine = engine;
		this.spiderScheduler = new BloomScheduler();
		this.executor = Executors.newFixedThreadPool(2);
		this.domain=domain;
		this.start_url=start_url;
	}

	public Scheduler getSpiderScheduler() {
		return spiderScheduler;
	}
	
	public String getDomain() {
		return domain;
	}
	
	public SpiderEngine getEngine() {
		return engine;
	}
	
	
	
	public void run() {
		if(domain.equals("www.gov.cn")) {
			GuowuyuanLeg leg1=new GuowuyuanLeg("leg-1",this,true);
			GuowuyuanLeg leg2=new GuowuyuanLeg("leg-2",this,true);
			GuowuyuanLeg leg3=new GuowuyuanLeg("leg-3",this,true);
			GuowuyuanLeg leg4=new GuowuyuanLeg("leg-4",this,true);
			executor.submit(leg1);
			executor.submit(leg2);
			executor.submit(leg3);
			executor.submit(leg4);
		}
		else if(domain.equals("www.shandong.gov.cn")) { 
			ShandongLeg leg1=new ShandongLeg("leg-1",this,true);
			ShandongLeg leg2=new ShandongLeg("leg-2",this,true);
			ShandongLeg leg3=new ShandongLeg("leg-3",this,true);
			ShandongLeg leg4=new ShandongLeg("leg-4",this,true);
			executor.submit(leg1);
			executor.submit(leg2);
			executor.submit(leg3);
			executor.submit(leg4);
		}
		else if(domain.equals("www.binzhou.gov.cn")){
			BinzhouLeg leg1=new BinzhouLeg("leg-1",this,true);
			BinzhouLeg leg2=new BinzhouLeg("leg-2",this,true);
			BinzhouLeg leg3=new BinzhouLeg("leg-3",this,true);
			BinzhouLeg leg4=new BinzhouLeg("leg-4",this,true);
			executor.submit(leg1);
			executor.submit(leg2);
			executor.submit(leg3);
			executor.submit(leg4);
		}
		
		spiderScheduler.input(new HttpGetRequest(start_url));
	}
	
	public void push(String domain,String url) {
		engine.hop(domain,url);
	}

}
