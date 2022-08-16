

import javax.sound.sampled.*;

public class Player implements Runnable{
    
    public static final int BITS = 16;
    public static final int HZ = 44100;
    public static final int MONO = 1;
    
    public static final int FRAME_W = 500;
    public static final int FRAME_H = 250;
    
    private SourceDataLine source;
    
    private Thread th = null;
    private byte[] silent = {0,0};
    
    private boolean isSoundPlaying = false;
    
    
    public Player(){
        try{
            AudioFormat linear = new AudioFormat(HZ, BITS, MONO, true, false);
            
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, linear);
            source = (SourceDataLine)AudioSystem.getLine(info);
            
            source.open( linear );
            
            source.start();
            
        }catch (LineUnavailableException e){
            e.printStackTrace();
        }
        
        th = new Thread(this);
        th.start();
    }
    
    public void run(){
        while(th != null){
            if(!isSoundPlaying){
                source.write(silent, 0, silent.length);
                source.drain();
            }
        }
    }
    
    public void playSound(byte[] sound){
        isSoundPlaying = true;
        source.flush();
        source.write(sound, 0, sound.length);
        
        isSoundPlaying = false;
    }
    
    public void end(){
        source.stop();
        
        source.close();
    }
    
}
