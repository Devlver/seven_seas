package GUI;

import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.util.Duration;

/**
 * Contains methods for node animations
 */
public final class Animation {
	/**
	 * Fades the specified node in to a specified max value or out
	 *
	 * @param node          Node to be animated
	 * @param duration      Animation duration
	 * @param targetOpacity Target opacity value where target <= 1.0
	 */
	public static void Fade(Node node, double duration, double targetOpacity) {
		FadeTransition transition = new FadeTransition();
		transition.setDuration(Duration.millis(duration));
		
		transition.setNode(node);
		
		transition.setFromValue(node.getOpacity());
		transition.setToValue(targetOpacity);
		
		transition.play();
	}
}
