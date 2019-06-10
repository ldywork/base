package util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;


public class Test {
	private static int connectionTimeout = 80000;
	private static int soTimeout = 100000;
	public final static int TIMEOUT = 100;//10s 
	public final static int TIMEOUTLONG = 200;//10s 
	public  static String encoding = "UTF-8";
	public static String httpPost(String uri,Map<String,String> params,String httpContentCharset,String clienttype,Long ctime,int length)throws Exception{
		PostMethod postMethod = null;
		try {
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams().setConnectionTimeout(connectionTimeout);
		    client.getHttpConnectionManager().getParams().setSoTimeout(soTimeout);
		    
		    postMethod = new PostMethod(uri);
		    if(null != httpContentCharset){
		    	postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, httpContentCharset);
		    	postMethod.setRequestHeader("Accept", "application/json, text/javascript, */*; q=0.01");
		    	postMethod.setRequestHeader("Accept-Encoding", "utf-8, deflate");
		    	postMethod.setRequestHeader("Accept-Language", "zh-CN,zh;q=0.9");
//		    	postMethod.setRequestHeader("Content-Length", length+"");
		    	postMethod.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
		    	postMethod.setRequestHeader("Cookie", "OUTFOX_SEARCH_USER_ID_NCOO=1537643834.9570553; OUTFOX_SEARCH_USER_ID=1799185238@10.169.0.83; fanyi-ad-id=43155; fanyi-ad-closed=1; JSESSIONID=aaaBwRanNsqoobhgvaHmw; _ntes_nnid=07e771bc10603d984c2dc8045a293d30,1525267244050; ___rl__test__cookies=" + String.valueOf(ctime));
		    	postMethod.setRequestHeader("Host", "fanyi.youdao.com");
		    	postMethod.setRequestHeader("Origin", "http://fanyi.youdao.com");
		    	postMethod.setRequestHeader("Proxy-Connection", "keep-alive");
		    	postMethod.setRequestHeader("Referer", "http://fanyi.youdao.com/");
		    	postMethod.setRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/73.0.3683.103 Safari/537.36");
		    	postMethod.setRequestHeader("X-Requested-With", "XMLHttpRequest");
		    }
		    if(null != clienttype){
		    	postMethod.setRequestHeader("clienttype", clienttype);
		    }
		    postMethod.addParameters(transFromMap(params));
    		client.executeMethod(postMethod);
//    		String responsetext = postMethod.getResponseBodyAsString();
    		byte[] responseBody = postMethod.getResponseBody();
    		InputStream ins   = new ByteArrayInputStream(responseBody);
    		BufferedReader reader = new BufferedReader(new InputStreamReader(ins,"utf-8"));   
    		StringBuilder sb = new StringBuilder();  
    		String line = null;   
    		while ((line = reader.readLine()) != null) {   
                   sb.append(line);   
               } 
    		return sb.toString();
		} catch (Exception e) {
			throw e;
		}finally{
			if(postMethod != null){
				postMethod.releaseConnection();
			}
		}
	}
	private static NameValuePair[] transFromMap(Map<String,String> map){
		NameValuePair[] pairs = new NameValuePair[map.size()];
		int i = 0;
		for(String key :map.keySet()){
			pairs[i] = new NameValuePair();
			pairs[i].setName(key);
			pairs[i].setValue(map.get(key));
			i++;
		}
		return pairs;
	}
		public static String fanYi(String args) throws Exception{
		String from = "AUTO";
        String to = "AUTO";
        String q = args;
        String url = "http://fanyi.youdao.com/translate_o?smartresult=dict&smartresult=rule";

        String u = "fanyideskweb";
        String d = q;
        long ctime = System.currentTimeMillis();
        String f = String.valueOf(ctime + (long)(Math.random() * 10 + 1));
        String c = "@6f#X3=cCuncYssPsuRUE";

        String sign = md5(u + d + f + c);
//        q = URLEncoder.encode(q,"UTF-8");

        Map<String, String> params = new HashMap<String, String>();
        params.put("from", from);
        params.put("to", to);
        params.put("smartresult", "dict");
        params.put("client", "fanyideskweb");
        params.put("salt", f);
        params.put("sign", sign.toLowerCase());
        params.put("ts", f.substring(0,f.length()-1));
        params.put("doctype", "json");
        params.put("version", "2.1");
        params.put("keyfrom", "fanyi.web");
        params.put("action", "FY_BY_CLICKBUTTION");
        params.put("i", q);
//        params.put("typoResult", "false");
        String httpPost = httpPost(url,params,encoding,null,ctime,q.length()+240);
        return httpPost;
        
	}
		public static String md5(String string) {
		    if(string == null){
		        return null;
		    }
		    char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		        'A', 'B', 'C', 'D', 'E', 'F'};
		byte[] btInput = string.getBytes();
		try{
		    MessageDigest mdInst = MessageDigest.getInstance("MD5");
		    mdInst.update(btInput);
		    byte[] md = mdInst.digest();
		        int j = md.length;
		        char str[] = new char[j * 2];
		        int k = 0;
		        for (byte byte0 : md) {
		            str[k++] = hexDigits[byte0 >>> 4 & 0xf];
		            str[k++] = hexDigits[byte0 & 0xf];
		        }
		        return new String(str);
		    }catch(NoSuchAlgorithmException e){
		        return null;
		    }
		}
		public static void main(String[] args){
			String a = "*asdfasef";
			a = a.replace("*", "//");
			System.out.println(a);
		}
}
