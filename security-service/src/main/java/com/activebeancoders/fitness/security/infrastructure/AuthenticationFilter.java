package com.activebeancoders.fitness.security.infrastructure;

import com.activebeancoders.fitness.security.api.SecurityClientController;
import com.activebeancoders.fitness.security.api.TokenValidationService;
import com.google.common.base.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The security service uses this filter to authenticate requests.  Other services should
 * use the {@link com.activebeancoders.fitness.security.infrastructure.SecuredServiceAuthenticationFilter}
 * to authenticate requests.
 *
 * @author Dan Barrese
 */
public class AuthenticationFilter extends GenericFilterBean {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private UrlPathHelper urlPathHelper;
    private TokenValidationService tokenValidationService;
    private AuthenticationDao authenticationDao;

    public AuthenticationFilter(TokenValidationService tokenValidationService,
                                AuthenticationDao authenticationDao) {
        this.tokenValidationService = tokenValidationService;
        this.authenticationDao = authenticationDao;
        urlPathHelper = new UrlPathHelper();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        Optional<String> token = Optional.fromNullable(httpRequest.getHeader("X-Auth-Token"));
        String resourcePath = urlPathHelper.getPathWithinApplication(httpRequest);
        String accessPath = resourcePath + " -> " + httpRequest.getServletPath();
        if (log.isInfoEnabled()) {
            log.info("resource '{}' requested via '{}'", accessPath, httpRequest.getMethod());
        }

        AuthenticationWithToken authentication = null;
        try {
            // Validate the token if one is present.
            if (token.isPresent()) {
                authentication = tokenValidationService.validateToken(token);
                if (!authentication.isAuthenticated()) {
                    throw new InternalAuthenticationServiceException("Invalid session token.");
                } else {
                    authenticationDao.save(authentication);
                    ThreadLocalContext.addSessionContextToLogging(authentication);
                }
            } else {
                if (log.isInfoEnabled()) {
                    log.info("User '{}' is attempting to access '{}' without a token.", extractUsername(authentication), accessPath);
                }
            }

            if (log.isDebugEnabled()) {
                log.debug("AuthenticationFilter is passing request down the filter chain");
            }

            // Continue processing.
            chain.doFilter(request, response);
        } catch (InternalAuthenticationServiceException internalAuthenticationServiceException) {
            logFailedAccess(authentication, accessPath);
            authenticationDao.clearCurrentSessionAuthentication();
            log.error("Internal authentication service exception", internalAuthenticationServiceException);
            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (AuthenticationException authenticationException) {
            logFailedAccess(authentication, accessPath);
            authenticationDao.clearCurrentSessionAuthentication();
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage());
        } finally {
            ThreadLocalContext.removeSessionContextFromLogging(authentication);
        }
    }

    // protected methods
    // ```````````````````````````````````````````````````````````````````````

    protected void logFailedAccess(AuthenticationWithToken authentication, String accessPath) {
        if (log.isDebugEnabled()) {
            log.debug("User '{}' --access-denied--> '{}'", extractUsername(authentication), accessPath);
        }
    }

    protected String extractUsername(AuthenticationWithToken authentication) {
        return authentication == null ? "<unauthorized>" : authentication.getUsername();
    }

    // private methods
    // ```````````````````````````````````````````````````````````````````````

    private boolean postToValidateTokenRestEndpoint(HttpServletRequest httpRequest, String resourcePath) {
        return httpRequest.getMethod().equals("POST") &&
                SecurityClientController.getTokenValidationEndpointFromRESTCall().equalsIgnoreCase(resourcePath);
    }

    private boolean postToAuthenticateRestEndpoint(HttpServletRequest httpRequest, String resourcePath) {
        return httpRequest.getMethod().equals("POST") &&
                SecurityClientController.getAuthenticateEndpointFromRESTCall().equalsIgnoreCase(resourcePath);
    }

    private boolean postToLogoutRestEndpoint(HttpServletRequest httpRequest, String resourcePath) {
        return httpRequest.getMethod().equals("POST") &&
                SecurityClientController.getLogoutEndpointFromRESTCall().equalsIgnoreCase(resourcePath);
    }

