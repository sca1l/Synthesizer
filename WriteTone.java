import java.text.DecimalFormat;

public class WriteTone{
    
    private TextRW trw;
    private DecimalFormat df = new DecimalFormat("0.#########");
    public static final String folder = "tone_data/";
    
    public WriteTone(){
        trw = new TextRW();
    }
    
    public boolean export(String name, Tone tone){
        int sineSize = tone.getSineSize();
        double[] sineFreq = tone.getSineFreq();
        double[] sineVelo = tone.getSineVelo();
        double[] sineAtte = tone.getSineAtte();
        double pichBend = tone.getPitchBend();
        
        if(sineSize == 0){
            return false;
        }
        
        String text = buildString(name, sineSize, sineFreq, sineVelo, sineAtte, pichBend);
        
        trw.write(folder + name, text);
        
        return true;
    }
    
    public String buildString(String name, int sineSize, 
                                           double[] sineFreq, 
                                           double[] sineVelo, 
                                           double[] sineAtte, 
                                           double pichBend){
        
        String str = "";//書き出しテキスト組み立て用
        
        //Toneの明示
        str += "@";
        
        //名前
        str += "/* " + name + " */ ";
        
        //sineSize
        str += "s{" + sineSize + "} ";
        
        //sineFreq
        str += "f{" + df.format(sineFreq[0]);
        for(int i=1; i<sineSize; i++){
            str += "," + df.format(sineFreq[i]);
        }
        str += "} ";
        
        //sineVelo
        str += "v{" + df.format(sineVelo[0]);
        for(int i=1; i<sineSize; i++){
            str += "," + df.format(sineVelo[i]);
        }
        str += "} ";
        
        //sineAtte
        str += "a{" + df.format(sineAtte[0]);
        for(int i=1; i<sineSize; i++){
            str += "," + df.format(sineAtte[i]);
        }
        str += "}";
        
        //pichBend
        if(pichBend != 0){
            str += " p{" + pichBend + "}";
        }
        
        return str;
    }
    
}