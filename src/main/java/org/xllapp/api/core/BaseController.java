package org.xllapp.api.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;
import org.xllapp.api.core.event.EventFirer;
import org.xllapp.api.core.exception.ApiException;
import org.xllapp.api.core.exception.InvalidRequestArgumentException;
import org.xllapp.api.core.vo.ApiResponse;
import org.xllapp.config.ApplicationConfigHolder;
import org.xllapp.api.support.ApiApplicationConfig;
import org.xllapp.api.support.RequestContextHolder;
import org.xllapp.api.util.JSONHelper;

/**
 * API基类.
 * 
 * @author dylan.chen Sep 20, 2013
 * 
 */
public abstract class BaseController implements HttpRequestHandler {

	public static final String REQUEST_PARAM_JSONP_CALLBACK = "jsonpCallback";

	protected final Logger logger;

	protected BeforeHandlerInterceptor beforeHandlerInterceptor;

	protected EventFirer eventFirer;

	public BaseController() {
		this.logger = LoggerFactory.getLogger(this.getClass());
	}

	public abstract Object resolveAndVerifyArgument(HttpServletRequest request) throws Exception;

	@Override
	public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final Object requestArgument;
		try {

			if (this.beforeHandlerInterceptor != null && !this.beforeHandlerInterceptor.beforeHandle(request, response)) {
				return;
			}

			requestArgument = resolveAndVerifyArgument(request);

			RequestContextHolder.setRequestArgument(requestArgument);

			this.logger.debug("request argument:{}", requestArgument);

			handleRequest(requestArgument, request, response);

		} catch (Exception e) {

			if (e instanceof InvalidRequestArgumentException) {
				this.logger.debug(e.getLocalizedMessage(), e);
			} else {
				this.logger.error(e.getLocalizedMessage(), e);
			}

			handleException(e, request, response);

		}

	}

	public void handleRequest(Object requestArgument, HttpServletRequest request, HttpServletResponse response) throws Exception {
		String responseContent = handleRequest(requestArgument, request);
		out(response, responseContent);
	}

	public abstract String handleRequest(Object requestArgument, HttpServletRequest request) throws Exception;

	public void handleException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
		if (exception instanceof ApiException) {
			ApiException apiException = (ApiException) exception;
			String content = JSONHelper.toJSONStringQuietly(new ApiResponse(apiException.getResultCode(), apiException.getResultDesc()), true);
			out(response, content);
		} else {
			out(response, ApiResponse.JSON_FAILURE_RESPONSE);
		}

	}

	public void out(HttpServletResponse response, String content) {
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		HttpServletRequest request = RequestContextHolder.getRequest();
		if (null != request) {
			String jsonpCallback = request.getParameter(REQUEST_PARAM_JSONP_CALLBACK);
			if (StringUtils.isNotBlank(jsonpCallback)) {// 支持jsonp方式请求
				response.setContentType("text/javascript");
				content = jsonpCallback + "(" + content + ");";
			}
		}

		this.logger.debug("response:{}", content);

		PrintWriter out = null;
		try {
			out = response.getWriter();
			out.write(content);
		} catch (Exception e) {
			this.logger.error("failure to send response data.caused by:" + e.getLocalizedMessage(), e);
		} finally {
			IOUtils.closeQuietly(out);
		}
		fireEvent(content);
	}

	public void fireEvent(String response) {
		if (null != this.eventFirer) {
			try {
				this.logger.debug("firing event");
				this.eventFirer.fireEvent(this.getClass(), RequestContextHolder.getRequest(), RequestContextHolder.getRequestArgument(), response);
			} catch (Throwable e) {
				this.logger.error("failure to fire event.caused by:" + e.getLocalizedMessage(), e);
			}
		}
	}

	public ApiApplicationConfig getApplicationConfig() {
		return (ApiApplicationConfig)ApplicationConfigHolder.getApplicationConfig();
	}

	public void setBeforeHandlerInterceptor(BeforeHandlerInterceptor beforeHandlerInterceptor) {
		this.beforeHandlerInterceptor = beforeHandlerInterceptor;
	}

	public void setEventFirer(EventFirer eventFirer) {
		this.eventFirer = eventFirer;
	}

}
