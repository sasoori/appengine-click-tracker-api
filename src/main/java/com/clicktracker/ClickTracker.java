package com.clicktracker;

import com.clicktracker.rest.CampaignRestService;
import com.clicktracker.utils.Constants;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.logging.Logger;

// On server init, check if table exists, if not create one
@WebListener
public class ClickTracker implements ServletContextListener {
    private static final Logger logger = Logger.getLogger(CampaignRestService.class.getName());

    public void contextInitialized(ServletContextEvent event) {
        String sqlCreate = "CREATE TABLE IF NOT EXISTS `clicks` (\n" +
                "  `id` int(11) NOT NULL AUTO_INCREMENT,\n" +
                "  `campaign_id` bigint(20) DEFAULT NULL,\n" +
                "  `timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  UNIQUE KEY `id_UNIQUE` (`id`)\n" +
                ")";
        Connection conn;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(Constants.SqlUrl);
            Statement stateManager = conn.createStatement();
            stateManager.execute(sqlCreate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("All is well");
    }

    public void contextDestroyed(ServletContextEvent event) {
        // Do stuff during webapp's shutdown.
    }

}
