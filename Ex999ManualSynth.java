import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Ex999ManualSynth extends Frame implements WindowListener, MouseListener, MouseMotionListener, ActionListener{
    
    public static final int FRAME_W = 1250;
    public static final int FRAME_H = 450;
    
    private Synth synth;
    private Tone nowTone;
    private Player player;
    private ReadTone readTone;
    private WriteTone writeTone;
    private int octave = 2;
    private double pitchBend = 0.0;
    
    private final double[] defaultPitch = {261.625, 277.182, 293.664, 311.126, 329.627, 349.228, 369.994, 391.995, 415.304, 440.0, 466.163, 493.883, 523.251};
    private double[] pitch = (double[])defaultPitch.clone();
    
    private int soundLength = Player.HZ * Player.BITS / 8 / 2;
    
    private final int parameterSize = 20;
    private Panel[] paraPanels = new Panel[parameterSize];
    private TextField[] paraTextFields = new TextField[parameterSize*3];
    private Button[] prewaveButtons = new Button[4];
    private String[] prewaveButtonLabels = {"sine", "square", "triangle", "saw"};
    private Button applyButton = new Button("Apply");
    private TextArea logTextArea = new TextArea();
    private TextField[] impexpTextFields = new TextField[2];
    private int[] impexpTextFieldsLength = {140,147};
    private Button[] impexpButtons = new Button[2];
    private String[] impexpButtonLabels = {"open", "save"};
    private String[] impexpLabels = {"[Import] Enter Script or Name","[Export] Enter Name"};
    private static final int IMPORT = 0;
    private static final int EXPORT = 1;
    private Panel acceptArea = new AcceptAreaPanel(this);
    private Label allGainArea;
    
    private int draggingY;
    private DecimalFormat df = new DecimalFormat("0.#########");
    
    private String[] preSine = {"440", "0.1", "0"};
    private String[] preSquare = {"440", "0.1", "0", "1320", "0.03333333333333333", "0", "2200", "0.02", "0", "3080", "0.014285714285714287", "0", "3960", "0.011111111111111112", "0", "4840", "0.009090909090909092", "0", "5720", "0.007692307692307693", "0", "6600", "0.006666666666666667", "0", "7480", "0.0058823529411764705", "0", "8360", "0.005263157894736842", "0", "9240", "0.004761904761904762", "0", "10120", "0.004347826086956522", "0", "11000", "0.004", "0", "11880", "0.003703703703703704", "0", "12760", "0.003448275862068966", "0", "13640", "0.0032258064516129032", "0", "14520", "0.0030303030303030303", "0", "15400", "0.002857142857142857", "0", "16280", "0.002702702702702703", "0", "17160", "0.002564102564102564", "0"};
    private String[] preTriangle = {"440", "0.1", "0", "1320", "-0.011111111", "0", "2200", "0.004", "0", "3080", "-0.002040816", "0", "3960", "0.001234568", "0", "4840", "-0.000826446", "0", "5720", "0.000591716", "0", "6600", "-0.000444444", "0", "7480", "0.000346021", "0", "8360", "-0.000277008", "0", "9240", "0.000226757", "0", "10120", "-0.000189036", "0", "11000", "0.00016", "0", "11880", "-0.000137174", "0", "12760", "0.000118906", "0", "13640", "-0.000104058", "0", "14520", "0.000091827", "0", "15400", "-0.000081633", "0", "16280", "0.000073046", "0", "17160", "-0.000065746", "0"};
    private String[] preSaw = {"440", "0.1", "0", "880", "0.05", "0", "1320", "0.03333333333333333", "0", "1760", "0.025", "0", "2200", "0.02", "0", "2640", "0.016666666666666666", "0", "3080", "0.014285714285714287", "0", "3520", "0.0125", "0", "3960", "0.011111111111111112", "0", "4400", "0.01", "0", "4840", "0.009090909090909092", "0", "5280", "0.008333333333333333", "0", "5720", "0.007692307692307693", "0", "6160", "0.0071428571428571435", "0", "6600", "0.006666666666666667", "0", "7040", "0.00625", "0", "7480", "0.0058823529411764705", "0", "7920", "0.005555555555555556", "0", "8360", "0.005263157894736842", "0", "8800", "0.005", "0"};
    
    
    public static void main(String[] args){
        new Ex999ManualSynth("Ex999ManualSynth");
    }
    
    public Ex999ManualSynth(String title){
        super(title);
        setResizable(false);
        setSize(FRAME_W,FRAME_H);
        
        this.synth = new Synth();
        this.nowTone = synth.getTone();
        this.player = new Player();
        this.readTone = new ReadTone();
        this.writeTone = new WriteTone();
        
        setLayout(new GridLayout(2,1));
        Panel top = new Panel();
        Panel bottom = new Panel();
        add(top);
        add(bottom);
        
        //top
        top.setLayout(new BorderLayout());
        Panel topWest = new Panel();
        topWest.setLayout(new GridLayout(1,2));
        Panel presets = new Panel();
        topWest.add(presets);
        top.add(topWest, "West");
        top.add(logTextArea, "East");
        presets.setLayout(new GridLayout(4,1));
        for(int i=0; i<prewaveButtons.length; i++){
            prewaveButtons[i] = new Button(prewaveButtonLabels[i]);
            presets.add(prewaveButtons[i]);
        }
        allGainArea = new Label("DragToAllGain");
        allGainArea.setBackground(new Color(250,250,252));
        allGainArea.addMouseListener(this);
        allGainArea.addMouseMotionListener(this);
        topWest.add(allGainArea);
        top.add(acceptArea);
        
        
        //bottom
        bottom.setLayout(new BorderLayout());
        Panel impexpBasePanel = new Panel();
        impexpBasePanel.setLayout(new GridLayout(2,1));
        Panel[] impexpPanel = new Panel[2];
        for(int i=0; i<impexpPanel.length; i++){
            impexpPanel[i] = new Panel();
            impexpPanel[i].setLayout(new FlowLayout());
            
            impexpButtons[i] = new Button(impexpButtonLabels[i]);
            impexpButtons[i].addActionListener(this);
            Label tmpLabel = new Label(impexpLabels[i]);
            impexpTextFields[i] = new TextField(impexpTextFieldsLength[i]);
            
            impexpPanel[i].add(tmpLabel);
            impexpPanel[i].add(impexpTextFields[i]);
            impexpPanel[i].add(impexpButtons[i]);
            impexpBasePanel.add(impexpPanel[i]);
        }
        bottom.add(impexpBasePanel, "South");
        
        Panel paramerterBasePanel = new Panel();
        paramerterBasePanel.setLayout(new GridLayout(1,parameterSize+1));
        for(int i=0; i<paraPanels.length; i++){
            paraPanels[i] = new Panel();
            paraPanels[i].setLayout(new GridLayout(4,1));
        }
        for(int i=0; i<paraTextFields.length; i++){
            paraTextFields[i] = new TextField(5);
        }
        for(int i=0; i<parameterSize; i++){
            Panel[] panelTmps = new Panel[4];
            for(int j=0; j<panelTmps.length; j++){
                panelTmps[j] = new Panel();
                panelTmps[j].setLayout(new FlowLayout());
            }
            Label sineTitle = new Label("sine" + (i+1));
            panelTmps[0].add(sineTitle);
            panelTmps[1].add(paraTextFields[i*3  ]);
            panelTmps[2].add(paraTextFields[i*3+1]);
            panelTmps[3].add(paraTextFields[i*3+2]);
            
            for(int k=0; k<panelTmps.length; k++){
                paraPanels[i].add(panelTmps[k]);
            }
        }
        for(int i=0; i<paraPanels.length; i++){
            paramerterBasePanel.add(paraPanels[i]);
        }
        paramerterBasePanel.add(applyButton);
        bottom.add(paramerterBasePanel);
        
        for(int i=0; i<prewaveButtons.length; i++){
            prewaveButtons[i].addActionListener(this);
        }
        applyButton.addActionListener(this);
        
        addWindowListener(this);
        setVisible(true);
        
    }
    
    
    public void octaveIncrement(){
        for(int i=0; i<pitch.length; i++){
            pitch[i] *= 2;
        }
        octave++;
        logTextArea.append("オクターブ" + octave + "\n");
    }
    
    public void octaveDecrement(){
        if(octave >= 1){
            for(int i=0; i<pitch.length; i++){
                pitch[i] /= 2;
            }
            octave--;
        }
        logTextArea.append("オクターブ" + octave + "\n");
    }
    
    public void actionPerformed(ActionEvent evt){
        Button evtbt = (Button)evt.getSource();
        if(evtbt == prewaveButtons[0]){                //sine
            for(int i=0; i<paraTextFields.length; i++){
                if(i<preSine.length){
                    paraTextFields[i].setText("" + preSine[i]);
                }else{
                    paraTextFields[i].setText("");
                }
            }
            apply();
        }else if(evtbt == prewaveButtons[1]){        //square
            for(int i=0; i<paraTextFields.length; i++){
                paraTextFields[i].setText("" + preSquare[i]);
            }
            apply();
        }else if(evtbt == prewaveButtons[2]){        //triangle
            for(int i=0; i<paraTextFields.length; i++){
                paraTextFields[i].setText("" + preTriangle[i]);
            }
            apply();
        }else if(evtbt == prewaveButtons[3]){        //saw
            for(int i=0; i<paraTextFields.length; i++){
                paraTextFields[i].setText("" + preSaw[i]);
            }
            apply();
        }else if(evtbt == applyButton){
            apply();
        }else if(evtbt == impexpButtons[IMPORT]){
            String script = impexpTextFields[IMPORT].getText();
            String[] values = readTone.getStringArrayFromScript(script);
            if(values != null){
                if(values[0] != null)pitchBend = Double.parseDouble(values[0]);
                for(int i=1; i<(paraTextFields.length+1)&&i<values.length; i++){
                    paraTextFields[i-1].setText(values[i]);
                }
                logTextArea.append("正常に読み込みました。\n");
                apply();
            }else{
                logTextArea.append("読み込めませんでした。\n");
            }
        }else if(evtbt == impexpButtons[EXPORT]){
            String name = impexpTextFields[EXPORT].getText();
            if(name.isEmpty()){
                logTextArea.append("名前を付けてください。\n");
            }else if(writeTone.export(name, nowTone)){
                logTextArea.append("正常にエクスポートしました。\n");
            }else{
                logTextArea.append("エクスポートできませんでした。\n");
            }
        }
    }
    
    public void apply(){
        Tone newTone;
        try{
            newTone = readTone.readFromTextFields(paraTextFields, parameterSize, pitchBend);
        }catch(NumberFormatException e){
            logTextArea.append("音色の変更を行うことができませんでした。無効な値が存在します。\n");
            return;
        }
        
        int newToneSineSize = newTone.getSineSize();
        if(newToneSineSize == 0){
            logTextArea.append("音色の変更を行うことができませんでした。\n");
            return;
        }
        
        synth.setTone(newTone);
        this.nowTone = synth.getTone();
        
        if(newToneSineSize == parameterSize){
            logTextArea.append("音色の変更を行いました。全てのsineが正常に書き込まれました。\n");
        }else{
            logTextArea.append("音色の変更を行いました。\nただし、sine" + (newToneSineSize+1) + "の値が無効だったためsine" + (newToneSineSize+1) + "以降は書き込まれませんでした。\n");
        }
        
        acceptArea.requestFocusInWindow();
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
    
    public void mouseClicked(MouseEvent e){}
    public void mouseEntered(MouseEvent e){}
    public void mouseExited(MouseEvent e){}
    public void mousePressed(MouseEvent e){
        draggingY = e.getY();
    }
    public void mouseReleased(MouseEvent e){
        apply();
    }

    public void mouseDragged(MouseEvent e){
        int newDraggingY = e.getY();
        int ydiff = draggingY - newDraggingY;
        double multiply = 1.0 + ydiff/100.0;
        try{
            Tone tmpTone = readTone.readFromTextFields(paraTextFields, parameterSize, pitchBend);
            double[] tmpSineVelo = tmpTone.getSineVelo();
            for(int i=0; i<tmpSineVelo.length; i++){
                tmpSineVelo[i] *= multiply;
                paraTextFields[i*3+1].setText(df.format(tmpSineVelo[i]));
            }
        }catch(NumberFormatException ex){
            logTextArea.append("不正な値があるためゲインの操作ができません。\n");
        }
        draggingY = e.getY();
    }
    public void mouseMoved(MouseEvent e){}
    
    //------------------------------------------------------------------------------
    
    public class AcceptAreaPanel extends Panel implements KeyListener, FocusListener{
        
        private Ex999ManualSynth mainFrame;
        
        private boolean isActive;
        private BufferedImage active, deactive;
        private String activeImagePath = "res/awkhz.png";
        private String deactiveImagePath = "res/awkhz2.png";
        
        private final int LOGO_X = 300;
        private final int LOGO_Y = 0;
        
        public AcceptAreaPanel(Ex999ManualSynth mainFrame){
            this.mainFrame = mainFrame;
            try {
                active = ImageIO.read(getClass().getResource(activeImagePath));
                deactive = ImageIO.read(getClass().getResource(deactiveImagePath));
            } catch (IOException ex) {
                ex.printStackTrace();
                active = null;
                deactive = null;
            }
            
            addFocusListener(this);
            addKeyListener(this);
        }
        
        public void keyReleased(KeyEvent e){}
        public void keyTyped(KeyEvent e){}
    
        public void keyPressed(KeyEvent e){
            int keyCode = e.getKeyCode();
            switch(keyCode){
                case KeyEvent.VK_Z:
                    player.playSound(synth.getSound(pitch[0], soundLength, true));
                    break;
                case KeyEvent.VK_S:
                    player.playSound(synth.getSound(pitch[1], soundLength, true));
                    break;
                case KeyEvent.VK_X:
                    player.playSound(synth.getSound(pitch[2], soundLength, true));
                    break;
                case KeyEvent.VK_D:
                    player.playSound(synth.getSound(pitch[3], soundLength, true));
                    break;
                case KeyEvent.VK_C:
                    player.playSound(synth.getSound(pitch[4], soundLength, true));
                    break;
                case KeyEvent.VK_V:
                    player.playSound(synth.getSound(pitch[5], soundLength, true));
                    break;
                case KeyEvent.VK_G:
                    player.playSound(synth.getSound(pitch[6], soundLength, true));
                    break;
                case KeyEvent.VK_B:
                    player.playSound(synth.getSound(pitch[7], soundLength, true));
                    break;
                case KeyEvent.VK_H:
                    player.playSound(synth.getSound(pitch[8], soundLength, true));
                    break;
                case KeyEvent.VK_N:
                    player.playSound(synth.getSound(pitch[9], soundLength, true));
                    break;
                case KeyEvent.VK_J:
                    player.playSound(synth.getSound(pitch[10], soundLength, true));
                    break;
                case KeyEvent.VK_M:
                    player.playSound(synth.getSound(pitch[11], soundLength, true));
                    break;
               case KeyEvent.VK_COMMA:
                    player.playSound(synth.getSound(pitch[12], soundLength, true));
                    break;
                    
                case KeyEvent.VK_UP:
                    octaveIncrement();
                    break;
                case KeyEvent.VK_DOWN:
                    octaveDecrement();
                    break;
            }
        }
        
        public void paint(Graphics g){
            if(isActive){
                g.drawImage(active, LOGO_X, LOGO_Y , acceptArea);
            }else{
                g.drawImage(deactive, LOGO_X, LOGO_Y , acceptArea);
            }
        }
        
        public void focusGained(FocusEvent e){
            isActive = true;
            repaint();
        }
        public void focusLost(FocusEvent e){
            isActive = false;
            repaint();
        }
    }
}

