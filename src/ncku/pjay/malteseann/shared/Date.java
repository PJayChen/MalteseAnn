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
public class Date implements Serializable {
//	@PrimaryKey
//	@Persistent(valueStrategy=IdGeneratorStrategy.IDENTITY)
//	private Key id;
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	@Extension(vendorName = "datanucleus", key = "gae.encoded-pk", value = "true")
	private String id;
	
	
	@Persistent
	private String createDate;
	
	@Persistent
	private List<PositionFix> positionFixs;

	public Date() {
		
	}
	
	public Date(String createDate, List<PositionFix> positionFixs) {
		super();
		this.createDate = createDate;
		this.positionFixs = positionFixs;
	}

	public String getId() {
		return id;
	}

	public String getCreateDate() {
		return createDate;
	}

	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	public List<PositionFix> getPositionFixs() {
		return positionFixs;
	}

	public void setPositionFixs(List<PositionFix> positionFixs) {
		this.positionFixs = positionFixs;
	}
	
	
}
