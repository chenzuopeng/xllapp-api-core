package org.xllapp.api.core;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.xllapp.api.core.exception.ApiException;
import org.xllapp.api.core.exception.InvalidRequestArgumentException;
import org.xllapp.api.support.RequestContextHolder;
import org.xllapp.jms.JMSProducer;

/**
 * 基于JMS的异步处理请求的基类.
 *
 * @author dylan.chen Nov 14, 2014
 * 
 */
public class JMSController extends JSONController {

	@SuppressWarnings("serial")
	private static final Set<String> EXCLUDE_HEADERS = new HashSet<String>() {
		{
			add("content-type");
			add("connection");
			add("content-length");
		}
	};

	private JMSProducer jmsProducer;

	private String queue;

	@Override
	public void verifyBusiArgument(Map<String, Object> requestArgument) throws InvalidRequestArgumentException {
	}

	@Override
	public String[] getSignItems(Map<String, Object> requestArgument) {
		return null;
	}

	@Override
	public Object handleRequest(Map<String, Object> requestArgument) throws ApiException {
		this.jmsProducer.sendSync(this.queue, buildMessage());
		return null;
	}

	public Object buildMessage() {
		Map<String, Object> message = new HashMap<String, Object>();
		message.put("request.headers", resolveHeaders());
		message.put("request.arguments", resolveArguments());
		return message;
	}

	@SuppressWarnings("unchecked")
	public Object resolveHeaders() {
		Map<String, String> headers = new HashMap<String, String>();
		HttpServletRequest request = RequestContextHolder.getRequest();
		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();
			if (EXCLUDE_HEADERS.contains(headerName)) {
				continue;
			}
			headers.put(headerName, request.getHeader(headerName));
		}
		return headers;
	}

	public Object resolveArguments() {
		return RequestContextHolder.getRequestArgument();
	}

	public JMSProducer getJmsProducer() {
		return this.jmsProducer;
	}

	@Autowired
	public void setJmsProducer(JMSProducer jmsProducer) {
		this.jmsProducer = jmsProducer;
	}

	public String getQueue() {
		return this.queue;
	}

	@Autowired
	public void setQueue(String queue) {
		this.queue = queue;
	}

}
