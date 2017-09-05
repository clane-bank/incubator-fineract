/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.gluu.authentication;

import org.apache.fineract.gluu.jwk.JWT;
import org.apache.fineract.gluu.message.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.util.Assert;


@SuppressWarnings("deprecation")
public class GluuAuthenticationProvider implements AuthenticationProvider, InitializingBean, MessageSourceAware {

	private final static Logger logger = LoggerFactory.getLogger(GluuAuthenticationProvider.class);

	@Value( "${gluu.require_u2f}" )
	private volatile boolean requireU2f ;

	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
	private UserCache userCache = new NullUserCache();
	private boolean forcePrincipalAsString = false;
	protected boolean hideUserNotFoundExceptions = true;
	private UserDetailsChecker preAuthenticationChecks = new DefaultPreAuthenticationChecks();
	private UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();
	private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

	private UserDetailsService userDetailsService;
    private GluuService gluuService;
	

	public GluuAuthenticationProvider() {
	}

	@Override
	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(this.userCache, "A user cache must be set");
		Assert.notNull(this.messages, "A message source must be set");
		doAfterPropertiesSet();
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		
		// Determine username
		String id_token     = authentication.getPrincipal().toString();
        String access_token = authentication.getCredentials().toString();
        
		//parse and validate id token
		JWT jwt    = gluuService.parseIdToken(id_token);
		
		if(requireU2f && !jwt.getBody().getAcr().equals("u2f"))
		{
			throw new BadCredentialsException("Invalid authentication method");
		}
		
		// get user details from authentication server
		UserInfo userInfo = gluuService.getUserInfo(access_token);
		
		logger.debug("UserInfo: " + userInfo);
		
		// invalidate this token/id token
		gluuService.endSession(id_token); // end_session endpoint not working!
		
		//TODO: we can add more checks here : for example "acr" = u2f
		if( !jwt.getBody().getSub().equals(userInfo.getSub()) ) 
		{
		   throw new BadCredentialsException("Invalid Authentication tokens!");
		}
		
		boolean cacheWasUsed = true;
		UserDetails user = this.userCache.getUserFromCache(userInfo.getUser_name());

		if (user == null) {
			cacheWasUsed = false;

			try {
				user = retrieveUser(userInfo.getUser_name());
			} catch (UsernameNotFoundException notFound) {
				logger.debug("User '" + userInfo.getUser_name() + "' not found");

				throw new BadCredentialsException(messages
						.getMessage("AbstractUserDetailsAuthenticationProvider.BadCredentialsException", "Bad credentials"));

			}

			Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");
		}

		try {
			preAuthenticationChecks.check(user);
			authenticateUser(user, userInfo);
		} catch (AuthenticationException exception) {
			if (cacheWasUsed) {
				cacheWasUsed = false;
				user = retrieveUser(userInfo.getUser_name());
				preAuthenticationChecks.check(user);
				authenticateUser(user, userInfo);
			} else {
				throw exception;
			}
		}

		postAuthenticationChecks.check(user);

		if (!cacheWasUsed) {
			this.userCache.putUserInCache(user);
		}

		Object principalToReturn = user;

		if (forcePrincipalAsString) {
			principalToReturn = user.getUsername();
		}

