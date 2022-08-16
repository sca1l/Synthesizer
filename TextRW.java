import java.io.*;

public class TextRW{
    
    public String read(String filename){
        StringBuffer sb = new StringBuffer();
        try{
            FileInputStream fis = new FileInputStream(filename);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            while((line = br.readLine()) != null/*br.ready()*/){
                sb.append(line + "\n");
            }
            br.close();
            fis.close();
            isr.close();
        }catch(FileNotFoundException e){
            //e.printStackTrace();
            return null;
        }catch(IOException e){
            //e.printStackTrace();
            return null;
        }
        
        return sb.toString();
    }
    
    public boolean write(String filename, String text){
        try{
            FileOutputStream fos = new FileOutputStream(filename);
            OutputStreamWriter osr = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osr);
            bw.write(text);
            bw.flush();
            bw.close();
            fos.close();
            osr.close();
        }catch(IOException e){
            //e.printStackTrace();
            return false;
        }
        
        return true;
    }
}