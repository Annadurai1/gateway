package com.curable.gateway.user;

import java.util.ArrayList;
import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.curable.gateway.config.PropertyConfig;
import com.curable.gateway.user.impl.UserServiceKeycloakWrapper;
import com.curable.gateway.util.JwtTokenUtil;

/**
 * 
 * @author Annadurai S
 *
 */
@Service(value = "userService")
public class UserServiceImpl extends UserServiceKeycloakWrapper implements UserDetailsService, UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

	@SuppressWarnings("unused")
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Value("${keycloak-admin.realm}")
	private String ADMIN_REALM;

	@Value("${keycloak-admin.username}")
	private String USERNAME;

	@Value("${keycloak-admin.password}")
	private String PASSWORD;

	@Value("${keycloak-admin.clientId}")
	private String CLIENT_ID;

	@Value("${keycloak-admin.userrealm}")
	private String USER_REALM;

	@Autowired
	PropertyConfig config;

	public UserServiceImpl() {
		logger.info("UserServiceImpl default constructor.");
	}

	private Keycloak getInstance() {
		return KeycloakBuilder.builder().serverUrl(config.getKeyadminserverUrl()).realm(ADMIN_REALM).username(USERNAME)
				.password(PASSWORD).clientId(CLIENT_ID).build();
	}

	@Override
	public ResponseEntity<?> loadUserByUsernameByKeyCloak(AppUserDTO userDetails) throws UsernameNotFoundException {
		HttpHeaders header = new HttpHeaders();
		boolean result = checkKeycloakUserAvailable(userDetails);
		if (result) {
			List<String> roles = new ArrayList<String>();
			roles.add(userDetails.getRole());
			String token = jwtTokenUtil.createToken(userDetails, roles);
			// userDetails.setTocken(token);
			userDetails.setToken(token);
		} else {
			long usercount = getInstance().realm(USER_REALM).users().search(userDetails.getUserName()).parallelStream()
					.filter(x -> x.getUsername().equalsIgnoreCase(userDetails.getUserName())).count();
			if (usercount <= 0) {
				throw new UsernameNotFoundException("Invalid username.");
			} else {
				long userInactive = getInstance().realm(USER_REALM).users().search(userDetails.getUserName())
						.parallelStream().filter(x -> x.getUsername().equalsIgnoreCase(userDetails.getUserName()))
						.filter(z -> z.isEnabled() != null).filter(y -> y.isEnabled().equals(false)).count();
				if (userInactive > 0) {
					throw new UsernameNotFoundException("User inactive.");
				} else {
					throw new UsernameNotFoundException("Invalid password.");
				}
			}
		}
		return new ResponseEntity<AppUserDTO>(userDetails, header, HttpStatus.OK);
	}

	/**
	 * This method ignore the spring security user and roles , roles and user are
	 * not implemented. All micro-services will implements via token access.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if (username == null || username.isEmpty()) {
			throw new UsernameNotFoundException("Invalid username or password.");
		}
		// --TODO fill the authenticated user from keycloak server.
		AppUserDTO userDetails = new AppUserDTO(username, "ROLE_DEFAULT", "default");
		List<GrantedAuthority> auth = AuthorityUtils.commaSeparatedStringToAuthorityList(userDetails.getRole());
		return new org.springframework.security.core.userdetails.User(userDetails.getUserName(),
				userDetails.getPassword(), auth);
	}

	@Override
	public ResponseEntity<?> resetPassword(AppUserDTO userDetails) {
		List<UserRepresentation> users = getInstance().realm(USER_REALM).users().search(userDetails.getUserName());
		if (users.isEmpty() || users == null)
			return new ResponseEntity<ForgotPasswordDTO>(new ForgotPasswordDTO("Invalid username", 1), HttpStatus.OK);
		CredentialRepresentation credential = new CredentialRepresentation();
		credential.setType(CredentialRepresentation.PASSWORD);
		credential.setValue(userDetails.getPassword());
		credential.setTemporary(false);
		users.get(0);
		getInstance().realm(USER_REALM).users().get(users.get(0).getId()).resetPassword(credential);
		
		return new ResponseEntity<ForgotPasswordDTO>(new ForgotPasswordDTO("", 2), HttpStatus.OK);
	}
}
