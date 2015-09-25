package com.demo.mike;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.demo.push.GcmListener;
import com.demo.service.RestClient;
import com.demo.utils.ActivityBase;
import com.demo.utils.AndroidCallback;
import com.demo.utils.Job;
import com.demo.utils.TinyDB;
import com.squareup.picasso.Picasso;
import de.hdodenhof.circleimageview.CircleImageView;

import java.util.ArrayList;

public class CustJobActivity extends ActivityBase {


    @Bind(R.id.lstJobs)
    ListView lstJobs;

    private ArrayList<Job> jobs = new ArrayList<Job>();
    private JobsAdapter jobsAdapter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentViewNoOverlay(R.layout.activity_cust_job);

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

        RestClient.getInstance().userListJob(utilities.getUser(context).getId(), new AndroidCallback<ArrayList<Job>>(context) {

            @Override
            public void done(ArrayList<Job> newjobs) {
                utilities.dismissProgress();
                jobs = newjobs;
                update();
            }


        });
    }

    public void update(){
        jobsAdapter.notifyDataSetChanged();
    }


    public class JobsAdapter extends BaseAdapter {

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
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;

            if (convertView==null){
                convertView= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cust_jobs,parent,false);
                viewHolder= new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }else{
                viewHolder= (ViewHolder) convertView.getTag();
            }
            Job job = jobs.get(position);

            viewHolder.txtCategory.setText(job.getCategory()+" Job");
            viewHolder.txtSubCategory.setText(job.getSubcategory());
            if (job.getPartner()!=null){

                viewHolder.txtMessage.setText("Alloted To");
                viewHolder.llPartner.setVisibility(View.VISIBLE);
                viewHolder.txtPartnerName.setText(job.getPartner().getName());
                Picasso.with(context).load(job.getPartner().getPhotoUrl()).fit().placeholder(R.mipmap.ic_launcher).into(viewHolder.imgPartnerIcon);


            }else{
                viewHolder.txtMessage.setText("No partner alloted yet");
                viewHolder.llPartner.setVisibility(View.GONE);
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

        @Bind(R.id.llPartner)
        LinearLayout llPartner;

        @Bind(R.id.imgPartnerIcon)
        CircleImageView imgPartnerIcon;

        @Bind(R.id.txtPartnerName)
        TextView txtPartnerName;



        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }


    }


}
