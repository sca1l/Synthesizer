import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Ex999AutoPlay extends Frame implements WindowListener, ActionListener, Runnable{
    
    public static final int FRAME_W = 350;
    public static final int FRAME_H = 350;
    public static final int MAXCHANNEL = 10;
    
    private Player player;
    private Channel[] channels;
    private SongBuilder songBuilder;
    private ReadScore readScore;
    private int bpm;
    private boolean ready = false;
    private boolean playing = false;
    
    private TextField tf;
    private int tfLength = 20;
    private Button bt;
    private Label titleLabel;
    private LogoPanel logoPanel;
    
    private Thread th = null;
    
    public static void main(String[] args){
        new Ex999AutoPlay("Ex999AutoPlay");
    }
    
    public Ex999AutoPlay(String title){
        super(title);
        setResizable(false);
        //setUndecorated(true);
        setSize(FRAME_W,FRAME_H);
        
        setLayout(new BorderLayout());
        Panel bottom = new Panel();
        bottom.setLayout(new GridLayout(2,1));
        Panel labelPanel = new Panel();
        titleLabel = new Label("                                                            ");//
        labelPanel.add(titleLabel);
        Panel importPanel = new Panel();
        tf = new TextField(tfLength);
        importPanel.add(tf);
        bt = new Button("open");
        importPanel.add(bt);
        bt.addActionListener(this);
        bottom.add(labelPanel);
        bottom.add(importPanel);
        add(bottom, "South");
        logoPanel = new LogoPanel();
        add(logoPanel);
        
        player = new Player();
        channels = new Channel[MAXCHANNEL];
        
        readScore = new ReadScore();
        
        th = new Thread(this);
        
        addWindowListener(this);
        setVisible(true);
    }
    
    public boolean buildSong(){
        int maxLength = 0;
        for(Channel ch : channels){
            int length = ch.getLength();
            if(length > maxLength){
                maxLength = length;
            }
        }
        boolean isChannelLiving = false;
        songBuilder = new SongBuilder(maxLength);
        for(int i=0; i<channels.length; i++){
            if(channels[i].writeToSongBuilder(songBuilder)){
                isChannelLiving = true;
            }
        }
        
        if(isChannelLiving){
            return songBuilder.build();
        }
        return false;
    }
    
    public boolean readRootFile(String filename){
        String[] strs = readScore.readRootScore(filename);
        try{
            this.bpm = Integer.parseInt(strs[0]);
        }catch(NumberFormatException e){
            return false;
        }
        for(int i=2; i<strs.length; i++){
            channels[i-2] = new Channel(strs[i], this.bpm);
        }
        int gotLength = 0;
        for(int i=0; i<channels.length; i++){
            int length = channels[i].getLength();
            if(gotLength < length){
                gotLength = length;
            }
        }
        if(gotLength > 0){
            titleLabel.setText(strs[1]);
            return true;
        }
        return false;
    }
    
    
    
    public void actionPerformed(ActionEvent evt){
        Button evtbt = (Button)evt.getSource();
        if(evtbt == bt && ready == false){
            boolean couldChannelsInit = readRootFile(tf.getText());
            if(couldChannelsInit){
                if(buildSong()){
                    ready = true;
                    bt.setLabel("play");
                }
            }
            
        }else if(evtbt == bt && ready == true){
            playing = true;
            th.start();
        }
    }
    
    public void run(){
        player.playSound(songBuilder.getSong());
        playing = false;
        ready = false;
        bt.setLabel("open");
    }
    
    public void windowClosing(WindowEvent e){
        dispose();
    }
    public void windowOpened(WindowEvent e){}
    public void windowIconified(WindowEvent e){}
    public void windowDeiconified(WindowEvent e){}
    public void windowClosed(WindowEvent e){}
    public void windowActivated(WindowEvent e){}
    public void windowDeactivated(WindowEvent e){}
    
    public class LogoPanel extends Panel implements Runnable{
        private BufferedImage playingImage;
        private BufferedImage guneImage;
        private BufferedImage readyImage;
        private String playingImagePath = "res/awkhzplaying1.png";
        private String guneImagePath = "res/awkhzplaying2.png";
        private String readyImagePath = "res/awkhz2.png";
        
        private int gunePosition = 0;
        private final int guneLoopPosition = 135;
        
        private int LOGO_X = FRAME_W/2 - 200/2;
        private int LOGO_Y = FRAME_H/2 - 200/2 -30;
        
        private Thread th = null;
        private Image offs = null;
        private Graphics offg = null;
        
        public LogoPanel(){
            try {
                playingImage = ImageIO.read(getClass().getResource(playingImagePath));
                guneImage = ImageIO.read(getClass().getResource(guneImagePath));
                readyImage = ImageIO.read(getClass().getResource(readyImagePath));
            } catch (IOException ex) {
                ex.printStackTrace();
                playingImage = null;
                guneImage = null;
                readyImage = null;
            }
            
            th = new Thread(this);
            th.start();
        }
        
        public void addNotify(){
            super.addNotify();
            offs = createImage(FRAME_W,FRAME_H);
            offg = offs.getGraphics();
        }
        
        public void update(Graphics g){
            paint(g);
        }
        
        public void run(){
            while(th != null){
                gunePosition++;
                gunePosition = gunePosition%guneLoopPosition;
                
                repaint();
                
                try{
                    Thread.sleep(30);
                }catch(InterruptedException e){
                }
            }
        }
        
        public void paint(Graphics g){
            offg.setColor(Color.WHITE);
            offg.fillRect(0,0,FRAME_W,FRAME_H);
            if(playing){
                offg.drawImage(playingImage, LOGO_X, LOGO_Y, this);
                offg.drawImage(guneImage, LOGO_X-gunePosition-guneLoopPosition, LOGO_Y, this);
                offg.drawImage(guneImage, LOGO_X-gunePosition, LOGO_Y, this);
            }else if(ready){
                offg.drawImage(readyImage, LOGO_X, LOGO_Y, this);
            }
            g.drawImage(offs,0,0,this);
        }
    }
    
}