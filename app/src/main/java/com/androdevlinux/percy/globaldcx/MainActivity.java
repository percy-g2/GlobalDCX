package com.androdevlinux.percy.globaldcx;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.androdevlinux.percy.globaldcx.network.ApiManager;
import com.androdevlinux.percy.globaldcx.utils.CustomMarkerViewLineChart;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    protected ApiManager apiManager;
    ArrayList<String> xValues = new ArrayList<>();
    ArrayList<Entry> yVals1 = new ArrayList<>();
    ArrayList<ILineDataSet> dataSets = new ArrayList<>();
    int count = -1;
    @BindView(R.id.LineChart)
    LineChart mChart;
    int checkItem = 0;
    @BindView(R.id.image)
    ImageView image;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.action0)
    LinearLayout action0;
    @BindView(R.id.intervalRadioGroup)
    RadioGroup intervalRadioGroup;
    Description description = new Description();
    @BindView(R.id.image2)
    ImageView image2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    @BindView(R.id.share)
    ImageView share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        apiManager = ApiManager.getInstance();
        CustomMarkerViewLineChart mv = new CustomMarkerViewLineChart(mChart, this, R.layout.custom_marker_view_layout);
        // set the marker to the chart
        mChart.setMarker(mv);
        image.setImageResource(R.drawable.btc);
        title.setText(R.string.bitcoin);
        intervalRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            Toast.makeText(getBaseContext(), ((RadioButton) findViewById(checkedId))
                    .getText().toString(), Toast.LENGTH_SHORT).show();
            String val = ((RadioButton) findViewById(checkedId))
                    .getText().toString();
            String interval = "1M";
            switch (val) {
                case "Daily":
                    interval = "1D";
                    break;
                case "Weekly":
                    interval = "7D";
                    break;
                case "Monthly":
                    interval = "1M";
                    break;
            }
            new fetchData(checkItem, interval).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        });

        new fetchData(checkItem, "1M").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    @OnClick({R.id.share, R.id.action0})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share:
                verifyStoragePermissions(this);
                mChart.saveToGallery("chart",100);

                File file = new File(Environment.getExternalStorageDirectory() + "/DCIM/chart.jpg");
                Intent target = new Intent();
                target.setAction(Intent.ACTION_SEND);
                Uri uri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    uri = FileProvider.getUriForFile(this,
                            BuildConfig.APPLICATION_ID + ".provider",
                            file);
                } else {
                    uri = Uri.fromFile(file);
                }
                target.putExtra(Intent.EXTRA_STREAM, uri);
                target.setType("image/jpeg");
                Intent intent = Intent.createChooser(target, "Share File");
                try {
                    startActivity(intent);
                } catch (Exception e) {
                    Log.i(MainActivity.class.getName(), e.getMessage());
                }
                break;
            case R.id.action0:
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Choose Currency");
                image2.setImageResource(R.drawable.ic_keyboard_arrow_up_black_48dp);
                //list of items
                String[] items = new String[]{"Bitcoin", "Ethereum", "Ripple", "Monero"};
                builder.setSingleChoiceItems(items, checkItem,
                        (dialog, which) -> {
                        });

                String positiveText = getString(android.R.string.ok);
                builder.setPositiveButton(positiveText,
                        (dialog, which) -> {
                            image2.setImageResource(R.drawable.ic_keyboard_arrow_down_black_48dp);
                            checkItem = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                            if (checkItem == 0) {
                                image.setImageResource(R.drawable.btc);
                                title.setText(R.string.bitcoin);
                            } else if (checkItem == 1) {
                                image.setImageResource(R.drawable.eth);
                                title.setText(R.string.ethereum);
                            } else if (checkItem == 2) {
                                image.setImageResource(R.drawable.xrp);
                                title.setText(R.string.ripple);
                            } else if (checkItem == 3) {
                                image.setImageResource(R.drawable.xmr);
                                title.setText(R.string.monero);
                            }
                            String val = ((RadioButton) findViewById(intervalRadioGroup.getCheckedRadioButtonId()))
                                    .getText().toString();
                            String interval = "1M";
                            switch (val) {
                                case "Daily":
                                    interval = "1D";
                                    break;
                                case "Weekly":
                                    interval = "7D";
                                    break;
                                case "Monthly":
                                    interval = "1M";
                                    break;
                            }
                            new fetchData(checkItem, interval).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            dialog.dismiss();
                        });

                String negativeText = getString(android.R.string.cancel);
                builder.setNegativeButton(negativeText,
                        (dialog, which) -> {
                            image2.setImageResource(R.drawable.ic_keyboard_arrow_down_black_48dp);
                            dialog.dismiss();
                        });

                AlertDialog dialog = builder.create();
                // display dialog
                dialog.show();
                break;
        }
    }


    @SuppressLint("StaticFieldLeak")
    private class fetchData extends AsyncTask<String, String, String> {
        ProgressDialog pdLoading = new ProgressDialog(MainActivity.this);
        int currencyId;
        String interval;

        private fetchData(int checkItem, String interval) {
            this.currencyId = checkItem;
            this.interval = interval;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pdLoading.setTitle("Loading...");
            pdLoading.setMessage("Please wait while loading...");
            pdLoading.setIndeterminate(true);
            pdLoading.setCancelable(false);
            pdLoading.show();
        }

        @Override
        protected String doInBackground(String... value) {
            getBitfinexData(currencyId, interval);
            return null;
        }

        @Override
        protected void onPostExecute(String value) {
            description.setText(title.getText().toString());
            description.setTextSize(20f);
            description.setTextAlign(Paint.Align.RIGHT);
            mChart.setDescription(description);
            // enable scaling and dragging
            mChart.setDragEnabled(true);
            mChart.setScaleEnabled(true);
            mChart.setExtraRightOffset(30f);
            mChart.setBackgroundColor(Color.WHITE);
            //mChart.setGridBackgroundColor(Color.WHITE);
            mChart.setDrawGridBackground(false);

            mChart.setDrawBorders(true);


            mChart.setPinchZoom(true);

            Legend l = mChart.getLegend();
            l.setEnabled(false);
            mChart.invalidate();
            pdLoading.dismiss();
        }
    }

    private void getBitfinexData(int currencyId, String interval) {
        String symbol = "tBTCUSD";
        if (currencyId == 0) {
            symbol = "tBTCUSD";
        } else if (currencyId == 1) {
            symbol = "tETHUSD";
        } else if (currencyId == 2) {
            symbol = "tXRPUSD";
        } else if (currencyId == 3) {
            symbol = "tXMRUSD";
        }
        apiManager.getBitfinexData(interval, symbol, new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                Gson gson = new Gson();
                try {
                    BigDecimal[][] newMap;
                    BigDecimal[][] dummy = new BigDecimal[0][0];
                    newMap = gson.fromJson(response.body().string(), dummy.getClass());
                    yVals1.clear();
                    xValues.clear();
                    mChart.clear();
                    dataSets.clear();
                    mChart.invalidate();
                    count = -1;

                    Collections.reverse(Arrays.asList(newMap));
                    for (BigDecimal[] s : newMap) {
                        count += 1;
                        //new

                        yVals1.add(new Entry(count, Float.valueOf(String.valueOf(s[2].multiply(new BigDecimal(69))))));
                        Date date = new Date(Long.valueOf(String.valueOf(s[0])));
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yy HH:mm", Locale.ENGLISH);
                        sdf.setTimeZone(TimeZone.getDefault());
                        String formattedDate = sdf.format(date);

                        xValues.add(formattedDate);


                        XAxis xAxis = mChart.getXAxis();
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setDrawGridLines(false);
                        xAxis.setAxisLineColor(Color.BLACK);
                        xAxis.setTextColor(Color.BLACK);
                        xAxis.setGranularity(1f);

                        xAxis.setValueFormatter((value1, axis) -> xValues.get((int) value1 % xValues.size()));

                        YAxis leftAxis = mChart.getAxisLeft();
                        leftAxis.setTextColor(Color.BLACK);
                        leftAxis.setDrawAxisLine(true);
                        leftAxis.setDrawZeroLine(true);
                        leftAxis.setDrawGridLines(false);
                        leftAxis.setGranularityEnabled(true);
                        leftAxis.setGridColor(Color.BLACK);
                        leftAxis.setAxisLineColor(Color.BLACK);

                        mChart.getAxisRight().setEnabled(false);
                        LineDataSet set1;

                        set1 = new LineDataSet(yVals1, "DataSet 1");

                        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                        set1.setColor(0x6290C8);
                        set1.setDrawCircles(false);
                        set1.setLineWidth(1f);
                        set1.setCircleRadius(3f);
                        set1.setFillAlpha(50);
                        set1.setDrawFilled(true);
                        Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.fade_red);
                        set1.setFillDrawable(drawable);
                        set1.setDrawCircleHole(false);
                        set1.setDrawHorizontalHighlightIndicator(true);
                        dataSets.add(set1);
                        LineData lineData = new LineData(dataSets);
                        lineData.setDrawValues(false);
                        mChart.animateY(30, Easing.EasingOption.EaseOutBack);
                        mChart.animateX(30, Easing.EasingOption.EaseOutBack);
                        mChart.setData(lineData);

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
            }
        });
    }

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }
}
