package org.apache.fineract.gluu.uma;

import org.apache.fineract.useradministration.domain.AppUser;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"basicauth", "oauth"})
public class DefaultUmaServiceImpl implements UmaService{
	
	@Override
	public void createUser(String unencodedPassword, AppUser appUser)
	{
		
	}
	
	@Override
	public void updateUser(String unencodedPassword, String originalUsername, AppUser appUser)
	{
		
	}
	
	@Override
	public void deleteUser(String username)
	{
		
	}
}
