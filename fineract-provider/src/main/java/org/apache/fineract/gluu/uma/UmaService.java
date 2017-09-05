package org.apache.fineract.gluu.uma;

import org.apache.fineract.useradministration.domain.AppUser;

public interface UmaService {
	
	public void createUser(String unencodedPassword, AppUser appUser);
	public void updateUser(String unencodedPassword, String originalUsername, AppUser appUser);
	public void deleteUser(String username);
}
