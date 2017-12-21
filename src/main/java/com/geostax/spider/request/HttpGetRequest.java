package com.geostax.spider.request;


public class HttpGetRequest {

	private static final long serialVersionUID = 6105458424891960971L;

	private String url;
	
	public HttpGetRequest(String url) {
		this.url=url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	

}
