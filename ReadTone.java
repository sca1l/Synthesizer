
import java.awt.*;
import java.io.IOException;

public class ReadTone{
    
    private TextRW trw;
    
    private static final int SINESIZE_MODE = 0;
    private static final int SINEFREQ_MODE = 1;
    private static final int SINEVELO_MODE = 2;
    private static final int SINEATTE_MODE = 3;
    private static final int PITCHBEND_MODE = 4;
    private static final int DONE = -1;
    
    public ReadTone(){
        trw = new TextRW();
    }
    
    public String[] getStringArrayFromScript(String script) throws NumberFormatException{
        char[] charArray = script.toCharArray();
        
        if(charArray[0] == '*'){
            try{
                charArray = trw.read(WriteTone.folder + script.substring(1,charArray.length)).toCharArray();//テキストから読み込み
            }catch(NullPointerException e){
                return null;
            }
        }else if(charArray[0] != '@'){
            return null;
        }
        
        int readMode = -1;
        
        int sineSize = 0;
        for(int i=2; i<charArray.length; i++){
            if(charArray[i-1] == 's' && charArray[i] == '{'){
                readMode = SINESIZE_MODE;
            }else if(readMode != DONE && charArray[i] == '}'){
                readMode = DONE;
            }else if(readMode == SINESIZE_MODE){
                sineSize = sineSize*10 + Integer.parseInt(String.valueOf(charArray[i]));
            }
        }
        
        if(readMode != DONE){
            return null;
        }
        
        String[] values = new String[sineSize*3+1];
        java.util.Arrays.fill(values, "");
        int idx = 0;
        for(int i=2; i<charArray.length; i++){
            if(charArray[i-1] == 'f' && charArray[i] == '{'){
                readMode = SINEFREQ_MODE;
            }else if(charArray[i-1] == 'v' && charArray[i] == '{'){
                readMode = SINEVELO_MODE;
            }else if(charArray[i-1] == 'a' && charArray[i] == '{'){
                readMode = SINEATTE_MODE;
            }else if(charArray[i-1] == 'p' && charArray[i] == '{'){
                readMode = PITCHBEND_MODE;
            }else if(readMode != DONE && charArray[i] == '}'){
                readMode = DONE;
                idx = 0;
            }else{
                switch(readMode){
                    case SINEFREQ_MODE:
                        if(charArray[i] == ' '){
                            break;
                        }else if(charArray[i] == ','){
                            idx++;
                            break;
                        }
                        values[idx*3  +1] += charArray[i];
                        break;
                    case SINEVELO_MODE:
                        if(charArray[i] == ' '){
                            break;
                        }else if(charArray[i] == ','){
                            idx++;
                            break;
                        }
                        values[idx*3+1+1] += charArray[i];
                        break;
                    case SINEATTE_MODE:
                        if(charArray[i] == ' '){
                            break;
                        }else if(charArray[i] == ','){
                            idx++;
                            break;
                        }
                        values[idx*3+2+1] += charArray[i];
                        break;
                    case PITCHBEND_MODE:
                        if(charArray[i] == ' '){
                            break;
                        }else if(charArray[i] == ','){
                            idx++;
                            break;
                        }
                        values[0] += charArray[i];
                        break;
                }
            }
        }
        
        if(values[0].isEmpty()) values[0] = "0";
        
        return values;
    }
    
    public Tone readFromStrings(String[] strs, int parameterSize) throws NumberFormatException{
        int effective = parameterSize;
        for(int i=0; i<strs.length; i++){
            if(!(isTextDouble(strs[i]))){
                effective = i/3;
                break;
            }
        }
        
        int sineSize = effective;
        double[] sineFreq = new double[effective];
        double[] sineVelo = new double[effective];
        double[] sineAtte = new double[effective];
        double pitchBend = Double.parseDouble(strs[0]);
        
        for(int i=0; i<effective; i++){
            sineFreq[i] = Double.parseDouble(strs[i*3  +1]);
            sineVelo[i] = Double.parseDouble(strs[i*3+1+1]);
            sineAtte[i] = Double.parseDouble(strs[i*3+2+1]);
        }
        
        return new Tone(sineSize, sineFreq, sineVelo, sineAtte, pitchBend);
    }
    
    public Tone readFromTextFields(TextField[] tfs, int parameterSize, double pitchBend) throws NumberFormatException{
        String[] strs = new String[tfs.length+1];
        for(int i=0; i<tfs.length; i++){
            strs[i+1] = tfs[i].getText();
        }
        strs[0] = String.valueOf(pitchBend);
        
        return readFromStrings(strs, parameterSize);
    }
    
    public boolean isTextDouble(String str){
        if(str.isEmpty()){
            return false;
        }
        final char period = '.';
        int periodCount = 0;
        char[] charArray = str.toCharArray();
        for(int i=0; i<charArray.length; i++){
            char c = charArray[i];
            if(!isCharOk(c)){
                return false;
            }
            if(c == period){
                periodCount++;
                if(periodCount >= 2){
                    return false;
                }
            }
        }
        return true;
    }
    
    public boolean isCharOk(char c){
        final char[] safeChars = {'0','1','2','3','4','5','6','7','8','9','.','-'};
        for(char ok : safeChars){
            if(c == ok){
                return true;
            }
        }
        return false;
    }
}