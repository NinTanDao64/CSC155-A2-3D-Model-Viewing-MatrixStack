package a2;

import graphicslib3D.*;
import graphicslib3D.GLSLUtils.*;
import graphicslib3D.shape.Sphere;

import java.io.File;
import java.nio.*;
import javax.swing.*;
import static com.jogamp.opengl.GL4.*;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GLContext;

public class Starter extends JFrame implements GLEventListener
{	
	private GLCanvas myCanvas;
	private int rendering_program;
	private int vao[] = new int[1];
	private int vbo[] = new int[17];
	private float cameraX, cameraY, cameraZ;
	private float cubeLocX, cubeLocY, cubeLocZ;
	private float pyrLocX, pyrLocY, pyrLocZ;
	private GLSLUtils util = new GLSLUtils();
	private Camera camera = new Camera(0, 0, 12);
	private boolean drawAxes = true;
	
	private int diamondTexture;
	private Texture joglDiamondTexture;
	
	private int sunTexture;
	private Texture joglSunTexture;
	
	private int planetTexture1;
	private Texture joglPlanetTexture1;
	
	private int planetTexture2;
	private Texture joglPlanetTexture2;
	
	private int redTexture;
	private Texture joglRedTexture;
	
	private int greenTexture;
	private Texture joglGreenTexture;
	
	private int blueTexture;
	private Texture joglBlueTexture;
	
	private	MatrixStack mvStack = new MatrixStack(20);
	private Sphere sun = new Sphere(24);
	private Sphere planet1 = new Sphere(24);
	private Sphere planet2 = new Sphere(24);

	public Starter()
	{	setTitle("CSC155 - A2");
		setSize(1000, 800);
		myCanvas = new GLCanvas();
		myCanvas.addGLEventListener(this);
		getContentPane().add(myCanvas);
		this.setVisible(true);
		
		JPanel contentPane = (JPanel) this.getContentPane();
		
		AbstractAction move_forward = new MoveForward(camera);
		AbstractAction move_backward = new MoveBackward(camera);
		AbstractAction move_left = new MoveLeft(camera);
		AbstractAction move_right = new MoveRight(camera);
		AbstractAction move_up = new MoveUp(camera);
		AbstractAction move_down = new MoveDown(camera);
		AbstractAction toggle_axes = new ToggleAxes(this);
		AbstractAction pan_left = new YawLeft(camera);
		AbstractAction pan_right = new YawRight(camera);
		AbstractAction look_up = new PitchUp(camera);
		AbstractAction look_down = new PitchDown(camera);

		int mapName = JComponent.WHEN_IN_FOCUSED_WINDOW;
		InputMap imap = contentPane.getInputMap(mapName);
		ActionMap amap = contentPane.getActionMap();
		
		KeyStroke wKey = KeyStroke.getKeyStroke('w');
		imap.put(wKey, "Forward");
		amap.put("Forward", move_forward);
		
		KeyStroke sKey = KeyStroke.getKeyStroke('s');
		imap.put(sKey, "Backward");
		amap.put("Backward", move_backward);
		
		KeyStroke aKey = KeyStroke.getKeyStroke('a');
		imap.put(aKey, "Left");
		amap.put("Left", move_left);
		
		KeyStroke dKey = KeyStroke.getKeyStroke('d');
		imap.put(dKey, "Right");
		amap.put("Right", move_right);
		
		KeyStroke qKey = KeyStroke.getKeyStroke('q');
		imap.put(qKey, "Up");
		amap.put("Up", move_up);
		
		KeyStroke eKey = KeyStroke.getKeyStroke('e');
		imap.put(eKey, "Down");
		amap.put("Down", move_down);
		
		KeyStroke spaceKey = KeyStroke.getKeyStroke("SPACE");
		imap.put(spaceKey, "Axes");
		amap.put("Axes", toggle_axes);
		
		KeyStroke leftKey = KeyStroke.getKeyStroke("LEFT");
		imap.put(leftKey, "Pan Left");
		amap.put("Pan Left", pan_left);
		
		KeyStroke rightKey = KeyStroke.getKeyStroke("RIGHT");
		imap.put(rightKey, "Pan Right");
		amap.put("Pan Right", pan_right);
		
		KeyStroke upKey = KeyStroke.getKeyStroke("UP");
		imap.put(upKey, "Look Up");
		amap.put("Look Up", look_up);
		
		KeyStroke downKey = KeyStroke.getKeyStroke("DOWN");
		imap.put(downKey, "Look Down");
		amap.put("Look Down", look_down);
		
		FPSAnimator animator = new FPSAnimator(myCanvas, 50);
		animator.start();
	}

