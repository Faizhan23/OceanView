package util;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;

/**
 * ServletContextListener that runs once when the application starts.
 * It sets the absolute path for the reservation data file so that
 * FileStorage always writes to a consistent location on disk.
 */
@WebListener
public class AppInitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext ctx = sce.getServletContext();

        // Store the data file next to the web application's root on disk.
        // getRealPath("/") returns the absolute file-system path to the
        // deployed webapp root (e.g. .../webapps/OceanViewResort/).
        String webRoot = ctx.getRealPath("/");
        if (webRoot == null) {
            // Fallback to the system temp directory
            webRoot = System.getProperty("java.io.tmpdir");
        }

        // Create a "data" sub-folder inside the webapp root
        String dataDir = webRoot + File.separator + "data";
        String dataFile = dataDir + File.separator + "reservations.txt";

        // Ensure the directory exists
        File dir = new File(dataDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        FileStorage.setFilePath(dataFile);
        ctx.log("Reservation data file: " + dataFile);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Nothing to clean up
    }
}
