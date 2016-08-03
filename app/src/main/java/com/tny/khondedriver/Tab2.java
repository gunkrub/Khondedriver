package com.tny.khondedriver;

/**
 * Created by Thitiphat on 9/17/2015.
 */
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hp1 on 21-01-2015.
 */
public class Tab2 extends Fragment {
    static JobsAdapter adapter;
     SwipeRefreshLayout swipeContainer;
    static String starting="",destination="";
    private int totalItem=0;
    boolean isLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tab_2,container,false);

        ArrayList<Job> arrayOfJobs = new ArrayList<Job>();
        adapter = new JobsAdapter(getActivity().getApplicationContext(),arrayOfJobs);

        ListView listView = (ListView)v.findViewById(R.id.jobListView);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {

                int job_id = adapter.getItem(position).job_id;

                Intent intent = new Intent(getActivity().getApplicationContext(), ViewJobActivity.class);
                intent.putExtra("job_id", job_id);
                intent.putExtra("job_index",position);
                startActivityForResult(intent,21);
            }
        });

        /*
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                // TODO Auto-generated method stub
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                // TODO Auto-generated method stub
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                totalItem = totalItemCount;


            }

            private void isScrollCompleted() {
                if (totalItem - currentFirstVisibleItem <= currentVisibleItemCount + 10
                        && this.currentScrollState == SCROLL_STATE_IDLE) {
                    if(!isLoading)
                    getJobList(starting, destination);

                }
            }
        });
                */


        swipeContainer = (SwipeRefreshLayout) v.findViewById(R.id.swipeContainer);
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                totalItem = 0;
                getJobList(starting, destination);
            }
        });

        swipeContainer.setColorSchemeResources(
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getJobList(starting, destination);

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        refreshJobList();
                    }
                },
                20000);

        return v;

    }



    public void getJobList(String starting,String destination){
        if(getActivity() == null || !isAdded())
            return;

        GetJobsTask g1 = new GetJobsTask();
        g1.execute(starting, destination,"");
        this.starting =starting;
        this.destination = destination;
    }

    public void refreshJobList(){
        if(getActivity() == null || !isAdded())
            return;

        GetJobsTask g1 = new GetJobsTask();
        g1.execute(starting, destination, "&getall");
        Log.e("REFRESHJOBLIST","Refreshing Tab2");
    }

    public class GetJobsTask extends AsyncTask<String, Void, Boolean> {
        String errorMsg = "";
        JSONArray array1 = new JSONArray();
        boolean continuos;

        @Override
        protected void onPreExecute() {
            isLoading = true;
        }

        @Override
        protected Boolean doInBackground(String... params) {
            BufferedReader reader;
            StringBuilder buffer = new StringBuilder();
            String line;

            try {
                if(!isAdded())
                    return false;
                URL u = new URL(getString(R.string.website_url) + "get_job.php?starting=" + params[0].trim() + "&destination=" + params[1].trim() + "&totalItem=" + totalItem + params[2]);

                HttpURLConnection h = (HttpURLConnection)u.openConnection();
                h.setRequestMethod("GET");
                h.setDoOutput(true);

                h.connect();


                int response = h.getResponseCode();
                if (response == 200) {
                    reader = new BufferedReader(new InputStreamReader(h.getInputStream(),"UTF-8"));
                    while((line = reader.readLine()) != null) {
                        buffer.append(line);
                    }

                    JSONObject return1 = new JSONObject(buffer.toString());
                    //continuos = return1.getBoolean("continuos");
                    boolean emptyResult = return1.getBoolean("emptyResult");

                    if(emptyResult)
                        return true;

                    array1 = return1.getJSONArray("job");

                    return true;

                }
                else {
                    Log.e("", "HTTP Error");
                }
            } catch (MalformedURLException e) {
                Log.e("", "URL Error");
            } catch (IOException e) {
                Log.e("", "กรุณาเชื่อมต่ออินเตอร์เน็ต");
                errorMsg = "กรุณาเชื่อมต่ออินเตอร์เน็ต";
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            if(result) {
                if(!continuos)
                    adapter.clear();

                ArrayList<Job> newJobs = Job.fromJson(array1);
                adapter.addAll(newJobs);
            } else {
                if(isAdded() && !errorMsg.equals(""))
                    makeToast(errorMsg);
            }
            swipeContainer.setRefreshing(false);
            isLoading = false;

            if(getActivity() != null)
                getActivity().findViewById(R.id.progressBar).setVisibility(View.GONE);

        }

    }
    public void makeToast(String msg){

        Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }


}