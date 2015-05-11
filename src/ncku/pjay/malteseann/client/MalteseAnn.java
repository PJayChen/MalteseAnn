package ncku.pjay.malteseann.client;

import java.util.ArrayList;
import java.util.List;

import ncku.pjay.malteseann.shared.Date;
import ncku.pjay.malteseann.shared.Device;
import ncku.pjay.malteseann.shared.PositionFix;

import com.google.gwt.ajaxloader.client.ArrayHelper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.http.client.URL;
import com.google.gwt.maps.client.LoadApi;
import com.google.gwt.maps.client.MapOptions;
import com.google.gwt.maps.client.MapTypeId;
import com.google.gwt.maps.client.MapWidget;
import com.google.gwt.maps.client.LoadApi.LoadLibrary;
import com.google.gwt.maps.client.base.LatLng;
import com.google.gwt.maps.client.overlays.IconSequence;
import com.google.gwt.maps.client.overlays.Marker;
import com.google.gwt.maps.client.overlays.MarkerOptions;
import com.google.gwt.maps.client.overlays.Polyline;
import com.google.gwt.maps.client.overlays.PolylineOptions;
import com.google.gwt.maps.client.overlays.Symbol;
import com.google.gwt.maps.client.overlays.SymbolPath;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;



/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class MalteseAnn implements EntryPoint {

	private VerticalPanel mainPanel = new VerticalPanel();
	private HorizontalPanel postPanel = new HorizontalPanel();
	private HorizontalPanel mapPanel = new HorizontalPanel();
	private HorizontalPanel queryPanel = new HorizontalPanel();
	
	private Label msgLabel = new Label();
	private Button sendBtn = new Button("Send");
	private TextBox dataText = new TextBox();
	private FlexTable fixsFlexTable = new FlexTable();
	
	private Label deviceLabel = new Label("Device ID: ");
	private Label dateLabel = new Label("Date: ");
	private ListBox deviceListBox = new ListBox();
	private ListBox dateListBox = new ListBox();
	
	private Button moveMapCenterBtn = new Button("Center");
	
	private String locDeviceId, locDate;
	private LatLng latestMapCenter;
	
	/* Initialize all widgets layout */
	private void initWidgets(){
		postPanel.add(dataText);
		postPanel.add(sendBtn);
		
		queryPanel.add(deviceLabel);
		queryPanel.add(deviceListBox);
		queryPanel.add(dateLabel);
		queryPanel.add(dateListBox);
		queryPanel.add(moveMapCenterBtn);
		
		mainPanel.add(msgLabel);
		mainPanel.add(postPanel);
		mainPanel.add(queryPanel);
		mainPanel.add(mapPanel);
		mainPanel.add(fixsFlexTable);
		RootPanel.get("mainList").add(mainPanel);
		
		msgLabel.setText("DeviceID, Date, UTCtime, Latitude, Longitude");
		
		dataText.setWidth("300px");
		dataText.setText("p001, 300315, 065812.000, 2259.8259N, 12013.3452E");
		
		fixsFlexTable.setText(0, 0, "id");
		fixsFlexTable.setText(0, 1, "Date");
		fixsFlexTable.setText(0, 2, "Time");
		fixsFlexTable.setText(0, 3, "Latitude");
		fixsFlexTable.setText(0, 4, "Longitude");
		
		// Add styles to elements in the stock list table.
		fixsFlexTable.getRowFormatter().addStyleName(0, "fixsListHeader");
		fixsFlexTable.addStyleName("fixsList");
		fixsFlexTable.getCellFormatter().addStyleName(0, 1, "fixsListNumericColumn");
		fixsFlexTable.getCellFormatter().addStyleName(0, 2, "fixsListNumericColumn");
		fixsFlexTable.getCellFormatter().addStyleName(0, 3, "fixsListNumericColumn");
		fixsFlexTable.getCellFormatter().addStyleName(0, 4, "fixsListNumericColumn");
		
	}
	
	/* Initialize event handlers */
	private void initHandlers(){
		sendBtn.addClickHandler(new ClickHandler(){
			@Override
			public void onClick(ClickEvent event) {
				doPost("/rxfix", dataText.getText().toString());
			}
		});
		
		dataText.addKeyPressHandler(new KeyPressHandler(){
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					doPost("http://127.0.0.1:8888/rxfix", dataText.getText());
			    }
			}
		});
		
		moveMapCenterBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				//Set map center to the latest position fix
				mapWidget.setCenter(latestMapCenter);	
			}
			
		});
		
		deviceListBox.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (deviceListBox.getItemCount() != 0 ) { 
					String item = deviceListBox.getItemText(deviceListBox.getSelectedIndex());
					System.out.println("Client-side > " + item);
					getDateByDeviceIdFromDataStore(item);
				}
			}
			
		});
				
		dateListBox.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				refreshTimer.cancel(); //Stop timer
				if (dateListBox.getItemCount() != 0) {
					
					locDeviceId = deviceListBox.getItemText(deviceListBox.getSelectedIndex());
					locDate = dateListBox.getItemText(dateListBox.getSelectedIndex());
					
					getFixsByIdDateFromDataStore(
							deviceListBox.getItemText(deviceListBox.getSelectedIndex()), 
							dateListBox.getItemText(dateListBox.getSelectedIndex())
					);
										
					//use timer to get the data from datastore repeatedly
					refreshTimer.scheduleRepeating(5000); //ms
				}
			}
			
		});
	}
	
	/* Http Post, use for send fake position fix to database */
	private void doPost(String url, String postData){
		StringBuffer sb = new StringBuffer();
		// note param pairs are separated by a '&' 
		// and each key-value pair is separated by a '='
		sb.append(URL.encode("fix")).append("=").append(URL.encode(postData));
		//sb.append("&");
		//sb.append(URL.encode("YourParameterName2")).append("=").append(URL.encode("YourParameterValue2"));

		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
		builder.setHeader("Content-type", "application/x-www-form-urlencoded");
		try {
		  builder.sendRequest(sb.toString(), new RequestCallback() {

		        public void onError(Request request, Throwable exception) {
		          // code omitted for clarity
		        }

		        public void onResponseReceived(Request request, Response response) {
		        	//fixsText.setText(fixsText.getText() + request.toString() + '\n');
		        	//fixsText.setText(fixsText.getText().toString() + response.getText() + "\n");
		        }
		  });
		} catch (RequestException e) {
		  // handle this
		}
	}

	/* ----------- Update web interface information ---------------*/
	
	private void updateFixsFlexTable(List<PositionFix> posFixs) {
		
		int row = 1;
		
		fixsFlexTable.removeAllRows();
		fixsFlexTable.setText(0, 0, "Time");
		fixsFlexTable.setText(0, 1, "Latitude");
		fixsFlexTable.setText(0, 2, "Longitude");
		fixsFlexTable.getRowFormatter().addStyleName(0, "fixsListHeader");
		fixsFlexTable.addStyleName("fixsList");
		
		for(PositionFix fix : posFixs){
			fixsFlexTable.setText(row, 0, fix.getTimeUTC());
			fixsFlexTable.setText(row, 1, fix.getLatitude());
			fixsFlexTable.setText(row, 2, fix.getLongitude());
			fixsFlexTable.getCellFormatter().addStyleName(row, 0, "fixsListNumericColumn");
			fixsFlexTable.getCellFormatter().addStyleName(row, 1, "fixsListNumericColumn");
			fixsFlexTable.getCellFormatter().addStyleName(row, 2, "fixsListNumericColumn");
			row++;
		}
	}
	
	private void updateMap(List<PositionFix> posFixs) {
		drawMarkers(posFixs);
		drawPolyLine(posFixs);
	}
	
	private void updateWebPage(List<PositionFix> posFixs) {
		updateFixsFlexTable(posFixs);
		updateMap(posFixs);
	}
	/* ----------- -------------------------------- ---------------*/
	
	
	/* -------------- RPC(Remote Procedure Call) Methods ------------------ */
	
	//Get the trajectory of specific day
	private void getFixsByIdDateFromDataStore(String deviceId, String date) {
		FixServiceAsync fixService = 
				(FixServiceAsync) GWT.create(FixService.class);
		fixService.getPositionFixsByIdDate(deviceId, date, new AsyncCallback<List<PositionFix>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(List<PositionFix> result) {
				
				updateWebPage(result);
				
				latestMapCenter = LatLng.newInstance(
						Double.valueOf(result.get(result.size() - 1).getLatitude()), 
						Double.valueOf(result.get(result.size() - 1).getLongitude())
				);
				
				//print debug message
				for (PositionFix pos : result) {
					System.out.println("Client-side > " + pos.getTimeUTC() + ", " 
							+ pos.getLatitude() + ", " 
							+ pos.getLongitude());
				}
				
			}
			
		});
	}
	
	//Get the date which has trajectory record by specific device ID 
	private void getDateByDeviceIdFromDataStore(String deviceId) {
		FixServiceAsync fixService = 
				(FixServiceAsync) GWT.create(FixService.class);
		fixService.getDateByID(deviceId, new AsyncCallback<List<Date>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(List<Date> result) {
				dateListBox.clear();
				for (Date date : result) {
					dateListBox.addItem(date.getCreateDate());
				}
				
			}
			
		});
	}
	
	//get all devices ID
	private void getDeviceIDfromDataStore() {
		FixServiceAsync fixService = 
				(FixServiceAsync) GWT.create(FixService.class);
		fixService.getAllDevice(new AsyncCallback<List<Device>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(List<Device> result) {
				deviceListBox.clear();
				for (Device device : result) {
					deviceListBox.addItem(device.getDeviceName());
				}
			}
			
		});
	}
	/* -------------- --------------------------------- ------------------ */
	
	
	/* --------------- Methods about Google Maps API --------------------- */
	private void loadMapApi() {
	    boolean sensor = true;

	    // load all the libs for use in the maps
	    ArrayList<LoadLibrary> loadLibraries = new ArrayList<LoadApi.LoadLibrary>();
	    //loadLibraries.add(LoadLibrary.ADSENSE);
	    loadLibraries.add(LoadLibrary.DRAWING);
	    //loadLibraries.add(LoadLibrary.GEOMETRY);
	    //loadLibraries.add(LoadLibrary.PANORAMIO);
	    //loadLibraries.add(LoadLibrary.PLACES);
	    //loadLibraries.add(LoadLibrary.WEATHER);
	    //loadLibraries.add(LoadLibrary.VISUALIZATION);

	    Runnable onLoad = new Runnable() {
	      @Override
	      public void run() {
	        drawMap();
	      }
	    };

	    LoadApi.go(onLoad, loadLibraries, sensor);
	  }
	
	private MapWidget mapWidget;
	private void drawMap(){
		LatLng center = LatLng.newInstance(22.9971d, 120.22192d);
	    MapOptions opts = MapOptions.newInstance();
	    opts.setZoom(16);
	    opts.setCenter(center);
	    opts.setMapTypeId(MapTypeId.ROADMAP);

	    mapWidget = new MapWidget(opts);
	    mapPanel.add(mapWidget);
	    mapWidget.setSize("1024px", "700px");
	}
	
	private Marker marker;
	//record all markers
	private List<Marker> markers = new ArrayList<Marker>();
	/* Draw markers on map */
	private void drawMarkers(List<PositionFix> fixs){
		clearMakers();
		for(PositionFix f: fixs){
			LatLng fix = LatLng.newInstance(Double.valueOf(f.getLatitude()), 
											Double.valueOf(f.getLongitude())
										   );
			MarkerOptions options = MarkerOptions.newInstance();
		    options.setPosition(fix);
		    options.setTitle(f.getTimeUTC() + ", " + f.getLatitude() + ", " + f.getLongitude());

		    marker = Marker.newInstance(options);
		    marker.setMap(mapWidget);
		    
		    //recode the marker for clear it from map 
		    markers.add(marker);
		    
		    	    		    
		}
	}
		
	/* Remove all makers from the mapWidget*/
	private void clearMakers() {
	    if (mapWidget != null) {
	        MapWidget map = null;
	        if (markers != null && markers.size() > 0) {
	            for (Marker marker : markers) {
	                marker.setMap(map);
	            }
	            markers = new ArrayList<Marker>();
	        }
	    }
	}
		
	private Polyline polyLine;
	private void drawPolyLine(List<PositionFix> fixs) {
		clearPolyLine(polyLine);
		LatLng[] tmp_LatLng = new LatLng[fixs.size()];
		int i = 0;
		for(PositionFix f: fixs){
			tmp_LatLng[i++] = LatLng.newInstance(Double.valueOf(f.getLatitude()), Double.valueOf(f.getLongitude()));
		}
		JsArray<LatLng> fixs_LatLng = ArrayHelper.toJsArray(tmp_LatLng) ;
		// Draw various recurring symbols on the map
	    // different colors and sizes/types shown
//		final Symbol icon1 = Symbol.newInstance(SymbolPath.CIRCLE, 4d);
//		icon1.setStrokeColor("blue");
		final Symbol icon2 = Symbol.newInstance(SymbolPath.FORWARD_CLOSED_ARROW, 5d);
		icon2.setStrokeColor("yellow");
		final Symbol icon3 = Symbol.newInstance(SymbolPath.FORWARD_OPEN_ARROW, 3d);
		icon3.setStrokeColor("orange");
		
		// start different sequences in different areas
//		final IconSequence iconSeq1 = IconSequence.newInstance();
//		iconSeq1.setIcon(icon1);
//		iconSeq1.setOffset("10%");
//		iconSeq1.setRepeat("22%");

		final IconSequence iconSeq2 = IconSequence.newInstance();
		iconSeq2.setIcon(icon2);
		iconSeq2.setOffset("10%");
		iconSeq2.setRepeat("20%");
		
		final IconSequence iconSeq3 = IconSequence.newInstance();
		iconSeq3.setIcon(icon3);
		iconSeq3.setOffset("10%");
		iconSeq3.setRepeat("20%");

		final JsArray<IconSequence> iconsArr = ArrayHelper
				.toJsArray(new IconSequence[] { iconSeq2, iconSeq3 });
		
		
		
		PolylineOptions opts = PolylineOptions.newInstance();
	    // opts.setMap(mapWidget); // you can attach it to the map here
	    opts.setPath(fixs_LatLng);
	    opts.setStrokeColor("#0000FF");
	    opts.setStrokeOpacity(1.0);
	    opts.setStrokeWeight(2);
	    opts.setIcons(iconsArr);;
	    
	    polyLine = Polyline.newInstance(opts);
	    polyLine.setMap(mapWidget);
	    
	}
	
	private void clearPolyLine(Polyline polyLine){
		if (mapWidget != null) {
			MapWidget map = null;
			if (polyLine != null) {
				polyLine.setMap(map);
				polyLine = null;
			}
		}
	}

	/* --------------- ----------------------------- --------------------- */
	
	
	Timer refreshTimer = new Timer(){

		@Override
		public void run() {
			System.out.println("Client-side > " + "Timer is running");
			getFixsByIdDateFromDataStore(locDeviceId, locDate);
		}
	};

	
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		initHandlers();
		initWidgets();		
		
		getDeviceIDfromDataStore();
		
		/* Initialize maps api and draw the map*/
		loadMapApi();
	}
}
