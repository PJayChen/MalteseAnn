package ncku.pjay.malteseann.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import ncku.pjay.malteseann.client.FixService;
import ncku.pjay.malteseann.shared.FixInfo;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class FixServiceImpl extends RemoteServiceServlet implements FixService {

	@Override
	public List<FixInfo> getAllFixs() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery("SELECT FROM " + FixInfo.class.getName());
		List<FixInfo> result = (List<FixInfo>) query.execute();
		List<FixInfo> fixs = new ArrayList<FixInfo>();
		
		for (FixInfo f : result){
			fixs.add(pm.detachCopy(f));
		}
		
		pm.close();
		return fixs;
	}

}
