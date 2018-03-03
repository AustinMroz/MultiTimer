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

public class MultiTimer extends Application {
    TreeMap<KeyCode,Timer> timers = new TreeMap<>();
    static Font f = new Font(30);
    public static void main(String[] args) {
	launch(args);
    }
    private double xOffset = 0;
    private double yOffset = 0;
    private Boolean paused = false;
    private long pauseTime;

    public void start(Stage ps) {
	ps.initStyle(StageStyle.TRANSPARENT);
	VBox pane = new VBox();
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
		    ps.setX(me.getScreenX() - xOffset);
		    ps.setY(me.getScreenY() - yOffset);
		}
	    });
	pane.setPadding(new Insets(10));
	ps.setTitle("MultiTimer");
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
			    synchronized(paused) {
				paused = false;
				paused.notify()
			    }
			    long pauseDuration = System.currentTimeMillis() - pauseTime;
			    for(Timer t : timers.values())
				t.offset+=pauseDuration;
			} else {
			    synchronized(paused) {
				paused = true;
				pauseTime = System.currentTimeMillis();
			    }
			}
			return;
		    }
		    if(ke.getCode().equals(KeyCode.BACK_SPACE)) {
			timers.clear();
			pane.getChildren().clear();
			return;
		    }
		    if(ke.getCode().equals(KeyCode.CONTROL))
			return;
		    if(timers.get(ke.getCode())==null) {
			Timer t = new Timer(ke.getText());
			timers.put(ke.getCode(),t);
			pane.getChildren().add(t.n);
			
		    } else {
			if(ke.isControlDown()) {
			    pane.getChildren().remove(timers.get(ke.getCode()).n);
			    timers.remove(ke.getCode());
			} else
			    timers.get(ke.getCode()).reset();
		    }
		}
	    });
	ps.setScene(new Scene(pane,300,300));
	/**/
	ps.setOnCloseRequest(new EventHandler<WindowEvent>() {
		@Override
		public void handle(WindowEvent event) {
		    System.exit(0);
		}
	    });
	ps.show();
	new Thread() {
	    @Override
	    public void run() {
		while(true) {
		    try {
			if(paused) {
			    synchronized(paused) {
				while(paused)
				    paused.wait(1000);
			    }
			}
			Thread.sleep(76);
		    } catch(Exception e) {}
		    //System.out.println("updated");
		    for(Timer t : timers.values())
			t.update();
		}
	    }
	}.start();
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
	    offset = System.currentTimeMillis();
	    t = new Text(toString());
	    t.setFont(MultiTimer.f);
	    n = t;
	    //t.autosize();
	}
	public long currentTime(){
	    return System.currentTimeMillis()-offset;
	}
	public void reset() {
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
