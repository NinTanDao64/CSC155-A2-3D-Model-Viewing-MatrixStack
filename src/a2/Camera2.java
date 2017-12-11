package a2;

import graphicslib3D.Matrix3D;
import graphicslib3D.Point3D;
import graphicslib3D.Vector3D;

//A new attempt at creating the camera the correct way
public class Camera2 {
	private Point3D location;
	private Vector3D U, V, N;
	
	public Camera2(double x, double y, double z) {
		this.location = new Point3D(x, y, z);
		this.U = new Vector3D(1, 0, 0);
		this.V = new Vector3D(0, 1, 0);
		this.N = new Vector3D(0, 0, 1);
	}
	
	public Point3D getLocation() {
		return location;
	}
	
	public void setLocation(Point3D point) {
		this.location.setX(point.getX());
		this.location.setY(point.getY());
		this.location.setZ(point.getZ());
	}
	
	public Vector3D getRightAxis() {
		return U;
	}
	
	public Vector3D getUpAxis() {
		return V;
	}
	
	public Vector3D getViewDirection() {
		return N;
	}
	
	public void moveForward() {
		Vector3D curLoc = new Vector3D(this.getLocation());
		Vector3D viewDir = this.getViewDirection().normalize();
		Vector3D newLocVec = curLoc.minus(viewDir.mult(0.125));
		Point3D newLoc = new Point3D(newLocVec.getX(), newLocVec.getY(), newLocVec.getZ());	
		setLocation(newLoc);
	}
	
	public Matrix3D getView() {
		Matrix3D viewMat = new Matrix3D();
		return viewMat;
	}
}
