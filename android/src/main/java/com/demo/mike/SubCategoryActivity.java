package com.demo.mike;

import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.demo.models.User;
import com.demo.push.RegistrationIntentService;
import com.demo.service.RestClient;
import com.demo.utils.*;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;
import timber.log.Timber;

import java.util.ArrayList;


public class SubCategoryActivity extends ActivityBase implements OnClickListener {


    @Bind(R.id.imgIcon)
    ImageView imgIcon;

    @Bind(R.id.txtName)
    TextView txtName;

    @Bind(R.id.lstSubCategory)
    ListView lstSubCategory;


    public static final String KEY_CATEGORY = "category";
    public static final String KEY_ICON = "icon";

    ArrayList<String> subCategories = new ArrayList<String>();
    private SubCategoryAdapter adapter;


    private GoogleSignUp googleSignUp;
    private DialogPlus signInDialog;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        actionBar.setTitle("Tell us your required service");
        actionBar.setDisplayHomeAsUpEnabled(true);

        imgIcon.setImageResource(getIntent().getIntExtra(KEY_ICON, R.drawable.ic_logo));
        txtName.setText(getIntent().getStringExtra(KEY_CATEGORY));

        if (txtName.getText().equals("Cleaning")){
            createCleaning();
        }else if (txtName.getText().equals("Plumbing")){
            createPlumbing();
        }else if (txtName.getText().equals("Electrical")){
            createElectrical();
        }else if(txtName.getText().equals("Painting")){
            createPainting();
        }

        Timber.v("Count: "+ subCategories.size());

        adapter= new SubCategoryAdapter();
        lstSubCategory.setAdapter(adapter);


    }

    public void createElectrical(){
        subCategories.clear();
        subCategories.add("AC cooling and filter cleaning");
        subCategories.add("Light Fixtures");
        subCategories.add("Fan Repair");
        subCategories.add("Fridge compressor repair");

    }

    public void createCleaning(){
        subCategories.clear();
        subCategories.add("Vehicle cleaning");
        subCategories.add("House cleaning");
        subCategories.add("Graden cleaning ");
        subCategories.add("House hold appliances cleaning");

    }


    public void createPlumbing(){
        subCategories.clear();
        subCategories.add("Add or fix Household tap");
        subCategories.add("Add or fix garden water system");


    }

    public void createPainting(){
        subCategories.clear();
        subCategories.add("Vehicle Painting");
        subCategories.add("House Painting");
        subCategories.add("Garage Painting");

    }



    @OnItemClick(R.id.lstSubCategory)
    public void createRequest(int position){

        Job job = new Job();
        job.setCategory(txtName.getText().toString());
        job.setSubcategory(subCategories.get(position));

        if (utilities.isUserSignedIn(context)){
            utilities.showProgress("Create Request", "please wait", context);
            postJob(job);
        }else{
            signUpAndPostJob(job);
        }

    }


    public void postJob(Job job){

        job.setCustomerid(utilities.getUser(context).getId());

        RestClient.getInstance().requestJob(job, new AndroidCallback<Job>(context) {
            @Override
            public void done(Job job) {
                utilities.dismissProgress();
                Intent intent = new Intent(context, CustJobActivity.class);
                startActivity(intent);

            }

        });
    }


    public void signUpAndPostJob(final Job job){

        googleSignUp= new GoogleSignUp(context, new GoogleSignUp.ISignUp() {
            @Override
            public void sucess() {
                signInDialog.dismiss();

                User user = utilities.getUser(context);

                RestClient.getInstance().signIn(user, new AndroidCallback<User>(context) {
                    @Override
                    public void done(User user) {

                        if (user.isPartner()){
                            utilities.dismissProgress();
                            utilities.alertError("Email already used","This email is already used",context);
                            return;
                        }

                        utilities.setUser(user,context);
                        postJob(job);
                        utilities.setUserSignedIn(context,true);

                        if (utilities.checkPlayServices(context)) {
                            // Start IntentService to register this application with GCM.
                            Intent intent = new Intent(context, RegistrationIntentService.class);
                            startService(intent);
                        }

                    }

                });


            }

            @Override
            public void failed() {
                utilities.dismissProgress();
                signInDialog.dismiss();

            }
        });

        signInDialog = DialogPlus.newDialog(context)
                .setContentHolder(new com.orhanobut.dialogplus.ViewHolder(R.layout.item_signup))
                .setOnClickListener(this)
                .setCancelable(true)
                .setGravity(Gravity.BOTTOM)
                .create();

        signInDialog.show();


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleSignUp.onActivityResult(requestCode, resultCode, data);
    }


    @Override
    public void onClick(DialogPlus dialogPlus, View view) {
        if (view.getId()==R.id.sign_in_button){
            utilities.showProgress("Sign In","Please wait",context);
            googleSignUp.signIn();
        }
    }


    private class SubCategoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            Timber.v("getCount"+subCategories.size());

            return subCategories.size();
        }

        @Override
        public String getItem(int position) {
            return subCategories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subcategory, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            String subCategory = subCategories.get(position);
            viewHolder.txtSubCat.setText(subCategory);

            return convertView;
        }
    }


    static class ViewHolder {


        @Bind(R.id.txtSubCat)
        TextView txtSubCat;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);

        }

    }





}
