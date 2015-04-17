package ncku.pjay.malteseann.client;

import java.util.List;

import ncku.pjay.malteseann.shared.FixInfo;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FixServiceAsync {

	void getAllFixs(AsyncCallback<List<FixInfo>> callback);

}
