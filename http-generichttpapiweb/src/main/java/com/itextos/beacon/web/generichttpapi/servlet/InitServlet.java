package com.itextos.beacon.web.generichttpapi.servlet;

import com.itextos.beacon.commonlib.apperrorhandler.servlets.BasicServlet;
import com.itextos.beacon.commonlib.constants.InterfaceType;
import com.itextos.beacon.commonlib.messageidentifier.MessageIdentifier;
import com.itextos.beacon.commonlib.prometheusmetricsutil.PrometheusMetrics;
import com.itextos.beacon.http.generichttpapi.common.utils.APIConstants;
import com.itextos.beacon.http.interfacefallback.inmem.FallbackQReaper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.util.Date;

public final class InitServlet extends BasicServlet {

    private static final long serialVersionUID = -922457646505165311L;

    private static final Logger logger = LoggerFactory.getLogger(InitServlet.class);


    public InitServlet() {
        super();
    }


    @Override
    public void init(ServletConfig config) throws ServletException {

        if (logger.isDebugEnabled())
            logger.debug("init() -  start ");

        createfolder();


        try {

            final MessageIdentifier lMsgIdentifier = MessageIdentifier.getInstance();
            lMsgIdentifier.init(InterfaceType.HTTP_JAPI);

            final String lAppInstanceId = lMsgIdentifier.getAppInstanceId();

            if (logger.isDebugEnabled())
                logger.debug("Appinstance ID : {}", lAppInstanceId);

            PrometheusMetrics.registerServer();
            PrometheusMetrics.registerApiMetrics();

            APIConstants.setAppInstanceId(lAppInstanceId);

            if (APIConstants.CLUSTER_INSTANCE == null) {
                logger.error("InitServlet.inti() - Instance Cluster value is not Configured..., Hence Stoping the Instance.");
                //  System.exit(-1);
            }

            FallbackQReaper.getInstance();
            // this consumer has been moved to platform-asynprocessor module
            // startConsumers();
        }
        catch (final Exception e) {
            logger.error("InitServlet.inti() - Error while gettting appInstance ID from property file due to ....", e);
        }
    }

    private void folderCreation(String folderPath) {
        // Create a File object representing the directory
        File folder = new File(folderPath);

        // Check if the directory exists
        if (!folder.exists()) {
            // Attempt to create the directory
            if (folder.mkdirs()) {
                System.out.println("Directory created successfully: " + folderPath);
                if (logger.isDebugEnabled()) {
                    logger.debug("Directory created successfully: {}", folderPath);
                }
            } else {
                System.out.println("Failed to create directory: " + folderPath);
            }
        } else {
            System.out.println("Directory already exists: " + folderPath);
            if (logger.isDebugEnabled()) {
                logger.debug("Directory already exists: {}", folderPath);
            }
        }
    }

    // TODO: remove this folder creation
    private void createfolder() {

        //        System.setProperty("common.property.file.location", "/global.properties");
        //        System.setProperty("log4j.configurationFile", "file:/log4j2-common.xml");
        //        System.setProperty("prometheus.jetty.port", "1075");

        folderCreation("/opt/jboss/wildfly/logs/dnp");
        folderCreation("/opt/jboss/wildfly/logs/http");

        folderCreation("/opt/jboss/wildfly/logs/k2e");

        folderCreation("/opt/jboss/wildfly/logs/topic");
        folderCreation("/opt/jboss/wildfly/logs/table2db");
        folderCreation("/opt/jboss/wildfly/logs/consumer");
        folderCreation("/opt/jboss/wildfly/logs/producer");
        folderCreation("/opt/jboss/wildfly/logs/kafkasender");
        folderCreation("/opt/jboss/wildfly/logs/executorlog1");
        folderCreation("/opt/jboss/wildfly/logs/executorlog2");
        folderCreation("/opt/jboss/wildfly/logs/application");
        folderCreation("/opt/jboss/wildfly/logs/kafkareceiver");
        folderCreation("/opt/jboss/wildfly/logs/timetaken");
        folderCreation("/opt/jboss/wildfly/logs/aux");

    }

    //    private static void startConsumers() {
    //
    //        if (CommonUtility.isEnabled(APIConstants.START_CONSUMER)) {
    //            if (logger.isDebugEnabled())
    //                logger.debug("**************Kafka consumer started*********************");
    //
    //            try {
    //                final ProcessorInfo lProcessor = new ProcessorInfo(Component.INTERFACE_ASYNC_PROCESS, false);
    //                lProcessor.process();
    //            } catch (final Exception e) {
    //                logger.error("Unable to start the Kafka Async Consumer.., Stopping the Instance..", e);
    //                System.exit(-1);
    //            }
    //
    //            final FilePoller lFilePoller = new FilePoller();
    //        } else if (logger.isDebugEnabled())
    //            logger.debug("**************Kafka consumer not started*********************");
    //    }

    @Override
    public void destroy() {
        MessageIdentifier.getInstance().resetMessageIdentifier();
    }

    @Override
    public void doGet(
            HttpServletRequest aRequest,
            HttpServletResponse aResponse)
            throws IOException {

        try (
                PrintWriter writer = aResponse.getWriter();) {
            writer.write("Time : " + new Date());
            writer.flush();
            aResponse.setStatus(HttpURLConnection.HTTP_OK);
        }
        catch (final Exception e) {
            aResponse.setStatus(HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    @Override
    public void doPost(
            HttpServletRequest aRequest,
            HttpServletResponse aResponse)
            throws IOException {
        doGet(aRequest, aResponse);
    }

}