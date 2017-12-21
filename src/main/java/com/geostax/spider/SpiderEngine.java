package com.geostax.spider;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * 爬虫引擎，每个爬虫引擎最好独立进程，在分布式爬虫场景下，可以单独分配一台爬虫服务器。 引擎包括
 * Scheduler、Downloader、Spider、SpiderBeanFactory 4个主要模块
 * 
 * @author xiaofei
 *
 */

public class SpiderEngine {

	private Map<String, Spider> spiders;

	public SpiderEngine() {
		spiders = new ConcurrentHashMap<>();
	}

	public Map<String, Spider> getSpiders() {
		return spiders;
	}

	public void start() {

		Map<String, String> url_map = getUrlMap("http://www.binzhou.gov.cn");
		for (String key : url_map.keySet()) {
			String value = url_map.get(key);
			Spider spider = new Spider(this, key, URI.create(value).getHost(), "binzhou", value, "date");
			spiders.put(URI.create(value).getHost(), spider);
			new Thread(spider).start();
		}
		new Thread(new Counter(spiders)).start();
	}

	private Map<String, String> getUrlMap(String url) {
		List<String> except = new ArrayList<>();
		except.add("www.bzzzb.gov.cn");
		except.add("www.bztzb.gov.cn");
		except.add("www.bzlgj.gov.cn");
		except.add("www.bzzzb.gov.cn");
		except.add("www.bztzb.gov.cn");
		except.add("www.bzlgj.gov.cn");
		except.add("www.bhcq.gov.cn");
		except.add("www.bzda.gov.cn");
		except.add("www.bzdx.gov.cn");
		except.add("www.bzxf.gov.cn");
		except.add("www.bzbb.gov.cn");
		except.add("www.bzjzfp.gov.cn");
		except.add("www.bzjjw.gov.cn");
		except.add("www.bzzf.gov.cn");

		except.add("www.sdbzedu.gov.cn");
		except.add("www.bzmz.gov.cn");
		except.add("www.bzhrss.gov.cn");
		except.add("www.bzga.gov.cn");
		except.add("www.bzczj.gov.cn");
		except.add("www.bzfgw.gov.cn");
		except.add("www.bzst.gov.cn");
		except.add("www.bzeic.gov.cn");

		except.add("www.sdbzjt.gov.cn");
		except.add("www.bzgtzy.gov.cn");
		except.add("www.sdbzly.gov.cn");
		except.add("www.bzwater.gov.cn");
		except.add("www.hsbz.gov.cn");
		except.add("www.bzssfj.gov.cn");
		except.add("www.bzjsxxw.gov.cn");

		except.add("www.bzein.gov.cn");
		except.add("www.sdbzaudit.gov.cn");
		except.add("www.bzfda.gov.cn");
		except.add("www.bzswsjsw.gov.cn");
		except.add("www.bzty.gov.cn");
		except.add("www.bztj.gov.cn");

		except.add("www.bzaic.gov.cn");
		except.add("www.bzsafety.gov.cn");
		except.add("www.bzsghj.gov.cn");
		except.add("binzhou.sdds.gov.cn");
		except.add("binzhou.sd-n-tax.gov.cn");
		except.add("www.bz12365.gov.cn");

		except.add("www.bzls.gov.cn");
		except.add("www.bzzfj.gov.cn");
		except.add("sdbz.spb.gov.cn");
		except.add("www.bzwgx.gov.cn");
		except.add("www.bzjgw.gov.cn");

		except.add("www.sdgh.org.cn");
		except.add("www.bzgqt.gov.cn");
		except.add("www.bzwomen.org.cn");
		except.add("www.bzmzzjj.gov.cn");
		except.add("www.bzta.gov.cn");
		except.add("www.bbz.gov.cn");
		except.add("www.bzjgjs.gov.cn");
		except.add("www.bzgzw.gov.cn");
		except.add("www.bzxzspzx.gov.cn");
		except.add("www.bzny.gov.cn");

		except.add("www.bzsgsl.gov.cn");
		except.add("www.zouping.gov.cn");
		except.add("www.bzkx.cn");
		except.add("www.bzredcross.com");
		except.add("www.bz.skj.gov.cn");
		except.add("www.yangxin.gov.cn");
		except.add("binzhou.sddpf.org.cn");
		except.add("www.bincheng.gov.cn");
		except.add("www.huimin.gov.cn");
		except.add("www.boxing.gov.cn");

		Map<String, String> url_map = new HashMap<String, String>();
		Document doc = null;
		try {

			doc = Jsoup.connect(url).timeout(30000).get();
			Elements links = doc.select(".f_link_sub li a[href]");
			int count = 0;
			for (Element element : links) {
				if (count >= 10)
					break;
				String name = element.text().trim();
				String suburl = element.absUrl("href").trim();
				if (except.contains(URI.create(suburl).getHost()))
					continue;
				url_map.put(name, suburl);
				count++;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// System.out.println(url_map);
		return url_map;
	}

	public void hop(String domain, String url) {
		/*
		 * if (spiders.keySet().contains(domain)) {
		 * spiders.get(domain).getSpiderScheduler().input(new
		 * HttpGetRequest(url)); } else { Spider spider = new Spider(this,
		 * domain, url); spiders.put(domain, spider); new
		 * Thread(spider).start(); }
		 */
	}

	public static void main(String[] args) {
		new SpiderEngine().start();
		// new SpiderEngine().getUrlMap("http://www.binzhou.gov.cn");
	}
}

class Counter implements Runnable {
	Map<String, Spider> spiders;

	public Counter(Map<String, Spider> spiders) {
		this.spiders = spiders;
	}

	@Override
	public void run() {
		while (true) {
			try {
				System.out.println(">>>> " + spiders.keySet().size());
				for (String s : spiders.keySet()) {
					System.out.println(s + ">>>> " + spiders.get(s).getSpiderScheduler().size());
				}
				// System.out.println(">>>> " +
				// spiders.get("www.gov.cn").getSpiderScheduler().size());
				// System.out.println(">>>> " +
				// spiders.get("www.shandong.gov.cn").getSpiderScheduler().size());
				// System.out.println(">>>> " +
				// spiders.get("www.binzhou.gov.cn").getSpiderScheduler().size());
				Thread.sleep(15000);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
}
