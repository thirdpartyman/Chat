package Auxillary;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.TransferHandler;

//Обработка перетаскивания файлов
public class FileTransferHandler extends TransferHandler
{
	private Consumer<List<File>> onDrop = (files)->{};
	
	public FileTransferHandler(Consumer<List<File>> onDrop)
	{
		this.onDrop = onDrop;
	}
	
	@Override
	public boolean canImport(TransferSupport support) {
		
		return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
		|| support.isDataFlavorSupported(DataFlavor.stringFlavor);
	}
	
	@Override
	public boolean importData(TransferSupport support) {
		
		Transferable t = support.getTransferable();
		try {
			
			if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {

				Object o = t.getTransferData(DataFlavor.javaFileListFlavor);
				
				@SuppressWarnings("unchecked")
				List<File> files = (List<File>) o;

				onDrop.accept(files);
			}
			else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {			
				Object o = t.getTransferData(DataFlavor.stringFlavor);
				String str = o.toString();
				
				onDrop.accept(Arrays.asList(new File(str)));
			}
			
		} catch (UnsupportedFlavorException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return super.importData(support);
	}
}
