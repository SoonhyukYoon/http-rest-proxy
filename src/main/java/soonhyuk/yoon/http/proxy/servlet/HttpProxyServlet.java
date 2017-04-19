/*------------------------------------------------------------------------------
 * PROJ   : http-rest-proxy
 * NAME   : HttpProxyServlet.java
 * DESC   : HTTP Proxy Servlet
 * Author : 윤순혁
 * VER    : 1.0
 *------------------------------------------------------------------------------
 *                  변         경         사         항                       
 *------------------------------------------------------------------------------
 *    DATE       AUTHOR                      DESCRIPTION                        
 * ----------    ------  --------------------------------------------------------- 
 * 2016. 1. 30.  윤순혁    최초 프로그램 작성                                     
 */ 

package soonhyuk.yoon.http.proxy.servlet;

import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIUtils;
import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.common.base.Splitter;

/**
 * <PRE>
 * HTTP Proxy Servlet
 * </PRE>
 *
 * @author    윤순혁
 * @version   1.0
 * @see       ProxyServlet
 */
public class HttpProxyServlet extends ProxyServlet {

	private static final long serialVersionUID = 1L;

	private static final String LOGGER_NAME = "HttpProxyServlet";

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger( LOGGER_NAME );

	private static final Splitter SLASH_SPLITTER = Splitter.on( '/' ).omitEmptyStrings();

	/**
	 * Spring Application Context
	 */
	private WebApplicationContext appContext;

	/* (non-Javadoc)
	 * @see org.mitre.dsmiley.httpproxy.ProxyServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void service( HttpServletRequest servletRequest, HttpServletResponse servletResponse ) throws ServletException, IOException {
		// http-proxy.properties 설정 파일의 "targetUri.TARGET_CODE" 값에 따라 동적으로 Proxy Host를 변경

		try {
			String targetCode = SLASH_SPLITTER.split( servletRequest.getRequestURI() ).iterator().next();
			servletRequest.setAttribute( "TargetCode", targetCode );

			targetUriObj = new URI( appContext.getEnvironment().getProperty( new StringBuilder( "targetUri." ).append( targetCode ).toString() ) );
			servletRequest.setAttribute( ATTR_TARGET_HOST, URIUtils.extractHost( targetUriObj ) );			
		} catch ( Exception targetCodeError ) {
			servletResponse.setStatus( HttpStatus.INTERNAL_SERVER_ERROR.value() );
			servletResponse.setContentType( MediaType.TEXT_PLAIN.toString() );
			servletResponse.getWriter().write( "Target Proxy Host Setup Failed..." );
			return;
		}

		super.service( servletRequest, servletResponse );
	}

	/* (non-Javadoc)
	 * @see org.mitre.dsmiley.httpproxy.ProxyServlet#rewriteUrlFromRequest(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	protected String rewriteUrlFromRequest( HttpServletRequest servletRequest ) {
		StringBuilder uri = new StringBuilder( 500 );
		uri.append( getTargetUri( servletRequest ) );
		// Handle the path given to the servlet
		uri.append( encodeUriQuery( StringUtils.remove( servletRequest.getRequestURI(), new StringBuilder("/").append( servletRequest.getAttribute( "TargetCode" ) ).toString() ) ) );

		// Handle the query string & fragment
		String queryString = servletRequest.getQueryString();//ex:(following '?'): name=value&foo=bar#fragment
		String fragment = null;
		//split off fragment from queryString, updating queryString if found
		if ( queryString != null ) {
			int fragIdx = queryString.indexOf( '#' );
			if ( fragIdx >= 0 ) {
				fragment = queryString.substring( fragIdx + 1 );
				queryString = queryString.substring( 0, fragIdx );
			}
		}

		queryString = rewriteQueryStringFromRequest( servletRequest, queryString );
		if ( queryString != null && queryString.length() > 0 ) {
			uri.append( '?' );
			uri.append( encodeUriQuery( queryString ) );
		}

		if ( doSendUrlFragment && fragment != null ) {
			uri.append( '#' );
			uri.append( encodeUriQuery( fragment ) );
		}
		return uri.toString();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jetty.proxy.ProxyServlet.Transparent#init(javax.servlet.ServletConfig)
	 */
	@Override
	public void init( final ServletConfig config ) throws ServletException {
		try  {
			appContext = WebApplicationContextUtils.getWebApplicationContext( config.getServletContext() );

			super.init( new ServletConfig() {
				@Override
				public String getServletName() {
					return config.getServletName();
				}

				@Override
				public ServletContext getServletContext() {
					return config.getServletContext();
				}
				
				@Override
				public Enumeration<String> getInitParameterNames() {
					return config.getInitParameterNames();
				}

				@Override
				public String getInitParameter( String paramString ) {
					String valueString = appContext.getEnvironment().getProperty( paramString );
					LOGGER.info( "Loading Configuration - {} = {}", paramString, valueString );
					return valueString;
				}
			} );
		} catch ( Exception ex ) {
			LOGGER.error( "Proxy Servlet Configuration Loading Failed.", ex );
			destroy();
		}
	}
}
