
public class Tone{
    
    private int sineSize;
    
    private double[] sineFreq;
    
    private double[] sineVelo;
    
    private double[] sineAtte;
    
    private double pitchBend;
    
    public Tone(int sineSize, double[] sineFreq, double[] sineVelo, double[] sineAtte){
        this.sineSize = sineSize;
        this.sineFreq = sineFreq;
        this.sineVelo = sineVelo;
        this.sineAtte = sineAtte;
        this.pitchBend = 0;
    }
    
    public Tone(int sineSize, double[] sineFreq, double[] sineVelo, double[] sineAtte, double pitchBend){
        this.sineSize = sineSize;
        this.sineFreq = sineFreq;
        this.sineVelo = sineVelo;
        this.sineAtte = sineAtte;
        this.pitchBend = pitchBend;
    }
    
    public int getSineSize(){
        return sineSize;
    }
    
    public double[] getSineFreq(){
        return sineFreq;
    }
    
    public double[] getSineVelo(){
        return sineVelo;
    }
    
    public double[] getSineAtte(){
        return sineAtte;
    }
    
    public double getPitchBend(){
        return pitchBend;
    }
    
    public void setPitchBend(double pitchBend){
        this.pitchBend = pitchBend;
    }
}