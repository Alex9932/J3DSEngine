package alex9932.utils;

public interface IMouseListener {
	public void buttonPressed(int button, double x, double y);
	public void buttonReleased(int button, double x, double y);
	public void drag(int button, double x, double y);
	public void move(double x, double y);
	public void scroll(int scroll);
}