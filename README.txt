Nicole Seleme (ns2873)
COMS 4160 PA1

*********************************************************

The program consists of two class files:

	* PlanetView.java
	* Camera.java

The resulting model was a miniature solar system composed of a few planets and triangles. They all orbit around the center Star, and rotate along their own axes as well.

The planets were rendered using GLU Spheres, and the orbits with GLU Cylinders. The rest of the shapes were constructed with GL Primitives of Triangles and Quads. 

The program consists of a skybox made up of a cube (built with Quads) and with a starry texture.

The Camera class was altered lightly to limit the movement of the camera to within the box only. (The Camera class was mostly left untouched from the PA1 skeleton code.)

After that, the planets, spheres, orbits, triangles and such were rendered as normal (GLU and GL library functions). Their movements and positions were created using the GL rotate, scale, and translate matrices.

In order to revert to the original coordinate system, the Identity Matrix is loaded after every transformation of the ModelView Matrix and then the Camera is reapplied every time. 

In order to apply textures to the objects, I used the Slick library utils API obtained from the internet. Please link with the attached library (slick-util.jar) in the zip files.


The Lighting is soft and its source is positioned at [-4, -3, 4] in the coordinate system. 

Display Lists are used to facilitate the rendering of the Skybox, the texture application of the same, and the drawing of triangles (which is used three times in this model).

Finally, the Java environment used to code this was JDK 1.8
