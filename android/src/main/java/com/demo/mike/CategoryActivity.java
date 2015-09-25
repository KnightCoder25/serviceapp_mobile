package com.demo.mike;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Bind;


import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.demo.mike.model.Category;
import com.demo.push.RegistrationIntentService;
import com.demo.service.RestClient;
import com.demo.utils.ActivityBase;
import com.demo.utils.AndroidCallback;
import com.demo.models.User;
import com.demo.utils.GoogleSignUp;
import com.demo.utils.TinyDB;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnClickListener;

import java.util.ArrayList;


public class CategoryActivity extends ActivityBase implements OnClickListener {


    @Bind(R.id.lstCategory)
    ListView lstCategory;

    ArrayList<Category> categories = new ArrayList<Category>();
    private CategoryAdapter adapter;


    public static String KEY_IS_CONSUMER = "is_consumer";

    private boolean isConsumer = false;

    private GoogleSignUp googleSignUp;
    private DialogPlus signInDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        isConsumer = getIntent().getBooleanExtra(KEY_IS_CONSUMER, false);

        createCategories();
        adapter = new CategoryAdapter();
        lstCategory.setAdapter(adapter);

        if (!isConsumer){
            actionBar.setTitle("Tell us your skills");

        }


    }


    @OnItemClick(R.id.lstCategory)
    public void selectCategory(int position) {

        //TODO: Reuse this for vendor
        Category category = categories.get(position);

        if (isConsumer) {

            Intent intent = new Intent(context, SubCategoryActivity.class);
            intent.putExtra(SubCategoryActivity.KEY_CATEGORY, category.getName());
            intent.putExtra(SubCategoryActivity.KEY_ICON, category.getImageId());

            if(utilities.isUserSignedIn(context)){
                startActivity(intent);
            }else{
                signUp();
            }


        } else {

            // Let him select, also show done button
            category.setIsSelected(!category.isSelected());
            updateCategory();


        }

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        googleSignUp.onActivityResult(requestCode, resultCode, data);
    }

    public void signUp(){

        googleSignUp= new GoogleSignUp(context, new GoogleSignUp.ISignUp() {
            @Override
            public void sucess() {
                signInDialog.dismiss();
                User user = utilities.getUser(context);
                RestClient.getInstance().signIn(user, new AndroidCallback<User>(context) {
                    @Override
                    public void done(User user) {
                        utilities.dismissProgress();

                        if (user.isPartner()){
                            utilities.alertError("Email already used","This email is already used",context);
                            utilities.setUserSignedIn(context,false);
                            return;
                        }

                        utilities.setUser(user, context);
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

    public void updateCategory() {
        adapter.notifyDataSetChanged();
    }


    // In realworld this wont be required as it would come from server
    public void createCategories() {
        categories.clear();
        User user = utilities.getUser(context);

        Category c1 = new Category();
        c1.setImageId(R.drawable.category1);
        c1.setName("Cleaning");
        if (!isConsumer){
            if (user.getSkills().contains(c1.getName())){
                c1.setIsSelected(true);
            }
        }
        categories.add(c1);

        Category c2 = new Category();
        c2.setImageId(R.drawable.category2);
        c2.setName("Plumbing");
        if (!isConsumer){
            if (user.getSkills().contains(c2.getName())){
                c2.setIsSelected(true);
            }
        }
        categories.add(c2);


        Category c3 = new Category();
        c3.setImageId(R.drawable.category3);
        c3.setName("Electrical");
        if (!isConsumer){
            if (user.getSkills().contains(c3.getName())){
                c3.setIsSelected(true);
            }
        }
        categories.add(c3);

        Category c4 = new Category();
        c4.setImageId(R.drawable.category4);
        c4.setName("Painting");
        if (!isConsumer){
            if (user.getSkills().contains(c4.getName())){
                c4.setIsSelected(true);
            }
        }
        categories.add(c4);


    }



    public void logoutCust(){
        Intent intent = new Intent(context,LandingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        new TinyDB(context).clear();
        startActivity(intent);


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (isConsumer) {
            getMenuInflater().inflate(R.menu.menu_category_consumer, menu);
        } else {
            getMenuInflater().inflate(R.menu.menu_category_partner, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_done) {
            updateProfile();
            return true;
        }else if (id == R.id.action_logout) {
            logoutCust();
            return true;
        }else if (id==R.id.action_jobs){
            Intent intent = new Intent(context,CustJobActivity.class);
            startActivity(intent);

            return true;
        }


        return super.onOptionsItemSelected(item);
    }



    public void updateProfile(){

        User currentUser = utilities.getUser(context);
        ArrayList<String> selectedCategories= new ArrayList<String>();

        for (Category c:categories){
            if (c.isSelected()){
                selectedCategories.add(c.getName());
            }
        }

        if (selectedCategories.isEmpty()){
            utilities.alertWarning("Category","Please select atleast 1 category",context);
            return;
        }

        String userId= currentUser.getId();
        currentUser.setSkills(selectedCategories);
        currentUser.setId(null);

        utilities.showProgress("Update","please wait..",context);
        RestClient.getInstance().updateProfile(userId, currentUser, new AndroidCallback<User>(context) {
            @Override
            public void done(User user) {
                utilities.dismissProgress();
                utilities.setUser(user, context);
                Intent intent = new Intent(context,JobActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }

        });



    }

    @Override
    public void onClick(DialogPlus dialogPlus, View view) {

        if (view.getId()==R.id.sign_in_button){
            utilities.showProgress("Sign In","Please wait",context);
            googleSignUp.signIn();
        }
    }


    private class CategoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return categories.size();
        }

        @Override
        public Category getItem(int position) {
            return categories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            Category category = categories.get(position);
            viewHolder.imgIcon.setImageResource(category.getImageId());
            viewHolder.txtName.setText(category.getName());
            viewHolder.imgSelected.setSelected(category.isSelected());

            return convertView;
        }
    }


    static class ViewHolder {

        @Bind(R.id.imgIcon)
        ImageView imgIcon;

        @Bind(R.id.txtName)
        TextView txtName;

        @Bind(R.id.imgSelected)
        ImageView imgSelected;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);

        }

    }


}
