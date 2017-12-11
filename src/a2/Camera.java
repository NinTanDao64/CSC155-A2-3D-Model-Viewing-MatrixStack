package a2;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

public class Camera {
	private double xLoc;
	private double yLoc;
	private double zLoc;
	float pitch = 0;
	float yaw = 0;
	private Matrix3D viewMat;
	
	public Camera(double x, double y, double z) {
		this.xLoc = x;
		this.yLoc = y;
		this.zLoc = z;
		
		viewMat = new Matrix3D();
		updateView();
	}
	
	public void updateView() {
		Vector3D newLoc = new Vector3D(xLoc, yLoc, zLoc);
		float cosYaw = (float)Math.cos(Math.toRadians(yaw));
		float sinYaw = (float)Math.sin(Math.toRadians(yaw));
		float cosPitch = (float)Math.cos(Math.toRadians(pitch));
		float sinPitch = (float)Math.sin(Math.toRadians(pitch));
		Vector3D xAxis = new Vector3D(cosYaw, 0, -sinYaw);
		Vector3D yAxis = new Vector3D(sinYaw * sinPitch, cosPitch, cosYaw * sinPitch);
		Vector3D zAxis = new Vector3D(sinYaw * cosPitch, -sinPitch, cosPitch * cosYaw);
		double[] matrixArray = new double[] {xAxis.getX(), yAxis.getX(), zAxis.getX(), 0, xAxis.getY(), yAxis.getY(), zAxis.getY(), 0, xAxis.getZ(), yAxis.getZ(), zAxis.getZ(), 0, -(xAxis.dot(newLoc)), -(yAxis.dot(newLoc)), -(zAxis.dot(newLoc)), 1};
		viewMat.setValues(matrixArray);
		//viewMat.translate(-xLoc, -yLoc, -zLoc);
	}
	
	public void moveForward() {
		this.zLoc = zLoc - 0.125;
		updateView();
	}
	
	public void moveBackward() {
		this.zLoc = zLoc + 0.125;
		updateView();
	}
	
	public void moveLeft() {
		this.xLoc = xLoc - 0.125;
		updateView();
	}
	
	public void moveRight() {
		this.xLoc = xLoc + 0.125;
		updateView();
	}
	
	public void moveUp() {
		this.yLoc = yLoc + 0.125;
		updateView();
	}
	
	public void moveDown() {
		this.yLoc = yLoc - 0.125;
		updateView();
	}
	
	public void yawLeft() {
		this.yaw++;
		updateView();
	}
	
	public void yawRight() {
		this.yaw--;
		updateView();
	}
	
	public void pitchUp() {
		this.pitch++;
		updateView();
	}
	
	public void pitchDown() {
		this.pitch--;
		updateView();
	}
	
	public Matrix3D getView() {
		return viewMat;
	}
	
}
