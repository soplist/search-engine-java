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


public class Engine {
	
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Engine engine = new Engine();
		String data = engine.getContent("https://www.hao123.com", "post");
		engine.writeFile(data,"D:\\wangkang\\html.txt");
	}

}
