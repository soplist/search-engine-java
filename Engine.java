import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Engine {
	
	private String[] domains = {".com",".net",".org",".gov",".edu",".link",".red",".int",".mil",".pub"};
	
	public String getContent(String strUrl, String httpMethod) {  
        
		StringBuffer buffer = new StringBuffer();  
        System.setProperty("sun.net.client.defaultConnectTimeout", "5000");  
        System.setProperty("sun.net.client.defaultReadTimeout", "5000");  
        
        try {  
            URL url = new URL(strUrl);  
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();  
            
            if (httpMethod.length() > 0) {  
            	connect.setDoOutput(true);  
                OutputStreamWriter out = new OutputStreamWriter(connect.getOutputStream());  
                out.write(httpMethod);  
                out.flush();  
                out.close();  
            }  
              
            InputStream inputStream = connect.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream,"utf-8"));  
            String tmpStr = ""; 
            StringBuffer sb=new StringBuffer("");
            while((tmpStr=br.readLine())!=null){
            	sb.append(tmpStr+"\r\n");
            }
            br.close();  
            connect.disconnect();  
            
            return sb.toString();
        } catch (Exception e) {
        	return null;
        }  
    }  
	
	public void writeFile(String data,String fileName){
		try{
		    File file =new File(fileName);

		    if(!file.exists()){
		        file.createNewFile();
		    }

		    FileWriter fileWritter = new FileWriter(file);
		    fileWritter.write(data);
		    fileWritter.close();

		    System.out.println("Done");

		}catch(IOException e){
		    e.printStackTrace();
		}
	}
	
	public List<String> extract(String data){
		List<String> hrefList = new ArrayList<String>();
		//Pattern pattern = Pattern.compile("<a\\s+[^<>]*\\s+href=\"([^<>\"]*)\"[^<>]*>");
		Pattern pattern = Pattern.compile("href=\"([^<>\"]*)\"");
		Matcher matcher = pattern.matcher(data);
		while(matcher.find()){
			hrefList.add(matcher.group(0));
        }
		return hrefList;
	}

	public List<String> getContentInDoubleQuotationMarks_g1_1(List<String> hrefList){
		List<String> contents = new ArrayList<String>();
		
		for (String href : hrefList) {
			int firstQuotationMarkIndex = href.indexOf("\"");
			int lastQuotationMarkIndex = href.lastIndexOf("\"");
			contents.add(href.substring(firstQuotationMarkIndex+1,lastQuotationMarkIndex));
		}
		
		return contents;
	}
	
	public List<String> getDomain_g1_2(List<String> contents){
		List<String> domainList = new ArrayList<String>();
		
		for (String content : contents) {
			boolean domainAvailable = false;
			String currentDomain = "";
			
			for (String domain : domains) {
				if(content.contains(domain)){
					domainAvailable = true;
					currentDomain = domain;
					break;
				}
			}
			
			if(domainAvailable){
				String d = content.substring(0, content.indexOf(currentDomain)+currentDomain.length());
				boolean exit = false;
				for (String domain : domainList) {
					if(domain.equals(d)){
						exit = true;
						break;
					}
				}
				if(!exit){
				    domainList.add(d.trim());
				}
			}
		}
		
		return domainList;
	}
	
	public List<String> getDomain_g1_3(List<String> OldDomainList){
		List<String> newDomainList = new ArrayList<String>();
		
		for (String domian : OldDomainList) {
			Pattern p = Pattern.compile("http://www.([^<>\"]*)"); 
			Matcher m = p.matcher(domian);
	        boolean flg = m.matches(); 
	        if(flg){
	        	newDomainList.add(domian);
	        }
		}
		
		return newDomainList;
	}
	
	public List<String> integration_g1(List<String> hrefs){
		List<String> contents = getContentInDoubleQuotationMarks_g1_1(hrefs);
		//for (String content : contents) {
			//System.out.println(content);
		//}
		List<String> domainList = getDomain_g1_2(contents);
		//System.out.println("list size:"+domainList.size());
		//for (String domian : domainList) {
			//System.out.println(domian);
		//}
		List<String> newDomainList = getDomain_g1_3(domainList);
		
		return newDomainList;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Engine engine = new Engine();
		String data = engine.getContent("https://www.hao123.com", "post");
		//engine.writeFile(data,"D:\\wangkang\\html.txt");
		List<String> hrefs = engine.extract(data);
		//for (String href : hrefs) {
			//System.out.println(href);
		//}
		List<String> domainList = engine.integration_g1(hrefs);
		System.out.println("list size:"+domainList.size());
		for (String domian : domainList) {
			System.out.println(domian);
		}
	}

}
