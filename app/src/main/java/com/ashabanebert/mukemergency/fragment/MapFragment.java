package com.ashabanebert.mukemergency.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.ashabanebert.mukemergency.R;
import com.ashabanebert.mukemergency.model.LocationUpdate;
import com.ashabanebert.mukemergency.service.LocationUpdateService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment implements OnMapReadyCallback {


    private GoogleMap googleMap;
    private Marker currentLocationMarker;
    private Location currentLocation;
    ImageButton img;
    Bundle args;
    String[] coordinates;
    String u = "";
    List<LatLng> locations = new ArrayList<>();
    List<Marker> markers = new ArrayList<>();
    private Animator animator = new Animator();
    private Marker selectedMarker;
    private final Handler mHandler = new Handler();



    public static MapFragment newInstance() {
        Bundle args = new Bundle();
        MapFragment mapFragment = new MapFragment();
        //mapFragment.setArguments(args);
        return mapFragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.map_fragment, container, false);

        if(!EventBus.getDefault().isRegistered(this)){
            EventBus.getDefault().register(this);
        }

        img = root.findViewById(R.id.currentLocationImageButton);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (googleMap != null){
                    toggleStyle();
                }

            }
        });

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.mapFragment);
        supportMapFragment.getMapAsync(this);

        return root;

    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        if(getArguments() != null){
            args = getArguments();
            coordinates = args.getStringArray("coordinates");
            u = args.getString("u");
            //hiddenSync(u);
            if(coordinates != null && coordinates.length != 0){
                Log.d("ZZZZZ MAP", coordinates[0]+", "+coordinates[1]);
                Location l = new Location("");
                l.setLatitude(Double.valueOf(coordinates[0]));
                l.setLongitude(Double.valueOf(coordinates[1]));
                currentLocation = l;
                locations.add(new LatLng(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1])));
                showMarker(currentLocation);
                animateCamera(currentLocation);
                locations.clear();
                for(LatLng loc : locations){
                    showMarker(currentLocation);
                    animateCamera(currentLocation);
                }
                Intent i= new Intent(getActivity(), LocationUpdateService.class);
                i.putExtra("u", u);
                getActivity().startService(i);
                animator.startAnimation(true);

            }

        }
    }



    private void showMarker(@NonNull Location currentLocation) {
        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        if (currentLocationMarker == null){
            currentLocationMarker = googleMap.addMarker(new MarkerOptions().position(latLng).title("Current Location"));
            currentLocationMarker.showInfoWindow();
        }

    }

    private void animateCamera(@NonNull Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(getCameraPositionWithBearing(latLng)));
    }

    @NonNull
    private CameraPosition getCameraPositionWithBearing(LatLng latLng) {
        return new CameraPosition.Builder().target(latLng).zoom(16).build();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
        googleMap = null;

    }

    @Override
    public void onStop() {
        super.onStop();
        if(mHandler != null){
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Subscribe
    public void onEvent(LocationUpdate locationUpdate){
        Location newLocation = new Location("");
        newLocation.setLongitude(Double.valueOf(locationUpdate.getLng()));
        newLocation.setLatitude(Double.valueOf(locationUpdate.getLat()));

        float distance = getDistance(currentLocation.getLatitude(), currentLocation.getLongitude(), newLocation.getLatitude(), newLocation.getLongitude());
        Log.d("ZZZZZ", "Distance is "+distance);


        if(distance > 2){

            LatLng oldL = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            LatLng newL = new LatLng(newLocation.getLatitude(), newLocation.getLongitude());

            locations.clear();
            locations.add(oldL);
            locations.add(newL);
            markers.clear();
            for(LatLng loc : locations){
                Marker marker = googleMap.addMarker(new MarkerOptions().position(loc).title("Current Location"));
                markers.add(marker);
            }
            Log.d("ZZZZZ", "Size of markers is "+markers.size());
            animator.startAnimation(true);
            currentLocation = newLocation;

        }

    }


    private float getDistance(double lat1, double lon1, double lat2, double lon2) {
        float[] distance = new float[2];
        Location.distanceBetween(lat1, lon1, lat2, lon2, distance);
        return distance[0];
    }



    public void toggleStyle() {
        if(googleMap != null){
            if (GoogleMap.MAP_TYPE_NORMAL == googleMap.getMapType()) {
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                img.setImageResource(R.drawable.map);
            } else {
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                img.setImageResource(R.drawable.satellite);
            }
        }
    }

    public class Animator implements Runnable {
        private static final int ANIMATE_SPEEED = 1500;
        private static final int ANIMATE_SPEEED_TURN = 1000;
        private static final int BEARING_OFFSET = 20;
        private final Interpolator interpolator = new LinearInterpolator();
        int currentIndex = 0;
        float tilt = 90;
        float zoom = 15.5f;
        boolean upward=true;
        long start = SystemClock.uptimeMillis();
        LatLng endLatLng = null;
        LatLng beginLatLng = null;
        boolean showPolyline = false;
        private Marker trackingMarker;

        public void reset() {
            resetMarkers();
            start = SystemClock.uptimeMillis();
            currentIndex = 0;
            endLatLng = getEndLatLng();
            beginLatLng = getBeginLatLng();

        }

        private void resetMarkers() {
            for (Marker marker : markers) {
                marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            }
        }

        public void stop() {
            trackingMarker.remove();
            mHandler.removeCallbacks(animator);
        }

        private void highLightMarker(int index) {
            highLightMarker(markers.get(index));
        }

        private void highLightMarker(Marker marker) {
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            marker.showInfoWindow();
            selectedMarker=marker;
        }

        public void initialize(boolean showPolyLine) {
            reset();
            this.showPolyline = showPolyLine;
            highLightMarker(0);
            if (showPolyLine) {
                polyLine = initializePolyLine();
            }
            LatLng markerPos = markers.get(0).getPosition();
            LatLng secondPos = markers.get(1).getPosition();
            setupCameraPositionForMovement(markerPos, secondPos);
        }

        private void setupCameraPositionForMovement(LatLng markerPos, LatLng secondPos) {
            float bearing = bearingBetweenLatLngs(markerPos,secondPos);
            trackingMarker = googleMap.addMarker(new MarkerOptions().position(markerPos)
                    .title("title")
                    .snippet("snippet"));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(markerPos)
                    .bearing(bearing + BEARING_OFFSET)
                    .tilt(90)
                    .zoom(googleMap.getCameraPosition().zoom >=16 ? googleMap.getCameraPosition().zoom : 16)
                    .build();

            googleMap.animateCamera(
                    CameraUpdateFactory.newCameraPosition(cameraPosition),
                    ANIMATE_SPEEED_TURN,
                    new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            System.out.println("finished camera");
                            Log.e("animator before reset", animator +"");
                            animator.reset();
                            Log.e("animator after reset", animator +"");
                            Handler handler = new Handler();
                            handler.post(animator);
                        }
                        @Override
                        public void onCancel() {
                            System.out.println("cancelling camera");
                        }
                    });
        }


        private Location convertLatLngToLocation(LatLng latLng) {
            Location loc = new Location("someLoc");
            loc.setLatitude(latLng.latitude);
            loc.setLongitude(latLng.longitude);
            return loc;
        }

        private float bearingBetweenLatLngs(LatLng begin,LatLng end) {
            Location beginL= convertLatLngToLocation(begin);
            Location endL= convertLatLngToLocation(end);
            return beginL.bearingTo(endL);
        }

        private Polyline polyLine;
        private PolylineOptions rectOptions = new PolylineOptions();
        private Polyline initializePolyLine() {
            rectOptions.color(Color.RED);
            rectOptions.add(markers.get(0).getPosition());
            return googleMap.addPolyline(rectOptions);
        }

        private void updatePolyLine(LatLng latLng) {
            List<LatLng> points = polyLine.getPoints();
            points.add(latLng);
            polyLine.setPoints(points);
        }
        public void stopAnimation() {
            animator.stop();
        }

        public void startAnimation(boolean showPolyLine) {
            if (markers.size()>1) {
                animator.initialize(showPolyLine);
            }
        }
        @Override
        public void run() {
            long elapsed = SystemClock.uptimeMillis() - start;
            double t = interpolator.getInterpolation((float)elapsed/ANIMATE_SPEEED);
            Log.w("interpolator", t +"");
            double lat = t * endLatLng.latitude + (1-t) * beginLatLng.latitude;
            double lng = t * endLatLng.longitude + (1-t) * beginLatLng.longitude;
            Log.w("lat. lng", lat + "," + lng +"");
            LatLng newPosition = new LatLng(lat, lng);
            Log.w("newPosition", newPosition +"");

            trackingMarker.setPosition(newPosition);
            if (showPolyline) {
                updatePolyLine(newPosition);
            }

            if (t< 1) {
                mHandler.postDelayed(this, 16);
            } else {

                if (currentIndex<markers.size()-2) {
                    currentIndex++;
                    endLatLng = getEndLatLng();
                    beginLatLng = getBeginLatLng();
                    start = SystemClock.uptimeMillis();
                    LatLng begin = getBeginLatLng();
                    LatLng end = getEndLatLng();
                    float bearingL = bearingBetweenLatLngs(begin, end);
                    highLightMarker(currentIndex);
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(end) // changed this...
                            .bearing(bearingL  + BEARING_OFFSET)
                            .tilt(tilt)
                            .zoom(googleMap.getCameraPosition().zoom)
                            .build();
                    googleMap.animateCamera(
                            CameraUpdateFactory.newCameraPosition(cameraPosition),
                            ANIMATE_SPEEED_TURN,
                            null
                    );
                    start = SystemClock.uptimeMillis();
                    mHandler.postDelayed(animator, 16);
                } else {

                    currentIndex++;
                    currentLocation = animator.convertLatLngToLocation(markers.get(currentIndex).getPosition());
                    highLightMarker(currentIndex);
                    stopAnimation();
                }
            }
        }
        private LatLng getEndLatLng() {
            return markers.get(currentIndex+1).getPosition();
        }
        private LatLng getBeginLatLng() {
            return markers.get(currentIndex).getPosition();
        }
    }

}
