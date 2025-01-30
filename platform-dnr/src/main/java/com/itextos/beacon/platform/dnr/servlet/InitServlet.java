package com.itextos.beacon.platform.dnr.servlet;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.itextos.beacon.commonlib.apperrorhandler.servlets.BasicServlet;
import com.itextos.beacon.commonlib.prometheusmetricsutil.PrometheusMetrics;
import com.itextos.beacon.platform.dnrfallback.inmem.DlrFallbackQReaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Servlet implementation class InitServlet
 */
public class InitServlet
        extends
        BasicServlet
{
    private static final Logger logger = LoggerFactory.getLogger(InitServlet.class);

    private static final long serialVersionUID = 1L;

    /**
     * Default constructor.
     */
    public InitServlet()
    {
        super();
    }

    @Override
    public void init(
            ServletConfig config)
            throws ServletException
    {
//    	com.itextos.beacon.App.createfolder();

        PrometheusMetrics.registerServer();
        PrometheusMetrics.registerApiMetrics();
        DlrFallbackQReaper.getInstance();
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException,
            IOException
    {}

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException,
            IOException
    {}

    private void createfolder() {

        System.setProperty("common.property.file.location", "/global.properties");
        System.setProperty("log4j.configurationFile", "file:/log4j2-common.xml");
        System.setProperty("prometheus.jetty.port", "1075");

        foldercreaton("/opt/jboss/wildfly/logs/dnp");
        foldercreaton("/opt/jboss/wildfly/logs/http");

        foldercreaton("/opt/jboss/wildfly/logs/k2e");

        foldercreaton("/opt/jboss/wildfly/logs/topic");
        foldercreaton("/opt/jboss/wildfly/logs/table2db");
        foldercreaton("/opt/jboss/wildfly/logs/consumer");
        foldercreaton("/opt/jboss/wildfly/logs/producer");
        foldercreaton("/opt/jboss/wildfly/logs/kafkasender");
        foldercreaton("/opt/jboss/wildfly/logs/executorlog1");
        foldercreaton("/opt/jboss/wildfly/logs/executorlog2");
        foldercreaton("/opt/jboss/wildfly/logs/application");
        foldercreaton("/opt/jboss/wildfly/logs/kafkareceiver");
        foldercreaton("/opt/jboss/wildfly/logs/timetaken");
        foldercreaton("/opt/jboss/wildfly/logs/aux");


		/*
		try {
			AppendToHosts.appendCustomHostsToSystemHosts();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
    }
    public static void foldercreaton(String folderPath) {


        // Create a File object representing the directory
        File folder = new File(folderPath);

        // Check if the directory exists
        if (!folder.exists()) {
            // Attempt to create the directory
            if (folder.mkdirs()) {
                System.out.println("Directory created successfully: " + folderPath);
                logger.debug("Directory created successfully: " + folderPath);

            } else {
                System.out.println("Failed to create directory: " + folderPath);
            }
        } else {
            System.out.println("Directory already exists: " + folderPath);
            logger.debug("Directory already exists: " + folderPath);

        }
    }
}