package Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.net.URISyntaxException;

/**
 * The LogManager class is responsible for managing log files and writing log messages to the files.
 */
public class LogManager {
    
    private File logFile;

    /**
     * Constructs a LogManager object and initializes the log file.
     */
    public LogManager() {
        try {
            String currentDir = getClassFolder(LogManager.class)+"/Logs";
            File parentDir = new File(currentDir).getParentFile();
            System.out.println("LogManager Is Generating Log Files in: " + currentDir);
            logFile = new File(parentDir, generateLogFileName());
            
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (Exception e ) {
            System.err.println("Failed to initialize LogManager: " + e.getMessage());
        }
    }

    /**
     * Constructs a LogManager object without initializing the log file.
     * This constructor is used for non-testing purposes.
     * @param temp a boolean value indicating if the constructor is used for non-testing purposes
     */
    public LogManager(boolean temp) {
        //Used for non testing
    }

    /**
     * Returns the folder path of the specified class.
     * @param clazz the class for which to get the folder path
     * @return the folder path of the specified class
     * @throws URISyntaxException if the URI syntax is invalid
     */
    private String getClassFolder(Class<?> clazz) throws URISyntaxException {
        URL location = clazz.getProtectionDomain().getCodeSource().getLocation();
        File file = new File(location.toURI());
        return file.getParentFile().getPath();
    }

    /**
     * Writes the specified log message to the log file.
     * @param message the log message to write
     */
    public void log(String message) {
        if (logFile == null){
            return;
        }
        try (FileWriter writer = new FileWriter(logFile, true)) {
            writer.write(message + System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    

    /**
     * Generates a unique log file name based on the current date and time.
     * @return the generated log file name
     */
    private String generateLogFileName() {
        LocalDateTime now = LocalDateTime.now();
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        return "log_" + now.format(formatter) + ".txt";
    }
}
