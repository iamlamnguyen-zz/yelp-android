package com.quiz.yelp.features.detail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;

import com.bumptech.glide.Glide;
import com.quiz.yelp.R;
import com.quiz.yelp.models.Restaurant;
import com.quiz.yelp.utils.Constant;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements DetailContract.View {
    private static final String TAG = DetailActivity.class.getSimpleName();

    private DetailContract.ActionListener detailPresenter;

    @BindView(R.id.ivThumb)
    ImageView ivThumb;
    @BindView(R.id.tvName)
    EditText tvName;
    @BindView(R.id.tvPhone)
    EditText tvPhone;
    @BindView(R.id.tvCategories)
    EditText tvCategories;
    @BindView(R.id.ratingBar)
    RatingBar ratingBar;
    @BindView(R.id.tvReview)
    EditText tvReview;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    public static void toActivity(Activity activity, Restaurant restaurant) {
        Intent intent = new Intent(activity, DetailActivity.class);
        intent.putExtra(Constant.EXTRA_DATA, restaurant);
        activity.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        initToolbar();

        detailPresenter = new DetailPresenter();
        detailPresenter.onCreated(this);
        hasExtras(getIntent());
    }

    private void initToolbar() {
        toolbar.setTitle("Restaurant Detail");
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null) {
            return;
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void hasExtras(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            return;
        }
        detailPresenter.onGetResultRestaurant(bundle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void displayImage(String url) {
        Glide.with(this)
                .load(url)
                .into(ivThumb);
    }

    @Override
    public void displayNameRestaurant(String name) {
        tvName.setText(name);
    }

    @Override
    public void displayRating(float rating) {
        ratingBar.setRating(rating);
    }

    @Override
    public void displayPhoneNumber(String phone) {
        tvPhone.setText(phone);
    }

    @Override
    public void displayReviewsNumber(String reviews) {
        tvReview.setText(reviews);
    }

    @Override
    public void displayCategories(String categories) {
        tvCategories.setText(categories);
    }
}
