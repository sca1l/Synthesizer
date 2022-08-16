

import java.util.*;

public class ReadScore{
    
    public static final int NOTNOTE = -10;
    public static final int REST = -100;
    public static final String SCOREFOLDER = "score_data/";
    
    private TextRW trw;
    private ReadTone readTone;
    private int octave = 2;
    private int pan = 3;
    private final double[] defaultPitch = {261.625, 277.182, 293.664, 311.126, 329.627, 349.228, 369.994, 391.995, 415.304, 440.0, 466.163, 493.883, 523.251};
    
    public ReadScore(){
        trw = new TextRW();
        readTone = new ReadTone();
    }
    
    public String[] readRootScore(String name){
        char[] charArray;
        try{
            charArray = trw.read(SCOREFOLDER + name).toCharArray();
        }catch(NullPointerException e){
            return null;
        }
        
        String[] strs = new String[2 + Ex999AutoPlay.MAXCHANNEL];
        java.util.Arrays.fill(strs, "");
        int idx = 0;
        for(int i=0; i<charArray.length; i++){
            if(charArray[i] == ' ' || charArray[i] == '\n'){
                idx++;
                if(idx >= strs.length){
                    break;
                }
                continue;
            }
            strs[idx] += charArray[i];
        }
        try{
            Integer.parseInt(strs[0]);
        }catch(NumberFormatException e){
            return null;
        }
        return strs;
    }
    
    public ArrayList<Note> readChannelScore(String name){
        ArrayList<Note> noteArray = new ArrayList<Note>();
        char[] charArray;
        try{
            charArray = trw.read(SCOREFOLDER + name).toCharArray();
        }catch(NullPointerException e){
            return null;
        }
        
        for(int i=0; i<charArray.length; i++){
            if(charArray[i] == '@' || charArray[i] == '*'){
                int newLine = i;
                while(charArray[newLine] != '\n'){
                    newLine++;
                }
                String toneScript = "";
                for(int j=i; j<newLine; j++) toneScript += charArray[j];
                String[] strs = readTone.getStringArrayFromScript(toneScript);
                Tone tone = readTone.readFromStrings(strs, (strs.length-1)/3);
                noteArray.add(new Note(NOTNOTE,NOTNOTE,true,tone,null));
                i = newLine;
            }else if(charArray[i] == 'o'){
                int newSpace = i;
                while(charArray[newSpace] != ' ' && charArray[newSpace] != '\n'){
                    newSpace++;
                }
                String octaveStr = "";
                for(int j=i+1; j<newSpace; j++) octaveStr += charArray[j];
                octave = Integer.parseInt(octaveStr);
                i = newSpace;
            }else if(charArray[i] == 'p'){
                int newSpace = i;
                while(charArray[newSpace] != ' ' && charArray[newSpace] != '\n'){
                    newSpace++;
                }
                String panStr = "";
                for(int j=i+1; j<newSpace; j++) panStr += charArray[j];
                pan = Integer.parseInt(panStr);
                i = newSpace;
            }else if(charArray[i] == 'c' || 
                     charArray[i] == 'd' || 
                     charArray[i] == 'e' || 
                     charArray[i] == 'f' || 
                     charArray[i] == 'g' || 
                     charArray[i] == 'a' || 
                     charArray[i] == 'b' || 
                     charArray[i] == 'r'){
                int newSpace = i;
                while(charArray[newSpace] != ' ' && charArray[newSpace] != '\n'){
                    newSpace++;
                }
                String noteStr = "";
                for(int j=i; j<newSpace; j++) noteStr += charArray[j];
                noteArray.add(createNote(noteStr));
                i = newSpace;
            }
        }
        return noteArray;
    }
    
    public Note createNote(String str){
        char[] charArray = str.toCharArray();
        int phoneticValueStart = 1;
        int phoneticValueEnd = charArray.length-1;
        int pitch = 0;
        switch(charArray[0]){
            case 'c':
                pitch = 0;
                break;
            case 'd':
                pitch = 2;
                break;
            case 'e':
                pitch = 4;
                break;
            case 'f':
                pitch = 5;
                break;
            case 'g':
                pitch = 7;
                break;
            case 'a':
                pitch = 9;
                break;
            case 'b':
                pitch = 11;
                break;
            case 'r':
                pitch = REST;
                break;
        }
        if(charArray[1] == '+'){
            pitch++;
            phoneticValueStart = 2;
        }else if(charArray[1] == '-'){
            pitch--;
            phoneticValueStart = 2;
        }
        
        boolean isNewNote;
        if(charArray[phoneticValueEnd] == '!'){
            isNewNote = false;
            phoneticValueEnd--;
        }else{
            isNewNote = true;
        }
        
        String phoneticValueStr = "";
        for(int i=phoneticValueStart; i<charArray.length; i++) phoneticValueStr += charArray[i];
        int phoneticValue = Integer.parseInt(phoneticValueStr);
        
        double freq;
        if(pitch == REST){
            freq = REST;
        }else if(pitch < 0){
            freq = defaultPitch[12 + pitch] * Math.pow(2, octave) / 2;
        }else{
            freq = defaultPitch[pitch] * Math.pow(2, octave);
        }
        
        return new Note(freq, phoneticValue, isNewNote, null, pan);
    }
}