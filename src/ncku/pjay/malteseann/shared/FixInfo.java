package ncku.pjay.malteseann.shared;

import java.io.Serializable;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class FixInfo implements Serializable{
	@PrimaryKey
	@Persistent(valueStrategy=IdGeneratorStrategy.SEQUENCE)
	private Long id;
	
	@Persistent
	private String createdDate; //ddmmyy
	
	@Persistent
	private String timeUTC; //UTC time, hhmmss.sss
	
	@Persistent
	private String latitude; //ddmm.mmmm
	
	@Persistent
	private String longitude; //dddmm.mmmm
	
	//must declare this no parameters constructor!!
	//or RPC will not works correctly.
	public FixInfo(){
		
	}
	
	public FixInfo(String date, String time, String latitude, String longitude) {
		super();
		this.createdDate = date;
		this.timeUTC = time;
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

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
