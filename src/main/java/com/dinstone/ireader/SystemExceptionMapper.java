/*
 * CopyRight (C) 2013~2014 北京中交兴路信息科技有限公司 保留所有权利
 */

package com.dinstone.ireader;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashMap;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.Response.StatusType;
import javax.ws.rs.ext.ExceptionMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author guojinfei
 * @version 1.0.0.2014-7-15
 */
public class SystemExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = LoggerFactory.getLogger(SystemExceptionMapper.class);

    @Override
    public Response toResponse(Exception exception) {
        LOG.debug("SystemExceptionMapper handle exception", exception);

        if (exception instanceof WebApplicationException) {
            return covertWebApplicationException((WebApplicationException) exception);
        } else {
            return covertUnkownException(exception);
        }
    }

    private Response covertWebApplicationException(WebApplicationException exception) {
        Response rep = exception.getResponse();
        if (rep == null) {
            rep = Response.serverError().build();
        }

        StatusType statusType = rep.getStatusInfo();
        int code = statusType.getStatusCode();
        String message = exception.getMessage();
        if (message == null) {
            Throwable cause = exception.getCause();
            if (cause != null) {
                message = cause.getMessage();
            }
        }

        HashMap<String, String> errorMessage = new HashMap<String, String>();
        errorMessage.put("code", String.valueOf(code));
        errorMessage.put("message", message);

        return Response.status(statusType).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
    }

    private Response covertUnkownException(Exception exception) {
        Status status = Status.INTERNAL_SERVER_ERROR;

        int code = status.getStatusCode();
        String message = exception.getMessage();
        if (message == null) {
            Throwable cause = exception.getCause();
            if (cause != null) {
                message = cause.getMessage();
            }
        }

        ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
        exception.printStackTrace(new PrintStream(byteArray));
        String trace = byteArray.toString();

        HashMap<String, String> errorMessage = new HashMap<String, String>();
        errorMessage.put("code", String.valueOf(code));
        errorMessage.put("message", message);
        errorMessage.put("trace", trace);

        return Response.status(status).entity(errorMessage).type(MediaType.APPLICATION_JSON).build();
    }

}
