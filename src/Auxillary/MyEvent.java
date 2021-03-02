package Auxillary;
import java.awt.EventQueue;
import java.util.ArrayList;


public class MyEvent extends ArrayList<Runnable> {
	public void evaluate()
	{
		for(Runnable event : this)
		{
			EventQueue.invokeLater(event);
		}
	}
}
