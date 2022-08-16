
public class SongBuilder{
    
    private int length;
    private short[] songData;
    private byte[] song;
    private int index;
    
    public SongBuilder(int length){
        this.length = length;
        this.songData = new short[length];//‚ 
        this.index = 0;
    }
    
    public void writeNote(Note n, Synth synth, int bpm){
        int noteLength = getLengthFromValue(bpm, n.phoneticValue, Player.HZ);
        
        short[] data = synth.getSoundShortArray(n.freq, noteLength, n.isNewNote);
        
        for(int i = 0; i<data.length; i++){
            songData[index + i] += data[i];
        }
        index += noteLength;
    }
    
    public void writeRest(Note n, int bpm){
        int noteLength = getLengthFromValue(bpm, n.phoneticValue, Player.HZ);
        
        int j = 0;
        for(int i=index; j<noteLength; i++){
            j++;
            index++;
        }
    }
    
    public void channelWriteEnd(){
        index = 0;
    }
    
    public boolean build(){
        if(songData == null){
            return false;
        }
        
        this.song = new byte[length*2];
        for(int n = 0; n < length; n++){
            short tmp = songData[n];
            this.song[n*2  ] = (byte)((tmp&0xff));
            this.song[n*2+1] = (byte)((tmp&0xff00) >> 8);
        }
        return true;
    }
    
    public byte[] getSong(){
        return this.song;
    }
    
    public static int getLengthFromValue(int bpm, int phoneticValue, int hz){
        return (int)(60.0 / bpm * 4 / phoneticValue * hz);
    }
    
    public static int getLengthFromMeasure(int bpm, int measure, int hz){
        return (int)(60.0 / bpm * 4 * measure * hz);
    }
}