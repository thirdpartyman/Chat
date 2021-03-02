package Auxillary;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

//Логгер. Записывает данные в файл.
public class FileLogger{

	private static Logger logger;
	private FileHandler fileHandler;
	
	public FileLogger(String filename)
	{
		logger = Logger.getLogger(filename);
		try {
			fileHandler = new FileHandler(filename + ".log", true);
			fileHandler.setFormatter(new SimpleFormatter());
			logger.addHandler(fileHandler);

		} catch (SecurityException | IOException e) {
			e.printStackTrace();
		}
	}
	
	public void log(String message)
	{
		logger.log(Level.INFO, message);
	}
	
	public void close()
	{
		logger.removeHandler(fileHandler);
		fileHandler.close();
	}
}
