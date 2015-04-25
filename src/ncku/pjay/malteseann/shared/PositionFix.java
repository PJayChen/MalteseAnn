package ncku.pjay.malteseann.shared;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class PositionFix implements Serializable {
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
	private Key id;
	
//	@Persistent
//	private String date; //ddmmyy
	
	@Persistent
	private String timeUTC; //UTC time, hhmmss.sss
	
	@Persistent
	private String latitude; //ddmm.mmmm
	
	@Persistent
	private String longitude; //dddmm.mmmm
	
	public PositionFix() {
		
	}
	
	public PositionFix(String timeUTC,
			String latitude, String longitude) {
		super();
//		this.date = date;
		this.timeUTC = timeUTC;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	
	public Key getId() {
		return id;
	}
//
//	public String getDate() {
//		return date;
//	}
//
//	public void setDate(String date) {
//		this.date = date;
//	}

	public String getTimeUTC() {
		return timeUTC;
	}

	public void setTimeUTC(String timeUTC) {
		this.timeUTC = timeUTC;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}
