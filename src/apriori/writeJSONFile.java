package apriori;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;



public class writeJSONFile {
	

    public static void main() throws IOException {

    	  JSONObject obj=new JSONObject();
    	  obj.put("name","foo");
    	  obj.put("num",new Integer(100));
    	  obj.put("balance",new Double(1000.21));
    	  obj.put("is_vip",new Boolean(true));
    	  obj.put("nickname",null);
    	  System.out.print(obj);
    }
  
}


