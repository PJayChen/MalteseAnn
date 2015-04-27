package ncku.pjay.malteseann.client;

import java.util.List;

import ncku.pjay.malteseann.shared.Date;
import ncku.pjay.malteseann.shared.Device;
import ncku.pjay.malteseann.shared.FixInfo;
import ncku.pjay.malteseann.shared.PositionFix;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("fix")
public interface FixService extends RemoteService {
	public List<FixInfo> getAllFixs(); //for no device id format
	public List<Device> getAllDevice();
	public List<Date> getDateByID(String deviceId);
	public List<PositionFix> getPositionFixsByIdDate(String deviceId, String date);
}
