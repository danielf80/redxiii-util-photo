package com.redxiii.util.photo.x9;

public class Sector {
    public int x;
	public int y;
    
    public int width;
    public int height;
    
    public int getX() {
  		return x;
  	}
    public int getXEnd() {
  		return x + width;
  	}
  	public void setX(int x) {
  		this.x = x;
  	}
  	public void setXEnd(int end) {
  		this.width = end - x;
  	}
  	public int getY() {
  		return y;
  	}
  	public int getYEnd() {
  		return y + height;
  	}
  	public void setY(int y) {
  		this.y = y;
  	}
  	public void setYEnd(int end) {
  		this.height = end - y;
  	}
  	public int getWidth() {
  		return width;
  	}
  	public void setWidth(int width) {
  		this.width = width;
  	}
  	public int getHeight() {
  		return height;
  	}
  	public void setHeight(int height) {
  		this.height = height;
  	}
    
}