	public void display(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		gl.glClear(GL_DEPTH_BUFFER_BIT);
		float bkg[] = { 0.0f, 0.0f, 0.0f, 1.0f };
		FloatBuffer bkgBuffer = Buffers.newDirectFloatBuffer(bkg);
		gl.glClearBufferfv(GL_COLOR, 0, bkgBuffer);

		gl.glClear(GL_DEPTH_BUFFER_BIT);

		gl.glUseProgram(rendering_program);

		int mv_loc = gl.glGetUniformLocation(rendering_program, "mv_matrix");
		int proj_loc = gl.glGetUniformLocation(rendering_program, "proj_matrix");

		float aspect = (float) myCanvas.getWidth() / (float) myCanvas.getHeight();
		Matrix3D pMat = perspective(60.0f, aspect, 0.1f, 1000.0f);
		
		// push view matrix onto the stack
		mvStack.pushMatrix();
		//mvStack.translate(-cameraX, -cameraY, -cameraZ);
		mvStack.multMatrix(camera.getView());
		double amt = (double)(System.currentTimeMillis())/1000.0;

		gl.glUniformMatrix4fv(proj_loc, 1, false, pMat.getFloatValues(), 0);
		
		// ----------------------  Sun  
		mvStack.pushMatrix();
		mvStack.translate(pyrLocX, pyrLocY, pyrLocZ);
		
		//Draw the lines for x,y,z Axes
		if(drawAxes) {
			gl.glDisable(GL_CULL_FACE);
			
			//Red x-axis
			gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
			gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(0);
		
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
			gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(1);
		
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(GL_TEXTURE_2D, redTexture);
		
			gl.glDrawArrays(GL_TRIANGLES, 0, 9);
			
			//Green y-axis
			gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
			gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(0);
		
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
			gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(1);
		
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(GL_TEXTURE_2D, greenTexture);
		
			gl.glDrawArrays(GL_TRIANGLES, 0, 9);
			
			//Blue z-axis
			gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[15]);
			gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(0);
		
			gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[16]);
			gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
			gl.glEnableVertexAttribArray(1);
		
			gl.glActiveTexture(GL_TEXTURE0);
			gl.glBindTexture(GL_TEXTURE_2D, blueTexture);
		
