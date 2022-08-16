


public class Note{
    
    public final double freq;
    public final int phoneticValue;
    public final boolean isNewNote;
    public final Tone tone;
    public final Integer pan;
    
    public Note(double freq, int phoneticValue, boolean isNewNote, Tone tone, Integer pan){
        this.freq = freq;
        this.phoneticValue = phoneticValue;
        this.isNewNote = isNewNote;
        this.tone = tone;
        this.pan = pan;
    }
}