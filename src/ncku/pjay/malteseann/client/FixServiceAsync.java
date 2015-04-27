package ncku.pjay.malteseann.client;

import java.util.List;

import ncku.pjay.malteseann.shared.Date;
import ncku.pjay.malteseann.shared.Device;
import ncku.pjay.malteseann.shared.FixInfo;
import ncku.pjay.malteseann.shared.PositionFix;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface FixServiceAsync {

	void getAllFixs(AsyncCallback<List<FixInfo>> callback);

	void getAllDevice(AsyncCallback<List<Device>> callback);

	void getDateByID(String deviceId, AsyncCallback<List<Date>> callback);

	void getPositionFixsByIdDate(String deviceId, String date,
			AsyncCallback<List<PositionFix>> callback);

}
