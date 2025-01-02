import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

public class MyContextListener implements ServletContextListener {

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        try {
            // Shutdown Abandoned Connection Cleanup Thread
            AbandonedConnectionCleanupThread.uncheckedShutdown();
            System.out.println("MySQL Cleanup thread shutdown.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Initialization logic if needed
        System.out.println("Context initialized.");
    }
}