			gl.glDrawArrays(GL_TRIANGLES, 0, 9);
		}
		
		mvStack.scale(2.0, 2.0, 2.0);
		mvStack.pushMatrix();
		mvStack.rotate((System.currentTimeMillis())/10.0,0.0,1.0,0.0);
		
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, sunTexture);
		
		gl.glEnable(GL_CULL_FACE);
		gl.glFrontFace(GL_CCW);
		
		gl.glEnable(GL_DEPTH_TEST);
		
		int sun_numVerts = sun.getIndices().length;
		gl.glDrawArrays(GL_TRIANGLES, 0, sun_numVerts); 
		
		mvStack.popMatrix(); //pop sun rotate
		
		//-----------------------  Planet 1  
		mvStack.pushMatrix();
		mvStack.translate(Math.sin(amt)*4.0f, 0.0f, Math.cos(amt)*4.0f);
		mvStack.pushMatrix();
		mvStack.rotate((System.currentTimeMillis())/10.0,1.0,1.0,0.0);
		
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, planetTexture1);
		
		int planet1_numVerts = planet1.getIndices().length;
		gl.glDrawArrays(GL_TRIANGLES, 0, planet1_numVerts);
		mvStack.popMatrix(); //pop planet rotate

		//-----------------------  Moon (Diamond)
		mvStack.pushMatrix();
		mvStack.translate(0.0f, Math.sin(amt)*2.0f, Math.cos(amt)*2.0f);
		mvStack.rotate((System.currentTimeMillis())/10.0,0.0,0.0,1.0);
		mvStack.scale(0.25, 0.25, 0.25);
		
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, diamondTexture);
		
		gl.glDrawArrays(GL_TRIANGLES, 0, 24);
		mvStack.popMatrix(); //pop moon rotate + translate
		mvStack.popMatrix(); //pop planet translate
		
		//-----------------------  Planet 2
		mvStack.pushMatrix();
		mvStack.translate(Math.cos(amt/3.5)*8.0f, 0.0f, Math.sin(amt/3.5)*8.0f);
		mvStack.pushMatrix();
		mvStack.rotate((System.currentTimeMillis())/10.0,0.0,1.0,1.0);
		
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, planetTexture2);
		
		int planet2_numVerts = planet2.getIndices().length;
		gl.glDrawArrays(GL_TRIANGLES, 0, planet2_numVerts);
		mvStack.popMatrix(); //pop planet#2 rotate
		
		//-----------------------  moon#2
		mvStack.pushMatrix();
		mvStack.translate(0.0f, Math.cos(amt)*2.0f, Math.sin(amt)*2.0f);
		mvStack.rotate((System.currentTimeMillis())/10.0,0.0,0.0,1.0);
		mvStack.scale(0.25, 0.25, 0.25);
		
		gl.glUniformMatrix4fv(mv_loc, 1, false, mvStack.peek().getFloatValues(), 0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		gl.glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(0);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		gl.glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		gl.glEnableVertexAttribArray(1);
		
		gl.glActiveTexture(GL_TEXTURE0);
		gl.glBindTexture(GL_TEXTURE_2D, diamondTexture);
		
		gl.glDrawArrays(GL_TRIANGLES, 0, 24);
		mvStack.popMatrix(); //pop moon#2 rotate+translate
		mvStack.popMatrix(); //pop planet#2 translate
		mvStack.popMatrix(); //pop sun translate
		mvStack.popMatrix(); //pop view
	}

	public void init(GLAutoDrawable drawable)
	{	GL4 gl = (GL4) GLContext.getCurrentGL();
		rendering_program = createShaderProgram();
		setupVertices();
		cameraX = 0.0f; cameraY = 0.0f; cameraZ = 12.0f;
		cubeLocX = 0.0f; cubeLocY = 0.0f; cubeLocZ = 0.0f;
		pyrLocX = 0.0f; pyrLocY = 0.0f; pyrLocZ = 0.0f;
		
		joglDiamondTexture = loadTexture("Diamond Texture.jpg");
		diamondTexture = joglDiamondTexture.getTextureObject();
		
		joglSunTexture = loadTexture("Trask.png");
		sunTexture = joglSunTexture.getTextureObject();
		
		joglPlanetTexture1 = loadTexture("Serendip.jpg");
		planetTexture1 = joglPlanetTexture1.getTextureObject();
		
		joglPlanetTexture2 = loadTexture("Telos.png");
		planetTexture2 = joglPlanetTexture2.getTextureObject();
		
		joglRedTexture = loadTexture("Red.jpg");
		redTexture = joglRedTexture.getTextureObject();
		
		joglGreenTexture = loadTexture("Green.jpg");
		greenTexture = joglGreenTexture.getTextureObject();
		
		joglBlueTexture = loadTexture("Blue.jpg");
		blueTexture = joglBlueTexture.getTextureObject();
	}

	private void setupVertices()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		float[] diamond_positions =
			{ -1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 2.5f, 0.0f,
			  1.0f, 0.0f, 1.0f, 1.0f, 0.0f, -1.0f, 0.0f, 2.5f, 0.0f,
			  1.0f, 0.0f, -1.0f, -1.0f, 0.0f, -1.0f, 0.0f, 2.5f, 0.0f,
			  -1.0f, 0.0f, -1.0f, -1.0f, 0.0f, 1.0f, 0.0f, 2.5f, 0.0f,
			  0.0f, -2.5f, 0.0f, 1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f,
			  0.0f, -2.5f, 0.0f, 1.0f, 0.0f, -1.0f, 1.0f, 0.0f, 1.0f,
			  0.0f, -2.5f, 0.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f, -1.0f,
			  0.0f, -2.5f, 0.0f, -1.0f, 0.0f, 1.0f, -1.0f, 0.0f, -1.0f
			};
		
		float[] diamond_texCoords =
			{ 0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			  0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			  0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			  0.0f, 0.0f, 1.0f, 0.0f, 0.5f, 1.0f,
			  0.05f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
			  0.05f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
			  0.05f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
			  0.05f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f,
			};
		
		float[] xLine_positions =
			{
				0.0f, 0.0f, 0.0f, 100.0f, 0.0f, 0.0f, 0.0f, 0.05f, 0.0f	
			};
		
		float[] red_texCoords =
			{
				0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f	
			};
		
		float[] yLine_positions =
			{
				0.0f, 0.0f, 0.0f, 0.05f, 0.0f, 0.0f, 0.0f, 100.0f, 0.0f
			};
		
		float[] green_texCoords =
			{
				0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f	
			};
		
		float[] zLine_positions =
			{
				0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 100.0f, 0.0f, -0.05f, 0.0f
			};
		
		float[] blue_texCoords =
			{
				0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f	
			};
		
		Vertex3D[] sun_vertices = sun.getVertices();
		int[] sun_indices = sun.getIndices();
		
		float[] sun_pvalues = new float[sun_indices.length*3];
		float[] sun_tvalues = new float[sun_indices.length*2];
		float[] sun_nvalues = new float[sun_indices.length*3];
		
		for (int i=0; i<sun_indices.length; i++)
		{	sun_pvalues[i*3] = (float) (sun_vertices[sun_indices[i]]).getX();
			sun_pvalues[i*3+1] = (float) (sun_vertices[sun_indices[i]]).getY();
			sun_pvalues[i*3+2] = (float) (sun_vertices[sun_indices[i]]).getZ();
			sun_tvalues[i*2] = (float) (sun_vertices[sun_indices[i]]).getS();
			sun_tvalues[i*2+1] = (float) (sun_vertices[sun_indices[i]]).getT();
			sun_nvalues[i*3] = (float) (sun_vertices[sun_indices[i]]).getNormalX();
			sun_nvalues[i*3+1]= (float)(sun_vertices[sun_indices[i]]).getNormalY();
			sun_nvalues[i*3+2]=(float) (sun_vertices[sun_indices[i]]).getNormalZ();
		}
		
		Vertex3D[] planet1_vertices = planet1.getVertices();
		int[] planet1_indices = planet1.getIndices();
		
		float[] planet1_pvalues = new float[planet1_indices.length*3];
		float[] planet1_tvalues = new float[planet1_indices.length*2];
		float[] planet1_nvalues = new float[planet1_indices.length*3];
		
		for (int i=0; i<planet1_indices.length; i++)
		{	planet1_pvalues[i*3] = (float) (planet1_vertices[planet1_indices[i]]).getX();
			planet1_pvalues[i*3+1] = (float) (planet1_vertices[planet1_indices[i]]).getY();
			planet1_pvalues[i*3+2] = (float) (planet1_vertices[planet1_indices[i]]).getZ();
			planet1_tvalues[i*2] = (float) (planet1_vertices[planet1_indices[i]]).getS();
			planet1_tvalues[i*2+1] = (float) (planet1_vertices[planet1_indices[i]]).getT();
			planet1_nvalues[i*3] = (float) (planet1_vertices[planet1_indices[i]]).getNormalX();
			planet1_nvalues[i*3+1]= (float)(planet1_vertices[planet1_indices[i]]).getNormalY();
			planet1_nvalues[i*3+2]=(float) (planet1_vertices[planet1_indices[i]]).getNormalZ();
		}
		
		Vertex3D[] planet2_vertices = planet2.getVertices();
		int[] planet2_indices = planet2.getIndices();
		
		float[] planet2_pvalues = new float[planet2_indices.length*3];
		float[] planet2_tvalues = new float[planet2_indices.length*2];
		float[] planet2_nvalues = new float[planet2_indices.length*3];
		
		for (int i=0; i<planet2_indices.length; i++)
		{	planet2_pvalues[i*3] = (float) (planet2_vertices[planet2_indices[i]]).getX();
			planet2_pvalues[i*3+1] = (float) (planet2_vertices[planet2_indices[i]]).getY();
			planet2_pvalues[i*3+2] = (float) (planet2_vertices[planet2_indices[i]]).getZ();
			planet2_tvalues[i*2] = (float) (planet2_vertices[planet2_indices[i]]).getS();
			planet2_tvalues[i*2+1] = (float) (planet2_vertices[planet2_indices[i]]).getT();
			planet2_nvalues[i*3] = (float) (planet2_vertices[planet2_indices[i]]).getNormalX();
			planet2_nvalues[i*3+1]= (float)(planet2_vertices[planet2_indices[i]]).getNormalY();
			planet2_nvalues[i*3+2]=(float) (planet2_vertices[planet2_indices[i]]).getNormalZ();
		}

		gl.glGenVertexArrays(vao.length, vao, 0);
		gl.glBindVertexArray(vao[0]);
		gl.glGenBuffers(vbo.length, vbo, 0);

		//-----------  VBO's for vertices  -----------
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[0]);
		FloatBuffer sun_pBuffer = Buffers.newDirectFloatBuffer(sun_pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, sun_pBuffer.limit()*4, sun_pBuffer, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[1]);
		FloatBuffer planet1_pBuffer = Buffers.newDirectFloatBuffer(planet1_pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, planet1_pBuffer.limit()*4, planet1_pBuffer, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER,  vbo[2]);
		FloatBuffer planet2_pBuffer = Buffers.newDirectFloatBuffer(planet2_pvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, planet2_pBuffer.limit()*4, planet2_pBuffer, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[3]);
		FloatBuffer diamond_pBuffer = Buffers.newDirectFloatBuffer(diamond_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, diamond_pBuffer.limit()*4, diamond_pBuffer, GL_STATIC_DRAW);
		
		//----------- VBO's for textures -----------
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[4]);
		FloatBuffer sun_texBuffer = Buffers.newDirectFloatBuffer(sun_tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, sun_texBuffer.limit()*4, sun_texBuffer, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[5]);
		FloatBuffer planet1_texBuffer = Buffers.newDirectFloatBuffer(planet1_tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, planet1_texBuffer.limit()*4, planet1_texBuffer, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[6]);
		FloatBuffer planet2_texBuffer = Buffers.newDirectFloatBuffer(planet2_tvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, planet2_texBuffer.limit()*4, planet2_texBuffer, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[7]);
		FloatBuffer diamond_texBuffer = Buffers.newDirectFloatBuffer(diamond_texCoords);
		gl.glBufferData(GL_ARRAY_BUFFER, diamond_texBuffer.limit()*4, diamond_texBuffer, GL_STATIC_DRAW);
		
		//----------- VBO's for normals -----------
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[8]);
		FloatBuffer sun_normBuffer = Buffers.newDirectFloatBuffer(sun_nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, sun_normBuffer.limit()*4, sun_normBuffer, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[9]);
		FloatBuffer planet1_normBuffer = Buffers.newDirectFloatBuffer(planet1_nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, planet1_normBuffer.limit()*4, planet1_normBuffer, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[10]);
		FloatBuffer planet2_normBuffer = Buffers.newDirectFloatBuffer(planet2_nvalues);
		gl.glBufferData(GL_ARRAY_BUFFER, planet2_normBuffer.limit()*4, planet2_normBuffer, GL_STATIC_DRAW);
		
		//VBO's for x,y,z colroed axes (positions + texture coordinates)
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[11]);
		FloatBuffer xLine_pBuffer = Buffers.newDirectFloatBuffer(xLine_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, xLine_pBuffer.limit()*4, xLine_pBuffer, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[12]);
		FloatBuffer red_texBuffer = Buffers.newDirectFloatBuffer(red_texCoords);
		gl.glBufferData(GL_ARRAY_BUFFER, red_texBuffer.limit()*4, red_texBuffer, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[13]);
		FloatBuffer yLine_pBuffer = Buffers.newDirectFloatBuffer(yLine_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, yLine_pBuffer.limit()*4, yLine_pBuffer, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[14]);
		FloatBuffer green_texBuffer = Buffers.newDirectFloatBuffer(green_texCoords);
		gl.glBufferData(GL_ARRAY_BUFFER, green_texBuffer.limit()*4, green_texBuffer, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[15]);
		FloatBuffer zLine_pBuffer = Buffers.newDirectFloatBuffer(zLine_positions);
		gl.glBufferData(GL_ARRAY_BUFFER, zLine_pBuffer.limit()*4, zLine_pBuffer, GL_STATIC_DRAW);
		
		gl.glBindBuffer(GL_ARRAY_BUFFER, vbo[16]);
		FloatBuffer blue_texBuffer = Buffers.newDirectFloatBuffer(blue_texCoords);
		gl.glBufferData(GL_ARRAY_BUFFER, blue_texBuffer.limit()*4, blue_texBuffer, GL_STATIC_DRAW);
	}
	
	public void toggleAxes() {
		drawAxes = !drawAxes;
	}

	private Matrix3D perspective(float fovy, float aspect, float n, float f)
	{	float q = 1.0f / ((float) Math.tan(Math.toRadians(0.5f * fovy)));
		float A = q / aspect;
		float B = (n + f) / (n - f);
		float C = (2.0f * n * f) / (n - f);
		Matrix3D r = new Matrix3D();
		r.setElementAt(0,0,A);
		r.setElementAt(1,1,q);
		r.setElementAt(2,2,B);
		r.setElementAt(3,2,-1.0f);
		r.setElementAt(2,3,C);
		return r;
	}

	public static void main(String[] args) { new Starter(); }
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {}
	public void dispose(GLAutoDrawable drawable) {}

	private int createShaderProgram()
	{	GL4 gl = (GL4) GLContext.getCurrentGL();

		String vshaderSource[] = util.readShaderSource("a2/vert.shader");
		String fshaderSource[] = util.readShaderSource("a2/frag.shader");
		//String vshaderSource[] = util.readShaderSource("C:" + File.separator + "Users" +  File.separator + "ninta" + File.separator + 
			//"workspace" + File.separator + "CSC 155 - A2" + File.separator + "src" + File.separator + "a2" + File.separator + "vert.shader");
		//String fshaderSource[] = util.readShaderSource("C:" + File.separator + "Users" +  File.separator + "ninta" + File.separator + 
			//"workspace" + File.separator + "CSC 155 - A2" + File.separator + "src" + File.separator + "a2" + File.separator + "frag.shader");

		int vShader = gl.glCreateShader(GL_VERTEX_SHADER);
		int fShader = gl.glCreateShader(GL_FRAGMENT_SHADER);

		gl.glShaderSource(vShader, vshaderSource.length, vshaderSource, null, 0);
		gl.glShaderSource(fShader, fshaderSource.length, fshaderSource, null, 0);

		gl.glCompileShader(vShader);
		gl.glCompileShader(fShader);

		int vfprogram = gl.glCreateProgram();
		gl.glAttachShader(vfprogram, vShader);
		gl.glAttachShader(vfprogram, fShader);
		gl.glLinkProgram(vfprogram);
		return vfprogram;
	}
	
	public Texture loadTexture(String textureFileName)
	{	Texture tex = null;
		try { tex = TextureIO.newTexture(new File(textureFileName), false); }
		catch (Exception e) { e.printStackTrace(); }
		return tex;
	}
}
