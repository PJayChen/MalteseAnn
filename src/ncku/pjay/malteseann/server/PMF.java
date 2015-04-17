package ncku.pjay.malteseann.server;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManagerFactory;

//Persistence Manager Factory
public class PMF {
	
	private static final PersistenceManagerFactory pmfInstance =
			JDOHelper.getPersistenceManagerFactory("transactions-optional");
	
	private PMF(){
		
	}
	
	public static PersistenceManagerFactory get(){
		return pmfInstance;
	}
}
