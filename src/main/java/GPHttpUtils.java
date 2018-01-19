import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * @author 高攀
 * @上午11:09:49
 * apache httpclient各种请求的帮助类
 */
public class GPHttpUtils {
	
	private static String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36"; 
	private static int CONNECTION_TIMEE_OUT = 3000; // 连接超时
	private static int CONNECTION_SOCKET_TIMEE_OUT = 3000;// socket超时
	private static int DefaultHttpMethodRetryCount = 0;// 失败重试1次
	private static String curdesktop = System.getProperty("user.home")+"\\Desktop\\GPHttpUtils\\";// 下载图片用
	
	public static enum METHOD_TYPE{
		GET,POST
	};
	
	public static void main(String[] args) {
//		System.out.println(GeneralRequest("http://www.bejson.com/",METHOD_TYPE.GET));
//		ImageRequest("http://www.innotree.cn/static/img/logo_wh.png", null);
//		BodyRequest("http://api.tw06.xlmc.sandai.net/api/file/video/like/list HTTP/1.1", param);
		
//		String html = BodyRequest("http://api.tw06.xlmc.sandai.net/api/file/list", null, "timeTick=1482905875015&ext_openAppNum=0&callId=1482905875019&orderBy=desc&ext_loadType=loadMore&mainName=cn.kuaipan.android&t=NC%21IyzNw--l8&v=1.3&gz=1&pid=21&sig=2c5e78a27bd545fa976c8e26b4b1fcf6&category=personal&appId=20&version=1.1&pageName=hot&pageSize=20&packageName=cn.kuaipan.android&recType=shortVideo&deviceId=751262152a4f2016fd3bc65b3365f5a710&ext_gender=male&startKey=31713803287921664&ext_needLoginVideo=true&appVersion=5.4.1&behavior=loadmore");
//		System.out.println(html);
	}
	/**
	 * 一般网络请求
	 * @param url：地址
	 * @param TYPE：POST/GET
	 * 
	 */
	public static String GeneralRequest(String url,METHOD_TYPE TYPE){
		String html = null;
		try {
			HttpClient client = new HttpClient();
			HttpMethodBase base =  null;
			if(TYPE == METHOD_TYPE.GET){
//				GET请求竖线|必须要URLEncoder.encode(url, "utf-8")编码一下！（其实还有很多特殊字符，最终解决办法是升级tomcat版本，服务器就不会拦截特殊字符了）
				url = url.replaceAll("\\|", URLEncoder.encode("|", "utf-8"));
				base =  new GetMethod(url);
			}else{
				base =  new PostMethod(url);
			}
			base.addRequestHeader("User-Agent", USER_AGENT);
			base.addRequestHeader("accept", "*/*");
			client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEE_OUT);
			client.getHttpConnectionManager().getParams().setSoTimeout(CONNECTION_SOCKET_TIMEE_OUT);
			client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler(DefaultHttpMethodRetryCount, false));
			client.executeMethod(base);
			System.out.println("请求状态："+base.getStatusCode());
			
