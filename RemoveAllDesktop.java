

import java.io.File;

import Resources.ResourceManager; 

// Removes all Desktop.ini files created by Google drive - breaks code if not removed.
//[WARNING]: PLEASE DO NOT MODIFY | YOU CAN ACCIDENTALLY DELETE THIS WHOLE WORKSPACE

/**
 * The RemoveAllDesktop class removes all desktop.ini files in the workspace.
 * This is useful for removing the desktop.ini files created by Google Drive.
 * 
 */
public class RemoveAllDesktop {
    public RemoveAllDesktop() {
       //
    }
    public static int Destroy() {
        String currentDir = System.getProperty("user.dir");
        String parentDir = new File(currentDir).getParent();
        return deleteDesktopIniFiles(parentDir);
    }

    private static int deleteDesktopIniFiles(String directory) {
        File dir = new File(directory);
        File[] files = dir.listFiles();
        int amt = 0;
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDesktopIniFiles(file.getAbsolutePath());
                } else if (file.getName().equals("desktop.ini")) {
                    file.delete();
                    amt++;
                }
            }
        }
        return amt;
    }
    public static void main(String[] args) {
        new RemoveAllDesktop();
        int amt = Destroy();
        System.out.println(""+amt+" Desktop.ini files have been removed.");
    }
} 