    // TODO: move to common api
//    private String toStringRequest(HttpServletRequest httpRequest) {
//        StringBuilder sb = new StringBuilder();
//        final String NEWLINE = System.lineSeparator();
//        sb.append(String.format("  getParameterMap=%s", httpRequest.getParameterMap())).append(NEWLINE);
//        sb.append(httpRequest.toString()).append(NEWLINE);
//        sb.append(String.format("  getAsyncContext=%s", httpRequest.getAsyncContext())).append(NEWLINE);
//        Enumeration<String> attributeNames = httpRequest.getAttributeNames();
//        if (attributeNames != null) {
//            while (attributeNames.hasMoreElements()) {
//                String attributeName = attributeNames.nextElement();
//                sb.append(String.format("  getAttributeNames=%s = %s", attributeName, httpRequest.getAttribute(attributeName))).append(NEWLINE);
//            }
//        }
//        sb.append(String.format("  getAuthType=%s", httpRequest.getAuthType())).append(NEWLINE);
//        sb.append(String.format("  getContentType=%s", httpRequest.getContentType())).append(NEWLINE);
//        sb.append(String.format("  getContextPath=%s", httpRequest.getContextPath())).append(NEWLINE);
//        if (httpRequest.getCookies() != null) {
//            for (int i = 0; i < httpRequest.getCookies().length; i++) {
//                sb.append(String.format("  getCookies=%s : %s", httpRequest.getCookies()[i].getName(), httpRequest.getCookies()[i].getValue())).append(NEWLINE);
//            }
//        }
//        sb.append(String.format("  getHeaderNames=%s", httpRequest.getHeaderNames())).append(NEWLINE);
//        Enumeration<String> headerNames = httpRequest.getAttributeNames();
//        if (headerNames != null) {
//            while (headerNames.hasMoreElements()) {
//                String headerName = headerNames.nextElement();
//                sb.append(String.format("  getHeaderNames=%s = %s", headerName, httpRequest.getHeader(headerName))).append(NEWLINE);
//            }
//        }
//        sb.append(String.format("  getLocalAddr=%s", httpRequest.getLocalAddr())).append(NEWLINE);
//        sb.append(String.format("  getLocalName=%s", httpRequest.getLocalName())).append(NEWLINE);
//        sb.append(String.format("  getLocalPort=%d", httpRequest.getLocalPort())).append(NEWLINE);
//        sb.append(String.format("  getMethod=%s", httpRequest.getMethod())).append(NEWLINE);
//        Enumeration<String> parameterNames = httpRequest.getParameterNames();
//        if (parameterNames != null) {
//            while (parameterNames.hasMoreElements()) {
//                String parameterName = parameterNames.nextElement();
//                sb.append(String.format("  getParameterNames=%s = %s", parameterName, httpRequest.getParameter(parameterName))).append(NEWLINE);
//            }
//        }
//        sb.append(String.format("  getPathInfo=%s", httpRequest.getPathInfo())).append(NEWLINE);
//        sb.append(String.format("  getPathTranslated=%s", httpRequest.getPathTranslated())).append(NEWLINE);
//        sb.append(String.format("  getProtocol=%s", httpRequest.getProtocol())).append(NEWLINE);
//        sb.append(String.format("  getQueryString=%s", httpRequest.getQueryString())).append(NEWLINE);
//        sb.append(String.format("  getRemoteAddr=%s", httpRequest.getRemoteAddr())).append(NEWLINE);
//        sb.append(String.format("  getRemoteHost=%s", httpRequest.getRemoteHost())).append(NEWLINE);
//        sb.append(String.format("  getRemotePort=%d", httpRequest.getRemotePort())).append(NEWLINE);
//        sb.append(String.format("  getRemoteUser=%s", httpRequest.getRemoteUser())).append(NEWLINE);
//        sb.append(String.format("  getRequestURI=%s", httpRequest.getRequestURI())).append(NEWLINE);
//        sb.append(String.format("  getRequestURL=%s", httpRequest.getRequestURL())).append(NEWLINE);
//        sb.append(String.format("  getRequestedSessionId=%s", httpRequest.getRequestedSessionId())).append(NEWLINE);
//        sb.append(String.format("  getScheme=%s", httpRequest.getScheme())).append(NEWLINE);
//        sb.append(String.format("  getServerName=%s", httpRequest.getServerName())).append(NEWLINE);
//        sb.append(String.format("  getServerPort=%d", httpRequest.getServerPort())).append(NEWLINE);
//        ServletContext context = httpRequest.getServletContext();
//        if (context != null) {
//            sb.append(String.format("  getServletContext.getContextPath=%s", httpRequest.getServletContext().getContextPath())).append(NEWLINE);
//            sb.append(String.format("  getServletContext.getServerInfo=%s", httpRequest.getServletContext().getServerInfo())).append(NEWLINE);
//            sb.append(String.format("  getServletContext.getServletContextName=%s", httpRequest.getServletContext().getServletContextName())).append(NEWLINE);
//            sb.append(String.format("  getServletContext.getServletRegistrations=%s", httpRequest.getServletContext().getServletRegistrations())).append(NEWLINE);
//            sb.append(String.format("  getServletContext.getVirtualServerName=%s", httpRequest.getServletContext().getVirtualServerName())).append(NEWLINE);
//            Enumeration<String> names = httpRequest.getServletContext().getAttributeNames();
//            if (names != null) {
//                while (names.hasMoreElements()) {
//                    String name = names.nextElement();
//                    sb.append(String.format("  getServletContext.getAttributeNames=%s = %s", name, httpRequest.getHeader(name))).append(NEWLINE);
//                }
//            }
//        }
//        sb.append(String.format("  getServletPath=%s", httpRequest.getServletPath())).append(NEWLINE);
//        sb.append(String.format("  getSession=%s", httpRequest.getSession())).append(NEWLINE);
//        sb.append(String.format("  getUserPrincipal=%s", httpRequest.getUserPrincipal())).append(NEWLINE);
//
//        sb.append(String.format("  getDispatcherType=%s", httpRequest.getDispatcherType())).append(NEWLINE);
//        return sb.toString();
//    }

}