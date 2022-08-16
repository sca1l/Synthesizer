import java.util.*;

public class Channel{
    private int bpm;
    private Synth synth;
    private ReadScore readScore;
    private ArrayList<Note> notes;
    
    public Channel(String path, int bpm){
        this.bpm = bpm;
        this.synth = new Synth();
        this.readScore = new ReadScore();
        this.notes = readScore.readChannelScore(path);
    }
    
    public int getLength(){
        if(notes == null){
            return 0;
        }
        
        double measure = 0;
        for(Note n : notes){
            if(n.tone != null){
                continue;
            }
            measure += 1.0 / n.phoneticValue;
        }
        int measureInt = (int)(measure)+1;
        
        return SongBuilder.getLengthFromMeasure(bpm, measureInt, Player.HZ);
    }
    
    public boolean writeToSongBuilder(SongBuilder songBuilder){
        if(notes == null){
            return false;
        }
        
        for(Note n : notes){
            if(n.tone != null){
                synth.setTone(n.tone);
                continue;
            }else if(n.freq == ReadScore.REST){
                songBuilder.writeRest(n, bpm);
            }else{
                songBuilder.writeNote(n, synth, bpm);
            }
        }
        songBuilder.channelWriteEnd();
        return true;
    }
}