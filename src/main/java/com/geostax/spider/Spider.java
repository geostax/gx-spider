package com.geostax.spider;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.geostax.spider.leg.BinzhouLeg;
import com.geostax.spider.leg.GuowuyuanLeg;
import com.geostax.spider.leg.ShandongLeg;
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

public class Spider implements Runnable {

	private SpiderEngine engine;

	private Scheduler spiderScheduler;

	private ExecutorService executor;

	private final String domain;

	private String start_url;

	private String endtag = "";
	private String department="";
	private String index_name = "";

	public Spider(SpiderEngine engine,String department, String domain, String index_name, String start_url, String endtag) {
		this.engine = engine;
		this.spiderScheduler = new BloomScheduler();
		this.executor = Executors.newFixedThreadPool(2);
		this.domain = domain;
		this.start_url = start_url;
		this.endtag = endtag;
		this.index_name = index_name;
		this.department=department;
	}

	public String getEndtag() {
		return endtag;
	}

	public String getIndex_name() {
		return index_name;
	}

	public String getDepartment() {
		return department;
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

		SpiderLegThread leg1 = new SpiderLegThread("leg-1", this, true);
		SpiderLegThread leg2 = new SpiderLegThread("leg-2", this, true);
		//SpiderLegThread leg3 = new SpiderLegThread("leg-3", this, true);
		//SpiderLegThread leg4 = new SpiderLegThread("leg-4", this, true);
		executor.submit(leg1);
		executor.submit(leg2);
		//executor.submit(leg3);
		//executor.submit(leg4);

		spiderScheduler.input(new HttpGetRequest(start_url));
	}

	public void push(String domain, String url) {
		engine.hop(domain, url);
	}

}