		return createSuccessAuthentication(principalToReturn, authentication, user);
	}

	/**
	 * compare stored password with credentials from gluu authentication server
	 * @param userDetails
	 * @param userInfo
	 * @throws AuthenticationException
	 */
	private void authenticateUser(UserDetails userDetails, UserInfo userInfo) throws AuthenticationException {
		
		logger.info("Gluu Userinfo :  " + userInfo);
		
		if (userInfo.getEmployee_number() == null || userDetails.getPassword() == null) {
            logger.info("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"), userDetails);
        }
			
		if( !userDetails.getPassword().equals(userInfo.getEmployee_number()) )
		{
			 logger.info("Authentication failed: no credentials provided");
            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"), userDetails);
		}
		
		logger.info("Authentication successful!");
	}

	/**
	 * validate gluu token and get user's password
	 * 
	 * @param username
	 * @param authentication
	 * @return
	 * @throws AuthenticationException
	 */
	protected UserDetails retrieveUser(String username)
			throws AuthenticationException {

		UserDetails loadedUser = null;
		
		try {
			loadedUser = this.getUserDetailsService().loadUserByUsername(username);
		
		} catch (UsernameNotFoundException notFound) {
			throw notFound;
		} catch (Exception repositoryProblem) {
			throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
		}

		if (loadedUser == null) {
			throw new InternalAuthenticationServiceException(
					"UserDetailsService returned null, which is an interface contract violation");
		}

		return loadedUser;
	}
    
	/**
	 * 
	 * @param principal
	 * @param authentication
	 * @param user
	 * @return
	 */
	protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
			UserDetails user) {
		// Ensure we return the original credentials the user supplied,
		// so subsequent attempts are successful even with encoded passwords.
		// Also ensure we return the original getDetails(), so that future
		// authentication events after cache expiry contain the details
		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(principal,
				authentication.getCredentials(), authoritiesMapper.mapAuthorities(user.getAuthorities()));
		result.setDetails(authentication.getDetails());

		return result;
	}

	protected void doAfterPropertiesSet() throws Exception {
	}

	public UserCache getUserCache() {
		return userCache;
	}

	public boolean isForcePrincipalAsString() {
		return forcePrincipalAsString;
	}

	public boolean isHideUserNotFoundExceptions() {
		return hideUserNotFoundExceptions;
	}

	public void setForcePrincipalAsString(boolean forcePrincipalAsString) {
		this.forcePrincipalAsString = forcePrincipalAsString;
	}

	/**
	 * By default the <code>AbstractUserDetailsAuthenticationProvider</code>
	 * throws a <code>BadCredentialsException</code> if a username is not found
	 * or the password is incorrect. Setting this property to <code>false</code>
	 * will cause <code>UsernameNotFoundException</code>s to be thrown instead
	 * for the former. Note this is considered less secure than throwing
	 * <code>BadCredentialsException</code> for both exceptions.
	 *
	 * @param hideUserNotFoundExceptions
	 *            set to <code>false</code> if you wish
	 *            <code>UsernameNotFoundException</code>s to be thrown instead
	 *            of the non-specific <code>BadCredentialsException</code>
	 *            (defaults to <code>true</code>)
	 */
	public void setHideUserNotFoundExceptions(boolean hideUserNotFoundExceptions) {
		this.hideUserNotFoundExceptions = hideUserNotFoundExceptions;
	}

	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messages = new MessageSourceAccessor(messageSource);
	}

	public void setUserCache(UserCache userCache) {
		this.userCache = userCache;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
	}

	protected UserDetailsChecker getPreAuthenticationChecks() {
		return preAuthenticationChecks;
	}

	public void setUserDetailsService(UserDetailsService userDetailsService) {
		this.userDetailsService = userDetailsService;
	}

	protected UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}
	
	public void setGluuService(GluuService gluuService) {
		this.gluuService = gluuService;
	}

	public GluuService getGluuService() {
		return gluuService;
	}

	/**
	 * Sets the policy will be used to verify the status of the loaded
	 * <tt>UserDetails</tt> <em>before</em> validation of the credentials takes
	 * place.
	 *
	 * @param preAuthenticationChecks
	 *            strategy to be invoked prior to authentication.
	 */
	public void setPreAuthenticationChecks(UserDetailsChecker preAuthenticationChecks) {
		this.preAuthenticationChecks = preAuthenticationChecks;
	}

	protected UserDetailsChecker getPostAuthenticationChecks() {
		return postAuthenticationChecks;
	}

	public void setPostAuthenticationChecks(UserDetailsChecker postAuthenticationChecks) {
		this.postAuthenticationChecks = postAuthenticationChecks;
	}

	public void setAuthoritiesMapper(GrantedAuthoritiesMapper authoritiesMapper) {
		this.authoritiesMapper = authoritiesMapper;
	}

	private class DefaultPreAuthenticationChecks implements UserDetailsChecker {
		@Override
		public void check(UserDetails user) {
			if (!user.isAccountNonLocked()) {
				logger.debug("User account is locked");

				throw new LockedException(messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked",
						"User account is locked"));
			}

			if (!user.isEnabled()) {
				logger.debug("User account is disabled");

				throw new DisabledException(
						messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
			}

			if (!user.isAccountNonExpired()) {
				logger.debug("User account is expired");

				throw new AccountExpiredException(messages
						.getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
			}
		}
	}

	private class DefaultPostAuthenticationChecks implements UserDetailsChecker {
		@Override
		public void check(UserDetails user) {
			if (!user.isCredentialsNonExpired()) {
				logger.debug("User account credentials have expired");

				throw new CredentialsExpiredException(
						messages.getMessage("AbstractUserDetailsAuthenticationProvider.credentialsExpired",
								"User credentials have expired"));
			}
		}
	}
}