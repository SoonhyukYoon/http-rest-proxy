/*------------------------------------------------------------------------------
 * PROJ   : http-rest-proxy
 * NAME   : Application.java
 * DESC   : Spring Boot Application Configuration
 * Author : 윤순혁
 * VER    : 1.0
 *------------------------------------------------------------------------------
 *                  변         경         사         항                       
 *------------------------------------------------------------------------------
 *    DATE       AUTHOR                      DESCRIPTION                        
 * ----------    ------  --------------------------------------------------------- 
 * 2016. 1. 30.  윤순혁    최초 프로그램 작성                                     
 */

package soonhyuk.yoon.http.proxy.boot;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.ServletContextInitializer;
import org.springframework.context.annotation.PropertySource;

import soonhyuk.yoon.http.proxy.servlet.HttpProxyServlet;

/**
 * <PRE>
 * Spring Boot Application Configuration
 * </PRE>
 *
 * @author    윤순혁
 * @version   1.0
 * @see       ServletContextInitializer
 * @see       EmbeddedServletContainerCustomizer
 */
@EnableAutoConfiguration
@PropertySource( "classpath:property/http-proxy.properties" )
public class Application implements ServletContextInitializer, EmbeddedServletContainerCustomizer {

	private static final Logger LOGGER = LoggerFactory.getLogger( Application.class );

	/* (non-Javadoc)
	 * @see org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer#customize(org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer)
	 */
	@Override
	public void customize( ConfigurableEmbeddedServletContainer configurableEmbeddedServletContainer ) {
		// DO Nothing
	}

	/**
	 * <PRE>
	 * Spring Boot Main Application
	 * </PRE>
	 * 
	 * @param args Arguments
	 */
	public static void main( String[] args ) {
		SpringApplication.run( Application.class, args );
	}

	/* (non-Javadoc)
	 * @see org.springframework.boot.context.embedded.ServletContextInitializer#onStartup(javax.servlet.ServletContext)
	 */
	@Override
	public void onStartup( ServletContext servletContext ) throws ServletException {
		ServletRegistration.Dynamic serviceServlet = servletContext.addServlet( "HttpProxyServlet", new HttpProxyServlet() );

		serviceServlet.addMapping( "/" );
		serviceServlet.setAsyncSupported( true );
		serviceServlet.setLoadOnStartup( 1 );

		// HTTP Proxy Servlet 등록
		LOGGER.info( "[HttpProxyServlet] Register Servlet" );
	}
}