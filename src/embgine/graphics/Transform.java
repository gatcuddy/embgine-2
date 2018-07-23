package embgine.graphics;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Transform {
	
	public Transform clone() {
		return new Transform(position.x, position.y, size.x, size.y, rotation);
	}
	
	public Transform(float x, float y, float w, float h, float r) {
		position = new Vector3f(x, y, 0);
		size = new Vector2f(w, h);
		rotation = r;
	}
	
	public Transform(Transform t) {
		position = new Vector3f(t.position);
		size = new Vector2f(t.size);
		rotation = t.rotation;
	}
	
	public Transform(float w, float h) {
		position = new Vector3f();
		size = new Vector2f(w, h);
		rotation = 0;
	}
	
	public Transform() {
		position = new Vector3f();
		size = new Vector2f(1, 1);
		rotation = 0;
	}
	
	protected Vector3f position;
	
	protected Vector2f size;
	
	protected float rotation;
	
	public void set(Transform t) {
		position = new Vector3f(t.position);
		size = new Vector2f(t.size);
		rotation = t.rotation;
	}
	
	public void set(float x, float y, float w, float h, float r) {
		position.set(x, y, 0);
		size.set(w, h);
		rotation = r;
	}
	
	public void set(float x, float y, float w, float h) {
		position.set(x, y, 0);
		size.set(w, h);
	}
	
	public void setPosition(float x, float y) {
		position.set(x, y, 0);
	}
	
	public void setPosition(Vector3f p) {
		position.set(p);
	}
	
	public void setPosition(Vector2f p) {
		position.set(p, 0);
	}
	
	public void setSize(Vector2f s) {
		size.set(s);
	}
	
	public void setRotation(float r) {
		rotation = r;
	}
	
	public void setSize(float w, float h) {
		size.set(w, h);
	}
	
	public void setWidth(float w) {
		size.x = w;
	}
	
	public void setHeight(float h) {
		size.y = h;
	}
	
	public float getHeight() {
		return size.y;
	}
	
	public float getWidth() {
		return size.x;
	}
	
	public void setX(float x) {
		position.x = x;
	}
	
	public float getX() {
		return position.x;
	}
	
	public void setY(float y) {
		position.y = y;
	}
	
	public float getY() {
		return position.y;
	}
	
	public Vector3f getPosition() {
		return position;
	}
	
	public Vector2f getSize() {
		return size;
	}
	
	public float getRotation() {
		return rotation;
	}

	public void move(float x, float y) {
		position.add(x, y, 0);
	}
	
	public void moveX(float x) {
		position.add(x, 0, 0);
	}
	
	public void moveY(float y) {
		position.add(0, y, 0);
	}
}