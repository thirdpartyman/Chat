package Settings;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;
import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import Auxillary.MyRange;
import Auxillary.StringUtilities;


//Создает окно JOptionPane с полями, соответствующими полям переданного объекта
public class SettingsPanel {

	private Box p = Box.createVerticalBox();

	private Object obj;
	
	public SettingsPanel(Object obj) {
//		p.setLayout(new VerticalLayout(p));
		create(obj);
		
		this.obj = obj;
	}

	private List<Component> fields = new ArrayList<Component>();
	
	private void create(Object obj)
	{
		Class<?> clazz = obj.getClass();
		Field[] fields = clazz.getFields();

		for (Field field : fields) {

			Object value;
			try {
				value = field.get(obj);
			} catch (IllegalAccessException e) {
				value = null;
			}
			String name = field.getName();
			Class myclass = value.getClass();

			if (myclass == Boolean.class) {
				JCheckBox comp = new JCheckBox(stringToLabel(name));
				comp.setAlignmentX(Component.LEFT_ALIGNMENT);
				if (value != null)
					comp.setSelected((Boolean) value);
				p.add(comp);
				this.fields.add(comp);
				continue;
			}
			
			if (myclass == MyRange.class) {
				JPanel panel = new JPanel(new FlowLayout());
				JLabel label = new JLabel(stringToLabel(name));
				Slider comp = new Slider(value != null ? (MyRange) value : new MyRange(0, 100));	
				panel.setAlignmentX(Component.LEFT_ALIGNMENT);
				panel.add(label);			
				panel.add(comp);
				p.add(panel);
				this.fields.add(comp);
				continue;
			}
		}
	}
	
	
	public void update(Object obj)
	{
		Class<?> clazz = obj.getClass();
		List<Field> fields = Arrays.asList(clazz.getFields());

	
		Iterator<Component> it_comp = this.fields.iterator();
		Iterator<Field> it_field = fields.iterator();
		
		while (it_comp.hasNext() && it_field.hasNext()) {
			Component comp = it_comp.next();
			Field field = it_field.next();
			
			Object value;
			try {
				value = field.get(obj);
			} catch (IllegalAccessException e) {
				value = null;
			}
			String name = field.getName();
			Class myclass = value.getClass();
			
			if (myclass == Boolean.class) {
				JCheckBox checkbox = (JCheckBox) comp;
				if (value != null)
					checkbox.setSelected((Boolean) value);
				continue;
			}
			
			if (myclass == MyRange.class) {
				Slider slider = (Slider) comp;
				
				if (value == null)
					try {
						field.set(obj, new MyRange(0, 100));
					} catch (IllegalArgumentException | IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
				if (value != null)
					slider.setRange((MyRange) value);
				
				continue;
			}
		}
	}
	
	
	private boolean save(Object obj)
	{
		boolean modified = false;
		
		Class<?> clazz = obj.getClass();
		Field[] fields = clazz.getFields();

	
		for(int i = 0; i != fields.length; i++){
			Component comp = this.fields.get(i);
			Field field = fields[i];
			
			Object value;
			try {
				value = field.get(obj);
			} catch (IllegalAccessException e) {
				value = null;
			}
			String name = field.getName();
			Class myclass = value.getClass();
			
			
			if (myclass == Boolean.class) {
				JCheckBox checkbox = (JCheckBox) comp;
				boolean newvalue = checkbox.isSelected();
				try {
					field.set(obj, newvalue);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
				if (value != null && (boolean) value != newvalue)
					modified = true;
				continue;
			}
			
			if (myclass == MyRange.class) {
				Slider slider = (Slider) comp;
				int newvalue = slider.getValue();
				
				if (value != null && ((MyRange) value).getValue() != newvalue)
					modified = true;
				
				((MyRange) value).setValue(newvalue);

				continue;
			}
		}
		System.out.println(obj);
		return modified;
	}
	
	
	public boolean show() {
		
		update(obj);
		
		int Result = JOptionPane.showConfirmDialog(Window.getWindows()[0], p, "Настройки", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		switch (Result) {
		case JOptionPane.YES_OPTION:
			return save(obj);
		case JOptionPane.CANCEL_OPTION:
		}
		return false;
	}
	
	
	private static String stringToLabel(String str)
	{
	      String[] strSplit = str.split("(?=[A-Z])");
	      
	      StringBuilder stringBuilder = new StringBuilder();
	      stringBuilder.append(StringUtilities.Capitalize(strSplit[0]));
	      for (int i = 1; i < strSplit.length; i++)
	      {  
	    	  stringBuilder.append(' ');
	    	  stringBuilder.append(strSplit[i].toLowerCase()); 
	      }
	      return stringBuilder.toString();
	}
	
	
	
	private class Slider extends JProgressBar
	{
        Slider(MyRange range)
        {
        	super(new DefaultBoundedRangeModel(range.value, 0, range.min, range.max));

			setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
	        setStringPainted(true);

			addMouseWheelListener(mouseWheelListener);       
			addMouseMotionListener(mouseMotionListener);
			addMouseListener(mouseListener);		
        }
        
        
        MouseListener mouseListener = new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				((Slider)e.getComponent()).setValue((int) ((double) e.getX() / (double) e.getComponent().getWidth() * 100.));
			}

   			@Override public void mouseEntered(MouseEvent e) {}
   			@Override public void mouseExited(MouseEvent e) {}
   			@Override public void mousePressed(MouseEvent e) {}
   			@Override public void mouseReleased(MouseEvent e) {}
		};
        
		MouseMotionListener mouseMotionListener = new MouseMotionListener() {

			@Override
			public void mouseDragged(MouseEvent e) {
				((Slider)e.getComponent()).setValue((int) ((double) e.getX() / (double) e.getComponent().getWidth() * 100.));
			}

			@Override
			public void mouseMoved(MouseEvent e) {}
		};
		
		MouseWheelListener mouseWheelListener = new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				((Slider)e.getComponent()).setValue(((Slider)e.getComponent()).getValue() - e.getWheelRotation());
			}
		};
        
        void setRange(MyRange range)
        {
        	setModel(new DefaultBoundedRangeModel(range.value, 0, range.min, range.max));
//        	this.updateUI();
        }
	}
}
