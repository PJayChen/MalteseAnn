package ncku.pjay.malteseann.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ncku.pjay.malteseann.server.PMF;
import ncku.pjay.malteseann.shared.Date;
import ncku.pjay.malteseann.shared.Device;
import ncku.pjay.malteseann.shared.FixInfo;
import ncku.pjay.malteseann.shared.PositionFix;

public class RxFixsServlet extends HttpServlet {

	//parameter latLng[0] for latitude, latLng[1] for longitude
	/* This method parse the lat and lng from degree and minute represent to
	 * only represented by degree
	 */
	private void parseLatLngtoDegree (double[] latLng, String[] lat, String[] lng) {
		latLng[0] = Double.valueOf(lat[0]) + Double.valueOf(lat[1]) / 60d;
		latLng[0] = (lat[2].equals("N"))?latLng[0]:-latLng[0];
		latLng[1] = Double.valueOf(lng[0]) + Double.valueOf(lng[1]) / 60d;
		latLng[1] = (lng[2].equals("E"))?latLng[1]:-latLng[1];
	}
	
	//Show all data under the specific device ID
	private void queryDevice (String deviceID) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = 
				pm.newQuery( "SELECT FROM " + Device.class.getName() + " WHERE deviceID == '" + deviceID + "'");
		//Device d1 = (Device) query.execute();
		List<Device> deviceList = (List<Device>) query.execute();
		
		if (deviceList.isEmpty()) {
			System.out.println("The device ID " + deviceID + " Not availible");
		} else {
			for (Device device : deviceList) {
				//Show each date
				List<Date> dateList = new ArrayList<Date>();
				dateList = device.getCreatedDate();
				System.out.println("Device Name: " + device.getDeviceName());
				
				for (Date date : dateList) {
					System.out.println("Date: " + date.getCreateDate());
					List<PositionFix> positionList = new ArrayList<PositionFix>();
					positionList = date.getPositionFixs();
					//Show all position fixs under this date
					for (PositionFix position : positionList) {
						System.out.println("Position fix: " 
								+ position.getTimeUTC() + ", " 
								+ position.getLatitude() + ", "
								+ position.getLongitude());
					}
				}
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("recevice post");
		String DATA_FORMAT = "DDMMYY, HHMMSS.SSS, ddmm.mmmmN, dddmm.mmmmE";
		String DATA_FORMAT_DEVICE = "PNNN, DDMMYY, HHMMSS.SSS, ddmm.mmmmN, dddmm.mmmmE";
		String fix = req.getParameter("fix");
		
		//basically check the input value's format is match or not.
		if ( fix != null && (fix.length() == DATA_FORMAT.length()) ) {
			System.out.println("No Device ID Format");
			
			String[] fixinfo = fix.split(", ");
			
			String[] lat_degree_min_NorS = 
				{	fixinfo[2].substring(0, 2), 
					fixinfo[2].substring(2, 9), 
					fixinfo[2].substring(9)
				};
			
			String[] lng_degree_min_EorW = 
				{	fixinfo[3].substring(0, 3),
					fixinfo[3].substring(3, 10),
					fixinfo[3].substring(10)
				};
			
			double[] latLng = new double[2];
			parseLatLngtoDegree(latLng, lat_degree_min_NorS, lng_degree_min_EorW);
		
			PrintWriter out = resp.getWriter();
			out.println(fix);
			
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				pm.makePersistent(new FixInfo(fixinfo[0], fixinfo[1], String.valueOf(latLng[0]), String.valueOf(latLng[1])));
			} finally{
				pm.close();	
			}
		} else if( fix != null && (fix.length() == DATA_FORMAT_DEVICE.length()) ) {
			System.out.println("Device ID Format");
			
			//--- Parse the received string into meaningful part --- 
			String[] fixinfo = fix.split(", ");
			
			String[] lat_degree_min_NorS = 
				{	fixinfo[3].substring(0, 2), 
					fixinfo[3].substring(2, 9), 
					fixinfo[3].substring(9)
				};
			
			String[] lng_degree_min_EorW = 
				{	fixinfo[4].substring(0, 3),
					fixinfo[4].substring(3, 10),
					fixinfo[4].substring(10)
				};
			
			double[] latLng = new double[2];
			parseLatLngtoDegree(latLng, lat_degree_min_NorS, lng_degree_min_EorW);
			//---------------------------------------------------------
			
			/* ------------------------------------------------------------
			 * --- Check the device ID(fixinfo[0]) exist or not 
			 * --- If it is exit, check the date(fixinfo[1]) exist or not 
			 */
			
			PersistenceManager pm = PMF.get().getPersistenceManager();
			Query query = pm.newQuery( "SELECT FROM " 
								+ Device.class.getName() 
								+ " WHERE deviceID == '" 
								+ fixinfo[0] + "'");

			List<Device> deviceList = (List<Device>) query.execute();
			
			if (deviceList.isEmpty()) {
				//device ID no exist, create new device ID
				System.out.println("The device ID " + fixinfo[0] + " Not availible");
				List<Date> date = new ArrayList<Date>();
				List<PositionFix> positionFixs = new ArrayList<PositionFix>();
				date.add(new Date(fixinfo[1], positionFixs));
				positionFixs.add(new PositionFix(fixinfo[2], String.valueOf(latLng[0]), String.valueOf(latLng[1])));
				pm.makePersistent(new Device(fixinfo[0], date));
			} else {
				for (Device device : deviceList) {
					//Show each date
					List<Date> dateList = new ArrayList<Date>();
					dateList = device.getCreatedDate();
					
					Boolean exist = false;
					for (Date date : dateList) {
						//if the date is exist, add the position fix to that day 
						if ( date.getCreateDate().equals(fixinfo[1]) ) {
							List<PositionFix> positionList = new ArrayList<PositionFix>();
							positionList = date.getPositionFixs();
							positionList.add(new PositionFix(fixinfo[2], String.valueOf(latLng[0]), String.valueOf(latLng[1])));
							exist = true;
							break;
						}
					}
					//if the date does not exist, create new date
					if (!exist) {
						dateList = device.getCreatedDate();
						List<PositionFix> positionList = new ArrayList<PositionFix>();
						positionList.add(new PositionFix(fixinfo[2], String.valueOf(latLng[0]), String.valueOf(latLng[1])));
						dateList.add(new Date(fixinfo[1], positionList));
					}
				}
			}
			
			/* ------------------------------------------------------------ */
			
			pm.close();
			
			//send the received data back to client
			PrintWriter out = resp.getWriter();
			out.println(fix);
			
			//Print the debug message to check datastore is correct or not
			queryDevice(fixinfo[0]);
		} else {
			System.out.println("Format incorrect!");
			PrintWriter out = resp.getWriter();
			out.println("Data format incorrect!");
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		PrintWriter out = resp.getWriter();
		out.println("Hello Ann Ann!!");
	}
	
	

}
