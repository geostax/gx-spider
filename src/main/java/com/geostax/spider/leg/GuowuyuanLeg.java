package com.geostax.spider.leg;

import java.net.InetAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.geostax.spider.Spider;
import com.geostax.spider.request.HttpGetRequest;
import com.geostax.spider.scheduler.Scheduler;

public class GuowuyuanLeg  implements Runnable{

	Scheduler scheduler;
	String domain;
	DomainSpider spider;
	boolean feed;
	TransportClient client = null;

	private final String leg_id;

	public GuowuyuanLeg(String leg_id, DomainSpider spider, boolean feed) {
		this.leg_id = leg_id;
		this.spider = spider;
		this.scheduler = spider.getSpiderScheduler();
		this.domain = spider.getDomain();
		this.feed = feed;
		try {
			client = new PreBuiltTransportClient(Settings.EMPTY)
					.addTransportAddress(new TransportAddress(InetAddress.getByName("121.41.227.187"), 9300));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		while (true) {
			HttpGetRequest request = scheduler.output();
			String url = request.getUrl();
			Document doc = null;
			try {
				Thread.sleep(1000);
				doc = Jsoup.connect(url).timeout(10000).get();
				parse(doc);

				if (!doc.select("meta[name=firstpublishedtime]").isEmpty()
						&& !doc.select("meta[name=firstpublishedtime]").first().attr("content").toString().equals("")) {
					// System.out.println(url);
					continue;
				}

				Elements links = doc.select("a[href]");
				for (Element element : links) {
					String name = element.text().trim();
					String suburl = element.absUrl("href").trim();

					if (!suburl.contains("http"))
						continue;

					String url_domain = getDomainName(suburl);
					if (!url_domain.equals(domain)) {
						spider.push(url_domain, suburl);
						continue;
					}

					if (suburl != "" && !name.equals("") && feed) {
						// System.out.println(leg_id+":"+title + "|" + url_domain + "|" + suburl);
						scheduler.input(new HttpGetRequest(suburl));
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void parse(Document doc) {

		String title = doc.title();
		Elements description = doc.select("meta[name=description]");

		String desc = "";
		if (!description.isEmpty()) {
			desc = description.first().attr("content").toString();
		}

		Map<String, Object> infoMap = new HashMap<String, Object>();
		infoMap.put("url", doc.baseUri());
		infoMap.put("domain", domain);
		infoMap.put("title", title);
		infoMap.put("description", desc);

		// System.out.println(infoMap);
		IndexResponse response = client.prepareIndex("guowuyuan", "page").setSource(infoMap).execute().actionGet();
		System.out.println("id:" + response.getId());

	}

	private String getIP(URI uri) {
		URI effectiveURI = null;

		try {
			effectiveURI = new URI(uri.getScheme(), uri.getUserInfo(), uri.getHost(), uri.getPort(), null, null, null);
		} catch (Throwable var4) {
			effectiveURI = null;
		}

		return effectiveURI.getHost();
	}

	public String getDomainName(String url) {
		String host_domain = "";
		if (url.contains("?")) {
			host_domain = URI.create(url.substring(0, url.indexOf("?"))).getHost();
		} else {
			host_domain = URI.create(url).getHost();
		}

		return host_domain;
	}
}
