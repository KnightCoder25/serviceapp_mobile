package com.demo.mike;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.*;
import android.widget.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.demo.push.GcmListener;
import com.demo.service.Callback;
import com.demo.service.RestClient;
import com.demo.utils.ActivityBase;
import com.demo.utils.AndroidCallback;
import com.demo.utils.Job;
import com.demo.utils.TinyDB;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit.RetrofitError;

import java.util.ArrayList;


public class JobActivity extends ActivityBase {

    @Bind(R.id.lstJobs)
    ListView lstJobs;


    private ArrayList<Job> jobs = new ArrayList<Job>();
    private JobsAdapter jobsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewNoOverlay(R.layout.activity_job);

        jobsAdapter = new JobsAdapter();
        lstJobs.setAdapter(jobsAdapter);
        getJobs();
        GcmListener.pushRecieveListener= new GcmListener.PushRecieveListener() {
            @Override
            public void onPushRecieve() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getJobs();

                    }
                });
            }
        };


    }


    private void getJobs(){

        utilities.showProgress("Jobs", "Please wait..", context);

        RestClient.getInstance().partnerListJob(utilities.getUser(context).getId(), new AndroidCallback<ArrayList<Job>>(context) {

            @Override
            public void done(ArrayList<Job> newjobs) {
                utilities.dismissProgress();
                jobs = newjobs;
                update();
            }


        });
    }


    private void acceptJob(String jobId){


        utilities.showProgress("Accept Job", "please wait..", context);
        RestClient.getInstance().acceptJob(jobId, utilities.getUser(context).getId(), new AndroidCallback<Job>(context) {

            @Override
            public void done(Job job) {
                utilities.dismissProgress();
                getJobs();

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_job, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logoutPartner();
            return true;
        }else if (id==R.id.action_skills) {

            Intent intent = new Intent(context,CategoryActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void logoutPartner(){
        Intent intent = new Intent(context,LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        new TinyDB(context).clear();
        startActivity(intent);


    }
    



    public void update(){
        jobsAdapter.notifyDataSetChanged();
    }


    public class JobsAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return jobs.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView==null){
                convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_partner_job,parent,false);
                viewHolder= new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder= (ViewHolder) convertView.getTag();
            }

            Job job = jobs.get(position);

            viewHolder.txtCategory.setText(job.getCategory() + " Job");
            viewHolder.txtSubCategory.setText(job.getSubcategory());
            viewHolder.txtMessage.setText("By");

            if (!TextUtils.isEmpty(job.getPartnerid())){
                viewHolder.btnAccept.setText("Unavailable");

            }
            viewHolder.btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptJob(jobs.get(position).getId());
                }
            });


            if (job.getUser()!=null){
                viewHolder.txtCustName.setText(job.getUser().getName());
                Picasso.with(context).load(job.getUser().getPhotoUrl()).fit().placeholder(R.mipmap.ic_launcher).into(viewHolder.imgCustName);

            }else{
                viewHolder.txtCustName.setText("Not Available");

            }

            return convertView;
        }
    }

    static class  ViewHolder{

        @Bind(R.id.txtMessage)
        TextView txtMessage;

        @Bind(R.id.txtCategory)
        TextView txtCategory;

        @Bind(R.id.txtSubCategory)
        TextView txtSubCategory;

        @Bind(R.id.llCustomer)
        LinearLayout llCustomer;

        @Bind(R.id.imgCustIcon)
        CircleImageView imgCustName;

        @Bind(R.id.txtCustName)
        TextView txtCustName;

        @Bind(R.id.btnAccept)
        Button btnAccept;


        public ViewHolder(View view){
            ButterKnife.bind(this,view);
        }


    }



}
