package view.CustomVisualization;

import edu.uci.ics.jung.visualization.FourPassImageShaper;
import edu.uci.ics.jung.visualization.decorators.VertexIconShapeTransformer;
import model.MyVertex;
import org.apache.commons.collections15.Transformer;
import view.IconsStore;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * A vertex icon shape transformer that does not hold a map of vertex-> icon but
 * instead delegates this task to an IconsStore Created by miroslavbatchkarov on
 * 11/03/2014.
 */
public class CustomVertexIconShapeTransformer extends VertexIconShapeTransformer {

    private IconsStore icons;

    /**
     * @param delegate
     */
    public CustomVertexIconShapeTransformer(Transformer delegate,
            IconsStore icons) {
        super(delegate);
        this.icons = icons;
    }

    /**
     * get the shape from the image. If not available, get the shape from the
     * delegate VertexShapeFunction
     */
    @Override
    public Shape transform(Object v) {
        Icon icon = this.icons.transform((MyVertex) v); //fuck generics

        // code below lifted from super
        if (icon != null && icon instanceof ImageIcon) {
            Image image = ((ImageIcon) icon).getImage();
            Shape shape = (Shape) shapeMap.get(image);
            if (shape == null) {
                shape = FourPassImageShaper.getShape(image, 30);
                if (shape.getBounds().getWidth() > 0
                        && shape.getBounds().getHeight() > 0) {
                    // don't cache a zero-sized shape, wait for the image
                    // to be ready
                    int width = image.getWidth(null);
                    int height = image.getHeight(null);
                    AffineTransform transform = AffineTransform
                            .getTranslateInstance(-width / 2, -height / 2);
                    shape = transform.createTransformedShape(shape);
                    shapeMap.put(image, shape);
                }
            }
            return shape;
        } else {
            return (Shape) delegate.transform(v);
        }
    }
}
