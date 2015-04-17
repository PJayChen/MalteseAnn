package ncku.pjay.malteseann.client;

import java.util.List;

import ncku.pjay.malteseann.shared.FixInfo;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("fix")
public interface FixService extends RemoteService {
	public List<FixInfo> getAllFixs();
}
