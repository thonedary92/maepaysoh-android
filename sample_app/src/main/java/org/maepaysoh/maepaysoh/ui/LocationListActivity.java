package org.maepaysoh.maepaysoh.ui;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import java.util.ArrayList;
import java.util.List;
import org.maepaysoh.maepaysoh.MaePaySoh;
import org.maepaysoh.maepaysoh.R;
import org.maepaysoh.maepaysoh.adapters.EndlessRecyclerViewAdapter;
import org.maepaysoh.maepaysoh.adapters.LocationAdapter;
import org.maepaysoh.maepaysoh.utils.InternetUtils;
import org.maepaysoh.maepaysoh.utils.ViewUtils;
import org.maepaysoh.maepaysohsdk.GeoAPIHelper;
import org.maepaysoh.maepaysohsdk.MaePaySohApiWrapper;
import org.maepaysoh.maepaysohsdk.models.Geo;
import org.maepaysoh.maepaysohsdk.utils.GeoAPIProperties;
import org.maepaysoh.maepaysohsdk.utils.GeoAPIPropertiesMap;

public class LocationListActivity extends BaseActivity implements LocationAdapter.ClickInterface {

  private Button ygnWestBtn;
  private ViewUtils viewUtils;
  private LinearLayoutManager mLayoutManager;
  private RecyclerView mLocationListRecyclerView;
  private LocationAdapter mLocationAdapter;
  private MaePaySohApiWrapper mMaePaySohApiWrapper;
  private GeoAPIHelper mGeoAPIHelper;
  private View mRetryBtn;
  private List<Geo> mGeos = new ArrayList<>();
  private ProgressBar mProgressView;
  private EndlessRecyclerViewAdapter mEndlessRecyclerViewAdapter;
  private int mCurrentPage = 1;
  private GeoAPIPropertiesMap mGeoAPIPropertiesMap;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_location);

    Toolbar mToolbar = (Toolbar) findViewById(R.id.location_list_toolbar);
    View mToolbarShadow = findViewById(R.id.location_list_toolbar_shadow);
    mProgressView = (ProgressBar) findViewById(R.id.location_list_progress_bar);
    mProgressView.getIndeterminateDrawable()
        .setColorFilter(getResources().getColor(R.color.primary), PorterDuff.Mode.SRC_ATOP);

    mToolbar.setTitle(getString(R.string.LocationList));
    hideToolBarShadowForLollipop(mToolbar, mToolbarShadow);

    setSupportActionBar(mToolbar);

    mGeoAPIPropertiesMap = new GeoAPIPropertiesMap();
    ActionBar mActionBar = getSupportActionBar();
    if (mActionBar != null) {
      // Showing Back Arrow  <-
      mActionBar.setDisplayHomeAsUpEnabled(true);
    }
    mLocationListRecyclerView = (RecyclerView) findViewById(R.id.location_list_recycler_view);
    mRetryBtn = findViewById(R.id.location_list_error_view);
    viewUtils = new ViewUtils(this);
    viewUtils.showProgress(mLocationListRecyclerView, mProgressView, true);
    mLayoutManager = new LinearLayoutManager(this);
    mLocationListRecyclerView.setLayoutManager(mLayoutManager);
    mLocationAdapter = new LocationAdapter();
    mLocationAdapter.setOnItemClickListener(this);
    mMaePaySohApiWrapper = MaePaySoh.getMaePaySohWrapper();
    mGeoAPIHelper = mMaePaySohApiWrapper.getGeoApiHelper();
    mEndlessRecyclerViewAdapter =
        new EndlessRecyclerViewAdapter(LocationListActivity.this, mLocationAdapter,
            new EndlessRecyclerViewAdapter.RequestToLoadMoreListener() {
              @Override public void onLoadMoreRequested() {
                loadLocationData();
              }
            });

    mLocationListRecyclerView.setAdapter(mEndlessRecyclerViewAdapter);
    if (InternetUtils.isNetworkAvailable(this)) {
      loadLocationData();
    } else {
    }
    mRetryBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        loadLocationData();
      }
    });
  }

  @Override public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == android.R.id.home) {
      onBackPressed();
    }
    return super.onOptionsItemSelected(item);
  }

  private void loadLocationData() {
    new LocationDownloadAsync().execute(mCurrentPage);
  }

  @Override public void onItemClick(View view, int position) {
    Intent locationDetailIntent =
        new Intent(LocationListActivity.this, LocationDetailActivity.class);
    locationDetailIntent.putExtra("GEO_OBJECT_ID",
        mGeos.get(position).getProperties().getDTPCODE());
    startActivity(locationDetailIntent);
  }

  class LocationDownloadAsync extends AsyncTask<Integer, Void, List<Geo>> {

    @Override protected List<Geo> doInBackground(Integer... integers) {
      mCurrentPage = integers[0];
      mGeoAPIPropertiesMap.put(GeoAPIProperties.PER_PAGE, 15);
      mGeoAPIPropertiesMap.put(GeoAPIProperties.NO_GEO, true);
      mGeoAPIPropertiesMap.put(GeoAPIProperties.FIRST_PAGE, mCurrentPage);
      List<Geo> geos = mGeoAPIHelper.getLocationList(mGeoAPIPropertiesMap);
      return geos;
    }

    @Override protected void onPostExecute(List<Geo> geos) {
      super.onPostExecute(geos);
      viewUtils.showProgress(mLocationListRecyclerView, mProgressView, false);
      if (geos.size() > 0) {
        if (mCurrentPage == 1) {
          mGeos = geos;
        } else {
          mGeos.addAll(geos);
        }
        mLocationAdapter.setGeos(geos);
        mCurrentPage++;
        mEndlessRecyclerViewAdapter.onDataReady(true);
      } else {
        mEndlessRecyclerViewAdapter.onDataReady(false);
      }
    }
  }
}
