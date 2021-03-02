package Auxillary;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.BoxLayout;

// Менеджер вертикального расположения компонентов
public class VerticalLayout extends BoxLayout
{
    public VerticalLayout(Container target) {
		super(target, BoxLayout.Y_AXIS);
	}

    // Метод расположения компонентов в контейнере
    @Override
    public void layoutContainer(Container container)
    {
    	super.layoutContainer(container);
        Component list[] = container.getComponents();
        int currentY = list.length > 0 ? list[0].getBounds().y : 0;
        for (int i = 0; i < list.length; i++) {
            Dimension pref = list[i].getPreferredSize();
            Rectangle rect = list[i].getBounds();

            if (pref.height < rect.height)
            	rect.height = pref.height;
            if (currentY != rect.y)
            	rect.y = currentY;
            list[i].setBounds(rect);
            currentY += rect.height;
        }
    }
}