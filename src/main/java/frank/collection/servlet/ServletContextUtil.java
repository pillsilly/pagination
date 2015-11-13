package frank.collection.servlet;


import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;


public class ServletContextUtil {

	public static final String getAppRealPath(ServletContext sc){
		return sc.getRealPath("/") ;
	}


	public static final String getBasePath(HttpServletRequest request) {
		String basePath = null;
		try {
			if (basePath == null)
				basePath = request.getScheme() + "://" + request.getServerName() + (80 == request.getServerPort() ? "" : ":" + request.getServerPort()) + request.getContextPath()
						+ "/";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return basePath;
	}

	public static final String getRealpath(HttpServletRequest request) {
		String realPath = null;
		try {
			if (realPath == null)
				realPath = request.getScheme() + "://" + request.getServerName() + (80 == request.getServerPort() ? "" : ":" + request.getServerPort()) + request.getContextPath()
						+ request.getServletPath();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return realPath;
	}
	public static final String SPRING_ACTION_PATH = "org.springframework.web.servlet.HandlerMapping.pathWithinHandlerMapping";
	public static final String getSpringActionPath (HttpServletRequest request){
		String path = null;
		try {
			if (path == null)
				path = request.getScheme() + "://" + request.getServerName() + (80 == request.getServerPort() ? "" : ":" + request.getServerPort()) + request.getContextPath()
						+ (String)request.getAttribute(SPRING_ACTION_PATH);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return path;
	}

	public static final String getSpringActionPathWithURLParams (HttpServletRequest request){
		String path = ServletContextUtil.getSpringActionPath(request);
		if(!StringUtils.isBlank(request.getQueryString())){
			path = path+"?"+request.getQueryString();
		}
		return path;
	}

	/**
	 *
	 * @param queryString like "a=1&b=2&c=3&d=4"
	 * @param urlkeys like "a"
	 * @return
	 */
	public static final String removeParamInURL (String queryString,String[] urlkeys){
		String resultQueryString = "";
		if(StringUtils.isBlank(queryString)||urlkeys==null||urlkeys.length==0){
			return resultQueryString;
		}
		String[] a = queryString.split("&");
		if(a.length==0){
			return resultQueryString;
		}
		LinkedList<String> list = new LinkedList<String>(Arrays.asList(a));
		Iterator<String>  it = list.iterator();
		while(it.hasNext()){
			String s = it.next();
			for(String checkStr:urlkeys){
				String toCheck = s.substring(0,s.indexOf("="));
				if(checkStr.equals(toCheck)){
					it.remove();
				}
			}
		}
		resultQueryString = StringUtils.join(list.toArray(),"&");
		return resultQueryString;
	}

	public static String getIpAddr(HttpServletRequest request) {
	     String ipAddress = null;
	     //ipAddress = request.getRemoteAddr();
	     ipAddress = request.getHeader("x-forwarded-for");
	     if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
	      ipAddress = request.getHeader("Proxy-Client-IP");
	     }
	     if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
	         ipAddress = request.getHeader("WL-Proxy-Client-IP");
	     }
	     if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
	      ipAddress = request.getRemoteAddr();
	      if(ipAddress.equals("127.0.0.1")){
	       //根据网卡取本机配置的IP
	       InetAddress inet=null;
	    try {
	     inet = InetAddress.getLocalHost();
	    } catch (UnknownHostException e) {
	     e.printStackTrace();
	    }
	    ipAddress= inet.getHostAddress();
	      }
	        
	     }

	     //对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
	     if(ipAddress!=null && ipAddress.length()>15){ //"***.***.***.***".length() = 15
	         if(ipAddress.indexOf(",")>0){
	             ipAddress = ipAddress.substring(0,ipAddress.indexOf(","));
	         }
	     }
	     return ipAddress;
	}



}
