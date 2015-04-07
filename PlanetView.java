import java.nio.ByteBuffer;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Sphere;
import org.lwjgl.util.glu.Cylinder;

import java.nio.FloatBuffer;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class PlanetView {

	String windowTitle = "PLANETS and Stuff";
	public boolean closeRequested = false;

	long lastFrameTime; // used to calculate delta


	float rotationAngle = 0;
	float orbit1Angle = 0;
	float orbit2Angle = 0;
	float orbit3Angle = 0;
	float pAngle1 = 0;
	float pAngle2 = 5;
	float pAngle3 = 10;
	float moonOrbitRadius = 0.8f;
	float moonRotAngle = 0;

	Texture meteor;
	Texture starry;
	Texture purplePaper;
	Texture sun;
	Texture pinkPlanet;
	Texture icePlanet;

	//displaylists:
	int makeSkyBox;
	int makePyramid;
	int makeCube;


	public void run() {

		createWindow();
		getDelta(); // Initialise delta timer
		initGL();

		while (!closeRequested) {
			int delta = getDelta();
			pollInput(delta);
			renderGL(delta);

			Display.update();
		}

		cleanup();
	}

	private void initGL() {

		/* OpenGL */
		int width = Display.getDisplayMode().getWidth();
		int height = Display.getDisplayMode().getHeight();

		glViewport(0, 0, width, height); // Reset The Current Viewport
		glMatrixMode(GL_PROJECTION); // Select The Projection Matrix
		glLoadIdentity(); // Reset The Projection Matrix
		GLU.gluPerspective(45.0f, ((float) width / (float) height), 0.1f, 100.0f); // Calculate The Aspect Ratio Of The Window
		glMatrixMode(GL_MODELVIEW); // Select The Modelview Matrix
		glLoadIdentity(); // Reset The Modelview Matrix


		glShadeModel(GL_SMOOTH); // Enables Smooth Shading
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // Black Background
		glClearDepth(1.0f); // Depth Buffer Setup
		glEnable(GL_DEPTH_TEST); // Enables Depth Testing
		glDepthFunc(GL_LEQUAL); // The Type Of Depth Test To Do
		glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST); // Really Nice Perspective Calculations

		try
		{
			meteor = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("/Textures/OuterSpace/meteorSurface.jpg"));
			starry = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("/Textures/OuterSpace/outerSpace.jpg"));
			purplePaper = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("/Textures/OuterSpace/crumpledPurple.jpg"));
			sun = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("/Textures/OuterSpace/texture_sun.jpg"));
			pinkPlanet = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("/Textures/OuterSpace/pinkGas.png"));
			icePlanet = TextureLoader.getTexture("JPG", ResourceLoader.getResourceAsStream("/Textures/OuterSpace/ice_surface.jpg"));
		}
		catch(IOException e)
		{
			System.out.println("MEH");
		}
		Camera.create();   

		//DisplayList for Skybox
		makeSkyBox = glGenLists(1);
		glNewList(makeSkyBox, GL_COMPILE);
		{
			glBegin(GL_QUADS);
			// Render the front quad
			glTexCoord2f(0, 0); glVertex3f(-1.0f, -1.0f, 1.0f); //BOTTOM LEFT
			glTexCoord2f(1, 0); glVertex3f(1.0f, -1.0f, 1.0f);	//BOTTOM RIGHT
			glTexCoord2f(1, 1); glVertex3f(1.0f, 1.0f, 1.0f);	//TOP RIGHT
			glTexCoord2f(0, 1); glVertex3f(-1.0f, 1.0f, 1.0f);	//TOP LEFT
			glEnd();

			//Render back of the quad BL, BR, TR, TL
			glBegin(GL_QUADS);
			glTexCoord2f(0, 0); glVertex3f(1.0f, -1.0f, -1.0f);
			glTexCoord2f(1, 0); glVertex3f(-1.0f, -1.0f, -1.0f);
			glTexCoord2f(1, 1); glVertex3f(-1.0f, 1.0f, -1.0f);
			glTexCoord2f(0, 1); glVertex3f(1.0f, 1.0f, -1.0f);
			glEnd();

			//Render the Left quad
			glBegin(GL_QUADS);
			glTexCoord2f(0, 0); glVertex3f(-1.0f, -1.0f, -1.0f);
			glTexCoord2f(1, 0); glVertex3f(-1.0f, -1.0f, 1.0f);
			glTexCoord2f(1, 1); glVertex3f(-1.0f, 1.0f, 1.0f);
			glTexCoord2f(0, 1); glVertex3f(-1.0f, 1.0f, -1.0f);
			glEnd();

			// Render the right quad
			glBegin(GL_QUADS);
			glTexCoord2f(0, 0); glVertex3f(1.0f, -1.0f, 1.0f);
			glTexCoord2f(1, 0); glVertex3f(1.0f, -1.0f, -1.0f);
			glTexCoord2f(1, 1); glVertex3f(1.0f, 1.0f, -1.0f);
			glTexCoord2f(0, 1); glVertex3f(1.0f, 1.0f, 1.0f);
			glEnd();


			// Render the top quad
			glBegin(GL_QUADS);
			glTexCoord2f(0, 1); glVertex3f(-1.0f, 1.0f, -1.0f);
			glTexCoord2f(0, 0); glVertex3f(-1.0f, 1.0f, 1.0f);
			glTexCoord2f(1, 0); glVertex3f(1.0f, 1.0f, 1.0f);
			glTexCoord2f(1, 1); glVertex3f(1.0f, 1.0f, -1.0f); 
			glEnd();

			// Render the bottom quad  
			glBegin(GL_QUADS);
			glTexCoord2f(0, 0); glVertex3f(-1.0f, -1.0f, -1.0f);
			glTexCoord2f(0, 1); glVertex3f(1.0f, -1.0f, -1.0f);
			glTexCoord2f(1, 1); glVertex3f(1.0f, -1.0f, 1.0f);
			glTexCoord2f(1, 0); glVertex3f(-1.0f, -1.0f, 1.0f);
			glEnd();
		}
		glEndList();

		//DisplayList to make Pyramid (square base)
		makePyramid = glGenLists(1);
		glNewList(makePyramid, GL_COMPILE);
		{
			//FACING ME
			glBegin( GL_TRIANGLES );
			glColor3f( 1.0f, 0.0f, 1.0f ); 
			glTexCoord2f(0, 0); glVertex3f( 0.0f, 1.f, 0.0f );
			glTexCoord2f(0.5f, 1); glVertex3f( -1.0f, -1.0f, 1.0f );
			glTexCoord2f(1, 0); glVertex3f( 1.0f, -1.0f, 1.0f);

			//FRONT FACE
			glColor3f( 1.0f, 1.0f, 0.0f ); 
			glTexCoord2f(0, 0); glVertex3f( 0.0f, 1.0f, 0.0f);
			glTexCoord2f(0.5f, 1); glVertex3f( -1.0f, -1.0f, -1.0f);
			glTexCoord2f(1, 0); glVertex3f( 1.0f, -1.0f, -1.0f);

			//RIGHT FACE
			glColor3f( 1.0f, 0.0f, 0.0f ); 
			glTexCoord2f(0, 0); glVertex3f( 0.0f, 1.0f, 0.0f);
			glTexCoord2f(0.5f, 1); glVertex3f( 1.0f, -1.0f, -1.0f);
			glTexCoord2f(1, 0); glVertex3f( 1.0f, -1.0f, 1.0f);

			//LEFT:
			glColor3f( 0.0f, 1.0f, 1.0f ); 
			glTexCoord2f(0, 0); glVertex3f( -1.0f, -1.0f, 1.0f);
			glTexCoord2f(0.5f, 1); glVertex3f( -1.0f, -1.0f, -1.0f);
			glTexCoord2f(1, 0); glVertex3f( 0.0f, 1.0f, 0.0f);

			glEnd();

			glBegin(GL_QUADS);
			//BASE:
			glTexCoord2f(1, 1); glVertex3f(1, -1, -1);
			glTexCoord2f(1, 0); glVertex3f(1, -1, 1);
			glTexCoord2f(0, 1); glVertex3f(-1, -1, 1);
			glTexCoord2f(0, 0); glVertex3f(-1, -1, -1);
			glEnd();
		}
		glEndList();

		//Display List to make a quadrilateral (Perfect Cube)
		makeCube = glGenLists(1);
		glNewList(makeCube, GL_COMPILE);
		{
			glBegin(GL_QUADS); // Start Drawing The Cube
			glColor3f(0.0f, 1.0f, 0.0f); // Set The Color To Green
			glVertex3f(1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Top)
			glVertex3f(-1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Top)
			glVertex3f(-1.0f, 1.0f, 1.0f); // Bottom Left Of The Quad (Top)
			glVertex3f(1.0f, 1.0f, 1.0f); // Bottom Right Of The Quad (Top)

			glColor3f(1.0f, 0.5f, 0.0f); // Set The Color To Orange
			glVertex3f(1.0f, -1.0f, 1.0f); // Top Right Of The Quad (Bottom)
			glVertex3f(-1.0f, -1.0f, 1.0f); // Top Left Of The Quad (Bottom)
			glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Bottom)
			glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Bottom)

			glColor3f(1.0f, 0.0f, 0.0f); // Set The Color To Red
			glVertex3f(1.0f, 1.0f, 1.0f); // Top Right Of The Quad (Front)
			glVertex3f(-1.0f, 1.0f, 1.0f); // Top Left Of The Quad (Front)
			glVertex3f(-1.0f, -1.0f, 1.0f); // Bottom Left Of The Quad (Front)
			glVertex3f(1.0f, -1.0f, 1.0f); // Bottom Right Of The Quad (Front)

			glColor3f(1.0f, 1.0f, 0.0f); // Set The Color To Yellow
			glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Back)
			glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Back)
			glVertex3f(-1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Back)
			glVertex3f(1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Back)

			glColor3f(0.0f, 0.0f, 1.0f); // Set The Color To Blue
			glVertex3f(-1.0f, 1.0f, 1.0f); // Top Right Of The Quad (Left)
			glVertex3f(-1.0f, 1.0f, -1.0f); // Top Left Of The Quad (Left)
			glVertex3f(-1.0f, -1.0f, -1.0f); // Bottom Left Of The Quad (Left)
			glVertex3f(-1.0f, -1.0f, 1.0f); // Bottom Right Of The Quad (Left)

			glColor3f(1.0f, 0.0f, 1.0f); // Set The Color To Violet
			glVertex3f(1.0f, 1.0f, -1.0f); // Top Right Of The Quad (Right)
			glVertex3f(1.0f, 1.0f, 1.0f); // Top Left Of The Quad (Right)
			glVertex3f(1.0f, -1.0f, 1.0f); // Bottom Left Of The Quad (Right)
			glVertex3f(1.0f, -1.0f, -1.0f); // Bottom Right Of The Quad (Right)
			glEnd(); // Done Drawing The Quad  
		}
		glEndList();
	}


	//Handles Lighting
	private void lighting()
	{

		FloatBuffer diffuseLight = BufferUtils.createFloatBuffer(4);
		diffuseLight.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();
		glLight(GL_LIGHT0, GL_DIFFUSE, diffuseLight);


		glColorMaterial(GL_FRONT_AND_BACK, GL_AMBIENT_AND_DIFFUSE);
		//FloatBuffer matDiffAmbLight = BufferUtils.createFloatBuffer(4);
		//matDiffAmbLight.(0.5f).put(0.5f).put(0.5f).put(1.0f).flip();
		glColor3f(0.5f, 0.5f, 0.5f);
		glMaterial(GL_FRONT, GL_DIFFUSE, diffuseLight);

		FloatBuffer ambientLight = BufferUtils.createFloatBuffer(4);
		ambientLight.put(0.3f).put(0.3f).put(0.3f).put(1.0f).flip();
		glLight(GL_LIGHT0, GL_AMBIENT, ambientLight);


		FloatBuffer shinyLight = BufferUtils.createFloatBuffer(4);
		shinyLight.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();
		glLight(GL_LIGHT0, GL_SPECULAR, shinyLight);

		FloatBuffer lightPosition = BufferUtils.createFloatBuffer(4);
		lightPosition.put(-4).put(3).put(4).put(0).flip();
		glLight(GL_LIGHT0,  GL_POSITION, lightPosition);

		//model ambient will affect ALL objects!! Its like a global light
		FloatBuffer modelAmbient = BufferUtils.createFloatBuffer(4);
		shinyLight.put(1.0f).put(1.0f).put(1.0f).put(1.0f).flip();
		glLightModel(GL_LIGHT_MODEL_AMBIENT, modelAmbient);


		glLight(GL_LIGHT0, GL_AMBIENT, ambientLight);
		glLight(GL_LIGHT0, GL_POSITION, lightPosition);

		glEnable(GL_NORMALIZE);
		glEnable(GL_COLOR_MATERIAL);
		glEnable(GL_LIGHTING);
		glEnable(GL_LIGHT0);


	}


	private void renderGL(int delta) {

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear The Screen And The Depth Buffer
		glLoadIdentity(); // Reset The View
		Camera.apply();

		lighting();

//		//X AXIS
//		glColor3f(0.0f, 1.0f, 1.f); //Green
//		glBegin(GL_LINES);
//		glVertex3f( 30,0, 0);
//		glVertex3f(-30,0,0);
//		glEnd();
//		glLoadIdentity( ); 
//		Camera.apply();
//
//		//Y AXIS
//		glColor3f(1.0f, 1.0f, 0.0f);
//		glBegin(GL_LINES);
//		glVertex3f(0,30, 0);
//		glVertex3f(0,-30,0);
//		glEnd();
//		glLoadIdentity( ); Camera.apply();
//
//		glLoadIdentity(); // Reset The View
//		Camera.apply();
//
//
//		//Z AXIS
//		glColor3f(0.0f, 1.0f, 0.5f);
//		glBegin(GL_LINES);
//		glVertex3f(0,0, 30);
//		glVertex3f(0,0,-30);
//		glEnd();
//		glLoadIdentity( );
//		Camera.apply();




		//////////////////////////////////// SKY BOX ////////////////////////////////////


		glPushAttrib(GL_ENABLE_BIT);
		glEnable(GL_TEXTURE_2D);
		glDisable(GL_DEPTH_TEST);
		glDisable(GL_LIGHTING);
		glDisable(GL_BLEND);

		glScalef(15.0f, 15.0f, 15.f);
		// Just in case we make the vertices white (for texturing)
		glColor3f(1,1,1);
		starry.bind();
		glTexParameterf(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S, GL_REPEAT );

		glCallList(makeSkyBox);

		glPopAttrib();
		glLoadIdentity( );
		Camera.apply();


		/////////////////////////////////////////////////////////////////////////////////////////   


		//DRAW THE SUN
		glRotatef(rotationAngle,0.0f, 1.0f, 0.0f);
		glScalef(2, 2, 2);
		glEnable(GL_TEXTURE_2D);
		sun.bind();
		glColor3f(1.0f, 1.0f, 1.0f); // Set The Color To 
		Sphere sun = new Sphere();
		sun.setTextureFlag(true);
		sun.setDrawStyle(GLU.GLU_FILL);
		sun.setNormals(GLU.GLU_SMOOTH);
		sun.draw(1.0f, 30, 30);
		glDisable(GL_TEXTURE_2D);

		glLoadIdentity( ); 
		Camera.apply();


		//DRAW THE BIG PLANET
		glEnable(GL_TEXTURE_2D);
		glTranslated(8*Math.cos(orbit3Angle +90), 0, 8*Math.sin(orbit3Angle +90));
		glRotatef(rotationAngle, 0.0f, 1.0f, 0.0f);
		glRotatef(-90, 1.0f, 0.0f, 0.0f);
		pinkPlanet.bind();
		glColor3f(1.0f, 1.0f, 1.0f); // Set The Color To white 
		Sphere centerPlanet = new Sphere();
		centerPlanet.setTextureFlag(true);
		centerPlanet.setDrawStyle(GLU.GLU_FILL);
		centerPlanet.setNormals(GLU.GLU_SMOOTH);        
		centerPlanet.draw(0.5f, 30, 30);
		glDisable(GL_TEXTURE_2D);

		glLoadIdentity( ); 
		Camera.apply();

		//DRAW RING 1
		glTranslated(8*Math.cos(orbit3Angle +90), 0, 8*Math.sin(orbit3Angle +90));
		glRotatef(rotationAngle, 0.0f, 1.0f, 0.0f);
		glRotatef(-90, 1.0f, 0.0f, 0.0f);
		glColor3f(1.0f, 1.0f, 1.0f);//white
		glEnable(GL_TEXTURE_2D);
		purplePaper.bind();
		Cylinder ring = new Cylinder();
		ring.setTextureFlag(true);
		ring.setDrawStyle(GLU.GLU_FILL);
		ring.setNormals(GLU.GLU_SMOOTH);
		ring.draw(1f, 0.8f, 0.1f, 40, 40);
		glDisable(GL_TEXTURE_2D);
		glLoadIdentity( ); 
		Camera.apply();

		//DRAW BIG PLANET ORBIT
		Cylinder orbit3 = new Cylinder();
		glColor3f(1.0f, 1.0f, 1.0f);
		glRotatef(-90, 1.0f, 0.0f, 0.0f);
		orbit3.draw(8, 8, 0.02f, 30, 30);
		glLoadIdentity( ); 
		Camera.apply();

		
		///DRAW FLOATING TRIANGLES
		glTranslated(6*Math.cos(orbit2Angle + 80), 0, 6*Math.sin(orbit2Angle + 80));
		glRotatef(rotationAngle, 0.0f, 1.0f, 0.0f);
		glRotatef(-90, 0.0f, 1.0f, 1.0f);
		glScalef(0.15f, 0.3f, 0.15f);
		glCallList(makePyramid);
		glLoadIdentity( ); 
		Camera.apply();

		glTranslated(6*Math.cos(orbit2Angle + 40), 0, 6*Math.sin(orbit2Angle + 40));
		glRotatef(rotationAngle, 0.0f, 0.0f, 1.0f);
		glRotatef(-90, 1, -1, 1);
		glScalef(0.15f, 0.3f, 0.15f);
		glCallList(makePyramid);
		glLoadIdentity( ); 
		Camera.apply();

		glTranslated(6.0*Math.cos(orbit2Angle), 0, 6.0*Math.sin(orbit2Angle));
		glRotatef(rotationAngle, 1.0f, 0.0f, 0.0f);
		glRotatef(-90, 1, 1, 1);
		glScalef(0.15f, 0.3f, 0.15f);
		glCallList(makePyramid);
		glLoadIdentity( ); 
		Camera.apply();

		//TRIANGLE ORBIT
		Cylinder orbit2 = new Cylinder();
		glColor3f(1.0f, 1.0f, 1.0f);
		glRotatef(-90, 1.0f, 0.0f, 0.0f);
		orbit2.draw(6, 6, 0.02f, 30, 30);
		glLoadIdentity( ); 
		Camera.apply();

		//DRAW SMALLEST PLANET
		glTranslated(4*Math.cos(orbit1Angle), 0, 4*Math.sin(orbit1Angle));
		glRotatef(rotationAngle, 0.0f, 1.0f, 0.0f);
		glColor3f(1.0f, 1.0f, 1.0f);//white
		glEnable(GL_TEXTURE_2D);
		icePlanet.bind();
		Sphere planet2 = new Sphere();
		planet2.setTextureFlag(true);
		planet2.setDrawStyle(GLU.GLU_FILL);
		planet2.setNormals(GLU.GLU_SMOOTH);
		planet2.draw(0.5f, 30, 30);
		glDisable(GL_TEXTURE_2D);
		glLoadIdentity( ); 
		Camera.apply();


		//DRAW THE LITTLE MOON
		if(moonRotAngle > 360)
			moonRotAngle = moonRotAngle%360;
		glTranslated(moonOrbitRadius*Math.cos(moonRotAngle), moonOrbitRadius*Math.cos(moonRotAngle), moonOrbitRadius*Math.sin(moonRotAngle));
		glTranslated(4*Math.cos(orbit1Angle), 0, 4*Math.sin(orbit1Angle));

		glColor3f(1.0f, 1f, 1f); // Set The Color To White
		glEnable(GL_TEXTURE_2D);
		meteor.bind();
		Sphere littleMoon = new Sphere();
		littleMoon.setTextureFlag(true);
		littleMoon.setDrawStyle(GLU.GLU_FILL);
		littleMoon.setNormals(GLU.GLU_SMOOTH);
		littleMoon.draw(0.2f, 30, 30);
		glDisable(GL_TEXTURE_2D);
		glLoadIdentity( ); 
		Camera.apply();


		//DRAW LITTLE PLANET ORBIT
		Cylinder orbit1 = new Cylinder();
		glColor3f(1.0f, 1.0f, 1.0f);
		glRotatef(-90, 1.0f, 0,0);
		orbit1.draw(4, 4, 0.02f, 30, 30);
		glLoadIdentity( ); 
		Camera.apply();


		moonRotAngle += 0.05f;
		rotationAngle += 1.0f;
		orbit1Angle += 0.01f;
		orbit2Angle += 0.007f;
		orbit3Angle += 0.004f;


		glLoadIdentity( );
		Camera.apply();


	}

	/**
	 * Poll Input
	 */
	public void pollInput(int delta) {
		Camera.acceptInput(delta);
		// scroll through key events
		while (Keyboard.next()) {
			if (Keyboard.getEventKeyState()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE)
					closeRequested = true;
				else if (Keyboard.getEventKey() == Keyboard.KEY_P)
					snapshot();
			}
		}

		if (Display.isCloseRequested()) {
			closeRequested = true;
		}
	}

	public void snapshot() {
		System.out.println("Taking a snapshot ... snapshot.png");

		glReadBuffer(GL_FRONT);

		int width = Display.getDisplayMode().getWidth();
		int height= Display.getDisplayMode().getHeight();
		int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		glReadPixels(0, 0, width, height, GL_RGBA, GL_UNSIGNED_BYTE, buffer );

		File file = new File("snapshot.png"); // The file to save to.
		String format = "PNG"; // Example: "PNG" or "JPG"
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		for(int x = 0; x < width; x++) {
			for(int y = 0; y < height; y++) {
				int i = (x + (width * y)) * bpp;
				int r = buffer.get(i) & 0xFF;
				int g = buffer.get(i + 1) & 0xFF;
				int b = buffer.get(i + 2) & 0xFF;
				image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			}
		}

		try {
			ImageIO.write(image, format, file);
		} catch (IOException e) { e.printStackTrace(); }
	}

	/** 
	 * Calculate how many milliseconds have passed 
	 * since last frame.
	 * 
	 * @return milliseconds passed since last frame 
	 */
	public int getDelta() {
		long time = (Sys.getTime() * 1000) / Sys.getTimerResolution();
		int delta = (int) (time - lastFrameTime);
		lastFrameTime = time;

		return delta;
	}

	private void createWindow() {
		try {
			Display.setDisplayMode(new DisplayMode(960, 540));
			Display.setVSyncEnabled(true);
			Display.setTitle(windowTitle);
			Display.create();
		} catch (LWJGLException e) {
			Sys.alert("Error", "Initialization failed!\n\n" + e.getMessage());
			System.exit(0);
		}
	}

	/**
	 * Destroy and clean up resources
	 */
	private void cleanup() {
		Display.destroy();
	}

	public static void main(String[] args) {
		new PlanetView().run();
	}


}


