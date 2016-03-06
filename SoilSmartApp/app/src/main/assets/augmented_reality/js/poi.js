// personalized implementation of AR-Experience (aka "World")
var World = {
    // you may request new data from server periodically, currently done only once
    isRequestingData: false,

	// POI-Marker asset
	markerDrawable_idle: {},
	markerDrawable_selected: {},
	markerDrawable_directionIndicator: null,
	// list of AR.GeoObjects nodes that are currently shown in the scene
    markerList: [],
    // The last selected
    currentMarker: null,

	// called to inject new POI data
	loadPoisFromJsonData: function loadPoisFromJsonDataFn(poiData) {

        World.markerList = [];
		World.markerDrawable_idle["full"] = new AR.ImageResource("assets/full_raindrop.png");
		World.markerDrawable_idle["almost_full"] = new AR.ImageResource("assets/almost_full_raindrop.png");
        World.markerDrawable_idle["half"] = new AR.ImageResource("assets/half_raindrop.png");
        World.markerDrawable_idle["low"] = new AR.ImageResource("assets/low_raindrop.png");
        World.markerDrawable_idle["very_low"] = new AR.ImageResource("assets/very_low_raindrop.png");

        World.markerDrawable_directionIndicator = new AR.ImageResource("assets/indi.png");

        World.markerDrawable_selected["full"] = new AR.ImageResource("assets/full_raindrop_selected.png");
        World.markerDrawable_selected["almost_full"] = new AR.ImageResource("assets/almost_full_raindrop_selected.png");
        World.markerDrawable_selected["half"] = new AR.ImageResource("assets/half_raindrop_selected.png");
        World.markerDrawable_selected["low"] = new AR.ImageResource("assets/low_raindrop_selected.png");
        World.markerDrawable_selected["very_low"] = new AR.ImageResource("assets/very_low_raindrop_selected.png");

		// loop through POI-information and create an AR.GeoObject (=Marker) per POI
		for (var currentPlaceNr = 0; currentPlaceNr < poiData.length; currentPlaceNr++) {
			var singlePoi = {
				"id": poiData[currentPlaceNr].id,
				"latitude": parseFloat(poiData[currentPlaceNr].latitude),
				"longitude": parseFloat(poiData[currentPlaceNr].longitude),
				"altitude": AR.CONST.UNKNOWN_ALTITUDE,
				"title": poiData[currentPlaceNr].name,
				"description": poiData[currentPlaceNr].description
			};

			/*
				To be able to deselect a marker while the user taps on the empty screen,
				the World object holds an array that contains each marker.
			*/
			World.markerList.push(new Marker(singlePoi, poiData[currentPlaceNr].level));
		}

		// Updates status message as a user feedback that everything was loaded properly.
		World.updateStatusMessage(currentPlaceNr + ' place loaded');
	},

	// updates status message shon in small "i"-button aligned bottom center
	updateStatusMessage: function updateStatusMessageFn(message, isWarning) {

		var themeToUse = isWarning ? "e" : "c";
		var iconToUse = isWarning ? "alert" : "info";

		$("#status-message").html(message);
		$("#popupInfoButton").buttonMarkup({
			theme: themeToUse
		});
		$("#popupInfoButton").buttonMarkup({
			icon: iconToUse
		});
	},

	// location updates, fired every time you call architectView.setLocation() in native environment
	locationChanged: function locationChangedFn(lat, lon, alt, acc) {
	},

	// fired when user pressed maker in cam
	onMarkerSelected: function onMarkerSelectedFn(marker) {

		// deselect previous marker
		if (World.currentMarker) {
			if (World.currentMarker.poiData.id == marker.poiData.id) {
				return;
			}
			World.currentMarker.setDeselected(World.currentMarker);
		}

		// highlight current one
		marker.setSelected(marker);
		World.currentMarker = marker;
	},

	// screen was clicked but no geo-object was hit
	onScreenClick: function onScreenClickFn() {
		if (World.currentMarker) {
			World.currentMarker.setDeselected(World.currentMarker);
		}
	},

};

/* 
	Set a custom function where location changes are forwarded to. There is also a possibility to set AR.context.onLocationChanged to null. In this case the function will not be called anymore and no further location updates will be received. 
*/
AR.context.onLocationChanged = World.locationChanged;
AR.context.onScreenClick = World.onScreenClick;