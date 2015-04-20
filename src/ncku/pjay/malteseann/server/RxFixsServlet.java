package ncku.pjay.malteseann.server;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jdo.PersistenceManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ncku.pjay.malteseann.server.PMF;
import ncku.pjay.malteseann.shared.FixInfo;

public class RxFixsServlet extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		System.out.println("recevice post");
		String DATA_FORMAT = "DDMMYY, HHMMSS.SSS, ddmm.mmmmN, dddmm.mmmmE";
		String fix = req.getParameter("fix");
		
		//basically check the input value's format is match or not.
		if ( fix != null && (fix.length() == DATA_FORMAT.length()) ) {
			System.out.println("Format correct!");
			String[] fixinfo = fix.split(", ");
			
			String lat_degree = fixinfo[2].substring(0, 2);
			String lat_min = fixinfo[2].substring(2, 9);
			String lat_NorS = fixinfo[2].substring(9);
//			System.out.println(lat_degree + lat_min + lat_NorS);
			String lng_degree = fixinfo[3].substring(0, 3);
			String lng_min = fixinfo[3].substring(3, 10);
			String lng_EorW = fixinfo[3].substring(10);
//			System.out.println(lng_degree + lng_min + lng_EorW);
			double lat, lng;
			
			lat = Double.valueOf(lat_degree) + Double.valueOf(lat_min) / 60d;
			lat = (lat_NorS.equals("N"))?lat:-lat;
			lng = Double.valueOf(lng_degree) + Double.valueOf(lng_min) / 60d;
			lng = (lng_EorW.equals("E"))?lng:-lng;
			
			PrintWriter out = resp.getWriter();
			out.println(fix);
			
			PersistenceManager pm = PMF.get().getPersistenceManager();
			try {
				pm.makePersistent(new FixInfo(fixinfo[0], fixinfo[1], String.valueOf(lat), String.valueOf(lng)));
			} finally{
				pm.close();	
			}		
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
