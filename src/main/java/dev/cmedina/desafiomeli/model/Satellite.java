package dev.cmedina.desafiomeli.model;

public enum Satellite {
	kenobi(new Point(-500f, -200f)), skywalker(new Point(100f, -100f)), sato(new Point(500f, 100f));
	
	private Point point;
	
	Satellite(Point point) {
		this.point = point;
	}
	
	public Point getPosition() {
		return this.point;
	}
}
