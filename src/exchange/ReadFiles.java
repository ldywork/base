package exchange;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import bean.Result;
import util.JSONUtil;
import util.Test;

public class ReadFiles {
	//获取文件路径
	public static String path ;
	//获取文件后缀
	public static String[] extnames ;
	//每次请求的时间间隔
	public static String waittime;
	//每次请求的时间间隔
	public static String newfilepath;
	//翻译起始符号
	public static String startflag;
	//翻译结束符号
	public static String endflag;
	
	public static void main (String[] args) throws UnsupportedEncodingException{
		//读取配置文件
		ResourceBundle rb = ResourceBundle.getBundle("source");
		//获取文件路径
		path = new String(rb.getString("path").toString().getBytes("ISO8859-1"),"UTF-8");
		//获取文件后缀
		extnames = rb.getString("extname").split(",");
		//每次请求的时间间隔
		waittime = rb.getString("waittime");
		//文件的生成位置
		newfilepath =new String(rb.getString("newfilepath").toString().getBytes("ISO8859-1"),"UTF-8");
		//翻译起始符号
		startflag = rb.getString("startflag");
		//翻译结束符号
		endflag = rb.getString("endflag");
		//校验数字
		if (!isInteger(waittime)){
			System.out.println("输入的时间不是数字");
			return;
		}
		//读取文件
		File file = new File(path);
		readFile(file,extnames);
	}
	/**
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
		return pattern.matcher(str).matches();
	}
	/**
	 * @param path
	 * @return
	 */
	public static void readFile(File file,String[] extnames){
		//遍历文件
		File[] fs = file.listFiles();
		//创建list集合,封装读取的内容
		List<String> fileStrList = new LinkedList<String>();
		if (0 == fs.length){
			System.out.println("文件为空");
		}
		//判断是不是文件
		for (File f : fs) {
			if (f.isDirectory()){
				readFile(f, extnames);
			}else if (f.isFile()){
				String ab = newfilepath+f.getAbsolutePath().substring(path.length());
	            File file4 = new File(ab);
	            if(file4.exists()){
	            	System.out.println("文件已经存在");
	            	continue;
	            }
				String name = f.getName();
				String[] names = name.split("\\.");
				//过滤隐藏文件
				if ( 2 > names.length){
					continue;
				}
				String extName = names[1];
				//判断扩展名是否是要翻译的文件类型
				boolean isFile = false;
				for(String ext : extnames){
					if (ext.equals(extName)){
						isFile = true;
						break;
					}
				}
				//如果不是要翻译的文件那么进入下次循环
				if (!isFile){
					continue;
				}
				try {
					InputStream ins = new FileInputStream(f);
					BufferedReader in = new BufferedReader(new InputStreamReader(ins, "utf-8"));
					String line = "";  
					StringBuilder sb = new StringBuilder();
					StringBuilder exchangeSb = new StringBuilder();
					//创建文件中的唯一值的部分参数
					Integer a = 0;
					Map<String,String> exchangeMap = new HashMap<String,String>();
		            while((line = in.readLine())!=null){  
		                //如果这一行有标识的翻译符号切不是结束行
		                if (checkFlag(line,startflag,",")&&!checkFlag(line,endflag,",")&&!line.contains("import")){
		                	//防止某一处的位置注释太多,在这里分成2000字符打一个标记
		                	if (exchangeSb.length()>2000){
		                		String washString = washString(exchangeSb.toString());
		                		//获取唯一值
		                		String key = getKey( a++);
		                		exchangeMap.put(key, washString);
		                		sb.append(key+"\r\n");
		                		exchangeSb = new StringBuilder();
		                	}
		                	exchangeSb.append(line+"\r\n");
		                }else if (checkFlag(line,startflag,",")&&checkFlag(line,endflag,",")&&!line.contains("import")){
		                	exchangeSb.append(line+"\r\n");
	                		String washString = washString(exchangeSb.toString());
	                		//获取唯一值
	                		String key = getKey( a++);
	                		exchangeMap.put(key, washString);
	                		sb.append(key+"\r\n");
	                		exchangeSb = new StringBuilder();
		                }else if(line.contains("//")&&!line.contains("import")){
		                	String key = getKey(a++)+"<////>";
		                	String washString = washString(line.toString());
		                	exchangeMap.put(key, washString);
	                		sb.append(key+"\r\n");
		                }else{
		                	sb.append(line+"\r\n");
		                }
		            }
		            //翻译文本
		            StringBuilder sbExchange = new StringBuilder();
		            Map<String,String> resultMap = new HashMap<>();
		            for(Entry entry : exchangeMap.entrySet()){
//		            	if (entry.getKey().toString().equals("@@@@@@@@1####")){
//		            		System.out.println(entry.getKey().toString());
//		            	}
		            	if (sbExchange.length()+entry.getValue().toString().length() > 2000){
		            		sbExchange.append(entry.getKey()+"\r\n");
		            		sbExchange.append(entry.getValue()+"\r\n");
		            		String fanYi = Test.fanYi(sbExchange.toString());
		            		System.out.println(fanYi);
		            		//防止封ip,添加线程等待
		                	Thread.sleep(Integer.valueOf(waittime)*1000);
		            		Result readJson2Entity = JSONUtil.readJson2Entity(fanYi, Result.class);
		            		sbExchange = new StringBuilder();
		                	//获取翻译结果
		                	List<List<Map<String, String>>> translateResult = readJson2Entity.getTranslateResult();
		                	if (translateResult== null || translateResult.size() == 0){
		                		continue;
		                	}else{
		                		String keyBack = "";
		                		StringBuilder keyValue = new StringBuilder();
		                		for(List<Map<String, String>> maps :translateResult){
		                			String tgt = maps.get(0).get("tgt").trim();
		                			String src = maps.get(0).get("src").trim();
		                			if(tgt.contains("@@@@@@@@")){
		                				if(!"".equals(keyBack)){
		                					resultMap.put(keyBack, keyValue.toString());
		                					keyBack = "";
		                					keyValue = new StringBuilder();
		                				}
		                				keyBack = tgt;
		                			}else{
		                				keyValue.append("*"+src+"\r\n");
		                				keyValue.append("*"+tgt+"\r\n");
		                			}
		                		}
		                		resultMap.put(keyBack, keyValue.toString());
		                	}
		            	}else {
		            		sbExchange.append(entry.getKey()+"\r\n");
		            		sbExchange.append(entry.getValue()+"\r\n");
		            	}
		            }
		            if (sbExchange.length()>0){
//		            	System.out.println(sbExchange);
		            	String fanYi = Test.fanYi(sbExchange.toString());
	            		//防止封ip,添加线程等待
	                	Thread.sleep(Integer.valueOf(waittime)*1000);
	            		Result readJson2Entity = JSONUtil.readJson2Entity(fanYi, Result.class);
	            		sbExchange = new StringBuilder();
	                	//获取翻译结果
	                	List<List<Map<String, String>>> translateResult = readJson2Entity.getTranslateResult();
	                	if (translateResult== null || translateResult.size() == 0){
	                		continue;
	                	}else{
	                		String keyBack = "";
	                		StringBuilder keyValue = new StringBuilder();
	                		for(List<Map<String, String>> maps :translateResult){
	                			String tgt = maps.get(0).get("tgt").trim();
	                			String src = maps.get(0).get("src").trim();
	                			if(tgt.contains("@@@@@@@@")){
	                				if(!"".equals(keyBack)){
	                					resultMap.put(keyBack, keyValue.toString());
	                					keyBack = "";
	                					keyValue = new StringBuilder();
	                				}
	                				keyBack = tgt;
	                			}else{
	                				keyValue.append("*"+src+"\r\n");
	                				keyValue.append("*"+tgt+"\r\n");
	                			}
	                		}
	                		resultMap.put(keyBack, keyValue.toString());
	                	}
		            }
		            String string = sb.toString();
		            //获取文件的路径,并去掉盘符并与新的目标位置拼接
//		            System.out.println(resultMap);
		            for(Entry entry : resultMap.entrySet()){
//		            	if (entry.getKey().toString().equals("@@@@@@@@1####")){
//		            		System.out.println(string);
//		            		System.out.println(entry.getKey().toString());
//		            	}
		            	String key = entry.getKey().toString().replace(" ", "");
		            	String value = entry.getValue().toString();
		            	if (key.contains("<////>")){
		            		value = value.replace("*", "//");
		            		string = string.replace(key, value);
		            	}else{
		            		string = string.replace(key, "/** \r\n"+value+"*/");
		            		
		            	}
		            
		            }
		            String absolutePath = newfilepath+f.getAbsolutePath().substring(path.length());
//		            FileOutputStream fileOutputStream = new FileOutputStream(new File(absolutePath));
		            File file2 = new File(absolutePath);
		            File parentFile = file2.getParentFile();
		            if (!parentFile.exists()){
		            	parentFile.mkdirs();
		            }
		            if (!file2.exists()){
		            	file2.createNewFile();
		            }
		            FileWriter output = new FileWriter(absolutePath);
//		            fileOutputStream.write(sb.toString().getBytes());
		            BufferedWriter writer  = new BufferedWriter(output);
		            writer.write(string);
		            writer.close();
		            ins.close();
		            System.out.println("已经翻译的文件"+absolutePath);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("文件读取异常");
				}
			}
		}
	}
	public static boolean checkFlag(String checked ,String flag , String seprator){
		String[] flags = flag.split(seprator);
		for (String string : flags) {
			if (checked.contains(string)){
				return true;
			}
		}
		return false;
	}
	/**
	 * @param str 	清洗字符串
	 * @return
	 */
	public static String washString(String str){
		str = str.replace("*", "");
		str = str.replace("/", " ");
		str = str.replace("\\", "");
		String[] split = str.split("\r\n");
		StringBuilder sb = new StringBuilder();
		for (String string : split) {
			if(string.length()>4){
				sb.append(string);
			}
		}
//		str=str.replaceAll("[<br>]{0,}","").replaceAll("(?m)^\\s*$(\\n|\r\n)", "   ");
		str = sb.toString();
		str = str.replace(".", ".\r\n");
		return str;
	}
	public static String getKey(int count){
		return "@@@@@@@@"+count+"####";
	}
}
