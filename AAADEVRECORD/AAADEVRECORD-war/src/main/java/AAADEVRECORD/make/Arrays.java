package AAADEVRECORD.make;
import java.io.File;

import org.json.JSONObject;

/**
 *
 * @author umansilla
 */
public class Arrays {
    /**
     * @return 
     */
    public static JSONObject main(String recordLocationOnHttpServer) {
        // TODO code application logicumansilla\\Documents\\NetBeansProjects\\Watson2\\src\\main\\webapp\\grabaciones" here
        JSONObject json = new JSONObject();
        int index = 0;
        String dirPath = recordLocationOnHttpServer;
        File dir = new File(dirPath); 
        String[] files = dir.list();
        if (files.length == 0) {
            System.out.println("The directory is empty");
        } else {

            for (String aFile : files) {
               json.put("Index "+index , aFile);
               index++;
            }
            
 
          
            System.out.println(json);
        
        
            
        }
        
        
        return json;
    }
    
}