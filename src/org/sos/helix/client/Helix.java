package org.sos.helix.client;

import java.util.LinkedList;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;
import org.jbox2d.dynamics.World;
import org.sos.helix.client.websocket.WebSocketCallback;
import org.sos.helix.client.websocket.WebSocketClient;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.widgetideas.graphics.client.Color;
import com.google.gwt.widgetideas.graphics.client.GWTCanvas;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Helix implements EntryPoint, WebSocketCallback {
	
	private GWTCanvas canvas;
	
	private int sizeX = 400;
	
	private int sizeY = 600;
	
	private float velocidadMaxima = 10f;
	
	private float escala = 50;
	
	private World mundo;
	
	private CircleShape circreShape;
	
	private FixtureDef circleFixture;
	
	private LinkedList<Body> cuerpos = new LinkedList<Body>();
	
	private Vec2 vectZero = new Vec2(0,0);
	
	private int numeroDeCuerpos = 1;
	
	final float timeStep = 1.0f / 60f;
	
	private long step = 0;
	
	private boolean onsync = false;
	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		Button bt = new Button("Reiniciar");
		Label lb = new Label("Particle number:");
		final TextBox ti = new TextBox();
		ti.setText(numeroDeCuerpos + "");
		ti.setMaxLength(5);
		bt.setStyleName("boton");
		bt.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				cuerpos = new LinkedList<Body>();
				try {
					int num = Integer.parseInt(ti.getText());
					numeroDeCuerpos = num;
				} catch (Exception e){
					ti.setText(numeroDeCuerpos + "");
				}
				inicializaObjetos();
			}
		});
	    canvas = new GWTCanvas(sizeX,sizeY);
	    RootPanel.get("canvas").add(canvas);
	    FlexTable t = new FlexTable();
	    t.setWidget(0, 0, lb);
	    t.setWidget(0, 1, ti);
	    t.setWidget(0, 2, bt);
	    RootPanel.get("control").add(t);
	    
        circleFixture = new FixtureDef();
        circreShape = new CircleShape();
        circreShape.m_radius = .1f;
        circleFixture.shape = circreShape;
        circleFixture.density = 1;
        circleFixture.friction = 0.0f;
        circleFixture.restitution = 1f;
        
        inicializaObjetos();
        
	    //RepeatingCommand cmd = new RepeatingCommand() {
			//public boolean execute() {
				//mundo.step(timeStep, 10, 10);
				//mundo.clearForces();
				//body.applyForce(force, point)
				//canvas.clear();
				//for (Body b: cuerpos) {
					//drawCircle(b.getPosition().x * escala, sizeY - (b.getPosition().y * escala));
				//}
				//System.out.println("x:" + body.getPosition().x + "y:" +  body.getPosition().y);
				//System.out.println("Y" +  (sizeY - (body.getPosition().y * escala)));
				//return true;
			//}
	    //};
	    //Scheduler.get().scheduleFixedPeriod(cmd,10);
        WebSocketClient webclient = new WebSocketClient(this);
        webclient.connect("ws://localhost:8080/Helix-Server-Test/HelixSync");
	}
	
	private void inicializaObjetos() {
		Vec2 gravity = new Vec2(0,0);
	    mundo = new World(gravity, true);
	    setLimites();
		for (int i = 0; i < this.numeroDeCuerpos; i++) {
			BodyDef bd = new BodyDef();
	        bd.type = BodyType.DYNAMIC;
	        bd.position.set(posicionInicial());
	        Body body = mundo.createBody(bd);
	        body.createFixture(circleFixture);
	        body.setBullet(false);
	        body.applyForce(fuerzaInicial(), vectZero);
	        this.cuerpos.add(body);
		}
	}
	
	private void setLimites() {
		Vec2 a = new Vec2(0,0);
		Vec2 b = new Vec2(sizeX/escala,0);
		Vec2 c = new Vec2(sizeX/escala,sizeY/escala);
		Vec2 d = new Vec2(0,sizeY/escala);
		creaLinea(a,b);
		creaLinea(a,d);
		creaLinea(b,c);
		creaLinea(d,c);
	}
	
	private void creaLinea(Vec2 a, Vec2 b) {
		PolygonShape lineShape = new PolygonShape();
		lineShape.setAsEdge(a,b);
		FixtureDef fdLine = new FixtureDef();
		fdLine.shape = lineShape;
		fdLine.friction = 0f;
        BodyDef bd1 = new BodyDef();
        bd1.type = BodyType.STATIC;
        Body b1 = mundo.createBody(bd1);
        b1.createFixture(fdLine);
	}
	
	private void drawCircle(double x, double y) {
		canvas.setLineWidth(1);
	    canvas.setStrokeStyle(Color.BLACK);
	    canvas.beginPath();
	    canvas.arc(x, y, 5, 0, Math.PI*2, true); 
	    canvas.closePath();
	    canvas.fill();
	}
	
	private Vec2 posicionInicial(){
		//Double a = Math.random();
		//Double b = Math.random();
		//return new Vec2(a.floatValue()*(sizeX - 15)/escala, b.floatValue()*(sizeY - 15)/escala );
		return new Vec2(1,3);
	}
	
	private Vec2 fuerzaInicial() {
		//Double a = Math.random() - .5;
		//Double b = Math.random() - .5;
		//return new Vec2(a.floatValue()*velocidadMaxima *2, b.floatValue()*velocidadMaxima *2);
		return new Vec2(8,10);
	}

	@Override
	public void connected() {
	}

	@Override
	public void disconnected() {
	}

	@Override
	public void message(String message) {
		if (!onsync) {
			step++;
			long stepServer = Long.parseLong(message);
			if (step < stepServer) {
				onsync = true;
				while (step < stepServer) {
					mundo.step(timeStep, 10, 10);
					mundo.clearForces();
					step++;
				}
				onsync = false;
			}
			mundo.step(timeStep, 10, 10);
			mundo.clearForces();
			canvas.clear();
			for (Body b: cuerpos) {
				drawCircle(b.getPosition().x * escala, sizeY - (b.getPosition().y * escala));
			}
		}
	}
	
}
