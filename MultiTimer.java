import java.util.TreeMap;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.KeyCode;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.layout.VBox;
import javafx.stage.WindowEvent;
import javafx.scene.Scene;
import javafx.scene.Node;

//import javafx.scene.layout.Priority;
import javafx.geometry.Insets;
import javafx.scene.text.Font;

import javafx.stage.StageStyle;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.MouseButton;
import java.lang.Math;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.StackPane;

public class MultiTimer extends Application {
    TreeMap<KeyCode,Timer> timers = new TreeMap<>();
    static Font f = new Font(30);
    public static void main(String[] args) {
	launch(args);
    }
    private double xOffset = 0;
    private double yOffset = 0;
    static private boolean paused = false;
    static private long pauseTime;
    Rectangle topDiv;

    public boolean addUp = true;

    public void start(Stage ps) {
	ps.initStyle(StageStyle.TRANSPARENT);
	ps.setAlwaysOnTop(true);
	
	VBox pane = new VBox();
	topDiv = new Rectangle(210,10);
	topDiv.setFill(new Color(1,1,1,1));
	topDiv.setArcWidth(5);
	topDiv.setArcHeight(5);
	pane.getChildren().add(topDiv);
	pane.setOnMousePressed(new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent me) {
		    xOffset = me.getSceneX();
		    yOffset = me.getSceneY();
		}
	    });
	pane.setOnMouseDragged(new EventHandler<MouseEvent>() {
		@Override
		public void handle(MouseEvent me) {
		    if(me.getButton().equals(MouseButton.PRIMARY)) {
			//move
			ps.setX(me.getScreenX() - xOffset);
			ps.setY(me.getScreenY() - yOffset);
		    } else if(me.getButton().equals(MouseButton.SECONDARY)) {
			//rescale
			double dx = me.getScreenX() - xOffset;
			double dy = me.getScreenY() - yOffset;
			//consider allowing change of aspect ratio?
			double distance = Math.sqrt(dx*dx+dy*dy);
		    }
		}
	    });
	pane.setPadding(new Insets(10));
	ps.setTitle("MultiTimer");
	Thread updateThread = new Thread() {
		@Override
		public void run() {
		    while(true) {
			try {
			    Thread.sleep(76);
			} catch(Exception e) {}
			//System.out.println("updated");
			for(Timer t : timers.values())
			    t.update();
		    }
		}
	    };
	ps.addEventHandler(KeyEvent.KEY_PRESSED,new EventHandler<KeyEvent>() {
		@Override
		public void handle(KeyEvent ke) {
		    //System.out.println(ke.getCode());
		    if(ke.getCode().equals(KeyCode.ESCAPE)) {
			//consider less forceful exit
			System.exit(0);
		    }
		    if(ke.getCode().equals(KeyCode.SPACE)) {
			if(paused) {
			    paused = false;
			    long pauseDuration = System.currentTimeMillis() - pauseTime;
			    for(Timer t : timers.values())
				t.offset+=pauseDuration;
			    updateThread.resume();
			} else {
			    paused = true;
			    updateThread.suspend();
			    pauseTime = System.currentTimeMillis();
			}
			return;
		    }
		    if(ke.getCode().equals(KeyCode.BACK_SPACE)) {
			timers.clear();
			pane.getChildren().clear();
			pane.getChildren().add(topDiv);
			return;
		    }
		    if(ke.getCode().equals(KeyCode.CONTROL) ||
		       ke.getCode().equals(KeyCode.ALT) ||
		       ke.getCode().equals(KeyCode.TAB))
			return;
		    if(timers.get(ke.getCode())==null) {
			Timer t = new Timer(ke.getText());
			timers.put(ke.getCode(),t);
			if(addUp) {
			    pane.getChildren().add(0,t.n);
			    ps.setY(ps.getY()-40);
			} else {
			    pane.getChildren().add(t.n);
			}
			//probably stage height too
			ps.setHeight(ps.getHeight()+40);
			
		    } else {
			if(ke.isControlDown()) {
			    pane.getChildren().remove(timers.get(ke.getCode()).n);
			    timers.remove(ke.getCode());
			    if(addUp)
				ps.setY(ps.getY()+40);
			    //probably lower stage height
			    ps.setHeight(ps.getHeight()-40);
			} else
			    timers.get(ke.getCode()).reset();
		    }
		}
	    });
	Scene s = new Scene(pane,220,300);
	s.setFill(new Color(1,1,1,0));
	ps.setScene(s);
	ps.setOnCloseRequest(new EventHandler<WindowEvent>() {
		@Override
		public void handle(WindowEvent event) {
		    System.exit(0);
		}
	    });
	ps.show();
	updateThread.start();
    }

    public void updateAll() {
	for(Timer t : timers.values())
	    t.update();
    }

    public static class Timer {
	Text t;
	Node n;
	long offset;
	String key;
	public Timer(String s) {
	    key = s;
	    if(paused)
		offset = pauseTime;
	    else
		offset = System.currentTimeMillis();
	    t = new Text(toString());
	    t.setFont(MultiTimer.f);
	    StackPane sp = new StackPane();
	    Rectangle r = new Rectangle(210,30);
	    r.setFill(new Color(1,1,1,1));
	    r.setArcWidth(5);
	    r.setArcHeight(5);
	    sp.getChildren().addAll(r,t);
	    n = sp;
	    //t.autosize();
	}
	public long currentTime(){
	    return System.currentTimeMillis()-offset;
	}
	public void reset() {
	    if(paused)
		offset = pauseTime;
	    else
		offset=System.currentTimeMillis();
	}
	public void update() {
	    t.setText(toString());
	    t.autosize();
	}
	@Override
	public String toString() {
	    long ct = currentTime();
	    long millis = ct%1000;
	    long sec = (ct/1000)%60;
	    long min = (ct/60000)%60;
	    long hour = (ct/(60*1000*60));
	    String out;
	    if(hour>0)
		out = String.format("%s %d:%02d:%02d.%03d",key,hour,min,sec,millis);
	    else if(min>0)
		out = String.format("%s %d:%02d.%03d",key,min,sec,millis);
	    else
		out = String.format("%s %d.%03d",key,sec,millis);
	    return out;
	}
	
    }
}
