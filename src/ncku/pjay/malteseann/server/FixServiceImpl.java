package ncku.pjay.malteseann.server;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import ncku.pjay.malteseann.client.FixService;
import ncku.pjay.malteseann.shared.Date;
import ncku.pjay.malteseann.shared.Device;
import ncku.pjay.malteseann.shared.FixInfo;
import ncku.pjay.malteseann.shared.PositionFix;

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

	@Override
	public List<Device> getAllDevice() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery("SELECT FROM " + Device.class.getName());
		List<Device> deviceList = (List<Device>) query.execute();
		List<Device> returnList = new ArrayList<Device>();
		int index = 0;
		
		for (Device device : deviceList) {
			returnList.add(pm.detachCopy(device));
		}
		
		pm.close();
		return returnList;
	}

	@Override
	public List<Date> getDateByID(String deviceId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery("SELECT FROM " + Device.class.getName());
		List<Device> deviceList = (List<Device>) query.execute();
		List<Date> returnList = new ArrayList<Date>();
		for (Device device : deviceList) {
			if (device.getDeviceName().equals(deviceId)) {
				for (Date date : device.getCreatedDate()) {
					returnList.add(pm.detachCopy(date));
				}
			break;
			}
		}
		
		pm.close();
		return returnList;
	}

	@Override
	public List<PositionFix> getPositionFixsByIdDate(String deviceId,
			String date) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery("SELECT FROM " + Device.class.getName());
		List<Device> deviceList = (List<Device>) query.execute();
		List<PositionFix> returnList = new ArrayList<PositionFix>();
		
		for (Device device : deviceList) {
			if (device.getDeviceName().equals(deviceId)) {
				for (Date dateDB : device.getCreatedDate()) {
					if(dateDB.getCreateDate().equals(date)) {
						for (PositionFix pos : dateDB.getPositionFixs()) {
							returnList.add(pm.detachCopy(pos));
						}
						break;
					}
				}
				break;
			}
		}
		
		pm.close();
		return returnList;
	}

}