			String acceptEncoding = "";
			if(base.getResponseHeader("Content-Encoding") != null){
				acceptEncoding = base.getResponseHeader("Content-Encoding").getValue();
			};
			// 如果是gzip压缩格式
			if(acceptEncoding.toLowerCase().indexOf("gzip") > -1) {
				System.out.println("g_zip");
				StringBuffer sb = new StringBuffer();
				InputStream is;
				is = base.getResponseBodyAsStream();
				GZIPInputStream gzin = new GZIPInputStream(is);
				InputStreamReader isr = new InputStreamReader(gzin, "utf-8");
				java.io.BufferedReader br = new java.io.BufferedReader(isr);
				String tempbf;
				while ((tempbf = br.readLine()) != null) {
					sb.append(tempbf);
					sb.append("\r\n");
				}
				isr.close();
				gzin.close();
				return sb.toString();
			}else {
				html = base.getResponseBodyAsString();
			}
//			html = base.getResponseBodyAsString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	/**
	 * body网络请求
	 * @param url 地址
	 * @param param map形式参数
	 * @param paramString String形式参数，如果map为空，就把这个字段转化为map参数
	 * @category 自动解析gzip的body返回
	 */
	public static String BodyRequest(String url,Map<String,Object> param,String paramString){
		String html = null;
		try {
			if(null==param){
				param = ArrayToMap(paramString);
			}
			HttpClient client = new HttpClient();
			PostMethod base =  new PostMethod(url);
			base.addRequestHeader("User-Agent", USER_AGENT);
			base.addRequestHeader("accept", "*/*");
			client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECTION_TIMEE_OUT);
			client.getHttpConnectionManager().getParams().setSoTimeout(CONNECTION_SOCKET_TIMEE_OUT);
			client.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,new DefaultHttpMethodRetryHandler(DefaultHttpMethodRetryCount, false));
			NameValuePair[] parametersBody = new NameValuePair[param.size()];
			Set<Entry<String, Object>> set = param.entrySet();
			int i = 0;
			for (Entry<String, Object> entry : set) {
				NameValuePair item = new NameValuePair();
				item.setName(entry.getKey());
				item.setValue(entry.getValue()+"");
				parametersBody[i] = item;
				i++;
			}
			if(parametersBody.length>0){
				base.setRequestBody(parametersBody);
			}
			client.executeMethod(base);
			System.out.println("请求状态："+base.getStatusCode());
			/*Header[] headers = base.getResponseHeaders();
			for (Header header : headers) {
				System.out.println(header.getName()+":"+header.getValue());
			}*/
			String acceptEncoding = "";
			if(base.getResponseHeader("Content-Encoding") != null){
				acceptEncoding = base.getResponseHeader("Content-Encoding").getValue();
			};
			// 如果是gzip压缩格式
			if(acceptEncoding.toLowerCase().indexOf("gzip") > -1) {
				StringBuffer sb = new StringBuffer();
				InputStream is;
				is = base.getResponseBodyAsStream();
				GZIPInputStream gzin = new GZIPInputStream(is);
				InputStreamReader isr = new InputStreamReader(gzin, "utf-8");
				java.io.BufferedReader br = new java.io.BufferedReader(isr);
				String tempbf;
				while ((tempbf = br.readLine()) != null) {
					sb.append(tempbf);
					sb.append("\r\n");
				}
				isr.close();
				gzin.close();
				return sb.toString();
			}else {
				html = base.getResponseBodyAsString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return html;
	}
	/**
	 * XML请求
	 */
	public static String xmlPost(String url,String xml){
		try{
		       HttpPost post = new HttpPost(url);   
		       StringEntity entity = new StringEntity(xml/*,"iso8859-1"*/);
//		       entity.setContentEncoding("utf-8");
		       entity.setContentType("text/xml");
		       post.setEntity(entity);
		       return EntityUtils.toString(new DefaultHttpClient().execute(post).getEntity(),"utf-8");
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 下载图片
	 * @param imageUrl：图片路径
	 * @param savePath：保存路径，为空就保存在桌面：Desktop\GPHttpUtils\
	 */
	private static void ImageRequest(String imageUrl,String savePath){
		GetMethod method = new GetMethod(imageUrl);
		HttpClient client = new HttpClient();
		try {
			client.executeMethod(method);
			InputStream inputStream = method.getResponseBodyAsStream();
			
			File file = new File(curdesktop);
			if(!file.exists()){
				try {
					file.mkdir();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			byte b[] = {1};
			int size = 0;
			if(!StringUtils.isBlank(savePath)){
				curdesktop = savePath;
			}
			FileOutputStream outputStream = new FileOutputStream(new File(curdesktop+getImageNameAndHouzui(imageUrl)));
			while((size=inputStream.read(b))!=-1){
				outputStream.write(b, 0, size);
			}
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			// 释放链接
			method.abort();
			method.releaseConnection();
		}
		
	}
	private static Map<String,Object> ArrayToMap(String param){
		Map<String,Object> map = new HashMap<String, Object>();
//		String param = "timeTick=1482905875015&ext_openAppNum=0&callId=1482905875019&orderBy=desc&ext_loadType=loadMore&mainName=cn.kuaipan.android&t=NC%21IyzNw--l8&v=1.3&gz=1&pid=21&sig=2c5e78a27bd545fa976c8e26b4b1fcf6&category=personal&appId=20&version=1.1&pageName=hot&pageSize=20&packageName=cn.kuaipan.android&recType=shortVideo&deviceId=751262152a4f2016fd3bc65b3365f5a710&ext_gender=male&startKey=31713803287921664&ext_needLoginVideo=true&appVersion=5.4.1&behavior=loadmore";
		if(null==param || !param.contains("=")){
			return map;
		}
		String[] params = param.split("&");
		for (String string : params) {
			map.put(string.split("=")[0], string.split("=")[1]);
		}
		return map;
	}
	
	/**
	 * @param imageUrl 图片地址
	 * @return 图片名称.后缀（如 ico_set.gif）
	 * 现在把重名的图片，比如（http://a/b/a.jpg会和http://a/c/.../a.jpg覆盖），更名为网址的项目路径下的图片（项目路径...》图片名称.后缀）
	 */
	private static String getImageNameAndHouzui(String imageUrl){
		/* 原来的
		String result = imageUrl.substring(imageUrl.lastIndexOf("/")+1, imageUrl.length());
		if(result.contains("?")){
			result = result.substring(0, result.indexOf("?"));
		}*/
		
		// 现在的
		String result = imageUrl.substring(imageUrl.lastIndexOf("/")+1, imageUrl.length());
		if(result.contains("?")){
			result = result.substring(0, result.indexOf("?"));
		}
		String resultFull = imageUrl.substring(getRootUrl(imageUrl).length()+1, imageUrl.length()); // 包括了图片目录的
		if(resultFull.contains("?")){
			resultFull = resultFull.substring(0, resultFull.indexOf("?"));
		}
//		System.out.println(resultFull);
//		System.out.println(result);
//		System.out.println("-----");
		// 重置result的图片名称为目录名称
		String[] item = resultFull.split("/");
		for (int i = 0; i < item.length-1; i++) {
//			System.out.println(item[i]);
			result = item[i] + "》" + result;
		}
		return result;
	}
	/**
	 * 得到url的跟路径
	 * @param url 原始url
	 * @return 不包括最后的 /
	 */
	private static String getRootUrl(String url){
		Integer endIndex = url.indexOf("/", url.indexOf("/")+2); // 第三个/的位置
		String rootUrl = url.substring(0, endIndex);
		return rootUrl;
	}
	public static String getUSER_AGENT() {
		return USER_AGENT;
	}
	public static void setUSER_AGENT(String uSER_AGENT) {
		USER_AGENT = uSER_AGENT;
	}
	public static int getCONNECTION_TIMEE_OUT() {
		return CONNECTION_TIMEE_OUT;
	}
	public static void setCONNECTION_TIMEE_OUT(int cONNECTION_TIMEE_OUT) {
		CONNECTION_TIMEE_OUT = cONNECTION_TIMEE_OUT;
	}
	public static int getCONNECTION_SOCKET_TIMEE_OUT() {
		return CONNECTION_SOCKET_TIMEE_OUT;
	}
	public static void setCONNECTION_SOCKET_TIMEE_OUT(
			int cONNECTION_SOCKET_TIMEE_OUT) {
		CONNECTION_SOCKET_TIMEE_OUT = cONNECTION_SOCKET_TIMEE_OUT;
	}
	public static int getDefaultHttpMethodRetryCount() {
		return DefaultHttpMethodRetryCount;
	}
	public static void setDefaultHttpMethodRetryCount(
			int defaultHttpMethodRetryCount) {
		DefaultHttpMethodRetryCount = defaultHttpMethodRetryCount;
	}
	public static String getCurdesktop() {
		return curdesktop;
	}
	public static void setCurdesktop(String curdesktop) {
		GPHttpUtils.curdesktop = curdesktop;
	}
	
}
