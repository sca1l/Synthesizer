
public class Synth{
    
    private Tone tone;
    
    private double[] sineFreqTmp;
    
    private double[] sineVeloTmp;
    
    private double offset;
    
    public Synth(){
        double[] sineFreq = {440.0};
        double[] sineVelo = {0.1};
        double[] sineAtte = {0.0};
        tone = new Tone(1, sineFreq, sineVelo, sineAtte);
        sineFreqTmp = (double[])tone.getSineFreq().clone();
        sineVeloTmp = (double[])tone.getSineVelo().clone();
        offset = 0;
    }
    
    public void setTone(Tone tone){
        this.tone = tone;
    }
    
    public Tone getTone(){
        return tone;
    }
    
    public byte[] getSound(double freq, int length, boolean isNewNote){
        byte[] sound = new byte[length];
        short[] soundTmp = getSoundShortArray(freq, length, isNewNote);
        
        for(int n = 0; n < sound.length/2; n++){
            short tmp = soundTmp[n];
            sound[n*2  ] = (byte)((tmp&0xff));
            sound[n*2+1] = (byte)((tmp&0xff00) >> 8);
        }
        return sound;
    }
    
    public short[] getSoundShortArray(double freq, int length, boolean isNewNote){
        if(isNewNote){
            sineFreqTmp = (double[])tone.getSineFreq().clone();
            sineVeloTmp = (double[])tone.getSineVelo().clone();
            offset = 0;
        }
        
        byte[] sound = new byte[length];
        short[] soundTmp = new short[length/2];
        
        int sineSize = tone.getSineSize();
        double[] sineAtte = tone.getSineAtte();
        double pitchBend = tone.getPitchBend();
        for(int i=0; i<sineSize; i++){
            for(int n = 0; n < sound.length/2; n++){
                soundTmp[n] += (short)(Short.MAX_VALUE * sineVeloTmp[i] * Math.sin(2.0 * Math.PI * (sineFreqTmp[i] / 440.0 * freq) * n / Player.HZ));
                
                sineFreqTmp[i] *= 1.0 + (pitchBend/100000.0);
                
                sineVeloTmp[i] *= 1.0 - Math.abs(sineAtte[i]/100.0);
            }
        }
        return soundTmp;
    }   
}
