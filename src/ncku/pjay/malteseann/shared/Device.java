package ncku.pjay.malteseann.shared;

import java.io.Serializable;
import java.util.List;

import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Key;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

//import com.google.appengine.api.datastore.Key;

@PersistenceCapable(identityType=IdentityType.APPLICATION)
public class Device implements Serializable {
//	@PrimaryKey
//	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
//	private Key id;
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String id;
	
	@Persistent
	private String deviceID;
	
	@Persistent
	private List<Date> createdDate;

	public Device() {
		
	}

	public Device(String deviceID, List<Date> createdDate) {
		super();
		this.deviceID = deviceID;
		this.createdDate = createdDate;
	}

	public String getId() {
		return id;
	}

	public String getDeviceName() {
		return deviceID;
	}

	public void setDeviceName(String deviceID) {
		this.deviceID = deviceID;
	}

	public List<Date> getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(List<Date> createdDate) {
		this.createdDate = createdDate;
	}


	
	
}
