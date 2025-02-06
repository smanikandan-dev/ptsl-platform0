package com.itextos.beacon.web.generichttpapi.servlet;

import com.itextos.beacon.commonlib.apperrorhandler.servlets.BasicServlet;
import com.itextos.beacon.commonlib.constants.InterfaceType;
import com.itextos.beacon.commonlib.prometheusmetricsutil.PrometheusMetrics;
import com.itextos.beacon.http.generichttpapi.common.utils.APIConstants;
import com.itextos.beacon.http.generichttpapi.common.utils.Utility;
import com.itextos.beacon.http.interfaceutil.MessageSource;
import com.itextos.beacon.interfaces.generichttpapi.processor.reader.QSRequestReader;
import com.itextos.beacon.interfaces.generichttpapi.processor.reader.RequestReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class QSGenericReceiver
        extends
        BasicServlet {

    private static final long serialVersionUID = -188048290390545145L;

    private static final Logger logger = LoggerFactory.getLogger(QSGenericReceiver.class);

    public QSGenericReceiver() {
    }

    @Override
    protected void doGet(
            HttpServletRequest aRequest,
            HttpServletResponse aResponse)
            throws ServletException,
            IOException {
        if (logger.isDebugEnabled())
            logger.debug("Incoming api request from remote {}", aRequest.getRemoteAddr());

        // TODO: remove sb arg from below method
        StringBuffer sb = new StringBuffer();

        final long processStart = System.nanoTime();
        PrometheusMetrics.apiIncrementAcceptCount(InterfaceType.HTTP_JAPI, MessageSource.GENERIC_QS, APIConstants.CLUSTER_INSTANCE, aRequest.getRemoteAddr());
        final RequestReader reader = new QSRequestReader(aRequest, aResponse, getServletName(), null, sb);
        reader.processGetRequest();

        final long processEnd = System.nanoTime();
        final long processTaken = processEnd - processStart;

        // Convert nanoseconds to milliseconds if needed
        final long processTakenMillis = processTaken / 1_000_000;


        if (logger.isDebugEnabled()) {
            logger.debug("Request Start time : '{}' End time : '{}' Processing time : '{}' milliseconds",
                    Utility.getFormattedDateTime(processStart / 1_000_000),  // Convert to milliseconds for logging
                    Utility.getFormattedDateTime(processEnd / 1_000_000),    // Convert to milliseconds for logging
                    processTakenMillis);
        }

    }

    @Override
    protected void doPost(
            HttpServletRequest arg0,
            HttpServletResponse arg1)
            throws ServletException,
            IOException {
        doGet(arg0, arg1);
    }

}
