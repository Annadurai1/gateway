package com.curable.gateway.user;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 
 * @author Annadurai S
 *
 */
public interface UserService {

	ResponseEntity<?> loadUserByUsernameByKeyCloak(AppUserDTO userDetails) throws UsernameNotFoundException;

	ResponseEntity<?> resetPassword(AppUserDTO userDetails);



	

}
