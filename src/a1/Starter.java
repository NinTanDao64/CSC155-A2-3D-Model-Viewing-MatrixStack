package a1;

import java.awt.BorderLayout;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.nio.*;
import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.GLContext;
import com.jogamp.common.nio.Buffers;

import com.jogamp.opengl.util.*;
import graphicslib3D.GLSLUtils.*;
import graphicslib3D.*;

import javax.swing.JPanel;
import javax.swing.JButton;

public class Starter extends JFrame implements GLEventListener, MouseWheelListener {
	private GLCanvas myCanvas;
	private int rendering_program;
	private int vao[] = new int[1];
	private GLSLUtils util = new GLSLUtils();
	
	private int moveFlag = 0;
	private float colorCount = 0.0f;
	private float color_flag = 0;
	private float x = 0.0f;
	private float horiz_inc = 0.01f;
	private float y = 0.0f;
	private float vert_inc = 0.01f;
	private float degrees = 0;
	private float size = 0.25f;
	
	public Starter() {
		setTitle("CSC 155 - A1");
		setSize(1000, 500);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		getContentPane().add(myCanvas);
		setVisible(true);
		
		//Create panel for north area of content pane, and obtain a reference to the content pane itself
		JPanel contentPane = (JPanel) this.getContentPane();
		JPanel topPanel = new JPanel();
		this.add(topPanel, BorderLayout.NORTH);
		
		//Create the 3 buttons and attach to north panel
		JButton horizontalButton = new JButton ("Horizontal");
		JButton verticalButton = new JButton ("Vertical");
		JButton circularButton = new JButton ("Circular");
		topPanel.add(horizontalButton);
		topPanel.add(verticalButton);
		topPanel.add(circularButton);
		
		//Create the 3 command objects, attach 2 to buttons
		AbstractAction horiz_movement = new HorizontalMovement(this);
		AbstractAction vert_movement = new VerticalMovement(this);
		AbstractAction circ_movement = new CircMovement(this);
		AbstractAction change_color = new ColorCommand(this);
		horizontalButton.setAction(horiz_movement);
		verticalButton.setAction(vert_movement);
		circularButton.setAction(circ_movement);
		
		//Attach the color-changing command to the 'c' keybinding
		int mapName = JComponent.WHEN_IN_FOCUSED_WINDOW;
		InputMap imap = contentPane.getInputMap(mapName);
		KeyStroke cKey = KeyStroke.getKeyStroke('c');
		imap.put(cKey, "color");
		ActionMap amap = contentPane.getActionMap();
		amap.put("color", change_color);
		
		//Start the animation
		FPSAnimator animator = new FPSAnimator(myCanvas, 60);
		animator.start();
	}
	
	public void display(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		gl.glUseProgram(rendering_program);
		
      //Clear out the zbuffers to prevent displaying a trail of the moving triangle
		float bkg[] = { 0.01f, 0.01f, 0.01f, 1.0f };
		FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
		gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);
		
		
		if (moveFlag == 0) {
			//Move triangle horizontally
			x += horiz_inc;
			if (x > 1.0f) horiz_inc = -0.01f;
			if (x < -1.0f) horiz_inc = 0.01f;
			int x_offset_loc = gl.glGetUniformLocation(rendering_program, "horiz_offset");
			int y_offset_loc = gl.glGetUniformLocation(rendering_program, "vert_offset");
			gl.glProgramUniform1f(rendering_program, x_offset_loc, x);
			gl.glProgramUniform1f(rendering_program, y_offset_loc, y);
		} else if (moveFlag == 1) {
			//Move triangle vertically
			y += vert_inc;
			if (y > 1.0f) vert_inc = -0.01f;
			if (y < -1.0f) vert_inc = 0.01f;
			int x_offset_loc = gl.glGetUniformLocation(rendering_program, "horiz_offset");
			int y_offset_loc = gl.glGetUniformLocation(rendering_program, "vert_offset");
			gl.glProgramUniform1f(rendering_program, x_offset_loc, x);
			gl.glProgramUniform1f(rendering_program, y_offset_loc, y);
		} else if (moveFlag == 2) {
			//Move triangle along circular path
			degrees += 1;
			if (degrees > 360) {
				degrees = degrees - 360f;
			}
			float radians = (float) Math.toRadians(degrees);
			x = (float) (0.5 * Math.cos(radians));
			y = (float) (0.5 * Math.sin(radians));
			int x_offset_loc = gl.glGetUniformLocation(rendering_program, "horiz_offset");
			int y_offset_loc = gl.glGetUniformLocation(rendering_program, "vert_offset");
			gl.glProgramUniform1f(rendering_program, x_offset_loc, x);
			gl.glProgramUniform1f(rendering_program, y_offset_loc, y);
		}
		
      //Send current size of triangle to the vertex shader
		int tri_size = gl.glGetUniformLocation(rendering_program, "size");
		gl.glProgramUniform1f(rendering_program, tri_size, size);
		
		color_flag = colorCount % 4; //Allows ability to cycle through 4 different color options, no matter how high colorCount gets
		int curr_color = gl.glGetUniformLocation(rendering_program, "colorFlag");
		gl.glProgramUniform1f(rendering_program, curr_color, color_flag);
		
