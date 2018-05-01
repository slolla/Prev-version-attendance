package JavaFXGUI;
/**
 * An interface that defines the something that can play an animation.
 * @author Kevin
 *
 */
public interface Playable {
	public void play();
	public void reverse();
	public int getPage();
	public void clear();
}