/* ORIGINAL MODEL LOADER:
viperDisplay = glGenLists(1);
glNewList(viperDisplay, GL_COMPILE);
{
	glColor3f(1.0f, 1.0f, 1.0f);
	Model m = null;
	try {
		m = OBJLoader.modelLoader(new File("cube.obj"));
	} catch (FileNotFoundException e) {
		e.printStackTrace();
		cleanup();
		System.exit(1);
	} catch (IOException e) {
		e.printStackTrace();
		cleanup();
		System.exit(1);
	}
	glBegin(GL_TRIANGLES);
	for(Face face : m.faces) 
	{
		Vector3f n1 = m.normals.get((int) face.normal.x -1);
		glNormal3f(n1.x, n1.y, n1.z);
		Vector3f v1 = m.vertices.get((int) face.vertex.x -1);
		glVertex3f(v1.x, v1.y, v1.z);

		Vector3f n2 = m.normals.get((int) face.normal.y -1);
		glNormal3f(n2.x, n2.y, n2.z);
		Vector3f v2 = m.vertices.get((int) face.vertex.y -1);
		glVertex3f(v2.x, v2.y, v2.z);

		Vector3f n3 = m.normals.get((int) face.normal.z -1);
		glNormal3f(n3.x, n3.y, n3.z);
		Vector3f v3 = m.vertices.get((int) face.vertex.z -1);
		glVertex3f(v3.x, v3.y, v3.z);
	}
	glEnd();
}
glEndList();

 */