		gl.glDrawArrays(GL_TRIANGLES, 0, 3);
	}
	
	public void init(GLAutoDrawable drawable) {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		rendering_program = createShaderProgram();
		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		
		//Print out information regarding JOGL/GL/Java versions
		System.out.println("GL Vendor: " + gl.glGetString(GL_VENDOR));
		System.out.println("GL Version: " + gl.glGetString(GL_VERSION));
		System.out.println("GL Renderer: " + gl.glGetString(GL_RENDERER));
		System.out.println();
		
		Package p = Package.getPackage("com.jogamp.opengl");
		System.out.println("JOGL Version: " + p.getImplementationVersion());
		System.out.println("Java Version: " + System.getProperty("java.version"));
		
		this.addMouseWheelListener(this);
	}
	
	private int createShaderProgram() {
		GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] vertCompiled = new int[1];
		int[] fragCompiled = new int[1];
		int[] linked = new int[1];
		
		String vshaderSource[] = util.readShaderSource("a1/vert.shader");
		String fshaderSource[] = util.readShaderSource("a1/frag.shader");
		//String vshaderSource[] = util.readShaderSource("C:" + File.separator + "Users" +  File.separator + "ninta" + File.separator + 
			//	"workspace" + File.separator + "CSC 155 - A1" + File.separator + "src" + File.separator + "a1" + File.separator + "vert.shader");
		//String fshaderSource[] = util.readShaderSource("C:" + File.separator + "Users" +  File.separator + "ninta" + File.separator + 
			//	"workspace" + File.separator + "CSC 155 - A1" + File.separator + "src" + File.separator + "a1" + File.separator + "frag.shader");
		int lengths[];
		
		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glCompileShader(vShader);
		
		checkOpenGLError();  // can use returned boolean if desired
		gl.glGetShaderiv(vShader, GL_COMPILE_STATUS, vertCompiled, 0);
		if (vertCompiled[0] == 1)
			{	System.out.println("Vertex compilation success!");
		} else
			{	System.out.println("Vertex compilation failed!");
				printShaderLog(vShader);
		}
		
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);
		gl.glCompileShader(fShader);
		
		checkOpenGLError();  // can use returned boolean if desired
		gl.glGetShaderiv(fShader, GL_COMPILE_STATUS, fragCompiled, 0);
		if (fragCompiled[0] == 1)
			{	System.out.println("Fragment compilation success!");
		} else
			{	System.out.println("Fragment compilation failed!");
				printShaderLog(fShader);
		}
		
		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		
		gl.glLinkProgram(vfprogram);
		
		checkOpenGLError();
		gl.glGetProgramiv(vfprogram, GL_LINK_STATUS, linked, 0);
		if (linked[0] == 1)
			{	System.out.println("Linking success!");
		} else
			{	System.out.println("Linking failed!");
				printProgramLog(vfprogram);
		}
		
		System.out.println("----------------------------");
		
		gl.glDeleteShader(vShader);
		gl.glDeleteShader(fShader);
		return vfprogram;
	}
	
	public void horizButton() {
		//Move triangle back to origin, then change it's path to horizontal
		x = 0;
		y = 0;
		moveFlag = 0;
	}
	
	public void vertButton() {
		//Move triangle back to origin, then change it's path to vertical
		x = 0;
		y = 0;
		moveFlag = 1;
	}
	
	public void circButton() {
		moveFlag = 2;
	}
	
	public void changeColor() {
		colorCount += 1;
	}
	
	private void printShaderLog(int shader)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;

		// determine the length of the shader compilation log
		gl.glGetShaderiv(shader, GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0)
		{	log = new byte[len[0]];
			gl.glGetShaderInfoLog(shader, len[0], chWrittn, 0, log, 0);
			System.out.println("Shader Info Log: ");
			for (int i = 0; i < log.length; i++)
			{	System.out.print((char) log[i]);
			}
		}
	}
	
	void printProgramLog(int prog)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		int[] len = new int[1];
		int[] chWrittn = new int[1];
		byte[] log = null;

		// determine length of the program compilation log
		gl.glGetProgramiv(prog, GL_INFO_LOG_LENGTH, len, 0);
		if (len[0] > 0)
		{	log = new byte[len[0]];
			gl.glGetProgramInfoLog(prog, len[0], chWrittn, 0, log, 0);
			System.out.println("Program Info Log: ");
			for (int i = 0; i < log.length; i++)
			{	System.out.print((char) log[i]);
			}
		}
	}
	
	boolean checkOpenGLError()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		boolean foundError = false;
		GLU glu = new GLU();
		int glErr = gl.glGetError();
		while (glErr != GL_NO_ERROR)
		{	System.err.println("glError: " + glu.gluErrorString(glErr));
			foundError = true;
			glErr = gl.glGetError();
		}
		return foundError;
	}
	
	public static void main(String[] args) { new Starter(); }
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
	public void dispose (GLAutoDrawable drawable) {}

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		   //Change size of triangle. Guarantees it cannot be smaller than 0.05, and cannot be larger than 0.6.
	       int notches = e.getWheelRotation();
	       if (notches < 0) {
	    	   //System.out.println("Mouse wheel moved UP " + -notches + " notch(es)");
	    	   size += 0.05f;
	    	   if(size > 0.6f) {
	    		   size = 0.6f;
	    	   }
	       } else {
	    	   //System.out.println("Mouse wheel moved DOWN " + notches + " notch(es)");
	    	   size -= 0.05f;
	    	   if(size < 0.05f) {
	    		   size = 0.05f;
	    	   }
	       }
	}
}
